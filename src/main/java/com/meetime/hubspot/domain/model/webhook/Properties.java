package com.meetime.hubspot.domain.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Properties(String email,
                         String lastname,
                         String firstname,
                         @JsonProperty("createdate")
                         OffsetDateTime creationDate) {
}
