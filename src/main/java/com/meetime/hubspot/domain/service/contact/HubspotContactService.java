package com.meetime.hubspot.domain.service.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetime.hubspot.application.port.contact.HubspotContactPort;
import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.domain.model.contact.ContactCreationResponse;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class HubspotContactService {

    private final HubspotContactPort contactPort;
    private final ObjectMapper objectMapper;

    public ContactCreationResponse createContact(ContactCreationRequest contactCreationRequest, String accessToken) throws IOException, HubspotAdapterException, InterruptedException {
        return objectMapper.readValue(contactPort.createContact(contactCreationRequest, accessToken), ContactCreationResponse.class);
    }


}
