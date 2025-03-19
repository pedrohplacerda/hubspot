package com.meetime.hubspot.infrastructure.http.adapter.auth;

import com.meetime.hubspot.application.port.auth.HubspotAuthPort;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import com.meetime.hubspot.infrastructure.http.model.ExceptionMessageEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.meetime.hubspot.infrastructure.utils.InfrastructureUtils.getParams;
import static com.meetime.hubspot.infrastructure.utils.InfrastructureUtils.handleUnexpectedStatus;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Component
public class HubspotAuthAdapter implements HubspotAuthPort {

    private final String authUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String hubspotApiUrl;
    private final HttpClient httpClient;

    public HubspotAuthAdapter(@Value("${hubspot.api.url}") String hubspotApiUrl,
                              @Value("${hubspot.redirect.uri}") String redirectUri,
                              @Value("${hubspot.client.secret}") String clientSecret,
                              @Value("${hubspot.client.id}") String clientId,
                              @Value("${hubspot.auth.url}") String authUrl,
                              HttpClient httpClient) {
        this.hubspotApiUrl = hubspotApiUrl;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.clientId = clientId;
        this.authUrl = authUrl;
        this.httpClient = httpClient;
    }

    @Override
    public String createAuthUrl() {
        return authUrl;
    }

    @Override
    public String generateAccessToken(String authorizationCode) throws IOException, HubspotAdapterException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(hubspotApiUrl + "/oauth/v1/token"))
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(getParams(authorizationCode, clientId, clientSecret, redirectUri)))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                return response.body();
            }
            handleUnexpectedStatus(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HubspotAdapterException(e.getMessage());
        }
        throw new HubspotAdapterException(ExceptionMessageEnum.ERROR_FETCHING_ACCESS_TOKEN_MESSAGE.getMessage());
    }
}
