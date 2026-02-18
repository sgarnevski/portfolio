package com.portfolio.rebalancer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Holding> holdings = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TargetAllocation> targetAllocations = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Portfolio() {
    }

    public Portfolio(Long id, String name, String description, User owner,
                     List<Holding> holdings, List<TargetAllocation> targetAllocations,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.holdings = holdings != null ? holdings : new ArrayList<>();
        this.targetAllocations = targetAllocations != null ? targetAllocations : new ArrayList<>();
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<Holding> getHoldings() { return holdings; }
    public void setHoldings(List<Holding> holdings) { this.holdings = holdings; }

    public List<TargetAllocation> getTargetAllocations() { return targetAllocations; }
    public void setTargetAllocations(List<TargetAllocation> targetAllocations) { this.targetAllocations = targetAllocations; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static PortfolioBuilder builder() { return new PortfolioBuilder(); }

    public static class PortfolioBuilder {
        private Long id;
        private String name;
        private String description;
        private User owner;
        private List<Holding> holdings = new ArrayList<>();
        private List<TargetAllocation> targetAllocations = new ArrayList<>();
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public PortfolioBuilder id(Long id) { this.id = id; return this; }
        public PortfolioBuilder name(String name) { this.name = name; return this; }
        public PortfolioBuilder description(String description) { this.description = description; return this; }
        public PortfolioBuilder owner(User owner) { this.owner = owner; return this; }
        public PortfolioBuilder holdings(List<Holding> holdings) { this.holdings = holdings; return this; }
        public PortfolioBuilder targetAllocations(List<TargetAllocation> targetAllocations) { this.targetAllocations = targetAllocations; return this; }
        public PortfolioBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PortfolioBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Portfolio build() {
            return new Portfolio(id, name, description, owner, holdings, targetAllocations, createdAt, updatedAt);
        }
    }
}
