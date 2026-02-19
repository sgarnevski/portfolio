package com.portfolio.gateway.controller;

import com.portfolio.gateway.dto.HistoricalDataPoint;
import com.portfolio.gateway.dto.QuoteResponse;
import com.portfolio.gateway.dto.TickerSearchResult;
import com.portfolio.gateway.service.YahooFinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/market-data")
@Tag(name = "Market Data", description = "Vendor-neutral market data: quotes, search, and historical prices")
public class MarketDataController {

    private final YahooFinanceService yahooFinanceService;

    public MarketDataController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/quotes/{symbol}")
    @Operation(summary = "Get quote for a single symbol")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable String symbol) {
        return ResponseEntity.ok(yahooFinanceService.fetchQuote(symbol));
    }

    @GetMapping("/quotes")
    @Operation(summary = "Get quotes for multiple symbols (comma-separated)")
    public ResponseEntity<List<QuoteResponse>> getQuotes(@RequestParam String symbols) {
        List<String> symbolList = Arrays.asList(symbols.split(","));
        return ResponseEntity.ok(yahooFinanceService.fetchQuotes(symbolList));
    }

    @GetMapping("/quotes/{symbol}/history")
    @Operation(summary = "Get historical price data for a symbol")
    public ResponseEntity<List<HistoricalDataPoint>> getHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1m") String range) {
        return ResponseEntity.ok(yahooFinanceService.fetchHistory(symbol, range));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for ticker symbols")
    public ResponseEntity<List<TickerSearchResult>> searchTickers(@RequestParam String q) {
        return ResponseEntity.ok(yahooFinanceService.searchTickers(q));
    }
}
