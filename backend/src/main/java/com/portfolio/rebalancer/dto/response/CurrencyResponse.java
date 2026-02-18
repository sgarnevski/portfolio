package com.portfolio.rebalancer.dto.response;

public class CurrencyResponse {

    private Long id;
    private String code;
    private String name;

    public CurrencyResponse() {
    }

    public CurrencyResponse(Long id, String code, String name) {
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

    public static CurrencyResponseBuilder builder() { return new CurrencyResponseBuilder(); }

    public static class CurrencyResponseBuilder {
        private Long id;
        private String code;
        private String name;

        public CurrencyResponseBuilder id(Long id) { this.id = id; return this; }
        public CurrencyResponseBuilder code(String code) { this.code = code; return this; }
        public CurrencyResponseBuilder name(String name) { this.name = name; return this; }

        public CurrencyResponse build() {
            return new CurrencyResponse(id, code, name);
        }
    }
}
