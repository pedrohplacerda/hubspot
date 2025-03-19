package com.meetime.hubspot.infrastructure.http.adapter.webhook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.hubspot.application.port.webhook.HubspotWebkhookPort;
import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.domain.model.webhook.ContactCreationWebhookRequest;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterRuntimeException;
import com.meetime.hubspot.infrastructure.http.model.ExceptionMessageEnum;
import com.meetime.hubspot.infrastructure.utils.InfrastructureUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.meetime.hubspot.infrastructure.constants.InfrastructureConstants.BEARER;
import static com.meetime.hubspot.infrastructure.constants.InfrastructureConstants.CONTACT_CREATION;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class HubspotWebhookAdapter implements HubspotWebkhookPort {

    private final String hubspotApiUrl;
    private final String clientSecret;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HubspotWebhookAdapter(@Value("${hubspot.api.url}") String hubspotApiUrl,
                                 @Value("${hubspot.client.secret}") String clientSecret,
                                 HttpClient httpClient,
                                 ObjectMapper objectMapper) {
        this.hubspotApiUrl = hubspotApiUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.clientSecret = clientSecret;
    }

    @Async
    @Override
    public Contact processContactEventAsync(String signature, String payload) throws HubspotAdapterException {
        if (!InfrastructureUtils.isValidSignature(signature, payload, clientSecret)) {
            throw new HubspotAdapterRuntimeException(ExceptionMessageEnum.UNAUTHORIZED_MESSAGE.getMessage());
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
            throw new HubspotAdapterRuntimeException(e.getMessage());
        }
        throw new HubspotAdapterException(ExceptionMessageEnum.INTERNAL_SERVER_ERROR_MESSAGE.getMessage());
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
            throw new HubspotAdapterRuntimeException(e.getMessage());
        }
    }

    private Contact processContactDetails(Contact contact) {
        // TODO: implement database contact saving method
        log.info("User created in database.");
        return contact;
    }
}
