package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.request.CreateHoldingRequest;
import com.portfolio.rebalancer.dto.response.HoldingResponse;
import com.portfolio.rebalancer.dto.response.LotResponse;
import com.portfolio.rebalancer.dto.response.TradeResponse;
import com.portfolio.rebalancer.entity.Holding;
import com.portfolio.rebalancer.entity.Portfolio;
import com.portfolio.rebalancer.entity.Trade;
import com.portfolio.rebalancer.entity.TradeType;
import com.portfolio.rebalancer.exception.ResourceNotFoundException;
import com.portfolio.rebalancer.dto.response.QuoteResponse;
import com.portfolio.rebalancer.repository.HoldingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Service
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final PortfolioService portfolioService;
    private final MarketDataClient marketDataClient;
    private final LotCalculationService lotCalculationService;

    public HoldingService(HoldingRepository holdingRepository, PortfolioService portfolioService,
                          MarketDataClient marketDataClient, LotCalculationService lotCalculationService) {
        this.holdingRepository = holdingRepository;
        this.portfolioService = portfolioService;
        this.marketDataClient = marketDataClient;
        this.lotCalculationService = lotCalculationService;
    }

    public List<HoldingResponse> getHoldings(Long portfolioId) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        return holdingRepository.findByPortfolioId(portfolioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public HoldingResponse addHolding(Long portfolioId, CreateHoldingRequest request) {
        validateTicker(request.getTickerSymbol());
        Portfolio portfolio = portfolioService.findPortfolioForCurrentUser(portfolioId);

        Holding holding = Holding.builder()
                .portfolio(portfolio)
                .tickerSymbol(request.getTickerSymbol().toUpperCase())
                .name(request.getName())
                .assetClass(request.getAssetClass())
                .currency(request.getCurrency())
                .build();
        holding = holdingRepository.save(holding);
        return toResponse(holding);
    }

    @Transactional
    public HoldingResponse updateHolding(Long portfolioId, Long holdingId, CreateHoldingRequest request) {
        validateTicker(request.getTickerSymbol());
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found"));

        holding.setTickerSymbol(request.getTickerSymbol().toUpperCase());
        holding.setName(request.getName());
        holding.setAssetClass(request.getAssetClass());
        holding.setCurrency(request.getCurrency());
        holding = holdingRepository.save(holding);
        return toResponse(holding);
    }

    private void validateTicker(String tickerSymbol) {
        QuoteResponse quote = marketDataClient.fetchQuote(tickerSymbol);
        if (quote.getRegularMarketPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid ticker symbol: " + tickerSymbol);
        }
    }

    @Transactional
    public void deleteHolding(Long portfolioId, Long holdingId) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        holdingRepository.deleteById(holdingId);
    }

    public HoldingResponse toResponse(Holding holding) {
        List<Trade> trades = holding.getTrades() != null ? holding.getTrades() : Collections.emptyList();

        // Use HIFO lot calculation for quantity, cost basis, and realized P/L
        List<LotCalculationService.Lot> openLots = lotCalculationService.computeOpenLots(trades);

        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        for (LotCalculationService.Lot lot : openLots) {
            quantity = quantity.add(lot.getRemainingQuantity());
            totalCost = totalCost.add(lot.getRemainingQuantity().multiply(lot.getCostBasisPerShare()));
        }

        // Realized P/L from HIFO dispositions
        BigDecimal realizedPnL = BigDecimal.ZERO;
        List<LotCalculationService.LotDisposition> dispositions = lotCalculationService.computeRealizedDispositions(trades);
        for (LotCalculationService.LotDisposition d : dispositions) {
            realizedPnL = realizedPnL.add(d.getRealizedGain());
        }

        BigDecimal averageCostBasis = BigDecimal.ZERO;
        if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            averageCostBasis = totalCost.divide(quantity, 4, RoundingMode.HALF_UP);
        }

        List<TradeResponse> tradeResponses = trades.stream()
                .map(t -> TradeResponse.builder()
                        .id(t.getId())
                        .date(t.getDate())
                        .type(t.getType())
                        .quantity(t.getQuantity())
                        .price(t.getPrice())
                        .fee(t.getFee())
                        .createdAt(t.getCreatedAt())
                        .build())
                .toList();

        List<LotResponse> lotResponses = openLots.stream()
                .map(lot -> LotResponse.builder()
                        .tradeId(lot.getTradeId())
                        .purchaseDate(lot.getPurchaseDate())
                        .originalQuantity(lot.getOriginalQuantity())
                        .remainingQuantity(lot.getRemainingQuantity())
                        .costBasisPerShare(lot.getCostBasisPerShare())
                        .build())
                .toList();

        return HoldingResponse.builder()
                .id(holding.getId())
                .tickerSymbol(holding.getTickerSymbol())
                .name(holding.getName())
                .assetClass(holding.getAssetClass())
                .quantity(quantity)
                .averageCostBasis(averageCostBasis)
                .totalCost(totalCost)
                .realizedPnL(realizedPnL)
                .currency(holding.getCurrency())
                .trades(tradeResponses)
                .lots(lotResponses)
                .build();
    }
}
