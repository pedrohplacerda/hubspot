package com.meetime.hubspot.domain.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContactCreationWebhookRequest(
        @JsonProperty("objectId") Long contactId,
        @JsonProperty("propertyName") String property,
        @JsonProperty("propertyValue") String value,
        @JsonProperty("subscriptionType") String eventType
) {
}
