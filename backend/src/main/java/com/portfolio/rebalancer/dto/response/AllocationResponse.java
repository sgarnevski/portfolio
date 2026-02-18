package com.portfolio.rebalancer.dto.response;

import com.portfolio.rebalancer.entity.AssetClass;
import java.math.BigDecimal;

public class AllocationResponse {
    private Long id;
    private AssetClass assetClass;
    private BigDecimal targetPercentage;

    public AllocationResponse() {
    }

    public AllocationResponse(Long id, AssetClass assetClass, BigDecimal targetPercentage) {
        this.id = id;
        this.assetClass = assetClass;
        this.targetPercentage = targetPercentage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AssetClass getAssetClass() { return assetClass; }
    public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

    public BigDecimal getTargetPercentage() { return targetPercentage; }
    public void setTargetPercentage(BigDecimal targetPercentage) { this.targetPercentage = targetPercentage; }

    public static AllocationResponseBuilder builder() { return new AllocationResponseBuilder(); }

    public static class AllocationResponseBuilder {
        private Long id;
        private AssetClass assetClass;
        private BigDecimal targetPercentage;

        public AllocationResponseBuilder id(Long id) { this.id = id; return this; }
        public AllocationResponseBuilder assetClass(AssetClass assetClass) { this.assetClass = assetClass; return this; }
        public AllocationResponseBuilder targetPercentage(BigDecimal targetPercentage) { this.targetPercentage = targetPercentage; return this; }

        public AllocationResponse build() {
            return new AllocationResponse(id, assetClass, targetPercentage);
        }
    }
}
