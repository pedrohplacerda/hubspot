package com.meetime.hubspot.domain.model.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record Properties(String email,
                         String lastname,
                         String firstname,
                         @JsonProperty("createdate")
                         OffsetDateTime creationDate,
                         String hs_object_id,
                         @JsonProperty("lastmodifieddate")
                         OffsetDateTime lastModifiedDate) {
}
