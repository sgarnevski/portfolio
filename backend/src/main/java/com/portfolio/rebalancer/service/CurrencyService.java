package com.portfolio.rebalancer.service;

import com.portfolio.rebalancer.dto.request.CreateCurrencyRequest;
import com.portfolio.rebalancer.dto.response.CurrencyResponse;
import com.portfolio.rebalancer.entity.Currency;
import com.portfolio.rebalancer.exception.ResourceNotFoundException;
import com.portfolio.rebalancer.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @PostConstruct
    @Transactional
    public void seedCurrencies() {
        Map<String, String> defaults = Map.of(
            "EUR", "Euro",
            "USD", "US Dollar",
            "GBP", "British Pound",
            "CHF", "Swiss Franc",
            "SEK", "Swedish Krona",
            "DKK", "Danish Krone",
            "NOK", "Norwegian Krone",
            "PLN", "Polish Zloty"
        );
        defaults.forEach((code, name) -> {
            if (!currencyRepository.existsByCode(code)) {
                currencyRepository.save(Currency.builder().code(code).name(name).build());
            }
        });
        log.info("Currency seed complete — {} currencies in database", currencyRepository.count());
    }

    public List<CurrencyResponse> getAllCurrencies() {
        return currencyRepository.findAllByOrderByCodeAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CurrencyResponse createCurrency(CreateCurrencyRequest request) {
        if (currencyRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new IllegalArgumentException("Currency code already exists: " + request.getCode());
        }
        Currency currency = Currency.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .build();
        currency = currencyRepository.save(currency);
        return toResponse(currency);
    }

    @Transactional
    public CurrencyResponse updateCurrency(Long id, CreateCurrencyRequest request) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found"));
        currency.setCode(request.getCode().toUpperCase());
        currency.setName(request.getName());
        currency = currencyRepository.save(currency);
        return toResponse(currency);
    }

    @Transactional
    public void deleteCurrency(Long id) {
        if (!currencyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Currency not found");
        }
        currencyRepository.deleteById(id);
    }

    private CurrencyResponse toResponse(Currency currency) {
        return CurrencyResponse.builder()
                .id(currency.getId())
                .code(currency.getCode())
                .name(currency.getName())
                .build();
    }
}
