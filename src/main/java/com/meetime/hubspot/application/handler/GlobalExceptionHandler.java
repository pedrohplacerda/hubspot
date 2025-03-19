package com.meetime.hubspot.application.handler;

import com.meetime.hubspot.infrastructure.http.exception.HubspotAdapterRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HubspotAdapterRuntimeException.class)
    public ResponseEntity<String> handleHubspotOutputAdapterException(HubspotAdapterRuntimeException e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
