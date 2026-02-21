package com.portfolio.rebalancer.dto.response;

import com.portfolio.rebalancer.entity.AssetClass;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RebalanceResponse {
    private Long portfolioId;
    private BigDecimal totalPortfolioValue;
    private String currency;
    private List<AllocationComparison> allocations;
    private List<TradeRecommendation> trades;
    private BigDecimal unallocatedCash;
    private LocalDateTime calculatedAt;

    public RebalanceResponse() {
    }

    public RebalanceResponse(Long portfolioId, BigDecimal totalPortfolioValue, String currency,
                             List<AllocationComparison> allocations, List<TradeRecommendation> trades,
                             BigDecimal unallocatedCash, LocalDateTime calculatedAt) {
        this.portfolioId = portfolioId;
        this.totalPortfolioValue = totalPortfolioValue;
        this.currency = currency;
        this.allocations = allocations;
        this.trades = trades;
        this.unallocatedCash = unallocatedCash;
        this.calculatedAt = calculatedAt;
    }

    public Long getPortfolioId() { return portfolioId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }

    public BigDecimal getTotalPortfolioValue() { return totalPortfolioValue; }
    public void setTotalPortfolioValue(BigDecimal totalPortfolioValue) { this.totalPortfolioValue = totalPortfolioValue; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<AllocationComparison> getAllocations() { return allocations; }
    public void setAllocations(List<AllocationComparison> allocations) { this.allocations = allocations; }

    public List<TradeRecommendation> getTrades() { return trades; }
    public void setTrades(List<TradeRecommendation> trades) { this.trades = trades; }

    public BigDecimal getUnallocatedCash() { return unallocatedCash; }
    public void setUnallocatedCash(BigDecimal unallocatedCash) { this.unallocatedCash = unallocatedCash; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }

    public static RebalanceResponseBuilder builder() { return new RebalanceResponseBuilder(); }

    public static class RebalanceResponseBuilder {
        private Long portfolioId;
        private BigDecimal totalPortfolioValue;
        private String currency;
        private List<AllocationComparison> allocations;
        private List<TradeRecommendation> trades;
        private BigDecimal unallocatedCash;
        private LocalDateTime calculatedAt;

        public RebalanceResponseBuilder portfolioId(Long portfolioId) { this.portfolioId = portfolioId; return this; }
        public RebalanceResponseBuilder totalPortfolioValue(BigDecimal totalPortfolioValue) { this.totalPortfolioValue = totalPortfolioValue; return this; }
        public RebalanceResponseBuilder currency(String currency) { this.currency = currency; return this; }
        public RebalanceResponseBuilder allocations(List<AllocationComparison> allocations) { this.allocations = allocations; return this; }
        public RebalanceResponseBuilder trades(List<TradeRecommendation> trades) { this.trades = trades; return this; }
        public RebalanceResponseBuilder unallocatedCash(BigDecimal unallocatedCash) { this.unallocatedCash = unallocatedCash; return this; }
        public RebalanceResponseBuilder calculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; return this; }

        public RebalanceResponse build() {
            return new RebalanceResponse(portfolioId, totalPortfolioValue, currency, allocations, trades, unallocatedCash, calculatedAt);
        }
    }

    public static class AllocationComparison {
        private AssetClass assetClass;
        private BigDecimal currentPercentage;
        private BigDecimal targetPercentage;
        private BigDecimal driftPercentage;
        private BigDecimal currentValue;
        private BigDecimal targetValue;

        public AllocationComparison() {
        }

        public AllocationComparison(AssetClass assetClass, BigDecimal currentPercentage,
                                    BigDecimal targetPercentage, BigDecimal driftPercentage,
                                    BigDecimal currentValue, BigDecimal targetValue) {
            this.assetClass = assetClass;
            this.currentPercentage = currentPercentage;
            this.targetPercentage = targetPercentage;
            this.driftPercentage = driftPercentage;
            this.currentValue = currentValue;
            this.targetValue = targetValue;
        }

        public AssetClass getAssetClass() { return assetClass; }
        public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

        public BigDecimal getCurrentPercentage() { return currentPercentage; }
        public void setCurrentPercentage(BigDecimal currentPercentage) { this.currentPercentage = currentPercentage; }

        public BigDecimal getTargetPercentage() { return targetPercentage; }
        public void setTargetPercentage(BigDecimal targetPercentage) { this.targetPercentage = targetPercentage; }

        public BigDecimal getDriftPercentage() { return driftPercentage; }
        public void setDriftPercentage(BigDecimal driftPercentage) { this.driftPercentage = driftPercentage; }

        public BigDecimal getCurrentValue() { return currentValue; }
        public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

        public BigDecimal getTargetValue() { return targetValue; }
        public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }

        public static AllocationComparisonBuilder builder() { return new AllocationComparisonBuilder(); }

        public static class AllocationComparisonBuilder {
            private AssetClass assetClass;
            private BigDecimal currentPercentage;
            private BigDecimal targetPercentage;
            private BigDecimal driftPercentage;
            private BigDecimal currentValue;
            private BigDecimal targetValue;

            public AllocationComparisonBuilder assetClass(AssetClass assetClass) { this.assetClass = assetClass; return this; }
            public AllocationComparisonBuilder currentPercentage(BigDecimal currentPercentage) { this.currentPercentage = currentPercentage; return this; }
            public AllocationComparisonBuilder targetPercentage(BigDecimal targetPercentage) { this.targetPercentage = targetPercentage; return this; }
            public AllocationComparisonBuilder driftPercentage(BigDecimal driftPercentage) { this.driftPercentage = driftPercentage; return this; }
            public AllocationComparisonBuilder currentValue(BigDecimal currentValue) { this.currentValue = currentValue; return this; }
            public AllocationComparisonBuilder targetValue(BigDecimal targetValue) { this.targetValue = targetValue; return this; }

            public AllocationComparison build() {
                return new AllocationComparison(assetClass, currentPercentage, targetPercentage,
                        driftPercentage, currentValue, targetValue);
            }
        }
    }

    public static class LotSaleDetail {
        private Long tradeId;
        private LocalDate purchaseDate;
        private BigDecimal quantity;
        private BigDecimal costBasis;
        private BigDecimal estimatedGain;

        public LotSaleDetail() {
        }

        public LotSaleDetail(Long tradeId, LocalDate purchaseDate, BigDecimal quantity,
                              BigDecimal costBasis, BigDecimal estimatedGain) {
            this.tradeId = tradeId;
            this.purchaseDate = purchaseDate;
            this.quantity = quantity;
            this.costBasis = costBasis;
            this.estimatedGain = estimatedGain;
        }

        public Long getTradeId() { return tradeId; }
        public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

        public LocalDate getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

        public BigDecimal getCostBasis() { return costBasis; }
        public void setCostBasis(BigDecimal costBasis) { this.costBasis = costBasis; }

        public BigDecimal getEstimatedGain() { return estimatedGain; }
        public void setEstimatedGain(BigDecimal estimatedGain) { this.estimatedGain = estimatedGain; }
    }

    public static class TradeRecommendation {
        private Long holdingId;
        private String tickerSymbol;
        private String name;
        private AssetClass assetClass;
        private String action; // BUY or SELL
        private int shares;
        private BigDecimal currentPrice;
        private BigDecimal estimatedCost;
        private BigDecimal currentWeight;
        private BigDecimal targetWeight;
        private List<LotSaleDetail> lotDetails;

        public TradeRecommendation() {
        }

        public TradeRecommendation(Long holdingId, String tickerSymbol, String name, AssetClass assetClass,
                                   String action, int shares, BigDecimal currentPrice,
                                   BigDecimal estimatedCost, BigDecimal currentWeight, BigDecimal targetWeight,
                                   List<LotSaleDetail> lotDetails) {
            this.holdingId = holdingId;
            this.tickerSymbol = tickerSymbol;
            this.name = name;
            this.assetClass = assetClass;
            this.action = action;
            this.shares = shares;
            this.currentPrice = currentPrice;
            this.estimatedCost = estimatedCost;
            this.currentWeight = currentWeight;
            this.targetWeight = targetWeight;
            this.lotDetails = lotDetails;
        }

        public Long getHoldingId() { return holdingId; }
        public void setHoldingId(Long holdingId) { this.holdingId = holdingId; }

        public String getTickerSymbol() { return tickerSymbol; }
        public void setTickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public AssetClass getAssetClass() { return assetClass; }
        public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public int getShares() { return shares; }
        public void setShares(int shares) { this.shares = shares; }

        public BigDecimal getCurrentPrice() { return currentPrice; }
        public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

        public BigDecimal getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

        public BigDecimal getCurrentWeight() { return currentWeight; }
        public void setCurrentWeight(BigDecimal currentWeight) { this.currentWeight = currentWeight; }

        public BigDecimal getTargetWeight() { return targetWeight; }
        public void setTargetWeight(BigDecimal targetWeight) { this.targetWeight = targetWeight; }

        public List<LotSaleDetail> getLotDetails() { return lotDetails; }
        public void setLotDetails(List<LotSaleDetail> lotDetails) { this.lotDetails = lotDetails; }

        public static TradeRecommendationBuilder builder() { return new TradeRecommendationBuilder(); }

        public static class TradeRecommendationBuilder {
            private Long holdingId;
            private String tickerSymbol;
            private String name;
            private AssetClass assetClass;
            private String action;
            private int shares;
            private BigDecimal currentPrice;
            private BigDecimal estimatedCost;
            private BigDecimal currentWeight;
            private BigDecimal targetWeight;
            private List<LotSaleDetail> lotDetails;

            public TradeRecommendationBuilder holdingId(Long holdingId) { this.holdingId = holdingId; return this; }
            public TradeRecommendationBuilder tickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; return this; }
            public TradeRecommendationBuilder name(String name) { this.name = name; return this; }
            public TradeRecommendationBuilder assetClass(AssetClass assetClass) { this.assetClass = assetClass; return this; }
            public TradeRecommendationBuilder action(String action) { this.action = action; return this; }
            public TradeRecommendationBuilder shares(int shares) { this.shares = shares; return this; }
            public TradeRecommendationBuilder currentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; return this; }
            public TradeRecommendationBuilder estimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; return this; }
            public TradeRecommendationBuilder currentWeight(BigDecimal currentWeight) { this.currentWeight = currentWeight; return this; }
            public TradeRecommendationBuilder targetWeight(BigDecimal targetWeight) { this.targetWeight = targetWeight; return this; }
            public TradeRecommendationBuilder lotDetails(List<LotSaleDetail> lotDetails) { this.lotDetails = lotDetails; return this; }

            public TradeRecommendation build() {
                return new TradeRecommendation(holdingId, tickerSymbol, name, assetClass, action, shares,
                        currentPrice, estimatedCost, currentWeight, targetWeight, lotDetails);
            }
        }
    }
}
