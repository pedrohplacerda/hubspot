package com.meetime.hubspot.application.controller;

import com.meetime.hubspot.application.model.WebhookResponse;
import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.domain.service.HubspotService;
import com.meetime.hubspot.infrastructure.http.exception.HubspotOutputAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/webhooks")
public class WebhookController {

    private final HubspotService hubspotService;

    @PostMapping("/contacts")
    public ResponseEntity<WebhookResponse> createContact(@RequestHeader("X-HubSpot-Signature") String signature, @RequestBody String payload) throws HubspotOutputAdapterException {
        Contact contact = hubspotService.processWebhook(signature, payload);
        return ResponseEntity.ok(WebhookResponse.builder()
                .response("Webhook successfully processed.")
                .createdContact(contact.id())
                .build());
    }
}
