package com.portfolio.rebalancer.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class CreatePortfolioRequest {
    @NotBlank @Size(max = 100)
    private String name;
    @Size(max = 500)
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal driftThreshold;

    @DecimalMin("0.0")
    private BigDecimal cashBalance;

    @Size(max = 3)
    private String baseCurrency;

    public BigDecimal getDriftThreshold() { return driftThreshold; }
    public void setDriftThreshold(BigDecimal driftThreshold) { this.driftThreshold = driftThreshold; }

    public BigDecimal getCashBalance() { return cashBalance; }
    public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
}
