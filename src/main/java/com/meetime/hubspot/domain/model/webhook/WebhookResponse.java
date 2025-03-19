package com.meetime.hubspot.domain.model.webhook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebhookResponse {
    private String response;
    private String createdContact;
}
