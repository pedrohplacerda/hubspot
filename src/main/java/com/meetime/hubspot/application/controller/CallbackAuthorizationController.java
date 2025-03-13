package com.meetime.hubspot.application.controller;

import com.meetime.hubspot.domain.model.AccessToken;
import com.meetime.hubspot.domain.service.HubspotService;
import com.meetime.hubspot.infrastructure.exception.HubspotOutputAdapterException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/callback")
public class CallbackAuthorizationController {

    private final HubspotService hubspotService;

    @GetMapping
    public AccessToken createAccessToken(@RequestParam("code") String authorizationCode) throws IOException, HubspotOutputAdapterException {
        return hubspotService.getAccessToken(authorizationCode);
    }

}
