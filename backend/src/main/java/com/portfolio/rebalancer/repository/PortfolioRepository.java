package com.portfolio.rebalancer.repository;

import com.portfolio.rebalancer.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByOwnerId(Long ownerId);
    Optional<Portfolio> findByIdAndOwnerId(Long id, Long ownerId);
}
