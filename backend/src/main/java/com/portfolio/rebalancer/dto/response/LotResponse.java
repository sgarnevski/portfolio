package com.portfolio.rebalancer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LotResponse {
    private Long tradeId;
    private LocalDate purchaseDate;
    private BigDecimal originalQuantity;
    private BigDecimal remainingQuantity;
    private BigDecimal costBasisPerShare;

    public LotResponse() {
    }

    public LotResponse(Long tradeId, LocalDate purchaseDate, BigDecimal originalQuantity,
                       BigDecimal remainingQuantity, BigDecimal costBasisPerShare) {
        this.tradeId = tradeId;
        this.purchaseDate = purchaseDate;
        this.originalQuantity = originalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.costBasisPerShare = costBasisPerShare;
    }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public BigDecimal getOriginalQuantity() { return originalQuantity; }
    public void setOriginalQuantity(BigDecimal originalQuantity) { this.originalQuantity = originalQuantity; }

    public BigDecimal getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; }

    public BigDecimal getCostBasisPerShare() { return costBasisPerShare; }
    public void setCostBasisPerShare(BigDecimal costBasisPerShare) { this.costBasisPerShare = costBasisPerShare; }

    public static LotResponseBuilder builder() { return new LotResponseBuilder(); }

    public static class LotResponseBuilder {
        private Long tradeId;
        private LocalDate purchaseDate;
        private BigDecimal originalQuantity;
        private BigDecimal remainingQuantity;
        private BigDecimal costBasisPerShare;

        public LotResponseBuilder tradeId(Long tradeId) { this.tradeId = tradeId; return this; }
        public LotResponseBuilder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public LotResponseBuilder originalQuantity(BigDecimal originalQuantity) { this.originalQuantity = originalQuantity; return this; }
        public LotResponseBuilder remainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; return this; }
        public LotResponseBuilder costBasisPerShare(BigDecimal costBasisPerShare) { this.costBasisPerShare = costBasisPerShare; return this; }

        public LotResponse build() {
            return new LotResponse(tradeId, purchaseDate, originalQuantity, remainingQuantity, costBasisPerShare);
        }
    }
}
