package com.meetime.hubspot.domain.service;

import com.meetime.hubspot.domain.model.AccessToken;
import com.meetime.hubspot.domain.model.AuthUrl;
import com.meetime.hubspot.infrastructure.adapter.HubspotOutputPortAdapter;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HubspotService {

    private final HubspotOutputPortAdapter hubspotOutputPortAdapter;

    public HubspotService(HubspotOutputPortAdapter hubspotOutputPortAdapter) {
        this.hubspotOutputPortAdapter = hubspotOutputPortAdapter;
    }

    public AuthUrl getAuthUrl() {
        return new AuthUrl(hubspotOutputPortAdapter.createAuthUrl());
    }

    public AccessToken getAccessToken(String authorizationCode) throws IOException, InterruptedException {
        return new AccessToken(hubspotOutputPortAdapter.generateAccessToken(authorizationCode));
    }
}
