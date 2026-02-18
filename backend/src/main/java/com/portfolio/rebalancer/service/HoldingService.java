package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.request.AddHoldingRequest;
import com.portfolio.rebalancer.dto.response.HoldingResponse;
import com.portfolio.rebalancer.entity.Holding;
import com.portfolio.rebalancer.entity.Portfolio;
import com.portfolio.rebalancer.exception.ResourceNotFoundException;
import com.portfolio.rebalancer.repository.HoldingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final PortfolioService portfolioService;

    public HoldingService(HoldingRepository holdingRepository, PortfolioService portfolioService) {
        this.holdingRepository = holdingRepository;
        this.portfolioService = portfolioService;
    }

    public List<HoldingResponse> getHoldings(Long portfolioId) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        return holdingRepository.findByPortfolioId(portfolioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public HoldingResponse addHolding(Long portfolioId, AddHoldingRequest request) {
        Portfolio portfolio = portfolioService.findPortfolioForCurrentUser(portfolioId);

        Holding holding = Holding.builder()
                .portfolio(portfolio)
                .tickerSymbol(request.getTickerSymbol().toUpperCase())
                .name(request.getName())
                .assetClass(request.getAssetClass())
                .quantity(request.getQuantity())
                .averageCostBasis(request.getAverageCostBasis())
                .initialValue(request.getInitialValue())
                .currency(request.getCurrency())
                .build();
        holding = holdingRepository.save(holding);
        return toResponse(holding);
    }

    @Transactional
    public HoldingResponse updateHolding(Long portfolioId, Long holdingId, AddHoldingRequest request) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found"));

        holding.setTickerSymbol(request.getTickerSymbol().toUpperCase());
        holding.setName(request.getName());
        holding.setAssetClass(request.getAssetClass());
        holding.setQuantity(request.getQuantity());
        holding.setAverageCostBasis(request.getAverageCostBasis());
        holding.setInitialValue(request.getInitialValue());
        holding.setCurrency(request.getCurrency());
        holding = holdingRepository.save(holding);
        return toResponse(holding);
    }

    @Transactional
    public void deleteHolding(Long portfolioId, Long holdingId) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        holdingRepository.deleteById(holdingId);
    }

    private HoldingResponse toResponse(Holding holding) {
        return HoldingResponse.builder()
                .id(holding.getId())
                .tickerSymbol(holding.getTickerSymbol())
                .name(holding.getName())
                .assetClass(holding.getAssetClass())
                .quantity(holding.getQuantity())
                .averageCostBasis(holding.getAverageCostBasis())
                .initialValue(holding.getInitialValue())
                .currency(holding.getCurrency())
                .build();
    }
}
