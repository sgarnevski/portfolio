package com.portfolio.gateway.dto;

import java.math.BigDecimal;

public class HistoricalDataPoint {
    private long timestamp;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private long volume;

    public HistoricalDataPoint() {
    }

    public HistoricalDataPoint(long timestamp, BigDecimal open, BigDecimal high,
                               BigDecimal low, BigDecimal close, long volume) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }

    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }

    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }

    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public static HistoricalDataPointBuilder builder() { return new HistoricalDataPointBuilder(); }

    public static class HistoricalDataPointBuilder {
        private long timestamp;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private long volume;

        public HistoricalDataPointBuilder timestamp(long timestamp) { this.timestamp = timestamp; return this; }
        public HistoricalDataPointBuilder open(BigDecimal open) { this.open = open; return this; }
        public HistoricalDataPointBuilder high(BigDecimal high) { this.high = high; return this; }
        public HistoricalDataPointBuilder low(BigDecimal low) { this.low = low; return this; }
        public HistoricalDataPointBuilder close(BigDecimal close) { this.close = close; return this; }
        public HistoricalDataPointBuilder volume(long volume) { this.volume = volume; return this; }

        public HistoricalDataPoint build() {
            return new HistoricalDataPoint(timestamp, open, high, low, close, volume);
        }
    }
}
