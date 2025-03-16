package com.meetime.hubspot.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.hubspot.domain.model.auth.AccessToken;
import com.meetime.hubspot.domain.model.auth.AuthUrl;
import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.domain.model.contact.ContactCreationResponse;
import com.meetime.hubspot.infrastructure.http.adapter.HubspotOutputPortAdapter;
import com.meetime.hubspot.infrastructure.http.exception.HubspotOutputAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class HubspotService {

    private final HubspotOutputPortAdapter hubspotOutputPortAdapter;
    private final ObjectMapper objectMapper;

    public AuthUrl getAuthUrl() {
        return new AuthUrl(hubspotOutputPortAdapter.createAuthUrl());
    }

    public AccessToken getAccessToken(String authorizationCode) throws IOException, HubspotOutputAdapterException {
        return objectMapper.readValue(hubspotOutputPortAdapter.generateAccessToken(authorizationCode), AccessToken.class);
    }

    public ContactCreationResponse createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, HubspotOutputAdapterException {
        return hubspotOutputPortAdapter.createContact(contactCreationRequest, accessToken);
    }

    public void processWebhook(String signature, String payload) {
        hubspotOutputPortAdapter.processContactEventAsync(signature, payload);
    }

}
