package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.request.SetAllocationRequest;
import com.portfolio.rebalancer.dto.response.AllocationResponse;
import com.portfolio.rebalancer.service.AllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/allocations")
@Tag(name = "Allocations", description = "Manage target asset allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping
    @Operation(summary = "Get target allocations for a portfolio")
    public ResponseEntity<List<AllocationResponse>> getAllocations(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(allocationService.getAllocations(portfolioId));
    }

    @PutMapping
    @Operation(summary = "Set target allocations for a portfolio")
    public ResponseEntity<List<AllocationResponse>> setAllocations(@PathVariable Long portfolioId,
                                                                   @Valid @RequestBody SetAllocationRequest request) {
        return ResponseEntity.ok(allocationService.setAllocations(portfolioId, request));
    }
}
