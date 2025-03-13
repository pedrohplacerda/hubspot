package com.meetime.hubspot.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class InfrastructureConfigurationFactory {
    @Bean
    public HttpClient buildHttpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public ObjectMapper buildObjectMapper() {
        return new ObjectMapper();
    }
}
