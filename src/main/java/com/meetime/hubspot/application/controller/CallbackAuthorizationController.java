package com.meetime.hubspot.application.controller;

import com.meetime.hubspot.domain.model.AccessToken;
import com.meetime.hubspot.domain.service.HubspotService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("meetime-hubspot/callback")
public class CallbackAuthorizationController {

    private final HubspotService hubspotService;

    public CallbackAuthorizationController(HubspotService hubspotService) {
        this.hubspotService = hubspotService;
    }

    @GetMapping
    public AccessToken createAccessToken(@RequestParam("code") String authorizationCode) throws IOException, InterruptedException {
        return hubspotService.getAccessToken(authorizationCode);
    }

}
