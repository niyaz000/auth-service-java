package com.niyaz000.auth_service.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import com.niyaz000.auth_service.context.RequestContextHolder;

public class EntityAuditor implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        if (!RequestContextHolder.isContextSet()) {
            return Optional.empty();
        }
        return Optional.of(RequestContextHolder.getContext().getUserId());
    }

}