package com.portfolio.rebalancer.dto.response;

import com.portfolio.rebalancer.entity.AssetClass;
import java.math.BigDecimal;
import java.util.List;

public class HoldingResponse {
    private Long id;
    private String tickerSymbol;
    private String name;
    private AssetClass assetClass;
    private BigDecimal quantity;
    private BigDecimal averageCostBasis;
    private BigDecimal totalCost;
    private BigDecimal realizedPnL;
    private String currency;
    private List<TradeResponse> trades;
    private List<LotResponse> lots;

    public HoldingResponse() {
    }

    public HoldingResponse(Long id, String tickerSymbol, String name, AssetClass assetClass,
                           BigDecimal quantity, BigDecimal averageCostBasis, BigDecimal totalCost,
                           BigDecimal realizedPnL, String currency, List<TradeResponse> trades,
                           List<LotResponse> lots) {
        this.id = id;
        this.tickerSymbol = tickerSymbol;
        this.name = name;
        this.assetClass = assetClass;
        this.quantity = quantity;
        this.averageCostBasis = averageCostBasis;
        this.totalCost = totalCost;
        this.realizedPnL = realizedPnL;
        this.currency = currency;
        this.trades = trades;
        this.lots = lots;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTickerSymbol() { return tickerSymbol; }
    public void setTickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AssetClass getAssetClass() { return assetClass; }
    public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAverageCostBasis() { return averageCostBasis; }
    public void setAverageCostBasis(BigDecimal averageCostBasis) { this.averageCostBasis = averageCostBasis; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getRealizedPnL() { return realizedPnL; }
    public void setRealizedPnL(BigDecimal realizedPnL) { this.realizedPnL = realizedPnL; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<TradeResponse> getTrades() { return trades; }
    public void setTrades(List<TradeResponse> trades) { this.trades = trades; }

    public List<LotResponse> getLots() { return lots; }
    public void setLots(List<LotResponse> lots) { this.lots = lots; }

    public static HoldingResponseBuilder builder() { return new HoldingResponseBuilder(); }

    public static class HoldingResponseBuilder {
        private Long id;
        private String tickerSymbol;
        private String name;
        private AssetClass assetClass;
        private BigDecimal quantity;
        private BigDecimal averageCostBasis;
        private BigDecimal totalCost;
        private BigDecimal realizedPnL;
        private String currency;
        private List<TradeResponse> trades;
        private List<LotResponse> lots;

        public HoldingResponseBuilder id(Long id) { this.id = id; return this; }
        public HoldingResponseBuilder tickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; return this; }
        public HoldingResponseBuilder name(String name) { this.name = name; return this; }
        public HoldingResponseBuilder assetClass(AssetClass assetClass) { this.assetClass = assetClass; return this; }
        public HoldingResponseBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public HoldingResponseBuilder averageCostBasis(BigDecimal averageCostBasis) { this.averageCostBasis = averageCostBasis; return this; }
        public HoldingResponseBuilder totalCost(BigDecimal totalCost) { this.totalCost = totalCost; return this; }
        public HoldingResponseBuilder realizedPnL(BigDecimal realizedPnL) { this.realizedPnL = realizedPnL; return this; }
        public HoldingResponseBuilder currency(String currency) { this.currency = currency; return this; }
        public HoldingResponseBuilder trades(List<TradeResponse> trades) { this.trades = trades; return this; }
        public HoldingResponseBuilder lots(List<LotResponse> lots) { this.lots = lots; return this; }

        public HoldingResponse build() {
            return new HoldingResponse(id, tickerSymbol, name, assetClass, quantity, averageCostBasis, totalCost, realizedPnL, currency, trades, lots);
        }
    }
}
