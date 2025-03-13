package com.meetime.hubspot.application.controller;

import com.meetime.hubspot.domain.model.ContactCreationRequest;
import com.meetime.hubspot.domain.model.ContactCreationResponse;
import com.meetime.hubspot.domain.service.HubspotService;
import com.meetime.hubspot.infrastructure.exception.HubspotOutputAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/create/contact")
public class CreateContactController {

    private final HubspotService hubspotService;

    @PostMapping
    public ResponseEntity<ContactCreationResponse> createContact(@RequestBody ContactCreationRequest request, @RequestHeader String accessToken) throws IOException, HubspotOutputAdapterException {
        return ResponseEntity.ok(hubspotService.createContact(request, accessToken));
    }
}
