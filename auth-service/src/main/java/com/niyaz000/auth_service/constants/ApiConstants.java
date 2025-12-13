package com.niyaz000.auth_service.constants;

import java.util.Map;

import com.niyaz000.auth_service.entity.EntityType;

public final class ApiConstants {

    public static final String API_V1 = "/v1/";

    public static final String TENANTS = "tenants";

    public static final String USERS = "users";

    public static final String DUPLICATE_VALIDATION_ERROR = "https://api.user-service.com/errors#duplicate-entity";

    public static final String ENTITY_NOT_FOUND_ERROR = "https://api.user-service.com/errors#entity-not-found";

    public static final String INTERNAL_SERVER_ERROR = "https://api.user-service.com/errors#internal-server-error";

    public static final String VALIDATION_ERROR = "https://api.user-service.com/errors#validation-error";

    public static final String MISSING_HEADER = "https://api.user-service.com/errors#missing-header";

    public static final String INVALID_HEADER = "https://api.user-service.com/errors#invalid-header";

    public static final Map<EntityType, String> ENTITY_API_PATHS = Map.of(
            EntityType.TENANTS, "/tenants",
            EntityType.USERS, "/users");
}
