package com.portfolio.rebalancer.repository;

import com.portfolio.rebalancer.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByPortfolioId(Long portfolioId);

    @Query("SELECT DISTINCT h.tickerSymbol FROM Holding h")
    List<String> findDistinctTickerSymbols();
}
