package com.meetime.hubspot.application.controller;

import com.meetime.hubspot.domain.model.AccessToken;
import com.meetime.hubspot.domain.service.HubspotService;
import com.meetime.hubspot.infrastructure.exception.HubspotOutputAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/callback")
public class CallbackAuthorizationController {

    private final HubspotService hubspotService;

    @GetMapping
    public ResponseEntity<AccessToken> createAccessToken(@RequestParam("code") String authorizationCode) throws IOException, HubspotOutputAdapterException {
        return ResponseEntity.ok(hubspotService.getAccessToken(authorizationCode));
    }

}
