package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.request.AddHoldingRequest;
import com.portfolio.rebalancer.dto.response.HoldingResponse;
import com.portfolio.rebalancer.service.HoldingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/holdings")
@Tag(name = "Holdings", description = "Manage holdings within a portfolio")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @GetMapping
    @Operation(summary = "Get all holdings for a portfolio")
    public ResponseEntity<List<HoldingResponse>> getHoldings(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(holdingService.getHoldings(portfolioId));
    }

    @PostMapping
    @Operation(summary = "Add a holding to a portfolio")
    public ResponseEntity<HoldingResponse> addHolding(@PathVariable Long portfolioId,
                                                      @Valid @RequestBody AddHoldingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(holdingService.addHolding(portfolioId, request));
    }

    @PutMapping("/{holdingId}")
    @Operation(summary = "Update a holding")
    public ResponseEntity<HoldingResponse> updateHolding(@PathVariable Long portfolioId,
                                                         @PathVariable Long holdingId,
                                                         @Valid @RequestBody AddHoldingRequest request) {
        return ResponseEntity.ok(holdingService.updateHolding(portfolioId, holdingId, request));
    }

    @DeleteMapping("/{holdingId}")
    @Operation(summary = "Delete a holding")
    public ResponseEntity<Void> deleteHolding(@PathVariable Long portfolioId, @PathVariable Long holdingId) {
        holdingService.deleteHolding(portfolioId, holdingId);
        return ResponseEntity.noContent().build();
    }
}
