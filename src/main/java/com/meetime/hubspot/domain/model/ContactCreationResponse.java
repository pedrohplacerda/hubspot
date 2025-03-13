package com.meetime.hubspot.domain.model;

import lombok.Data;

@Data
public class ContactCreationResponse {
    private String createdAt;
    private Boolean archived;
    private String archivedAt;
    private Object propertiesWithHistory;
    private String id;
    private Object properties;
    private String updatedAt;
}
