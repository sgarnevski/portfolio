package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.response.QuoteResponse;
import com.portfolio.rebalancer.repository.HoldingRepository;
import com.portfolio.rebalancer.websocket.StockPriceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceUpdateScheduler {

    private static final Logger log = LoggerFactory.getLogger(PriceUpdateScheduler.class);

    private final MarketDataClient marketDataClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final HoldingRepository holdingRepository;

    public PriceUpdateScheduler(MarketDataClient marketDataClient,
                                SimpMessagingTemplate messagingTemplate,
                                HoldingRepository holdingRepository) {
        this.marketDataClient = marketDataClient;
        this.messagingTemplate = messagingTemplate;
        this.holdingRepository = holdingRepository;
    }

    @Scheduled(fixedRate = 15000)
    public void pushPriceUpdates() {
        List<String> allTickers = holdingRepository.findDistinctTickerSymbols();
        if (allTickers.isEmpty()) return;

        try {
            List<QuoteResponse> quotes = marketDataClient.fetchQuotes(allTickers);
            StockPriceMessage message = new StockPriceMessage(quotes, LocalDateTime.now());
            messagingTemplate.convertAndSend("/topic/prices", message);
            log.debug("Pushed price updates for {} tickers", allTickers.size());
        } catch (Exception e) {
            log.error("Failed to push price updates: {}", e.getMessage());
        }
    }
}
