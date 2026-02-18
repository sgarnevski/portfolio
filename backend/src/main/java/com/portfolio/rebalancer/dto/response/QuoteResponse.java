package com.portfolio.rebalancer.dto.response;

import java.math.BigDecimal;

public class QuoteResponse {
    private String symbol;
    private String shortName;
    private BigDecimal regularMarketPrice;
    private BigDecimal regularMarketChange;
    private BigDecimal regularMarketChangePercent;
    private String currency;
    private String exchangeName;

    public QuoteResponse() {
    }

    public QuoteResponse(String symbol, String shortName, BigDecimal regularMarketPrice,
                         BigDecimal regularMarketChange, BigDecimal regularMarketChangePercent,
                         String currency, String exchangeName) {
        this.symbol = symbol;
        this.shortName = shortName;
        this.regularMarketPrice = regularMarketPrice;
        this.regularMarketChange = regularMarketChange;
        this.regularMarketChangePercent = regularMarketChangePercent;
        this.currency = currency;
        this.exchangeName = exchangeName;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public BigDecimal getRegularMarketPrice() { return regularMarketPrice; }
    public void setRegularMarketPrice(BigDecimal regularMarketPrice) { this.regularMarketPrice = regularMarketPrice; }

    public BigDecimal getRegularMarketChange() { return regularMarketChange; }
    public void setRegularMarketChange(BigDecimal regularMarketChange) { this.regularMarketChange = regularMarketChange; }

    public BigDecimal getRegularMarketChangePercent() { return regularMarketChangePercent; }
    public void setRegularMarketChangePercent(BigDecimal regularMarketChangePercent) { this.regularMarketChangePercent = regularMarketChangePercent; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }

    public static QuoteResponseBuilder builder() { return new QuoteResponseBuilder(); }

    public static class QuoteResponseBuilder {
        private String symbol;
        private String shortName;
        private BigDecimal regularMarketPrice;
        private BigDecimal regularMarketChange;
        private BigDecimal regularMarketChangePercent;
        private String currency;
        private String exchangeName;

        public QuoteResponseBuilder symbol(String symbol) { this.symbol = symbol; return this; }
        public QuoteResponseBuilder shortName(String shortName) { this.shortName = shortName; return this; }
        public QuoteResponseBuilder regularMarketPrice(BigDecimal regularMarketPrice) { this.regularMarketPrice = regularMarketPrice; return this; }
        public QuoteResponseBuilder regularMarketChange(BigDecimal regularMarketChange) { this.regularMarketChange = regularMarketChange; return this; }
        public QuoteResponseBuilder regularMarketChangePercent(BigDecimal regularMarketChangePercent) { this.regularMarketChangePercent = regularMarketChangePercent; return this; }
        public QuoteResponseBuilder currency(String currency) { this.currency = currency; return this; }
        public QuoteResponseBuilder exchangeName(String exchangeName) { this.exchangeName = exchangeName; return this; }

        public QuoteResponse build() {
            return new QuoteResponse(symbol, shortName, regularMarketPrice, regularMarketChange,
                    regularMarketChangePercent, currency, exchangeName);
        }
    }
}
