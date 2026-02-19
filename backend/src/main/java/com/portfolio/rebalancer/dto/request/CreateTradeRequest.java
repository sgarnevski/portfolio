package com.portfolio.rebalancer.dto.request;

import com.portfolio.rebalancer.entity.TradeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTradeRequest {
    @NotNull
    private LocalDate date;
    @NotNull
    private TradeType type;
    @NotNull @Positive
    private BigDecimal quantity;
    @NotNull @Positive
    private BigDecimal price;
    private BigDecimal fee;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public TradeType getType() { return type; }
    public void setType(TradeType type) { this.type = type; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
