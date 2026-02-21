package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.request.CreatePortfolioRequest;
import com.portfolio.rebalancer.dto.request.UpdateCashBalanceRequest;
import com.portfolio.rebalancer.dto.response.PortfolioResponse;
import com.portfolio.rebalancer.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@Tag(name = "Portfolios", description = "Manage investment portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    @Operation(summary = "Get all portfolios for the current user")
    public ResponseEntity<List<PortfolioResponse>> getAllPortfolios() {
        return ResponseEntity.ok(portfolioService.getAllPortfolios());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a portfolio by ID")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getPortfolio(id));
    }

    @PostMapping
    @Operation(summary = "Create a new portfolio")
    public ResponseEntity<PortfolioResponse> createPortfolio(@Valid @RequestBody CreatePortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.createPortfolio(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a portfolio")
    public ResponseEntity<PortfolioResponse> updatePortfolio(@PathVariable Long id,
                                                             @Valid @RequestBody CreatePortfolioRequest request) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, request));
    }

    @PatchMapping("/{id}/cash-balance")
    @Operation(summary = "Update portfolio cash balance")
    public ResponseEntity<PortfolioResponse> updateCashBalance(@PathVariable Long id,
                                                                @Valid @RequestBody UpdateCashBalanceRequest request) {
        return ResponseEntity.ok(portfolioService.updateCashBalance(id, request.getCashBalance()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a portfolio")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }
}
