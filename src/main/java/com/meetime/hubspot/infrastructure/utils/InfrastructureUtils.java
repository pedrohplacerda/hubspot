package com.meetime.hubspot.infrastructure.utils;

import com.meetime.hubspot.infrastructure.exception.HubspotOutputAdapterRuntimeException;
import com.meetime.hubspot.infrastructure.model.ExceptionMessageEnum;
import org.springframework.http.HttpStatus;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class InfrastructureUtils {
    public static String getParams(String authorizationCode, String clientId, String clientSecret, String redirectUri) {
        return "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&code=" + authorizationCode;
    }

    public static void handleUnexpectedStatus(HttpResponse<?> response) {
        var statusCode = HttpStatus.valueOf(response.statusCode());
        String exceptionMessage = switch (statusCode) {
            case HttpStatus.BAD_REQUEST -> ExceptionMessageEnum.BAD_REQUEST_MESSAGE.getMessage();
            case HttpStatus.FORBIDDEN -> ExceptionMessageEnum.FORBIDDEN_MESSAGE.getMessage();
            case HttpStatus.UNAUTHORIZED -> ExceptionMessageEnum.UNAUTHORIZED_MESSAGE.getMessage();
            case HttpStatus.INTERNAL_SERVER_ERROR -> ExceptionMessageEnum.INTERNAL_SERVER_ERROR_MESSAGE.getMessage();
            default -> String.format(ExceptionMessageEnum.UNEXPECTED_STATUS_MESSAGE.getMessage(), statusCode.value());
        };
        throw new HubspotOutputAdapterRuntimeException(exceptionMessage);
    }
}
