package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.request.CreatePortfolioRequest;
import com.portfolio.rebalancer.dto.response.AllocationResponse;
import com.portfolio.rebalancer.dto.response.HoldingResponse;
import com.portfolio.rebalancer.dto.response.PortfolioResponse;
import com.portfolio.rebalancer.entity.Portfolio;
import com.portfolio.rebalancer.entity.User;
import com.portfolio.rebalancer.exception.ResourceNotFoundException;
import com.portfolio.rebalancer.repository.PortfolioRepository;
import com.portfolio.rebalancer.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
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
        User user = userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Portfolio portfolio = Portfolio.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
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
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private PortfolioResponse toResponse(Portfolio portfolio) {
        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .holdings(portfolio.getHoldings().stream()
                        .map(h -> HoldingResponse.builder()
                                .id(h.getId())
                                .tickerSymbol(h.getTickerSymbol())
                                .name(h.getName())
                                .assetClass(h.getAssetClass())
                                .quantity(h.getQuantity())
                                .averageCostBasis(h.getAverageCostBasis())
                                .currency(h.getCurrency())
                                .build())
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
}
