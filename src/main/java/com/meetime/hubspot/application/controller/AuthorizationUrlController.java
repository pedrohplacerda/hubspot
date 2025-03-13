package com.meetime.hubspot.application.controller;

import com.meetime.hubspot.domain.model.AuthUrl;
import com.meetime.hubspot.domain.service.HubspotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/auth")
public class AuthorizationUrlController {

    private final HubspotService service;

    @GetMapping
    public AuthUrl authUrl() {
        return service.getAuthUrl();
    }
}
