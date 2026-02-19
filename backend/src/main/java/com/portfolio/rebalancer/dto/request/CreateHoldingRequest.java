package com.portfolio.rebalancer.dto.request;

import com.portfolio.rebalancer.entity.AssetClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateHoldingRequest {
    @NotBlank
    private String tickerSymbol;
    @NotBlank
    private String name;
    @NotNull
    private AssetClass assetClass;
    private String currency;

    public String getTickerSymbol() { return tickerSymbol; }
    public void setTickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AssetClass getAssetClass() { return assetClass; }
    public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
