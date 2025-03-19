package com.meetime.hubspot.domain.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.hubspot.application.port.auth.HubspotAuthPort;
import com.meetime.hubspot.domain.model.auth.AccessToken;
import com.meetime.hubspot.domain.model.auth.AuthUrl;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class HubspotAuthService {

    private final HubspotAuthPort authPort;
    private final ObjectMapper objectMapper;

    public AuthUrl getAuthUrl() {
        return new AuthUrl(authPort.createAuthUrl());
    }

    public AccessToken getAccessToken(String authorizationCode) throws IOException, HubspotAdapterException, InterruptedException {
        return objectMapper.readValue(authPort.generateAccessToken(authorizationCode), AccessToken.class);
    }

}
