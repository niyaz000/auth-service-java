package com.niyaz000.auth_service.exception;

import com.niyaz000.auth_service.entity.EntityType;

import lombok.Data;

@Data
public class DuplicateEntityException extends RuntimeException {

    private final EntityType entity;

    private final String field;

    private final String value;
}
