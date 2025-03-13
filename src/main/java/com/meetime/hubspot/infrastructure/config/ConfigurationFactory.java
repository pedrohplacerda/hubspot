package com.meetime.hubspot.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class ConfigurationFactory {
    @Bean
    public HttpClient buildHttpClient() {
        return HttpClient.newHttpClient();
    }
}
