package com.portfolio.rebalancer.dto.response;

import com.portfolio.rebalancer.entity.TradeType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TradeResponse {
    private Long id;
    private LocalDate date;
    private TradeType type;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal fee;
    private LocalDateTime createdAt;

    public TradeResponse() {
    }

    public TradeResponse(Long id, LocalDate date, TradeType type, BigDecimal quantity,
                         BigDecimal price, BigDecimal fee, LocalDateTime createdAt) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.fee = fee;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public TradeType getType() { return type; }
    public void setType(TradeType type) { this.type = type; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TradeResponseBuilder builder() { return new TradeResponseBuilder(); }

    public static class TradeResponseBuilder {
        private Long id;
        private LocalDate date;
        private TradeType type;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal fee;
        private LocalDateTime createdAt;

        public TradeResponseBuilder id(Long id) { this.id = id; return this; }
        public TradeResponseBuilder date(LocalDate date) { this.date = date; return this; }
        public TradeResponseBuilder type(TradeType type) { this.type = type; return this; }
        public TradeResponseBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public TradeResponseBuilder price(BigDecimal price) { this.price = price; return this; }
        public TradeResponseBuilder fee(BigDecimal fee) { this.fee = fee; return this; }
        public TradeResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TradeResponse build() {
            return new TradeResponse(id, date, type, quantity, price, fee, createdAt);
        }
    }
}
