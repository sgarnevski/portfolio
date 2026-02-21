package com.portfolio.rebalancer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Holding> holdings = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TargetAllocation> targetAllocations = new ArrayList<>();

    @Column(name = "drift_threshold", precision = 5, scale = 2)
    private BigDecimal driftThreshold = new BigDecimal("5.00");

    @Column(name = "cash_balance", precision = 19, scale = 2)
    private BigDecimal cashBalance = BigDecimal.ZERO;

    @Column(name = "base_currency", length = 3)
    private String baseCurrency;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Portfolio() {
    }

    public Portfolio(Long id, String name, String description, Long ownerId,
                     List<Holding> holdings, List<TargetAllocation> targetAllocations,
                     BigDecimal driftThreshold, BigDecimal cashBalance, String baseCurrency,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.holdings = holdings != null ? holdings : new ArrayList<>();
        this.targetAllocations = targetAllocations != null ? targetAllocations : new ArrayList<>();
        this.driftThreshold = driftThreshold != null ? driftThreshold : new BigDecimal("5.00");
        this.cashBalance = cashBalance != null ? cashBalance : BigDecimal.ZERO;
        this.baseCurrency = baseCurrency;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public List<Holding> getHoldings() { return holdings; }
    public void setHoldings(List<Holding> holdings) { this.holdings = holdings; }

    public List<TargetAllocation> getTargetAllocations() { return targetAllocations; }
    public void setTargetAllocations(List<TargetAllocation> targetAllocations) { this.targetAllocations = targetAllocations; }

    public BigDecimal getDriftThreshold() { return driftThreshold != null ? driftThreshold : new BigDecimal("5.00"); }
    public void setDriftThreshold(BigDecimal driftThreshold) { this.driftThreshold = driftThreshold; }

    public BigDecimal getCashBalance() { return cashBalance != null ? cashBalance : BigDecimal.ZERO; }
    public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }

    public String getBaseCurrency() { return baseCurrency != null ? baseCurrency : "USD"; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static PortfolioBuilder builder() { return new PortfolioBuilder(); }

    public static class PortfolioBuilder {
        private Long id;
        private String name;
        private String description;
        private Long ownerId;
        private List<Holding> holdings = new ArrayList<>();
        private List<TargetAllocation> targetAllocations = new ArrayList<>();
        private BigDecimal driftThreshold;
        private BigDecimal cashBalance;
        private String baseCurrency;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public PortfolioBuilder id(Long id) { this.id = id; return this; }
        public PortfolioBuilder name(String name) { this.name = name; return this; }
        public PortfolioBuilder description(String description) { this.description = description; return this; }
        public PortfolioBuilder ownerId(Long ownerId) { this.ownerId = ownerId; return this; }
        public PortfolioBuilder holdings(List<Holding> holdings) { this.holdings = holdings; return this; }
        public PortfolioBuilder targetAllocations(List<TargetAllocation> targetAllocations) { this.targetAllocations = targetAllocations; return this; }
        public PortfolioBuilder driftThreshold(BigDecimal driftThreshold) { this.driftThreshold = driftThreshold; return this; }
        public PortfolioBuilder cashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; return this; }
        public PortfolioBuilder baseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; return this; }
        public PortfolioBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PortfolioBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Portfolio build() {
            return new Portfolio(id, name, description, ownerId, holdings, targetAllocations, driftThreshold, cashBalance, baseCurrency, createdAt, updatedAt);
        }
    }
}
