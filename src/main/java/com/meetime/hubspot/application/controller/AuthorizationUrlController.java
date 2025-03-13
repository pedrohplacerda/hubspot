package com.meetime.hubspot.application.controller;

import com.meetime.hubspot.domain.model.AuthUrl;
import com.meetime.hubspot.domain.service.HubspotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("meetime-hubspot/auth")
public class AuthorizationUrlController {

    private final HubspotService service;

    public AuthorizationUrlController(HubspotService service) {
        this.service = service;
    }

    @GetMapping
    public AuthUrl authUrl() {
        return service.getAuthUrl();
    }

}
