package com.meetime.hubspot.application.controller.webhook;

import com.meetime.hubspot.domain.model.webhook.WebhookResponse;
import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.domain.service.webhook.HubspotWebhookService;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.meetime.hubspot.infrastructure.constants.InfrastructureConstants.X_HUB_SPOT_SIGNATURE;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/webhooks")
public class WebhookController {

    private final HubspotWebhookService hubspotService;

    @PostMapping("/contacts")
    public ResponseEntity<WebhookResponse> createContact(@RequestHeader(X_HUB_SPOT_SIGNATURE) String signature, @RequestBody String payload) throws HubspotAdapterException {
        Contact contact = hubspotService.processWebhook(signature, payload);
        return ResponseEntity.ok(WebhookResponse.builder()
                .response("Webhook successfully processed.")
                .createdContact(contact.id())
                .build());
    }
}
