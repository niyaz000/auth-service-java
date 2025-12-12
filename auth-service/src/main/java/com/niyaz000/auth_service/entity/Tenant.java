package com.niyaz000.auth_service.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.niyaz000.auth_service.enums.CountryCode;
import com.niyaz000.auth_service.enums.TenantStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenants")
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@DynamicUpdate
@SuperBuilder
public class Tenant extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private TenantStatus status;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "country_code", nullable = false)
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private CountryCode countryCode;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number_verified_at", nullable = true)
    private OffsetDateTime phoneNumberVerifiedAt;

    @Column(name = "email_verified_at", nullable = true)
    private OffsetDateTime emailVerifiedAt;
}
