package com.meetime.hubspot.domain.model;

public record ContactCreationRequest(
        String email,
        String firstname,
        String lastname,
        String phone
) {}
