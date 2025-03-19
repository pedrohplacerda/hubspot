package com.meetime.hubspot.domain.service.webhook;

import com.meetime.hubspot.application.port.webhook.HubspotWebkhookPort;
import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubspotWebhookService {

    private final HubspotWebkhookPort webhookPort;

    public Contact processWebhook(String signature, String payload) throws HubspotAdapterException {
        return webhookPort.processContactEventAsync(signature, payload);
    }

}
