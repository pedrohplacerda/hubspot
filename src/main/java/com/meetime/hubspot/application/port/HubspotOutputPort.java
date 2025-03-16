package com.meetime.hubspot.application.port;

import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.domain.model.contact.ContactCreationResponse;
import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.infrastructure.http.exception.HubspotOutputAdapterException;

import java.io.IOException;

public interface HubspotOutputPort {
    String createAuthUrl();

    String generateAccessToken(String authorizationCode) throws IOException, InterruptedException, HubspotOutputAdapterException;

    ContactCreationResponse createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, InterruptedException, HubspotOutputAdapterException;

    Contact fetchContactDetails(Long contactId);

    void processContactDetails(Contact contact);

    void processContactEventAsync(String signature, String payload);
}
