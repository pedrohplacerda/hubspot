package com.meetime.hubspot.infrastructure.http.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.hubspot.application.port.HubspotOutputPort;
import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.domain.model.webhook.ContactCreationWebhookRequest;
import com.meetime.hubspot.infrastructure.config.HubspotRateLimiter;
import com.meetime.hubspot.infrastructure.http.exception.HubspotOutputAdapterException;
import com.meetime.hubspot.infrastructure.http.exception.HubspotOutputAdapterRuntimeException;
import com.meetime.hubspot.infrastructure.http.model.ExceptionMessageEnum;
import com.meetime.hubspot.infrastructure.utils.InfrastructureUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.meetime.hubspot.infrastructure.constants.InfrastructureConstants.*;
import static com.meetime.hubspot.infrastructure.http.model.ExceptionMessageEnum.*;
import static com.meetime.hubspot.infrastructure.utils.InfrastructureUtils.getParams;
import static com.meetime.hubspot.infrastructure.utils.InfrastructureUtils.handleUnexpectedStatus;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@Service
public class HubspotOutputPortAdapter implements HubspotOutputPort {

    private final String authUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String hubspotApiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final HubspotRateLimiter hubspotRateLimiter;

    public HubspotOutputPortAdapter(@Value("${hubspot.api.url}") String hubspotApiUrl,
                                    @Value("${hubspot.redirect.uri}") String redirectUri,
                                    @Value("${hubspot.client.secret}") String clientSecret,
                                    @Value("${hubspot.client.id}") String clientId,
                                    @Value("${hubspot.auth.url}") String authUrl,
                                    HttpClient httpClient,
                                    ObjectMapper objectMapper,
                                    HubspotRateLimiter hubspotRateLimiter) {
        this.hubspotApiUrl = hubspotApiUrl;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.clientId = clientId;
        this.authUrl = authUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.hubspotRateLimiter = hubspotRateLimiter;
    }

    @Override
    public String createAuthUrl() {
        return authUrl;
    }

    @Override
    public String generateAccessToken(String authorizationCode) throws IOException, HubspotOutputAdapterException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(hubspotApiUrl + "/oauth/v1/token"))
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(getParams(authorizationCode, clientId, clientSecret, redirectUri)))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                return response.body();
            }
            handleUnexpectedStatus(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HubspotOutputAdapterException(e.getMessage());
        }
        throw new HubspotOutputAdapterException(ExceptionMessageEnum.ERROR_FETCHING_ACCESS_TOKEN_MESSAGE.getMessage());
    }

    @Override
    public String createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, HubspotOutputAdapterException {
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
                throw new HubspotOutputAdapterRuntimeException(ExceptionMessageEnum.RATE_LIMIT_EXCEPTION_MESSAGE.getMessage());
            }
            handleUnexpectedStatus(response);
        } catch (InterruptedException e) {
            log.error("Error while crating user. {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new HubspotOutputAdapterException(e.getMessage());
        }
        throw new HubspotOutputAdapterRuntimeException(ERROR_CREATING_CONTACT.getMessage());
    }

    @Async
    @Override
    public Contact processContactEventAsync(String signature, String payload) throws HubspotOutputAdapterException {
        if (!InfrastructureUtils.isValidSignature(signature, payload, clientSecret)) {
            throw new HubspotOutputAdapterRuntimeException(UNAUTHORIZED_MESSAGE.getMessage());
        }
        try {
            List<ContactCreationWebhookRequest> contactWebhookRequest = objectMapper.readValue(payload, new TypeReference<>() {
            });
            ContactCreationWebhookRequest event = contactWebhookRequest.getFirst();
            if (CONTACT_CREATION.equals(event.eventType())) {
                log.info("Processing contact creation. Id: {}...", event.contactId());
                Contact contact = processContactDetails(fetchContactDetails(event.contactId(), ""));
                log.info("Contact creation processed successfully.");
                return contact;
            }
        } catch (Exception e) {
            log.error("An error happened while processing the event.", e);
            throw new HubspotOutputAdapterRuntimeException(e.getMessage());
        }
        throw new HubspotOutputAdapterException(INTERNAL_SERVER_ERROR_MESSAGE.getMessage());
    }

    // TODO: verify why the contact is being created but when requested it comes with null lastname, firstname and email
    private Contact fetchContactDetails(Long contactId, String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(hubspotApiUrl + "/crm/v3/objects/contacts/" + contactId))
                    .header(ACCEPT, APPLICATION_JSON_VALUE)
                    // TODO: after implementing a way to retrieve another accessToken, remove hardcoded one
                    .header(AUTHORIZATION, String.format(BEARER, accessToken))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), Contact.class);
        } catch (Exception e) {
            log.error("Error while fetching contact information.", e);
            throw new HubspotOutputAdapterRuntimeException(e.getMessage());
        }
    }

    private Contact processContactDetails(Contact contact) {
        // TODO: implement database contact saving method
        log.info("User created in database.");
        return contact;
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
