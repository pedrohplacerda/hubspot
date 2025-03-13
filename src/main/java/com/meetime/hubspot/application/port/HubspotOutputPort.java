package com.meetime.hubspot.application.port;

import com.meetime.hubspot.domain.model.ContactCreationRequest;
import com.meetime.hubspot.domain.model.ContactCreationResponse;
import com.meetime.hubspot.infrastructure.exception.HubspotOutputAdapterException;

import java.io.IOException;

public interface HubspotOutputPort {
    String createAuthUrl();

    String generateAccessToken(String authorizationCode) throws IOException, InterruptedException, HubspotOutputAdapterException;

    ContactCreationResponse createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, InterruptedException, HubspotOutputAdapterException;
}
