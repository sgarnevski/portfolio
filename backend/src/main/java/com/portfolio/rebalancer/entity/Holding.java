package com.portfolio.rebalancer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "holdings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "ticker_symbol"}))
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "ticker_symbol", nullable = false, length = 20)
    private String tickerSymbol;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetClass assetClass;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal averageCostBasis;

    @Column(precision = 19, scale = 4)
    private BigDecimal initialValue;

    @Column(length = 3)
    private String currency;

    public Holding() {
    }

    public Holding(Long id, Portfolio portfolio, String tickerSymbol, String name,
                   AssetClass assetClass, BigDecimal quantity, BigDecimal averageCostBasis,
                   BigDecimal initialValue, String currency) {
        this.id = id;
        this.portfolio = portfolio;
        this.tickerSymbol = tickerSymbol;
        this.name = name;
        this.assetClass = assetClass;
        this.quantity = quantity;
        this.averageCostBasis = averageCostBasis;
        this.initialValue = initialValue;
        this.currency = currency;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Portfolio getPortfolio() { return portfolio; }
    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }

    public String getTickerSymbol() { return tickerSymbol; }
    public void setTickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AssetClass getAssetClass() { return assetClass; }
    public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAverageCostBasis() { return averageCostBasis; }
    public void setAverageCostBasis(BigDecimal averageCostBasis) { this.averageCostBasis = averageCostBasis; }

    public BigDecimal getInitialValue() { return initialValue; }
    public void setInitialValue(BigDecimal initialValue) { this.initialValue = initialValue; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public static HoldingBuilder builder() { return new HoldingBuilder(); }

    public static class HoldingBuilder {
        private Long id;
        private Portfolio portfolio;
        private String tickerSymbol;
        private String name;
        private AssetClass assetClass;
        private BigDecimal quantity;
        private BigDecimal averageCostBasis;
        private BigDecimal initialValue;
        private String currency;

        public HoldingBuilder id(Long id) { this.id = id; return this; }
        public HoldingBuilder portfolio(Portfolio portfolio) { this.portfolio = portfolio; return this; }
        public HoldingBuilder tickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; return this; }
        public HoldingBuilder name(String name) { this.name = name; return this; }
        public HoldingBuilder assetClass(AssetClass assetClass) { this.assetClass = assetClass; return this; }
        public HoldingBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public HoldingBuilder averageCostBasis(BigDecimal averageCostBasis) { this.averageCostBasis = averageCostBasis; return this; }
        public HoldingBuilder initialValue(BigDecimal initialValue) { this.initialValue = initialValue; return this; }
        public HoldingBuilder currency(String currency) { this.currency = currency; return this; }

        public Holding build() {
            return new Holding(id, portfolio, tickerSymbol, name, assetClass, quantity, averageCostBasis, initialValue, currency);
        }
    }
}
