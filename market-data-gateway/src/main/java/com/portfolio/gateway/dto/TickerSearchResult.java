package com.portfolio.gateway.dto;

public class TickerSearchResult {
    private String symbol;
    private String shortName;
    private String longName;
    private String exchange;
    private String assetType;

    public TickerSearchResult() {
    }

    public TickerSearchResult(String symbol, String shortName, String longName, String exchange, String assetType) {
        this.symbol = symbol;
        this.shortName = shortName;
        this.longName = longName;
        this.exchange = exchange;
        this.assetType = assetType;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getLongName() { return longName; }
    public void setLongName(String longName) { this.longName = longName; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }

    public static TickerSearchResultBuilder builder() { return new TickerSearchResultBuilder(); }

    public static class TickerSearchResultBuilder {
        private String symbol;
        private String shortName;
        private String longName;
        private String exchange;
        private String assetType;

        public TickerSearchResultBuilder symbol(String symbol) { this.symbol = symbol; return this; }
        public TickerSearchResultBuilder shortName(String shortName) { this.shortName = shortName; return this; }
        public TickerSearchResultBuilder longName(String longName) { this.longName = longName; return this; }
        public TickerSearchResultBuilder exchange(String exchange) { this.exchange = exchange; return this; }
        public TickerSearchResultBuilder assetType(String assetType) { this.assetType = assetType; return this; }

        public TickerSearchResult build() {
            return new TickerSearchResult(symbol, shortName, longName, exchange, assetType);
        }
    }
}
