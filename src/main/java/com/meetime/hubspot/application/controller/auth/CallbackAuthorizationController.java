package com.meetime.hubspot.application.controller.auth;

import com.meetime.hubspot.domain.model.auth.AccessToken;
import com.meetime.hubspot.domain.service.auth.HubspotAuthService;
import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterException;
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

    private final HubspotAuthService hubspotService;

    @GetMapping
    public ResponseEntity<AccessToken> createAccessToken(@RequestParam("code") String authorizationCode) throws IOException, HubspotAdapterException, InterruptedException {
        return ResponseEntity.ok(hubspotService.getAccessToken(authorizationCode));
    }

}
