package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.request.CreateTradeRequest;
import com.portfolio.rebalancer.dto.response.TradeResponse;
import com.portfolio.rebalancer.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/holdings/{holdingId}/trades")
@Tag(name = "Trades", description = "Manage trades within a holding")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping
    @Operation(summary = "Get all trades for a holding")
    public ResponseEntity<List<TradeResponse>> getTrades(@PathVariable Long portfolioId,
                                                          @PathVariable Long holdingId) {
        return ResponseEntity.ok(tradeService.getTrades(portfolioId, holdingId));
    }

    @PostMapping
    @Operation(summary = "Add a trade to a holding")
    public ResponseEntity<TradeResponse> addTrade(@PathVariable Long portfolioId,
                                                   @PathVariable Long holdingId,
                                                   @Valid @RequestBody CreateTradeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tradeService.addTrade(portfolioId, holdingId, request));
    }

    @PutMapping("/{tradeId}")
    @Operation(summary = "Update a trade")
    public ResponseEntity<TradeResponse> updateTrade(@PathVariable Long portfolioId,
                                                      @PathVariable Long holdingId,
                                                      @PathVariable Long tradeId,
                                                      @Valid @RequestBody CreateTradeRequest request) {
        return ResponseEntity.ok(tradeService.updateTrade(portfolioId, holdingId, tradeId, request));
    }

    @DeleteMapping("/{tradeId}")
    @Operation(summary = "Delete a trade")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long portfolioId,
                                             @PathVariable Long holdingId,
                                             @PathVariable Long tradeId) {
        tradeService.deleteTrade(portfolioId, holdingId, tradeId);
        return ResponseEntity.noContent().build();
    }
}
