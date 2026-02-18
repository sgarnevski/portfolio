package com.portfolio.rebalancer.repository;

import com.portfolio.rebalancer.entity.TargetAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TargetAllocationRepository extends JpaRepository<TargetAllocation, Long> {
    List<TargetAllocation> findByPortfolioId(Long portfolioId);
    void deleteByPortfolioId(Long portfolioId);
}
