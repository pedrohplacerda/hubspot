package com.meetime.hubspot.application.port.auth;

import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;

import java.io.IOException;

public interface HubspotAuthPort {

    String createAuthUrl();

    String generateAccessToken(String authorizationCode) throws IOException, InterruptedException, HubspotAdapterException;

}
