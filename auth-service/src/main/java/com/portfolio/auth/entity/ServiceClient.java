package com.portfolio.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_clients")
public class ServiceClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String clientSecretHash;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String scope;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ServiceClient() {
    }

    public ServiceClient(Long id, String clientId, String clientSecretHash, String name,
                          String scope, boolean enabled, LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecretHash = clientSecretHash;
        this.name = name;
        this.scope = scope;
        this.enabled = enabled;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecretHash() { return clientSecretHash; }
    public void setClientSecretHash(String clientSecretHash) { this.clientSecretHash = clientSecretHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ServiceClientBuilder builder() { return new ServiceClientBuilder(); }

    public static class ServiceClientBuilder {
        private Long id;
        private String clientId;
        private String clientSecretHash;
        private String name;
        private String scope;
        private boolean enabled = true;
        private LocalDateTime createdAt = LocalDateTime.now();

        public ServiceClientBuilder id(Long id) { this.id = id; return this; }
        public ServiceClientBuilder clientId(String clientId) { this.clientId = clientId; return this; }
        public ServiceClientBuilder clientSecretHash(String clientSecretHash) { this.clientSecretHash = clientSecretHash; return this; }
        public ServiceClientBuilder name(String name) { this.name = name; return this; }
        public ServiceClientBuilder scope(String scope) { this.scope = scope; return this; }
        public ServiceClientBuilder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public ServiceClientBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ServiceClient build() {
            return new ServiceClient(id, clientId, clientSecretHash, name, scope, enabled, createdAt);
        }
    }
}
