package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.response.QuoteResponse;
import com.portfolio.rebalancer.dto.response.RebalanceResponse;
import com.portfolio.rebalancer.dto.response.RebalanceResponse.AllocationComparison;
import com.portfolio.rebalancer.dto.response.RebalanceResponse.TradeRecommendation;
import com.portfolio.rebalancer.entity.AssetClass;
import com.portfolio.rebalancer.entity.Holding;
import com.portfolio.rebalancer.entity.Portfolio;
import com.portfolio.rebalancer.entity.TargetAllocation;
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
    private final YahooFinanceService yahooFinanceService;
    private final PortfolioService portfolioService;

    public RebalanceService(HoldingRepository holdingRepository, TargetAllocationRepository allocationRepository,
                            YahooFinanceService yahooFinanceService, PortfolioService portfolioService) {
        this.holdingRepository = holdingRepository;
        this.allocationRepository = allocationRepository;
        this.yahooFinanceService = yahooFinanceService;
        this.portfolioService = portfolioService;
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
        for (Holding h : holdings) {
            BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                    QuoteResponse.builder().regularMarketPrice(BigDecimal.ZERO).build())
                    .getRegularMarketPrice();
            BigDecimal value = h.getQuantity().multiply(price);
            holdingValues.put(h.getTickerSymbol(), value);
            totalValue = totalValue.add(value);
        }

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
        List<TradeRecommendation> trades = generateTrades(holdings, priceMap, currentValueByClass,
                targetMap, totalValue);

        return RebalanceResponse.builder()
                .portfolioId(portfolioId)
                .totalPortfolioValue(totalValue.setScale(2, RoundingMode.HALF_UP))
                .currency(currency)
                .allocations(comparisons)
                .trades(trades)
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

        // Allocate new cash according to target percentages
        List<TradeRecommendation> trades = new ArrayList<>();
        for (var entry : targetMap.entrySet()) {
            AssetClass ac = entry.getKey();
            BigDecimal pct = entry.getValue();
            BigDecimal cashForClass = newCash.multiply(pct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            // Distribute to first holding in this asset class
            List<Holding> classHoldings = holdingsByClass.getOrDefault(ac, Collections.emptyList());
            if (!classHoldings.isEmpty()) {
                Holding h = classHoldings.get(0);
                BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                        QuoteResponse.builder().regularMarketPrice(BigDecimal.ONE).build())
                        .getRegularMarketPrice();
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    int shares = cashForClass.divide(price, 0, RoundingMode.DOWN).intValue();
                    if (shares > 0) {
                        trades.add(TradeRecommendation.builder()
                                .tickerSymbol(h.getTickerSymbol())
                                .name(h.getName())
                                .assetClass(ac)
                                .action("BUY")
                                .shares(shares)
                                .currentPrice(price)
                                .estimatedCost(price.multiply(BigDecimal.valueOf(shares)))
                                .build());
                    }
                }
            }
        }

        String currency = holdings.stream()
                .map(Holding::getCurrency)
                .filter(c -> c != null && !c.isEmpty())
                .findFirst()
                .orElse("USD");

        return RebalanceResponse.builder()
                .portfolioId(portfolioId)
                .totalPortfolioValue(newCash)
                .currency(currency)
                .trades(trades)
                .allocations(Collections.emptyList())
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    private List<TradeRecommendation> generateTrades(
            List<Holding> holdings,
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
                        return h.getQuantity().multiply(price);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (Holding h : classHoldings) {
                BigDecimal price = priceMap.getOrDefault(h.getTickerSymbol(),
                        QuoteResponse.builder().regularMarketPrice(BigDecimal.ONE).build())
                        .getRegularMarketPrice();
                if (price.compareTo(BigDecimal.ZERO) == 0) continue;

                BigDecimal holdingVal = h.getQuantity().multiply(price);
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

                trades.add(TradeRecommendation.builder()
                        .tickerSymbol(h.getTickerSymbol())
                        .name(h.getName())
                        .assetClass(ac)
                        .action(action)
                        .shares(sharesToTrade)
                        .currentPrice(price)
                        .estimatedCost(price.multiply(BigDecimal.valueOf(sharesToTrade)))
                        .currentWeight(currentWeight)
                        .targetWeight(targetWeight)
                        .build());
            }
        }

        return trades;
    }

    private Map<String, QuoteResponse> fetchPriceMap(List<String> tickers) {
        List<QuoteResponse> quotes = yahooFinanceService.fetchQuotes(tickers);
        return quotes.stream().collect(Collectors.toMap(QuoteResponse::getSymbol, q -> q, (a, b) -> a));
    }
}
