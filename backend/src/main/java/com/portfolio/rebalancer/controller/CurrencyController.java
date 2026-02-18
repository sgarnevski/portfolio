package com.portfolio.rebalancer.controller;

import com.portfolio.rebalancer.dto.request.CreateCurrencyRequest;
import com.portfolio.rebalancer.dto.response.CurrencyResponse;
import com.portfolio.rebalancer.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@Tag(name = "Currencies", description = "Manage available currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    @Operation(summary = "Get all currencies")
    public ResponseEntity<List<CurrencyResponse>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @PostMapping
    @Operation(summary = "Create a new currency")
    public ResponseEntity<CurrencyResponse> createCurrency(@Valid @RequestBody CreateCurrencyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyService.createCurrency(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a currency")
    public ResponseEntity<CurrencyResponse> updateCurrency(@PathVariable Long id,
                                                            @Valid @RequestBody CreateCurrencyRequest request) {
        return ResponseEntity.ok(currencyService.updateCurrency(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a currency")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return ResponseEntity.noContent().build();
    }
}
