package com.meetime.hubspot.application.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebhookResponse {
    private String response;
}
