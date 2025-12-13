package com.niyaz000.auth_service.configuration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.niyaz000.auth_service.context.RequestContext;
import com.niyaz000.auth_service.context.RequestContextHolder;

import jakarta.validation.constraints.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TenantAwareDataSource extends AbstractRoutingDataSource {

    @Override
    public @NotNull Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        String lookupKey = (String) determineCurrentLookupKey();
        if (lookupKey != null) {
            setTenantOnConnection(connection);
        }
        return connection;
    }

    private void setTenantOnConnection(Connection conn) {
        RequestContext ctx = RequestContextHolder.getContext();
        String tenantId = String.valueOf(ctx.getTenant().getId());
        try (Statement st = conn.createStatement()) {
            st.execute("SET app.current_tenant_id = '" + tenantId + "'");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set app.current_tenant_id on connection", e);
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        if (!RequestContextHolder.isContextSet()) {
            return null;
        }
        return RequestContextHolder.getContext().getTenantId().toString();
    }

    @Override
    public String toString() {
        return determineTargetDataSource().toString();
    }
}
