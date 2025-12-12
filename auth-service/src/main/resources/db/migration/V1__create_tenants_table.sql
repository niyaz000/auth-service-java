CREATE TABLE IF NOT EXISTS tenants (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(17) NOT NULL,
    name VARCHAR(64) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'UNVERIFIED',
    email VARCHAR(64) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    country_code VARCHAR(3) NOT NULL DEFAULT 'IN',
    email_verified_at TIMESTAMPTZ,
    phone_number_verified_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    request_id UUID NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    tags JSONB DEFAULT '{}'
);

CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_email_unique ON tenants(email);

CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_phone_number_unique ON tenants(phone_number);

CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_name ON tenants(name);

CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_public_id ON tenants(public_id);

-- Enable Row Level Security
ALTER TABLE
    tenants ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS tenant_isolation_read_policy ON tenants;

DROP POLICY IF EXISTS tenant_isolation_update_policy ON tenants;

DROP POLICY IF EXISTS tenant_delete_policy ON tenants;

DROP POLICY IF EXISTS tenant_allow_insert_policy ON tenants;

CREATE POLICY tenant_isolation_read_policy
ON tenants
FOR SELECT
USING (
    id = current_setting('app.current_tenant_id', false)::BIGINT
);

CREATE POLICY tenant_isolation_update_policy
ON tenants
FOR UPDATE
USING (
    id = current_setting('app.current_tenant_id', false)::BIGINT
)
WITH CHECK (
    id = current_setting('app.current_tenant_id', false)::BIGINT
);


CREATE POLICY tenant_delete_policy ON tenants FOR DELETE USING (
    id = current_setting('app.current_tenant_id', false) :: BIGINT
    AND current_user = 'admin_user'
);

CREATE POLICY tenant_allow_insert_policy ON tenants FOR
INSERT
    WITH CHECK (true);