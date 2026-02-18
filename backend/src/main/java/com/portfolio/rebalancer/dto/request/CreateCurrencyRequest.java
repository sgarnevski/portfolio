package com.portfolio.rebalancer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCurrencyRequest {
    @NotBlank @Size(min = 3, max = 3)
    private String code;
    @NotBlank @Size(max = 100)
    private String name;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
