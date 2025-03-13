package com.meetime.hubspot.application.port;

import java.io.IOException;

public interface HubspotOutputPort {
    String createAuthUrl();
    String generateAccessToken(String authorizationCode) throws IOException, InterruptedException;
}
