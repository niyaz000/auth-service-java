package com.niyaz000.auth_service.entity;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public class ScopedEntity extends BaseEntity {

    @NotNull
    @Column(name = "tenant_id", nullable = true, updatable = false)
    private Long tenantId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> tags;
}
