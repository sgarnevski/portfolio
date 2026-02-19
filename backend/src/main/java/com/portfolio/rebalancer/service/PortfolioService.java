package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.request.CreatePortfolioRequest;
import com.portfolio.rebalancer.dto.response.AllocationResponse;
import com.portfolio.rebalancer.dto.response.HoldingResponse;
import com.portfolio.rebalancer.dto.response.PortfolioResponse;
import com.portfolio.rebalancer.dto.response.TradeResponse;
import com.portfolio.rebalancer.entity.Holding;
import com.portfolio.rebalancer.entity.Portfolio;
import com.portfolio.rebalancer.entity.Trade;
import com.portfolio.rebalancer.entity.TradeType;
import com.portfolio.rebalancer.exception.ResourceNotFoundException;
import com.portfolio.rebalancer.repository.PortfolioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public List<PortfolioResponse> getAllPortfolios() {
        Long userId = getCurrentUserId();
        return portfolioRepository.findByOwnerId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public PortfolioResponse getPortfolio(Long id) {
        Portfolio portfolio = findPortfolioForCurrentUser(id);
        return toResponse(portfolio);
    }

    @Transactional
    public PortfolioResponse createPortfolio(CreatePortfolioRequest request) {
        Portfolio portfolio = Portfolio.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ownerId(getCurrentUserId())
                .build();
        portfolio = portfolioRepository.save(portfolio);
        return toResponse(portfolio);
    }

    @Transactional
    public PortfolioResponse updatePortfolio(Long id, CreatePortfolioRequest request) {
        Portfolio portfolio = findPortfolioForCurrentUser(id);
        portfolio.setName(request.getName());
        portfolio.setDescription(request.getDescription());
        portfolio = portfolioRepository.save(portfolio);
        return toResponse(portfolio);
    }

    @Transactional
    public void deletePortfolio(Long id) {
        Portfolio portfolio = findPortfolioForCurrentUser(id);
        portfolioRepository.delete(portfolio);
    }

    public Portfolio findPortfolioForCurrentUser(Long portfolioId) {
        Long userId = getCurrentUserId();
        return portfolioRepository.findByIdAndOwnerId(portfolioId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }

    private PortfolioResponse toResponse(Portfolio portfolio) {
        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .holdings(portfolio.getHoldings().stream()
                        .map(this::toHoldingResponse)
                        .toList())
                .targetAllocations(portfolio.getTargetAllocations().stream()
                        .map(a -> AllocationResponse.builder()
                                .id(a.getId())
                                .assetClass(a.getAssetClass())
                                .targetPercentage(a.getTargetPercentage())
                                .build())
                        .toList())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }

    private HoldingResponse toHoldingResponse(Holding h) {
        List<Trade> trades = h.getTrades() != null ? h.getTrades() : Collections.emptyList();

        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Trade t : trades) {
            BigDecimal tradeCost = t.getQuantity().multiply(t.getPrice());
            BigDecimal fee = t.getFee() != null ? t.getFee() : BigDecimal.ZERO;
            if (t.getType() == TradeType.BUY) {
                quantity = quantity.add(t.getQuantity());
                totalCost = totalCost.add(tradeCost).add(fee);
            } else {
                quantity = quantity.subtract(t.getQuantity());
            }
        }

        BigDecimal averageCostBasis = BigDecimal.ZERO;
        if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            averageCostBasis = totalCost.divide(quantity, 4, RoundingMode.HALF_UP);
        }

        List<TradeResponse> tradeResponses = trades.stream()
                .map(t -> TradeResponse.builder()
                        .id(t.getId())
                        .date(t.getDate())
                        .type(t.getType())
                        .quantity(t.getQuantity())
                        .price(t.getPrice())
                        .fee(t.getFee())
                        .createdAt(t.getCreatedAt())
                        .build())
                .toList();

        return HoldingResponse.builder()
                .id(h.getId())
                .tickerSymbol(h.getTickerSymbol())
                .name(h.getName())
                .assetClass(h.getAssetClass())
                .quantity(quantity)
                .averageCostBasis(averageCostBasis)
                .totalCost(totalCost)
                .currency(h.getCurrency())
                .trades(tradeResponses)
                .build();
    }
}
