package com.meetime.hubspot.application.controller.contact;

import com.meetime.hubspot.domain.model.contact.ContactCreationRequest;
import com.meetime.hubspot.domain.model.contact.ContactCreationResponse;
import com.meetime.hubspot.domain.service.contact.HubspotContactService;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/create/contact")
public class ContactController {

    private final HubspotContactService hubspotService;

    @PostMapping
    public ResponseEntity<ContactCreationResponse> createContact(@RequestBody ContactCreationRequest request, @RequestHeader String accessToken) throws IOException, HubspotAdapterException, InterruptedException {
        return ResponseEntity.ok(hubspotService.createContact(request, accessToken));
    }
}
