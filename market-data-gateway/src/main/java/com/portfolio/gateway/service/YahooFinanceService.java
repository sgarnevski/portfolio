package com.portfolio.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.portfolio.gateway.dto.HistoricalDataPoint;
import com.portfolio.gateway.dto.QuoteResponse;
import com.portfolio.gateway.dto.TickerSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class YahooFinanceService {

    private static final Logger log = LoggerFactory.getLogger(YahooFinanceService.class);

    private final RestClient restClient;

    public YahooFinanceService(RestClient restClient) {
        this.restClient = restClient;
    }

    private static final String CHART_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private static final String SEARCH_URL = "https://query1.finance.yahoo.com/v1/finance/search";

    private static final Map<String, String[]> RANGE_INTERVAL_MAP = Map.of(
            "1d", new String[]{"1d", "5m"},
            "1w", new String[]{"5d", "30m"},
            "1m", new String[]{"1mo", "1d"},
            "3m", new String[]{"3mo", "1d"},
            "6m", new String[]{"6mo", "1d"},
            "1y", new String[]{"1y", "1wk"}
    );

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

    public List<TickerSearchResult> searchTickers(String query) {
        try {
            JsonNode root = restClient.get()
                    .uri(SEARCH_URL + "?q={query}&quotesCount=8&newsCount=0", query)
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode quotes = root.path("quotes");
            List<TickerSearchResult> results = new ArrayList<>();
            for (JsonNode q : quotes) {
                results.add(TickerSearchResult.builder()
                        .symbol(q.path("symbol").asText())
                        .shortName(q.path("shortname").asText(""))
                        .longName(q.path("longname").asText(""))
                        .exchange(q.path("exchange").asText(""))
                        .assetType(q.path("quoteType").asText(""))
                        .build());
            }
            return results;
        } catch (Exception e) {
            log.error("Failed to search tickers for '{}': {}", query, e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<HistoricalDataPoint> fetchHistory(String symbol, String range) {
        String[] params = RANGE_INTERVAL_MAP.get(range);
        if (params == null) {
            throw new IllegalArgumentException("Invalid range: " + range + ". Allowed: 1d, 1w, 1m, 3m, 6m, 1y");
        }
        try {
            JsonNode root = restClient.get()
                    .uri(CHART_URL + symbol + "?range=" + params[0] + "&interval=" + params[1])
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode result = root.path("chart").path("result").get(0);
            JsonNode timestamps = result.path("timestamp");
            JsonNode quote = result.path("indicators").path("quote").get(0);
            JsonNode opens = quote.path("open");
            JsonNode highs = quote.path("high");
            JsonNode lows = quote.path("low");
            JsonNode closes = quote.path("close");
            JsonNode volumes = quote.path("volume");

            List<HistoricalDataPoint> points = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                if (closes.get(i).isNull()) continue;
                points.add(HistoricalDataPoint.builder()
                        .timestamp(timestamps.get(i).asLong())
                        .open(new BigDecimal(opens.get(i).asText("0")))
                        .high(new BigDecimal(highs.get(i).asText("0")))
                        .low(new BigDecimal(lows.get(i).asText("0")))
                        .close(new BigDecimal(closes.get(i).asText("0")))
                        .volume(volumes.get(i).asLong(0))
                        .build());
            }
            return points;
        } catch (Exception e) {
            log.error("Failed to fetch history for {} (range={}): {}", symbol, range, e.getMessage());
            return Collections.emptyList();
        }
    }
}
