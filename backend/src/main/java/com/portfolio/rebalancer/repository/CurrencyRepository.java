package com.portfolio.rebalancer.repository;

import com.portfolio.rebalancer.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    List<Currency> findAllByOrderByCodeAsc();
    boolean existsByCode(String code);
}
