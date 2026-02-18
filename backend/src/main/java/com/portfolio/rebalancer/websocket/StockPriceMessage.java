package com.portfolio.rebalancer.websocket;

import com.portfolio.rebalancer.dto.response.QuoteResponse;

import java.time.LocalDateTime;
import java.util.List;

public class StockPriceMessage {
    private List<QuoteResponse> quotes;
    private LocalDateTime timestamp;

    public StockPriceMessage() {
    }

    public StockPriceMessage(List<QuoteResponse> quotes, LocalDateTime timestamp) {
        this.quotes = quotes;
        this.timestamp = timestamp;
    }

    public List<QuoteResponse> getQuotes() { return quotes; }
    public void setQuotes(List<QuoteResponse> quotes) { this.quotes = quotes; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
