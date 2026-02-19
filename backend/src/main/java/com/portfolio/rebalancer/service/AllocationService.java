package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.request.SetAllocationRequest;
import com.portfolio.rebalancer.dto.response.AllocationResponse;
import com.portfolio.rebalancer.entity.Portfolio;
import com.portfolio.rebalancer.entity.TargetAllocation;
import com.portfolio.rebalancer.exception.InvalidAllocationException;
import com.portfolio.rebalancer.repository.TargetAllocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AllocationService {

    private final TargetAllocationRepository allocationRepository;
    private final PortfolioService portfolioService;

    public AllocationService(TargetAllocationRepository allocationRepository, PortfolioService portfolioService) {
        this.allocationRepository = allocationRepository;
        this.portfolioService = portfolioService;
    }

    public List<AllocationResponse> getAllocations(Long portfolioId) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        return allocationRepository.findByPortfolioId(portfolioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<AllocationResponse> setAllocations(Long portfolioId, SetAllocationRequest request) {
        Portfolio portfolio = portfolioService.findPortfolioForCurrentUser(portfolioId);

        BigDecimal total = request.getAllocations().stream()
                .map(SetAllocationRequest.AllocationEntry::getTargetPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(new BigDecimal("100")) != 0) {
            throw new InvalidAllocationException(
                    "Target allocations must sum to 100%. Current sum: " + total);
        }

        allocationRepository.deleteByPortfolioId(portfolioId);
        allocationRepository.flush();

        List<TargetAllocation> allocations = request.getAllocations().stream()
                .map(entry -> TargetAllocation.builder()
                        .portfolio(portfolio)
                        .assetClass(entry.getAssetClass())
                        .targetPercentage(entry.getTargetPercentage())
                        .build())
                .toList();

        return allocationRepository.saveAll(allocations).stream()
                .map(this::toResponse)
                .toList();
    }

    private AllocationResponse toResponse(TargetAllocation allocation) {
        return AllocationResponse.builder()
                .id(allocation.getId())
                .assetClass(allocation.getAssetClass())
                .targetPercentage(allocation.getTargetPercentage())
                .build();
    }
}
