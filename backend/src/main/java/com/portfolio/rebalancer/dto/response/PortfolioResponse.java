package com.portfolio.rebalancer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioResponse {
    private Long id;
    private String name;
    private String description;
    private List<HoldingResponse> holdings;
    private List<AllocationResponse> targetAllocations;
    private BigDecimal driftThreshold;
    private BigDecimal cashBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PortfolioResponse() {
    }

    public PortfolioResponse(Long id, String name, String description,
                             List<HoldingResponse> holdings, List<AllocationResponse> targetAllocations,
                             BigDecimal driftThreshold, BigDecimal cashBalance,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.holdings = holdings;
        this.targetAllocations = targetAllocations;
        this.driftThreshold = driftThreshold;
        this.cashBalance = cashBalance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<HoldingResponse> getHoldings() { return holdings; }
    public void setHoldings(List<HoldingResponse> holdings) { this.holdings = holdings; }

    public List<AllocationResponse> getTargetAllocations() { return targetAllocations; }
    public void setTargetAllocations(List<AllocationResponse> targetAllocations) { this.targetAllocations = targetAllocations; }

    public BigDecimal getDriftThreshold() { return driftThreshold; }
    public void setDriftThreshold(BigDecimal driftThreshold) { this.driftThreshold = driftThreshold; }

    public BigDecimal getCashBalance() { return cashBalance; }
    public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static PortfolioResponseBuilder builder() { return new PortfolioResponseBuilder(); }

    public static class PortfolioResponseBuilder {
        private Long id;
        private String name;
        private String description;
        private List<HoldingResponse> holdings;
        private List<AllocationResponse> targetAllocations;
        private BigDecimal driftThreshold;
        private BigDecimal cashBalance;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PortfolioResponseBuilder id(Long id) { this.id = id; return this; }
        public PortfolioResponseBuilder name(String name) { this.name = name; return this; }
        public PortfolioResponseBuilder description(String description) { this.description = description; return this; }
        public PortfolioResponseBuilder holdings(List<HoldingResponse> holdings) { this.holdings = holdings; return this; }
        public PortfolioResponseBuilder targetAllocations(List<AllocationResponse> targetAllocations) { this.targetAllocations = targetAllocations; return this; }
        public PortfolioResponseBuilder driftThreshold(BigDecimal driftThreshold) { this.driftThreshold = driftThreshold; return this; }
        public PortfolioResponseBuilder cashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; return this; }
        public PortfolioResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PortfolioResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public PortfolioResponse build() {
            return new PortfolioResponse(id, name, description, holdings, targetAllocations, driftThreshold, cashBalance, createdAt, updatedAt);
        }
    }
}
