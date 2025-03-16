package com.meetime.hubspot.domain.model.contact;

public record ContactCreationRequest(
        String email,
        String firstname,
        String lastname,
        String phone
) {
}
