package com.meetime.hubspot.domain.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Contact(String id, Properties properties) {
}
