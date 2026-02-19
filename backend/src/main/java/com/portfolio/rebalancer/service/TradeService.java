package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.request.CreateTradeRequest;
import com.portfolio.rebalancer.dto.response.TradeResponse;
import com.portfolio.rebalancer.entity.Holding;
import com.portfolio.rebalancer.entity.Trade;
import com.portfolio.rebalancer.exception.ResourceNotFoundException;
import com.portfolio.rebalancer.repository.HoldingRepository;
import com.portfolio.rebalancer.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioService portfolioService;

    public TradeService(TradeRepository tradeRepository, HoldingRepository holdingRepository,
                        PortfolioService portfolioService) {
        this.tradeRepository = tradeRepository;
        this.holdingRepository = holdingRepository;
        this.portfolioService = portfolioService;
    }

    public List<TradeResponse> getTrades(Long portfolioId, Long holdingId) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        return tradeRepository.findByHoldingIdOrderByDateDesc(holdingId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TradeResponse addTrade(Long portfolioId, Long holdingId, CreateTradeRequest request) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found"));

        Trade trade = Trade.builder()
                .holding(holding)
                .date(request.getDate())
                .type(request.getType())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .fee(request.getFee())
                .build();
        trade = tradeRepository.save(trade);
        return toResponse(trade);
    }

    @Transactional
    public TradeResponse updateTrade(Long portfolioId, Long holdingId, Long tradeId,
                                     CreateTradeRequest request) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade not found"));

        trade.setDate(request.getDate());
        trade.setType(request.getType());
        trade.setQuantity(request.getQuantity());
        trade.setPrice(request.getPrice());
        trade.setFee(request.getFee());
        trade = tradeRepository.save(trade);
        return toResponse(trade);
    }

    @Transactional
    public void deleteTrade(Long portfolioId, Long holdingId, Long tradeId) {
        portfolioService.findPortfolioForCurrentUser(portfolioId);
        tradeRepository.deleteById(tradeId);
    }

    private TradeResponse toResponse(Trade trade) {
        return TradeResponse.builder()
                .id(trade.getId())
                .date(trade.getDate())
                .type(trade.getType())
                .quantity(trade.getQuantity())
                .price(trade.getPrice())
                .fee(trade.getFee())
                .createdAt(trade.getCreatedAt())
                .build();
    }
}
