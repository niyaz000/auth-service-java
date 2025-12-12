CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(17) NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    first_name VARCHAR(64) NOT NULL,
    last_name VARCHAR(64) NOT NULL,
    profile_picture_uri VARCHAR(128) DEFAULT NULL,
    activated_at TIMESTAMPTZ DEFAULT NULL,
    email VARCHAR(64),
    email_verified_at TIMESTAMPTZ DEFAULT NULL,
    phone_number VARCHAR(10),
    phone_number_verified_at TIMESTAMPTZ DEFAULT NULL,
    status VARCHAR(32) DEFAULT 'PROVISIONED',
    external_id VARCHAR(40) DEFAULT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_by BIGINT NOT NULL REFERENCES users(id),
    request_id UUID NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    tags JSONB DEFAULT '{}' NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uc_users_tenant_id_email ON users(tenant_id, email);

CREATE UNIQUE INDEX IF NOT EXISTS uc_users_tenant_id_phone_number ON users(tenant_id, phone_number);

CREATE UNIQUE INDEX IF NOT EXISTS uc_users_public_id ON users(public_id);

ALTER TABLE
    users ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS users_isolation_read_policy ON users;

DROP POLICY IF EXISTS users_isolation_update_policy ON users;

DROP POLICY IF EXISTS users_delete_policy ON users;

DROP POLICY IF EXISTS users_allow_insert_policy ON users;

CREATE POLICY users_isolation_read_policy ON users FOR
SELECT
    USING (
        tenant_id = current_setting('app.current_tenant_id', false) :: BIGINT
    );

CREATE POLICY users_isolation_update_policy ON users FOR
UPDATE
    USING (
        tenant_id = current_setting('app.current_tenant_id', false) :: BIGINT
    ) WITH CHECK (
        tenant_id = current_setting('app.current_tenant_id', false) :: BIGINT
    );

CREATE POLICY users_delete_policy ON users FOR DELETE USING (
    tenant_id = current_setting('app.current_tenant_id', false) :: BIGINT
    AND current_user = 'admin_user'
);

CREATE POLICY users_allow_insert_policy ON users FOR
INSERT
    WITH CHECK (
        tenant_id = current_setting('app.current_tenant_id', false) :: BIGINT
    );