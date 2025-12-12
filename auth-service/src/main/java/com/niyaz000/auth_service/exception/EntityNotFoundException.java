package com.niyaz000.auth_service.exception;

import com.niyaz000.auth_service.entity.EntityType;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private EntityType entityName;

    private String identifierName;

    private String identifierValue;

    public EntityNotFoundException(EntityType entityName, String identifierName, String identifierValue) {
        super(String.format("%s not found with %s: %s", entityName, identifierName, identifierValue));
        this.entityName = entityName;
        this.identifierName = identifierName;
        this.identifierValue = identifierValue;
    }
}