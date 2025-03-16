package com.meetime.hubspot.infrastructure.utils;

import com.meetime.hubspot.infrastructure.http.exception.HubspotOutputAdapterRuntimeException;
import com.meetime.hubspot.infrastructure.http.model.ExceptionMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
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

    public static boolean isValidSignature(String signature, String payload, String clientSecret) {
        try {
            String expectedSignature = clientSecret.concat(payload);
            return generateSHA256Hash(expectedSignature).equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private static String generateSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
