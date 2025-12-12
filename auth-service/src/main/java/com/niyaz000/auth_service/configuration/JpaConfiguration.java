package com.niyaz000.auth_service.configuration;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

import com.niyaz000.auth_service.audit.EntityAuditor;

@Configuration
public class JpaConfiguration {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
    }

    @Bean
    public AuditorAware<Long> entityAuditor() {
        return new EntityAuditor();
    }

}
