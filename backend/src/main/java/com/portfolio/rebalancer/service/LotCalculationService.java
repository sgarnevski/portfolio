package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.entity.Trade;
import com.portfolio.rebalancer.entity.TradeType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class LotCalculationService {

    public static class Lot {
        private final Long tradeId;
        private final LocalDate purchaseDate;
        private final BigDecimal originalQuantity;
        private BigDecimal remainingQuantity;
        private final BigDecimal costBasisPerShare;

        public Lot(Long tradeId, LocalDate purchaseDate, BigDecimal originalQuantity,
                   BigDecimal remainingQuantity, BigDecimal costBasisPerShare) {
            this.tradeId = tradeId;
            this.purchaseDate = purchaseDate;
            this.originalQuantity = originalQuantity;
            this.remainingQuantity = remainingQuantity;
            this.costBasisPerShare = costBasisPerShare;
        }

        public Long getTradeId() { return tradeId; }
        public LocalDate getPurchaseDate() { return purchaseDate; }
        public BigDecimal getOriginalQuantity() { return originalQuantity; }
        public BigDecimal getRemainingQuantity() { return remainingQuantity; }
        public void setRemainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; }
        public BigDecimal getCostBasisPerShare() { return costBasisPerShare; }
    }

    public static class LotDisposition {
        private final Long buyTradeId;
        private final Long sellTradeId;
        private final BigDecimal quantitySold;
        private final BigDecimal costBasisPerShare;
        private final BigDecimal sellPricePerShare;
        private final BigDecimal realizedGain;

        public LotDisposition(Long buyTradeId, Long sellTradeId, BigDecimal quantitySold,
                              BigDecimal costBasisPerShare, BigDecimal sellPricePerShare, BigDecimal realizedGain) {
            this.buyTradeId = buyTradeId;
            this.sellTradeId = sellTradeId;
            this.quantitySold = quantitySold;
            this.costBasisPerShare = costBasisPerShare;
            this.sellPricePerShare = sellPricePerShare;
            this.realizedGain = realizedGain;
        }

        public Long getBuyTradeId() { return buyTradeId; }
        public Long getSellTradeId() { return sellTradeId; }
        public BigDecimal getQuantitySold() { return quantitySold; }
        public BigDecimal getCostBasisPerShare() { return costBasisPerShare; }
        public BigDecimal getSellPricePerShare() { return sellPricePerShare; }
        public BigDecimal getRealizedGain() { return realizedGain; }
    }

    /**
     * Compute open (remaining) lots using HIFO consumption.
     * BUY trades become lots. SELL trades consume lots starting with the highest cost basis.
     */
    public List<Lot> computeOpenLots(List<Trade> trades) {
        List<Lot> lots = buildLots(trades);
        List<Trade> sells = trades.stream()
                .filter(t -> t.getType() == TradeType.SELL)
                .sorted(Comparator.comparing(Trade::getDate).thenComparing(Trade::getId))
                .toList();

        for (Trade sell : sells) {
            consumeLotsHifo(lots, sell.getQuantity());
        }

        return lots.stream()
                .filter(lot -> lot.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    /**
     * Compute all realized dispositions (lot-level realized P/L) using HIFO.
     */
    public List<LotDisposition> computeRealizedDispositions(List<Trade> trades) {
        List<Lot> lots = buildLots(trades);
        List<Trade> sells = trades.stream()
                .filter(t -> t.getType() == TradeType.SELL)
                .sorted(Comparator.comparing(Trade::getDate).thenComparing(Trade::getId))
                .toList();

        List<LotDisposition> dispositions = new ArrayList<>();
        for (Trade sell : sells) {
            BigDecimal remaining = sell.getQuantity();
            BigDecimal sellFeePerShare = BigDecimal.ZERO;
            if (sell.getFee() != null && sell.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                sellFeePerShare = sell.getFee().divide(sell.getQuantity(), 6, RoundingMode.HALF_UP);
            }
            BigDecimal sellNetPrice = sell.getPrice().subtract(sellFeePerShare);

            // Sort lots by cost basis DESC (HIFO)
            lots.sort((a, b) -> b.getCostBasisPerShare().compareTo(a.getCostBasisPerShare()));

            for (Lot lot : lots) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                if (lot.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) continue;

                BigDecimal consumed = lot.getRemainingQuantity().min(remaining);
                lot.setRemainingQuantity(lot.getRemainingQuantity().subtract(consumed));
                remaining = remaining.subtract(consumed);

                BigDecimal gain = sellNetPrice.subtract(lot.getCostBasisPerShare()).multiply(consumed);
                dispositions.add(new LotDisposition(
                        lot.getTradeId(), sell.getId(), consumed,
                        lot.getCostBasisPerShare(), sell.getPrice(), gain));
            }
        }

        return dispositions;
    }

    /**
     * Select lots for a proposed sale quantity using HIFO ordering.
     * Returns list of (Lot, quantityToSell) pairs.
     */
    public List<Map.Entry<Lot, BigDecimal>> selectLotsForSale(List<Lot> openLots, BigDecimal qtyToSell) {
        List<Lot> sorted = new ArrayList<>(openLots);
        sorted.sort((a, b) -> b.getCostBasisPerShare().compareTo(a.getCostBasisPerShare()));

        List<Map.Entry<Lot, BigDecimal>> selections = new ArrayList<>();
        BigDecimal remaining = qtyToSell;

        for (Lot lot : sorted) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            if (lot.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal qty = lot.getRemainingQuantity().min(remaining);
            selections.add(Map.entry(lot, qty));
            remaining = remaining.subtract(qty);
        }

        return selections;
    }

    private List<Lot> buildLots(List<Trade> trades) {
        List<Lot> lots = new ArrayList<>();
        for (Trade t : trades) {
            if (t.getType() == TradeType.BUY) {
                BigDecimal fee = t.getFee() != null ? t.getFee() : BigDecimal.ZERO;
                BigDecimal costPerShare = t.getPrice().add(
                        fee.divide(t.getQuantity(), 6, RoundingMode.HALF_UP));
                lots.add(new Lot(t.getId(), t.getDate(), t.getQuantity(), t.getQuantity(), costPerShare));
            }
        }
        return lots;
    }

    private void consumeLotsHifo(List<Lot> lots, BigDecimal qtyToConsume) {
        // Sort by cost basis DESC (HIFO)
        lots.sort((a, b) -> b.getCostBasisPerShare().compareTo(a.getCostBasisPerShare()));

        BigDecimal remaining = qtyToConsume;
        for (Lot lot : lots) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            if (lot.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal consumed = lot.getRemainingQuantity().min(remaining);
            lot.setRemainingQuantity(lot.getRemainingQuantity().subtract(consumed));
            remaining = remaining.subtract(consumed);
        }
    }
}
