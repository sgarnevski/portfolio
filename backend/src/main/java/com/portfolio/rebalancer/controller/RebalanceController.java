package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.request.CashRebalanceRequest;
import com.portfolio.rebalancer.dto.response.RebalanceResponse;
import com.portfolio.rebalancer.service.RebalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/rebalance")
@Tag(name = "Rebalance", description = "Calculate portfolio rebalancing recommendations")
public class RebalanceController {

    private final RebalanceService rebalanceService;

    public RebalanceController(RebalanceService rebalanceService) {
        this.rebalanceService = rebalanceService;
    }

    @GetMapping
    @Operation(summary = "Calculate rebalance recommendations")
    public ResponseEntity<RebalanceResponse> calculateRebalance(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(rebalanceService.calculateRebalance(portfolioId));
    }

    @PostMapping("/cash")
    @Operation(summary = "Calculate rebalance with additional cash to invest")
    public ResponseEntity<RebalanceResponse> calculateCashRebalance(@PathVariable Long portfolioId,
                                                                    @Valid @RequestBody CashRebalanceRequest request) {
        return ResponseEntity.ok(rebalanceService.calculateCashRebalance(portfolioId, request.getAmount()));
    }
}
