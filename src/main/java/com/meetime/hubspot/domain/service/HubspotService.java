package com.meetime.hubspot.domain.service;

import com.meetime.hubspot.domain.model.AccessToken;
import com.meetime.hubspot.domain.model.AuthUrl;
import com.meetime.hubspot.domain.model.ContactCreationRequest;
import com.meetime.hubspot.domain.model.ContactCreationResponse;
import com.meetime.hubspot.infrastructure.adapter.HubspotOutputPortAdapter;
import com.meetime.hubspot.infrastructure.exception.HubspotOutputAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class HubspotService {

    private final HubspotOutputPortAdapter hubspotOutputPortAdapter;

    public AuthUrl getAuthUrl() {
        return new AuthUrl(hubspotOutputPortAdapter.createAuthUrl());
    }

    public AccessToken getAccessToken(String authorizationCode) throws IOException, HubspotOutputAdapterException {
        return new AccessToken(hubspotOutputPortAdapter.generateAccessToken(authorizationCode));
    }

    public ContactCreationResponse createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, HubspotOutputAdapterException {
        return hubspotOutputPortAdapter.createContact(contactCreationRequest, accessToken);
    }
}
