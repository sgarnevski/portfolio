package com.portfolio.auth.dto;

public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String username, String email, String createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public static UserProfileResponseBuilder builder() { return new UserProfileResponseBuilder(); }

    public static class UserProfileResponseBuilder {
        private Long id;
        private String username;
        private String email;
        private String createdAt;

        public UserProfileResponseBuilder id(Long id) { this.id = id; return this; }
        public UserProfileResponseBuilder username(String username) { this.username = username; return this; }
        public UserProfileResponseBuilder email(String email) { this.email = email; return this; }
        public UserProfileResponseBuilder createdAt(String createdAt) { this.createdAt = createdAt; return this; }

        public UserProfileResponse build() {
            return new UserProfileResponse(id, username, email, createdAt);
        }
    }
}
