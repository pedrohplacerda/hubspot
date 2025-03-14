package com.meetime.hubspot.domain.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessToken(@JsonProperty("refresh_token") String refreshToken,
                          @JsonProperty("access_token") String accessToken,
                          @JsonProperty("expires_in") Integer expiresIn,
                          @JsonProperty("token_type") String tokenType) {
}
