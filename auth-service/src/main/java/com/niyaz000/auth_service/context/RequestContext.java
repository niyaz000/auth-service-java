package com.niyaz000.auth_service.context;

import java.util.Objects;

import com.niyaz000.auth_service.entity.Tenant;
import com.niyaz000.auth_service.entity.User;

import lombok.Getter;

@Getter
public class RequestContext {

    private final Tenant tenant;
    private final User user;

    public RequestContext(Tenant tenant, User user) {
        this.tenant = Objects.requireNonNull(tenant, "Tenant cannot be null");
        this.user = Objects.requireNonNull(user, "User cannot be null");
    }

    public Long getTenantId() {
        return tenant.getId();
    }

    public Long getUserId() {
        return user.getId();
    }

}
