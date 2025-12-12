package com.niyaz000.auth_service.exception;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApiErrorResponse {

    private String type;

    private String title;

    private int status;

    private String detail;

    private String instance;

    @JsonProperty("request_id")
    private String requestId;

    private Date timestamp;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FieldError> errors;

    public static record FieldError(String field, String value, String type, String message) {

    }
}
