package com.niyaz000.auth_service.utils;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String toJsonString(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }
}
