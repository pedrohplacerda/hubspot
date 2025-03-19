package com.meetime.hubspot.application.port.contact;

import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;

import java.io.IOException;

public interface HubspotContactPort {

    String createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, InterruptedException, HubspotAdapterException;

}
