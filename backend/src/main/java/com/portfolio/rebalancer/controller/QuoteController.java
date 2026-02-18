package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.response.QuoteResponse;
import com.portfolio.rebalancer.service.YahooFinanceService;
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

    private final YahooFinanceService yahooFinanceService;

    public QuoteController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/{symbol}")
    @Operation(summary = "Get quote for a single symbol")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable String symbol) {
        return ResponseEntity.ok(yahooFinanceService.fetchQuote(symbol));
    }

    @GetMapping
    @Operation(summary = "Get quotes for multiple symbols (comma-separated)")
    public ResponseEntity<List<QuoteResponse>> getQuotes(@RequestParam String symbols) {
        List<String> symbolList = Arrays.asList(symbols.split(","));
        return ResponseEntity.ok(yahooFinanceService.fetchQuotes(symbolList));
    }
}
