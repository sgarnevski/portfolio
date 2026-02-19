package com.portfolio.auth.service;

import com.portfolio.auth.entity.ServiceClient;
import com.portfolio.auth.repository.ServiceClientRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ServiceClientSeeder {

    private static final Logger logger = LoggerFactory.getLogger(ServiceClientSeeder.class);

    private final ServiceClientRepository serviceClientRepository;
    private final PasswordEncoder passwordEncoder;

    public ServiceClientSeeder(ServiceClientRepository serviceClientRepository, PasswordEncoder passwordEncoder) {
        this.serviceClientRepository = serviceClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void seed() {
        if (!serviceClientRepository.existsByClientId("portfolio-backend")) {
            ServiceClient client = ServiceClient.builder()
                    .clientId("portfolio-backend")
                    .clientSecretHash(passwordEncoder.encode("gateway-secret-change-in-prod"))
                    .name("Portfolio Backend")
                    .scope("market-data")
                    .enabled(true)
                    .build();
            serviceClientRepository.save(client);
            logger.info("Seeded default service client: portfolio-backend");
        }
    }
}
