package com.meetime.hubspot.application.port;

import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.infrastructure.http.exception.HubspotOutputAdapterException;

import java.io.IOException;

public interface HubspotOutputPort {
    String createAuthUrl();

    String generateAccessToken(String authorizationCode) throws IOException, InterruptedException, HubspotOutputAdapterException;

    String createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, InterruptedException, HubspotOutputAdapterException;

    Contact processContactEventAsync(String signature, String payload) throws HubspotOutputAdapterException;
}
