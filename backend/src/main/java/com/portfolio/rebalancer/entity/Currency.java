package com.portfolio.rebalancer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    public Currency() {
    }

    public Currency(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static CurrencyBuilder builder() { return new CurrencyBuilder(); }

    public static class CurrencyBuilder {
        private Long id;
        private String code;
        private String name;

        public CurrencyBuilder id(Long id) { this.id = id; return this; }
        public CurrencyBuilder code(String code) { this.code = code; return this; }
        public CurrencyBuilder name(String name) { this.name = name; return this; }

        public Currency build() {
            return new Currency(id, code, name);
        }
    }
}
