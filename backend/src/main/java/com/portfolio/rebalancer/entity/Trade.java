package com.portfolio.rebalancer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holding_id", nullable = false)
    private Holding holding;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 4)
    private TradeType type;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(precision = 19, scale = 4)
    private BigDecimal fee;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Trade() {
    }

    public Trade(Long id, Holding holding, LocalDate date, TradeType type,
                 BigDecimal quantity, BigDecimal price, BigDecimal fee, LocalDateTime createdAt) {
        this.id = id;
        this.holding = holding;
        this.date = date;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.fee = fee;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Holding getHolding() { return holding; }
    public void setHolding(Holding holding) { this.holding = holding; }

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

    public static TradeBuilder builder() { return new TradeBuilder(); }

    public static class TradeBuilder {
        private Long id;
        private Holding holding;
        private LocalDate date;
        private TradeType type;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal fee;
        private LocalDateTime createdAt;

        public TradeBuilder id(Long id) { this.id = id; return this; }
        public TradeBuilder holding(Holding holding) { this.holding = holding; return this; }
        public TradeBuilder date(LocalDate date) { this.date = date; return this; }
        public TradeBuilder type(TradeType type) { this.type = type; return this; }
        public TradeBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public TradeBuilder price(BigDecimal price) { this.price = price; return this; }
        public TradeBuilder fee(BigDecimal fee) { this.fee = fee; return this; }
        public TradeBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Trade build() {
            return new Trade(id, holding, date, type, quantity, price, fee, createdAt);
        }
    }
}
