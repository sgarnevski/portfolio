package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.response.HistoricalDataPoint;
import com.portfolio.rebalancer.dto.response.QuoteResponse;
import com.portfolio.rebalancer.dto.response.TickerSearchResult;
import com.portfolio.rebalancer.service.MarketDataClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@Tag(name = "Quotes", description = "Fetch market price quotes")
@SecurityRequirements
public class QuoteController {

    private final MarketDataClient marketDataClient;

    public QuoteController(MarketDataClient marketDataClient) {
        this.marketDataClient = marketDataClient;
    }

    @GetMapping("/search")
    @Operation(summary = "Search for ticker symbols")
    public ResponseEntity<List<TickerSearchResult>> searchTickers(@RequestParam String q) {
        return ResponseEntity.ok(marketDataClient.searchTickers(q));
    }

    @GetMapping("/{symbol}")
    @Operation(summary = "Get quote for a single symbol")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable String symbol) {
        return ResponseEntity.ok(marketDataClient.fetchQuote(symbol));
    }

    @GetMapping("/{symbol}/history")
    @Operation(summary = "Get historical price data for a symbol")
    public ResponseEntity<List<HistoricalDataPoint>> getHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1m") String range) {
        return ResponseEntity.ok(marketDataClient.fetchHistory(symbol, range));
    }

    @GetMapping
    @Operation(summary = "Get quotes for multiple symbols (comma-separated)")
    public ResponseEntity<List<QuoteResponse>> getQuotes(@RequestParam String symbols) {
        List<String> symbolList = Arrays.asList(symbols.split(","));
        return ResponseEntity.ok(marketDataClient.fetchQuotes(symbolList));
    }
}
