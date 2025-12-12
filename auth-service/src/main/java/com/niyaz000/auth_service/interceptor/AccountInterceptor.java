package com.niyaz000.auth_service.interceptor;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.niyaz000.auth_service.constants.ApiConstants;
import com.niyaz000.auth_service.constants.LoggerConstants;
import com.niyaz000.auth_service.context.RequestContext;
import com.niyaz000.auth_service.context.RequestContextHolder;
import com.niyaz000.auth_service.entity.Tenant;
import com.niyaz000.auth_service.entity.User;
import com.niyaz000.auth_service.exception.ApiErrorResponse.FieldError;
import com.niyaz000.auth_service.repository.TenantRepository;
import com.niyaz000.auth_service.repository.UserRepository;
import com.niyaz000.auth_service.utils.ResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountInterceptor implements HandlerInterceptor {

    private final TenantRepository tenantRepository;

    private final UserRepository userRepository;

    private final ResponseUtil responseUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        RequestContextHolder.clearContext();
        if ("POST".equals(request.getMethod()) && request.getContextPath().contains("/tenants")) {
            return true;
        }

        var tenant = validateAndGetTenant(request, response);
        if (tenant.isEmpty()) {
            return false;
        }
        var user = validateAndGetUser(tenant.get(), request, response);
        if (user.isEmpty()) {
            return false;
        }
        RequestContextHolder.setContext(new RequestContext(tenant.get(), user.get()));
        return true;
    }

    private Optional<User> validateAndGetUser(
            Tenant tenant,
            HttpServletRequest request,
            HttpServletResponse response) {
        var userIdHeader = Objects.requireNonNullElse(request.getHeader(LoggerConstants.X_USER_ID), "");
        if (userIdHeader.isBlank()) {
            var error = new FieldError(LoggerConstants.X_USER_ID, userIdHeader, ApiConstants.MISSING_HEADER,
                    "User-ID header is required.");
            responseUtil.handleValidationError(request, error, response);
            return Optional.empty();
        }
        String userId = userIdHeader;
        var user = userRepository.findByPublicId(userId);
        if (user.isEmpty()) {
            var error = new FieldError(LoggerConstants.X_USER_ID, userIdHeader, "not_found",
                    "User with given id does not exists.");
            responseUtil.handleNotFoundError(request, error, response);
            return Optional.empty();
        }
        if (!user.get().isActive()) {
            var error = new FieldError(LoggerConstants.X_USER_ID, userIdHeader, "deleted_user",
                    "The user associated with the given User-ID has been deleted.");
            responseUtil.handleValidationError(request, error, response);
            return Optional.empty();
        }
        if (user.get().getTenantId().longValue() != tenant.getId().longValue()) {
            var error = new FieldError(LoggerConstants.X_USER_ID, userIdHeader, "user_tenant_mismatch",
                    "The user does not belong to the specified tenant.");
            responseUtil.handleValidationError(request, error, response);
            return Optional.empty();
        }
        return Optional.of(user.get());
    }

    private Optional<Tenant> validateAndGetTenant(HttpServletRequest request, HttpServletResponse response) {
        var tenantIdHeader = Objects.requireNonNullElse(request.getHeader(LoggerConstants.X_TENANT_ID), "");
        if (tenantIdHeader.isBlank()) {
            var error = new FieldError(LoggerConstants.X_TENANT_ID, tenantIdHeader, ApiConstants.MISSING_HEADER,
                    "Tenant-ID header is required.");
            responseUtil.handleValidationError(request, error, response);
            return Optional.empty();
        }
        String tenantId = tenantIdHeader;
        var tenant = tenantRepository.findByPublicId(tenantId);
        if (tenant.isEmpty()) {
            var error = new FieldError(LoggerConstants.X_TENANT_ID, tenantIdHeader, "not_found",
                    "Tenant with given id does not exists.");
            responseUtil.handleNotFoundError(request, error, response);
            return Optional.empty();
        }
        if (!tenant.get().isActive()) {
            var error = new FieldError(LoggerConstants.X_TENANT_ID, tenantIdHeader, "deleted_tenant",
                    "The tenant associated with the given X-Tenant-ID has been deleted.");
            responseUtil.handleValidationError(request, error, response);
            return Optional.empty();
        }
        return Optional.of(tenant.get());
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            org.springframework.web.servlet.ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        RequestContextHolder.clearContext();
    }
}
