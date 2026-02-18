package com.portfolio.rebalancer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.portfolio.rebalancer.dto.response.QuoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class YahooFinanceService {

    private static final Logger log = LoggerFactory.getLogger(YahooFinanceService.class);

    private final RestClient restClient;

    public YahooFinanceService(RestClient restClient) {
        this.restClient = restClient;
    }

    private static final String CHART_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private static final String QUOTE_URL = "https://query2.finance.yahoo.com/v7/finance/quote";

    public QuoteResponse fetchQuote(String symbol) {
        try {
            JsonNode root = restClient.get()
                    .uri(CHART_URL + symbol + "?range=1d&interval=1d")
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode meta = root.path("chart").path("result").get(0).path("meta");
            BigDecimal currentPrice = new BigDecimal(meta.path("regularMarketPrice").asText("0"));
            BigDecimal previousClose = new BigDecimal(meta.path("chartPreviousClose").asText(
                    meta.path("previousClose").asText("0")));
            BigDecimal change = currentPrice.subtract(previousClose);
            BigDecimal changePct = previousClose.compareTo(BigDecimal.ZERO) > 0
                    ? change.multiply(new BigDecimal("100")).divide(previousClose, 4, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            return QuoteResponse.builder()
                    .symbol(meta.path("symbol").asText())
                    .shortName(meta.path("shortName").asText(symbol))
                    .regularMarketPrice(currentPrice)
                    .regularMarketChange(change)
                    .regularMarketChangePercent(changePct)
                    .currency(meta.path("currency").asText("USD"))
                    .exchangeName(meta.path("exchangeName").asText(""))
                    .build();
        } catch (Exception e) {
            log.error("Failed to fetch quote for {}: {}", symbol, e.getMessage());
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
        List<QuoteResponse> quotes = new ArrayList<>();
        for (String symbol : symbols) {
            quotes.add(fetchQuote(symbol));
        }
        return quotes;
    }

    private BigDecimal toBigDecimal(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return BigDecimal.ZERO;
        return new BigDecimal(value.asText("0"));
    }
}
