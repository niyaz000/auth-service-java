# auth-service-java

User authentication and authorization service for cloud and on-prem use case. Simple yet diverse.

**Database Design Rules & Practices**

- **Naming conventions**: all table names are plural and use snake_case (underscore separated) for multi-word names, e.g. `user_accounts`, `tenant_settings`.
- **Primary keys**: all primary keys are integers (use `BIGSERIAL`/`BIGINT` for auto-increment and capacity).
- **No hard deletes**: prefer soft deletes (via. `active` flag). Application-level logic and DB policies should prevent hard deletes.
- **Versioning**: use `version` column for optimistic locking.
- **Timestamps**: include `created_at` and `updated_at` (TIMESTAMPTZ) with `NOW()` defaults and all timestamps are kept in UTC.
- **Indexing & uniqueness**: enforce uniqueness via unique indexes (example: email, phone_number, name for tenants).
- **Auditing**: audit all user actions (INSERT/UPDATE/DELETE). Audits are captured via `envers`. modifying user is captured in the table via `created_by` and `updated_by` columns.
- **Traceability**- `request_id` column captures the app trace_id / request_id which made the last update to the row. This helps in quickly seeing which action caused the last table update.
- **Extensibility** - All tables have `tags` column which can store key value pairs to store arbitrary information.

## Naming Conventions

- all table names are plural and use snake_case (underscore separated) for multi-word names, e.g. `user_accounts`, `tenant_settings`
- all column names should be in snake_case (underscore separated) for multi-word names, e.g `created_by`
- all unique indexes must be in format `uc_table_name_col_name` e.g : `uc_tenants_external_id`
- **Database Security Best Practices**

- **Least privilege**: application DB role have the least previlage required.
- **No hard deletes for `app_user`**: prevent destructive actions by restricting DELETE permissions (example: via RLS policy or specific role checks) for the `app_user` role.
- **RLS for tenant isolation**: enable Row-Level Security (RLS) for tenant-scoped tables and use connection-local GUCs (like `app.current_tenant_id`) to scope queries per-tenant.
- **Audit on user actions**: ensure all user actions are captured by triggers or middleware and stored in an `audit_log` (actor, role, action, target_table, target_id, old_values, new_values, timestamp, request_id).
- **Secure configuration**: use parameterized queries, encrypt sensitive connection strings, and rotate credentials regularly.

**Tenant Isolation at Database Level (RLS) — explanation**

- Enable RLS on tenant-scoped tables, then create policies that:

  - Restrict `SELECT`/`UPDATE/DELETE` using `USING ( ... )` conditions against `current_setting('app.current_tenant_id', ...)`.
  - Use `WITH CHECK` to validate `INSERT`/`UPDATE/DELETE` new-row values when you want to ensure rows belong to the configured tenant.
  - Optionally separate INSERT policy (if inserts should not be tenant-scoped) or add triggers to set tenant id on insert.
  - `BYPASSRLS` and superusers bypass policies — this is kept for manual deletion by developer or support.

  Example runtime usage:

```sql
-- set tenant per-connection (session or transaction-local)
SET LOCAL app.current_tenant_id = '123';

-- then application queries operate under that tenant's scope
SELECT * FROM tenants;
```

For the tenants table and exact migration SQL see the Tenants section below and the migration file `auth-service/src/main/resources/db/migration/V1__create_tenants_table.sql`.

**Database Schema**

- **Table**: `tenants` — tenant directory and metadata.

- **Columns**: `id` (BIGSERIAL PK), `name` (VARCHAR), `active` (BOOLEAN), `status` (VARCHAR), `created_at`/`updated_at` (TIMESTAMPTZ), `created_by`/`updated_by` (BIGINT), `request_id` (UUID), `version` (BIGINT), `email` (VARCHAR), `phone_number` (VARCHAR), `country_code` (VARCHAR), `email_verified_at`/`phone_number_verified_at` (TIMESTAMPTZ).

- **Indexes**: `uc_tenants_email_unique` (`email`), `uc_tenants_phone_number_unique` (`phone_number`), `uc_tenants_name` (`name`).

- **Row-Level Security (RLS)**: enabled on the `tenants` table via the migration `auth-service/src/main/resources/db/migration/V1__create_tenants_table.sql`.

- **RLS policies (summary)**:

  - `tenant_isolation_policy` — FOR `SELECT, UPDATE`: restricts visible/affectable rows to those where `id = current_setting('app.current_tenant_id', false)::BIGINT`.
  - `tenant_delete_policy` — FOR `DELETE`: allows deletes only when `current_user <> 'app_user'` (effectively prevents role `app_user` from deleting rows).
  - `tenant_allow_insert_policy` — FOR `INSERT`: allows inserts (no tenant-scoping applied).

- **Notes**:
  - The migration uses `current_setting('app.current_tenant_id', false)` which will raise an error if the GUC is not set; change the `false` to `true` in the SQL to make it permissive (`missing_ok`).
  - Superusers or roles with `BYPASSRLS` bypass these policies — ensure your application DB role does not have bypass privileges.
  - To scope requests at runtime, set the per-connection GUC, e.g.:

```sql
SET session app.current_tenant_id = '123';
-- or inside a transaction:
SET LOCAL app.current_tenant_id = '123';
```

For the exact migration SQL, see `auth-service/src/main/resources/db/migration/V1__create_tenants_table.sql`.

# Tenants

Tenants represent isolated customer or organization accounts that own and operate a subset
of the application's data. Each tenant groups users, settings, and resources so the
application can enforce isolation and multi-tenancy rules.

- **Primary identifier**: `id` (BIGSERIAL PRIMARY KEY) — the unique internal identifier for each tenant stored in the `tenants` table.
- **External identifiers**: you may also use `request_id` (UUID) or any other GUID issued by upstream systems as a globally-unique identifier; if you need a stable external tenant id use a dedicated `tenant_external_id` column.

- **Important uniqueness rules**:

  - `email` and `phone_number` are enforced unique via indexes `uc_tenants_email_unique` and `uc_tenants_phone_number_unique`.
  - `name` is also unique via `uc_tenants_name`.

- **Schema (summary)**:

  - `id` BIGSERIAL PRIMARY KEY
  - `name` VARCHAR(64) NOT NULL
  - `active` BOOLEAN NOT NULL DEFAULT true
  - `status` VARCHAR(16) NOT NULL DEFAULT 'UNVERIFIED'
  - `created_at` / `updated_at` TIMESTAMPTZ NOT NULL DEFAULT NOW()
  - `created_by` / `updated_by` BIGINT NOT NULL
  - `request_id` UUID NOT NULL
  - `version` BIGINT NOT NULL DEFAULT 0
  - `email` VARCHAR(64) NOT NULL
  - `phone_number` VARCHAR(10) NOT NULL
  - `country_code` VARCHAR(5) NOT NULL DEFAULT 'IN'
  - `email_verified_at` / `phone_number_verified_at` TIMESTAMPTZ

- **RLS & runtime behavior**:
  - Row-Level Security is enabled on `tenants`; application requests are scoped by the
    `app.current_tenant_id` setting. The migrations define policies that restrict
    SELECT/UPDATE to the configured tenant and allow INSERTs; a delete policy was
    added to prevent role `app_user` from performing deletes.
  - To ensure queries are scoped properly, set the connection's tenant GUC before
    executing user-scoped queries (e.g. `SET LOCAL app.current_tenant_id = '123';`).

If you'd like, I can add an example of application code that sets the GUC on each
connection (Spring Boot / HikariCP example), or add a `tenant_external_id` column
and a migration to populate it.

Below is the exact `tenants` table and related DDL (copied from the migration):

```sql
CREATE TABLE IF NOT EXISTS tenants (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT true,
  status VARCHAR(16) NOT NULL DEFAULT 'UNVERIFIED',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by BIGINT NOT NULL,
  updated_by BIGINT NOT NULL,
  request_id UUID NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  email VARCHAR(64) NOT NULL,
  phone_number VARCHAR(10) NOT NULL,
  country_code VARCHAR(5) NOT NULL DEFAULT 'IN',
  email_verified_at TIMESTAMPTZ,
  phone_number_verified_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_email_unique ON tenants(email);

CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_phone_number_unique ON tenants(phone_number);

CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_name ON tenants(name);

-- Enable Row Level Security
ALTER TABLE
  tenants ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS tenant_isolation_policy ON tenants;

DROP POLICY IF EXISTS tenant_delete_policy ON tenants;

DROP POLICY IF EXISTS tenant_allow_insert_policy ON tenants;

CREATE POLICY tenant_isolation_policy ON tenants FOR
SELECT
,
UPDATE
  USING (
    id = current_setting('app.current_tenant_id', false) :: BIGINT
  ) WITH CHECK (
    id = current_setting('app.current_tenant_id', false) :: BIGINT
  );

-- Block deletes for role `app_user` by making DELETE require that
-- the current user is NOT `app_user`. If `current_user = 'app_user'`, no
-- DELETE policy will allow the operation and the DELETE will be denied.
CREATE POLICY tenant_delete_policy ON tenants FOR DELETE USING (
  id = current_setting('app.current_tenant_id', false) :: BIGINT
  AND current_user <> 'app_user'
);

CREATE POLICY tenant_allow_insert_policy ON tenants FOR
INSERT
  WITH CHECK (true);
```

Migration source: `auth-service/src/main/resources/db/migration/V1__create_tenants_table.sql`.

# auth-service-java

User authentication and authorization service for cloud and on-prem use case. Simple yet diverse.
