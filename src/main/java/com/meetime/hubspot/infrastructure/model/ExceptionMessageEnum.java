package com.meetime.hubspot.infrastructure.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessageEnum {
    BAD_REQUEST_MESSAGE("Bad request."),
    FORBIDDEN_MESSAGE("Not allowed to access the resource."),
    UNAUTHORIZED_MESSAGE("Not authorized."),
    INTERNAL_SERVER_ERROR_MESSAGE("Internal server error."),
    UNEXPECTED_STATUS_MESSAGE("Unexpected status: %s."),
    ERROR_FETCHING_ACCESS_TOKEN_MESSAGE("Error while fetching access token."),
    RATE_LIMIT_EXCEPTION_MESSAGE("Rate limit exceeded. Try again later."),
    ERROR_CREATING_CONTACT("An error occurred while creating contact.");

    private final String message;
}
