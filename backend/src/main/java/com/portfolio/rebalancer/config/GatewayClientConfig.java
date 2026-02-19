package com.portfolio.rebalancer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GatewayClientConfig {

    @Bean
    public RestClient gatewayRestClient(RestClient.Builder builder,
                                         @Value("${app.gateway.url}") String gatewayUrl) {
        return builder
                .baseUrl(gatewayUrl)
                .build();
    }
}
