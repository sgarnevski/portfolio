package com.portfolio.rebalancer.repository;

import com.portfolio.rebalancer.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByHoldingId(Long holdingId);
    List<Trade> findByHoldingIdOrderByDateDesc(Long holdingId);
}
