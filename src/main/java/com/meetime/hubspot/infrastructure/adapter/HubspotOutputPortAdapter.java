package com.meetime.hubspot.infrastructure.adapter;

import com.meetime.hubspot.application.port.HubspotOutputPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.FORM_DATA;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class HubspotOutputPortAdapter implements HubspotOutputPort {

    private final String authUrl;
    private final String clientId;
    private final String clientScret;
    private final String redirectUri;
    private final String tokenUrl;
    private final HttpClient httpClient;

    public HubspotOutputPortAdapter(@Value("${hubspot.auth.url}") String authUrl,
                                    @Value("${hubspot.client.id}") String clientId,
                                    @Value("${hubspot.client.secret}") String clientScret,
                                    @Value("${hubspot.redirect.uri}") String redirectUri,
                                    @Value("${hubspot.token.url}") String tokenUrl,
                                    HttpClient httpClient
    ) {
        this.authUrl = authUrl;
        this.clientId = clientId;
        this.clientScret = clientScret;
        this.redirectUri = redirectUri;
        this.tokenUrl = tokenUrl;
        this.httpClient = httpClient;
    }

    @Override
    public String createAuthUrl() {
        return authUrl;
    }

    @Override
    public String generateAccessToken(String authorizationCode) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(getParams(authorizationCode)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpStatus.OK.value()) {
            return response.body();
        }
        throw new InterruptedIOException();
    }

    private String getParams(String authorizationCode) {
        return "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&client_secret=" + clientScret +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&code=" + authorizationCode;
    }
}
