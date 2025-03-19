package com.meetime.hubspot.infrastructure.http.adapter.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.hubspot.application.port.contact.HubspotContactPort;
import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.infrastructure.config.HubspotRateLimiter;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterRuntimeException;
import com.meetime.hubspot.infrastructure.http.model.ExceptionMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.meetime.hubspot.infrastructure.constants.InfrastructureConstants.*;
import static com.meetime.hubspot.infrastructure.http.model.ExceptionMessageEnum.ERROR_CREATING_CONTACT;
import static com.meetime.hubspot.infrastructure.utils.InfrastructureUtils.handleUnexpectedStatus;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@Component
public class HubspotContactAdapter implements HubspotContactPort {

    private final String hubspotApiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final HubspotRateLimiter hubspotRateLimiter;

    public HubspotContactAdapter(@Value("${hubspot.api.url}") String hubspotApiUrl,
                                 HttpClient httpClient,
                                 ObjectMapper objectMapper,
                                 HubspotRateLimiter hubspotRateLimiter) {
        this.hubspotApiUrl = hubspotApiUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.hubspotRateLimiter = hubspotRateLimiter;
    }

    @Override
    public String createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, HubspotAdapterException {
        log.info("Creating user...");
        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contactCreationRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(hubspotApiUrl + "/crm/v3/objects/contacts/"))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, String.format(BEARER, accessToken))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            hubspotRateLimiter.acquire();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.CREATED.value()) {
                log.info("User created successfully.");
                updateRateLimiter(response.headers());
                return response.body();
            } else if (response.statusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                log.error("Rate limit exceeded.");
                handleRateLimitExceeded(response.headers());
                throw new HubspotAdapterRuntimeException(ExceptionMessageEnum.RATE_LIMIT_EXCEPTION_MESSAGE.getMessage());
            }
            handleUnexpectedStatus(response);
        } catch (InterruptedException e) {
            log.error("Error while crating user. {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new HubspotAdapterException(e.getMessage());
        }
        throw new HubspotAdapterRuntimeException(ERROR_CREATING_CONTACT.getMessage());
    }

    private void updateRateLimiter(HttpHeaders headers) {
        int remaining = Integer.parseInt(headers.firstValue(X_HUB_SPOT_RATE_LIMIT_REMAINING).orElse("100"));
        long interval = Long.parseLong(headers.firstValue(X_HUB_SPOT_RATE_LIMIT_INTERVAL_MILLISECONDS).orElse("60000"));
        hubspotRateLimiter.update(remaining, interval);
    }

    private void handleRateLimitExceeded(HttpHeaders headers) {
        long retryAfter = Long.parseLong(headers.firstValue(RETRY_AFTER).orElse("60"));
        hubspotRateLimiter.handleRateLimitExceeded(retryAfter * 1000);
    }
}
