package com.portfolio.rebalancer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "target_allocations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "asset_class"}))
public class TargetAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_class", nullable = false, length = 20)
    private AssetClass assetClass;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal targetPercentage;

    public TargetAllocation() {
    }

    public TargetAllocation(Long id, Portfolio portfolio, AssetClass assetClass, BigDecimal targetPercentage) {
        this.id = id;
        this.portfolio = portfolio;
        this.assetClass = assetClass;
        this.targetPercentage = targetPercentage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Portfolio getPortfolio() { return portfolio; }
    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }

    public AssetClass getAssetClass() { return assetClass; }
    public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

    public BigDecimal getTargetPercentage() { return targetPercentage; }
    public void setTargetPercentage(BigDecimal targetPercentage) { this.targetPercentage = targetPercentage; }

    public static TargetAllocationBuilder builder() { return new TargetAllocationBuilder(); }

    public static class TargetAllocationBuilder {
        private Long id;
        private Portfolio portfolio;
        private AssetClass assetClass;
        private BigDecimal targetPercentage;

        public TargetAllocationBuilder id(Long id) { this.id = id; return this; }
        public TargetAllocationBuilder portfolio(Portfolio portfolio) { this.portfolio = portfolio; return this; }
        public TargetAllocationBuilder assetClass(AssetClass assetClass) { this.assetClass = assetClass; return this; }
        public TargetAllocationBuilder targetPercentage(BigDecimal targetPercentage) { this.targetPercentage = targetPercentage; return this; }

        public TargetAllocation build() {
            return new TargetAllocation(id, portfolio, assetClass, targetPercentage);
        }
    }
}
