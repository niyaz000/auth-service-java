package com.niyaz000.auth_service.utils;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.MDC;

import com.niyaz000.auth_service.constants.LoggerConstants;

public final class LoggerUtil {

    public static void withMDC(Runnable runnable, String key, String value) {
        try {
            MDC.put(key, value);
            runnable.run();
        } finally {
            MDC.remove(key);
        }
    }

    public static String getRequestId(String headerValue) {
        return Objects.requireNonNullElse(headerValue, UUID.randomUUID().toString());
    }

    public static String currentRequestId() {
        return MDC.get(LoggerConstants.X_REQUEST_ID);
    }

    public static UUID currentRequestUuId() {
        return UUID.fromString(MDC.get(LoggerConstants.X_REQUEST_ID));
    }

    public static String currentOrNewRequestId() {
        return Objects.requireNonNullElse(MDC.get(LoggerConstants.X_REQUEST_ID), newRequestId());
    }

    public static String newRequestId() {
        return UUID.randomUUID().toString();
    }

    public static void runWithRequestId(Runnable runnable) {
        withMDC(runnable, LoggerConstants.X_REQUEST_ID, newRequestId());
    }

}
