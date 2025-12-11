package com.niyaz000.auth_service.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.niyaz000.auth_service.constants.PublicIdConstants;
import com.niyaz000.auth_service.utils.LoggerUtil;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false, length = 16)
    @NotNull
    private String publicId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    @CreatedBy
    private Long createdBy;

    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    private Long updatedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "deleted_at", nullable = true)
    private OffsetDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        requestId = LoggerUtil.currentRequestUuId();
        publicId = NanoIdUtils.randomNanoId(
                PublicIdConstants.DEFAULT_NUMBER_GENERATOR, PublicIdConstants.DEFAULT_ALPHABET,
                PublicIdConstants.DEFAULT_SIZE);
    }

    @PreUpdate
    protected void onUpdate() {
        requestId = LoggerUtil.currentRequestUuId();
    }

}
