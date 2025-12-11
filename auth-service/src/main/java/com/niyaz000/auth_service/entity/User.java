package com.niyaz000.auth_service.entity;

import java.time.OffsetDateTime;

import com.niyaz000.auth_service.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "users")
@Entity
public class User extends ScopedEntity {
    /*
     * 
     * CREATE TABLE IF NOT EXISTS users (
     * id SERIAL PRIMARY KEY,
     * public_id VARCHAR(15) NOT NULL,
     * 
     * 
     * status VARCHAR(32) DEFAULT 'PROVISIONED',
     * 
     * 
     * deleted_at TIMESTAMPTZ DEFAULT NULL,
     * created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     * updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     * created_by BIGINT NOT NULL REFERENCES users(id),
     * updated_by BIGINT NOT NULL REFERENCES users(id),
     * request_id UUID NOT NULL,
     * version BIGINT NOT NULL DEFAULT 0
     * );
     */

    @Column(name = "first_name", nullable = false, length = 64)
    @NotBlank
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 64)
    @NotBlank
    private String lastName;

    @Column(name = "email", nullable = false, length = 64, unique = true)
    @NotBlank
    private String email;

    @Column(name = "profile_picture_uri", nullable = true, length = 256)
    private String profilePictureUri;

    @Column()
    private OffsetDateTime activatedAt;

    @Column(name = "email_verified_at", nullable = true)
    private OffsetDateTime emailVerifiedAt;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "phone_number_verified_at", nullable = true)
    private OffsetDateTime phoneNumberVerifiedAt;

    @Column(name = "external_id", nullable = true, length = 40)
    private String externalId;

    @Column(name = "status", nullable = false, length = 32)
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private UserStatus status;
}
