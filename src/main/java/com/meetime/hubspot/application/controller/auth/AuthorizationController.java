package com.meetime.hubspot.application.controller.auth;

import com.meetime.hubspot.domain.model.auth.AuthUrl;
import com.meetime.hubspot.domain.service.auth.HubspotAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetime-hubspot/auth")
public class AuthorizationController {

    private final HubspotAuthService service;

    @GetMapping
    public AuthUrl authUrl() {
        return service.getAuthUrl();
    }
}
