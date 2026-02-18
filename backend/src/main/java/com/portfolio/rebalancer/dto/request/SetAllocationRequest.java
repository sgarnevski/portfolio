package com.portfolio.rebalancer.dto.request;

import com.portfolio.rebalancer.entity.AssetClass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

public class SetAllocationRequest {

    @NotEmpty
    @Valid
    private List<AllocationEntry> allocations;

    public List<AllocationEntry> getAllocations() { return allocations; }
    public void setAllocations(List<AllocationEntry> allocations) { this.allocations = allocations; }

    public static class AllocationEntry {
        private AssetClass assetClass;
        private BigDecimal targetPercentage;

        public AssetClass getAssetClass() { return assetClass; }
        public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

        public BigDecimal getTargetPercentage() { return targetPercentage; }
        public void setTargetPercentage(BigDecimal targetPercentage) { this.targetPercentage = targetPercentage; }
    }
}
