package com.meetime.hubspot.application.port.webhook;

import com.meetime.hubspot.domain.model.webhook.Contact;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;

public interface HubspotWebkhookPort {

    Contact processContactEventAsync(String signature, String payload) throws HubspotAdapterException;

}
