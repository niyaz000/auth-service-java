package com.niyaz000.auth_service.context;

public class RequestContextHolder {

    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();

    public static void setContext(RequestContext context) {
        contextHolder.set(context);
    }

    public static RequestContext getContext() {
        if (contextHolder.get() == null) {
            throw new IllegalStateException("RequestContext is not set");
        }
        return contextHolder.get();
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
