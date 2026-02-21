package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.response.QuoteResponse;
import com.portfolio.rebalancer.dto.response.RebalanceResponse;
import com.portfolio.rebalancer.dto.response.RebalanceResponse.AllocationComparison;
import com.portfolio.rebalancer.dto.response.RebalanceResponse.LotSaleDetail;
import com.portfolio.rebalancer.dto.response.RebalanceResponse.TradeRecommendation;
import com.portfolio.rebalancer.entity.AssetClass;
import com.portfolio.rebalancer.entity.Holding;
import com.portfolio.rebalancer.entity.Portfolio;
import com.portfolio.rebalancer.entity.TargetAllocation;
import com.portfolio.rebalancer.entity.Trade;
import com.portfolio.rebalancer.entity.TradeType;
import com.portfolio.rebalancer.exception.InvalidAllocationException;
import com.portfolio.rebalancer.repository.HoldingRepository;
import com.portfolio.rebalancer.repository.TargetAllocationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RebalanceService {

    private final HoldingRepository holdingRepository;
    private final TargetAllocationRepository allocationRepository;
    private final MarketDataClient marketDataClient;
    private final PortfolioService portfolioService;
    private final LotCalculationService lotCalculationService;

    public RebalanceService(HoldingRepository holdingRepository, TargetAllocationRepository allocationRepository,
                            MarketDataClient marketDataClient, PortfolioService portfolioService,
                            LotCalculationService lotCalculationService) {
        this.holdingRepository = holdingRepository;
        this.allocationRepository = allocationRepository;
        this.marketDataClient = marketDataClient;
        this.portfolioService = portfolioService;
        this.lotCalculationService = lotCalculationService;
    }

    public RebalanceResponse calculateRebalance(Long portfolioId) {
        Portfolio portfolio = portfolioService.findPortfolioForCurrentUser(portfolioId);
        List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);
        List<TargetAllocation> targets = allocationRepository.findByPortfolioId(portfolioId);

        if (holdings.isEmpty()) {
            throw new InvalidAllocationException("Portfolio has no holdings");
        }
        if (targets.isEmpty()) {
            throw new InvalidAllocationException("No target allocations defined");
        }

        // Fetch current prices
        List<String> tickers = holdings.stream().map(Holding::getTickerSymbol).toList();
        Map<String, QuoteResponse> priceMap = fetchPriceMap(tickers);

        // Calculate total portfolio value
        BigDecimal totalValue = BigDecimal.ZERO;
        Map<String, BigDecimal> holdingValues = new HashMap<>();
        Map<String, BigDecimal> holdingQuantities = new HashMap<>();
        for (Holding h : holdings) {
            BigDecimal qty = computeQuantity(h);
            holdingQuantities.put(h.getTickerSymbol(), qty);
            BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                    QuoteResponse.builder().regularMarketPrice(BigDecimal.ZERO).build())
                    .getRegularMarketPrice();
            BigDecimal value = qty.multiply(price);
            holdingValues.put(h.getTickerSymbol(), value);
            totalValue = totalValue.add(value);
        }

        // Include unallocated cash balance in total portfolio value
        totalValue = totalValue.add(portfolio.getCashBalance());

        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidAllocationException("Portfolio total value is zero. Check prices.");
        }

        // Determine portfolio currency from first holding with a currency set
        String currency = holdings.stream()
                .map(Holding::getCurrency)
                .filter(c -> c != null && !c.isEmpty())
                .findFirst()
                .orElse("USD");

        // Calculate current allocation per asset class
        Map<AssetClass, BigDecimal> currentValueByClass = new EnumMap<>(AssetClass.class);
        for (Holding h : holdings) {
            BigDecimal value = holdingValues.get(h.getTickerSymbol());
            currentValueByClass.merge(h.getAssetClass(), value, BigDecimal::add);
        }

        // Build target map
        Map<AssetClass, BigDecimal> targetMap = targets.stream()
                .collect(Collectors.toMap(TargetAllocation::getAssetClass, TargetAllocation::getTargetPercentage));

        // Build allocation comparisons
        List<AllocationComparison> comparisons = new ArrayList<>();
        Set<AssetClass> allClasses = new HashSet<>(currentValueByClass.keySet());
        allClasses.addAll(targetMap.keySet());

        for (AssetClass ac : allClasses) {
            BigDecimal currentVal = currentValueByClass.getOrDefault(ac, BigDecimal.ZERO);
            BigDecimal currentPct = currentVal.multiply(new BigDecimal("100"))
                    .divide(totalValue, 2, RoundingMode.HALF_UP);
            BigDecimal targetPct = targetMap.getOrDefault(ac, BigDecimal.ZERO);
            BigDecimal targetVal = totalValue.multiply(targetPct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            comparisons.add(AllocationComparison.builder()
                    .assetClass(ac)
                    .currentPercentage(currentPct)
                    .targetPercentage(targetPct)
                    .driftPercentage(currentPct.subtract(targetPct))
                    .currentValue(currentVal)
                    .targetValue(targetVal)
                    .build());
        }

        // Generate trade recommendations
        List<TradeRecommendation> trades = generateTrades(holdings, holdingQuantities, priceMap,
                currentValueByClass, targetMap, totalValue);

        // Calculate unallocated cash: sell proceeds minus buy costs
        BigDecimal sellProceeds = BigDecimal.ZERO;
        BigDecimal buyCosts = BigDecimal.ZERO;
        for (TradeRecommendation t : trades) {
            if ("SELL".equals(t.getAction())) {
                sellProceeds = sellProceeds.add(t.getEstimatedCost());
            } else {
                buyCosts = buyCosts.add(t.getEstimatedCost());
            }
        }
        BigDecimal unallocatedCash = sellProceeds.subtract(buyCosts).max(BigDecimal.ZERO);

        return RebalanceResponse.builder()
                .portfolioId(portfolioId)
                .totalPortfolioValue(totalValue.setScale(2, RoundingMode.HALF_UP))
                .currency(currency)
                .allocations(comparisons)
                .trades(trades)
                .unallocatedCash(unallocatedCash.setScale(2, RoundingMode.HALF_UP))
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    public RebalanceResponse calculateCashRebalance(Long portfolioId, BigDecimal newCash) {
        Portfolio portfolio = portfolioService.findPortfolioForCurrentUser(portfolioId);
        List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);
        List<TargetAllocation> targets = allocationRepository.findByPortfolioId(portfolioId);

        if (targets.isEmpty()) {
            throw new InvalidAllocationException("No target allocations defined");
        }

        List<String> tickers = holdings.stream().map(Holding::getTickerSymbol).toList();
        Map<String, QuoteResponse> priceMap = fetchPriceMap(tickers);

        // Group holdings by asset class
        Map<AssetClass, List<Holding>> holdingsByClass = holdings.stream()
                .collect(Collectors.groupingBy(Holding::getAssetClass));

        Map<AssetClass, BigDecimal> targetMap = targets.stream()
                .collect(Collectors.toMap(TargetAllocation::getAssetClass, TargetAllocation::getTargetPercentage));

        // Calculate current value per asset class
        Map<AssetClass, BigDecimal> currentValueByClass = new EnumMap<>(AssetClass.class);
        BigDecimal currentTotalValue = BigDecimal.ZERO;
        for (Holding h : holdings) {
            BigDecimal qty = computeQuantity(h);
            BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                    QuoteResponse.builder().regularMarketPrice(BigDecimal.ZERO).build())
                    .getRegularMarketPrice();
            BigDecimal value = qty.multiply(price);
            currentValueByClass.merge(h.getAssetClass(), value, BigDecimal::add);
            currentTotalValue = currentTotalValue.add(value);
        }

        // Include existing cash balance in total value
        currentTotalValue = currentTotalValue.add(portfolio.getCashBalance());

        // Budget = new cash + existing cash balance
        BigDecimal budget = newCash.add(portfolio.getCashBalance());

        BigDecimal newTotalValue = currentTotalValue.add(newCash);

        // Build list of underweight classes sorted by drift % (most underweight first)
        List<Map.Entry<AssetClass, BigDecimal>> deficits = new ArrayList<>();
        for (var entry : targetMap.entrySet()) {
            AssetClass ac = entry.getKey();
            BigDecimal targetVal = newTotalValue.multiply(entry.getValue())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal currentVal = currentValueByClass.getOrDefault(ac, BigDecimal.ZERO);
            BigDecimal deficit = targetVal.subtract(currentVal);
            if (deficit.compareTo(BigDecimal.ZERO) > 0) {
                deficits.add(Map.entry(ac, deficit));
            }
        }
        // Sort by deficit descending so most underweight class gets cash first
        deficits.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Fill-the-gap: allocate cash to most underweight class first, then next
        List<TradeRecommendation> trades = new ArrayList<>();
        BigDecimal remainingCash = budget;
        for (var deficit : deficits) {
            if (remainingCash.compareTo(BigDecimal.ONE) < 0) break;

            AssetClass ac = deficit.getKey();
            BigDecimal cashForClass = remainingCash.min(deficit.getValue());

            // Pick the holding with the highest current value in this class (most active position)
            List<Holding> classHoldings = holdingsByClass.getOrDefault(ac, Collections.emptyList());
            if (classHoldings.isEmpty()) continue;

            Holding bestHolding = classHoldings.stream()
                    .max(Comparator.comparing(h -> {
                        BigDecimal qty = computeQuantity(h);
                        BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                                QuoteResponse.builder().regularMarketPrice(BigDecimal.ZERO).build())
                                .getRegularMarketPrice();
                        return qty.multiply(price);
                    }))
                    .orElse(classHoldings.get(0));

            BigDecimal price = priceMap.getOrDefault(bestHolding.getTickerSymbol(),
                    QuoteResponse.builder().regularMarketPrice(BigDecimal.ONE).build())
                    .getRegularMarketPrice();
            if (price.compareTo(BigDecimal.ZERO) <= 0) continue;

            int shares = cashForClass.divide(price, 0, RoundingMode.DOWN).intValue();
            if (shares > 0) {
                BigDecimal cost = price.multiply(BigDecimal.valueOf(shares));
                trades.add(TradeRecommendation.builder()
                        .holdingId(bestHolding.getId())
                        .tickerSymbol(bestHolding.getTickerSymbol())
                        .name(bestHolding.getName())
                        .assetClass(ac)
                        .action("BUY")
                        .shares(shares)
                        .currentPrice(price)
                        .estimatedCost(cost)
                        .build());
                remainingCash = remainingCash.subtract(cost);
            }
        }

        // Build allocation comparisons showing before/after
        List<AllocationComparison> comparisons = new ArrayList<>();
        Set<AssetClass> allClasses = new HashSet<>(currentValueByClass.keySet());
        allClasses.addAll(targetMap.keySet());
        for (AssetClass ac : allClasses) {
            BigDecimal currentVal = currentValueByClass.getOrDefault(ac, BigDecimal.ZERO);
            BigDecimal currentPct = currentTotalValue.compareTo(BigDecimal.ZERO) > 0
                    ? currentVal.multiply(new BigDecimal("100")).divide(currentTotalValue, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal targetPct = targetMap.getOrDefault(ac, BigDecimal.ZERO);
            BigDecimal targetVal = newTotalValue.multiply(targetPct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            comparisons.add(AllocationComparison.builder()
                    .assetClass(ac)
                    .currentPercentage(currentPct)
                    .targetPercentage(targetPct)
                    .driftPercentage(currentPct.subtract(targetPct))
                    .currentValue(currentVal)
                    .targetValue(targetVal)
                    .build());
        }

        String currency = holdings.stream()
                .map(Holding::getCurrency)
                .filter(c -> c != null && !c.isEmpty())
                .findFirst()
                .orElse("USD");

        return RebalanceResponse.builder()
                .portfolioId(portfolioId)
                .totalPortfolioValue(newTotalValue)
                .currency(currency)
                .allocations(comparisons)
                .trades(trades)
                .unallocatedCash(remainingCash.setScale(2, RoundingMode.HALF_UP))
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    private BigDecimal computeQuantity(Holding holding) {
        if (holding.getTrades() == null) return BigDecimal.ZERO;
        BigDecimal qty = BigDecimal.ZERO;
        for (Trade t : holding.getTrades()) {
            if (t.getType() == TradeType.BUY) {
                qty = qty.add(t.getQuantity());
            } else {
                qty = qty.subtract(t.getQuantity());
            }
        }
        return qty;
    }

    private List<TradeRecommendation> generateTrades(
            List<Holding> holdings,
            Map<String, BigDecimal> holdingQuantities,
            Map<String, QuoteResponse> priceMap,
            Map<AssetClass, BigDecimal> currentValueByClass,
            Map<AssetClass, BigDecimal> targetMap,
            BigDecimal totalValue) {

        List<TradeRecommendation> trades = new ArrayList<>();

        // Group holdings by asset class
        Map<AssetClass, List<Holding>> holdingsByClass = holdings.stream()
                .collect(Collectors.groupingBy(Holding::getAssetClass));

        for (var entry : holdingsByClass.entrySet()) {
            AssetClass ac = entry.getKey();
            List<Holding> classHoldings = entry.getValue();

            BigDecimal currentVal = currentValueByClass.getOrDefault(ac, BigDecimal.ZERO);
            BigDecimal targetPct = targetMap.getOrDefault(ac, BigDecimal.ZERO);
            BigDecimal targetVal = totalValue.multiply(targetPct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal delta = targetVal.subtract(currentVal);

            if (delta.abs().compareTo(new BigDecimal("1")) < 0) {
                continue; // Skip tiny adjustments
            }

            // Distribute delta proportionally across holdings in this class
            BigDecimal classTotal = classHoldings.stream()
                    .map(h -> {
                        BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                                QuoteResponse.builder().regularMarketPrice(BigDecimal.ONE).build())
                                .getRegularMarketPrice();
                        BigDecimal qty = holdingQuantities.getOrDefault(h.getTickerSymbol(), BigDecimal.ZERO);
                        return qty.multiply(price);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (Holding h : classHoldings) {
                BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                        QuoteResponse.builder().regularMarketPrice(BigDecimal.ONE).build())
                        .getRegularMarketPrice();
                if (price.compareTo(BigDecimal.ZERO) == 0) continue;

                BigDecimal qty = holdingQuantities.getOrDefault(h.getTickerSymbol(), BigDecimal.ZERO);
                BigDecimal holdingVal = qty.multiply(price);
                BigDecimal proportion = classTotal.compareTo(BigDecimal.ZERO) > 0
                        ? holdingVal.divide(classTotal, 6, RoundingMode.HALF_UP)
                        : BigDecimal.ONE.divide(BigDecimal.valueOf(classHoldings.size()), 6, RoundingMode.HALF_UP);

                BigDecimal holdingDelta = delta.multiply(proportion);
                int sharesToTrade = holdingDelta.abs().divide(price, 0, RoundingMode.DOWN).intValue();

                if (sharesToTrade == 0) continue;

                String action = delta.compareTo(BigDecimal.ZERO) > 0 ? "BUY" : "SELL";
                BigDecimal currentWeight = holdingVal.multiply(new BigDecimal("100"))
                        .divide(totalValue, 2, RoundingMode.HALF_UP);
                BigDecimal newVal = holdingVal.add(holdingDelta);
                BigDecimal targetWeight = newVal.multiply(new BigDecimal("100"))
                        .divide(totalValue, 2, RoundingMode.HALF_UP);

                // For SELL actions, compute HIFO lot details
                List<LotSaleDetail> lotDetails = null;
                if ("SELL".equals(action)) {
                    List<Trade> holdingTrades = h.getTrades() != null ? h.getTrades() : Collections.emptyList();
                    List<LotCalculationService.Lot> openLots = lotCalculationService.computeOpenLots(holdingTrades);
                    List<Map.Entry<LotCalculationService.Lot, BigDecimal>> selections =
                            lotCalculationService.selectLotsForSale(openLots, BigDecimal.valueOf(sharesToTrade));
                    lotDetails = new ArrayList<>();
                    for (Map.Entry<LotCalculationService.Lot, BigDecimal> sel : selections) {
                        LotCalculationService.Lot lot = sel.getKey();
                        BigDecimal sellQty = sel.getValue();
                        BigDecimal gain = price.subtract(lot.getCostBasisPerShare()).multiply(sellQty);
                        lotDetails.add(new LotSaleDetail(
                                lot.getTradeId(), lot.getPurchaseDate(), sellQty,
                                lot.getCostBasisPerShare(), gain));
                    }
                }

                trades.add(TradeRecommendation.builder()
                        .holdingId(h.getId())
                        .tickerSymbol(h.getTickerSymbol())
                        .name(h.getName())
                        .assetClass(ac)
                        .action(action)
                        .shares(sharesToTrade)
                        .currentPrice(price)
                        .estimatedCost(price.multiply(BigDecimal.valueOf(sharesToTrade)))
                        .currentWeight(currentWeight)
                        .targetWeight(targetWeight)
                        .lotDetails(lotDetails)
                        .build());
            }
        }

        return trades;
    }

    private Map<String, QuoteResponse> fetchPriceMap(List<String> tickers) {
        List<QuoteResponse> quotes = marketDataClient.fetchQuotes(tickers);
        return quotes.stream().collect(Collectors.toMap(QuoteResponse::getSymbol, q -> q, (a, b) -> a));
    }
}
