package com.meetime.hubspot.domain.model;

import java.util.Map;

public record ContactCreationResponse(String createdAt,
                                      Boolean archived,
                                      String archivedAt,
                                      Map<String, Object> propertiesWithHistory,
                                      String id,
                                      Map<String, Object> properties,
                                      String updatedAt) {
}
