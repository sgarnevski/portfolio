package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.response.HistoricalDataPoint;
import com.portfolio.rebalancer.dto.response.QuoteResponse;
import com.portfolio.rebalancer.dto.response.TickerSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class MarketDataClient {

    private static final Logger log = LoggerFactory.getLogger(MarketDataClient.class);

    private final RestClient gatewayRestClient;
    private final ServiceTokenManager tokenManager;

    public MarketDataClient(RestClient gatewayRestClient, ServiceTokenManager tokenManager) {
        this.gatewayRestClient = gatewayRestClient;
        this.tokenManager = tokenManager;
    }

    public QuoteResponse fetchQuote(String symbol) {
        try {
            return gatewayRestClient.get()
                    .uri("/api/market-data/quotes/{symbol}", symbol)
                    .header("Authorization", "Bearer " + tokenManager.getToken())
                    .retrieve()
                    .body(QuoteResponse.class);
        } catch (Exception e) {
            log.error("Failed to fetch quote for {} from gateway: {}", symbol, e.getMessage());
            return QuoteResponse.builder()
                    .symbol(symbol)
                    .shortName(symbol)
                    .regularMarketPrice(BigDecimal.ZERO)
                    .regularMarketChange(BigDecimal.ZERO)
                    .regularMarketChangePercent(BigDecimal.ZERO)
                    .currency("USD")
                    .build();
        }
    }

    public List<QuoteResponse> fetchQuotes(List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            String joined = String.join(",", symbols);
            return gatewayRestClient.get()
                    .uri("/api/market-data/quotes?symbols={symbols}", joined)
                    .header("Authorization", "Bearer " + tokenManager.getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QuoteResponse>>() {});
        } catch (Exception e) {
            log.error("Failed to fetch batch quotes from gateway: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<TickerSearchResult> searchTickers(String query) {
        try {
            return gatewayRestClient.get()
                    .uri("/api/market-data/search?q={query}", query)
                    .header("Authorization", "Bearer " + tokenManager.getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TickerSearchResult>>() {});
        } catch (Exception e) {
            log.error("Failed to search tickers from gateway: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<HistoricalDataPoint> fetchHistory(String symbol, String range) {
        try {
            return gatewayRestClient.get()
                    .uri("/api/market-data/quotes/{symbol}/history?range={range}", symbol, range)
                    .header("Authorization", "Bearer " + tokenManager.getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<HistoricalDataPoint>>() {});
        } catch (Exception e) {
            log.error("Failed to fetch history from gateway for {} (range={}): {}", symbol, range, e.getMessage());
            return Collections.emptyList();
        }
    }
}
