# Tenants

This document describes the `tenants` resource: what a tenant is, the
identifiers used, constraints, and the database schema and the rest apis exposed to the clients.

## What is a tenant

A tenant represents an isolated customer or organization account. Tenants are
the highest-level unit of isolation in the system; other entities (users,
settings, api_keys) are scoped to a tenant. Tenants are intended to be
independent and non-overlapping.

## Identifiers

- Primary identifier: `id` (integer, `BIGSERIAL`) — internal primary key used by the
  database.
- External identifier: `external_id` (string/UUID) — optional stable external id.
- Human-friendly identifier: `name` — treated as unique and used for display.

## Constraints & conventions

- `name` must be unique, stored in lowercase, and should not contain leading or trailing whitespace.
- `email` and `phone_number` must be unique across tenants (enforced by unique indexes).
- If used, `external_id` must be unique.

## Database schema (canonical DDL — derived from migration)

```sql
CREATE TABLE IF NOT EXISTS tenants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    status VARCHAR(16) NOT NULL DEFAULT 'UNVERIFIED',
    email VARCHAR(64) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    country_code VARCHAR(3) NOT NULL DEFAULT 'IN',
    email_verified_at TIMESTAMPTZ,
    phone_number_verified_at TIMESTAMPTZ,
    external_id VARCHAR(40) DEFAULT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    request_id UUID NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    tags JSONB DEFAULT '{}'
);

-- Unique indexes
CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_email_unique ON tenants(email);
CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_phone_number_unique ON tenants(phone_number);
CREATE UNIQUE INDEX IF NOT EXISTS uc_tenants_name ON tenants(name);

-- Row-Level Security and policies are defined in the migration
-- See the canonical migration for the exact RLS definitions below.
```

Migration source: `auth-service/src/main/resources/db/migration/V1__create_tenants_table.sql`

## Rest Api Design

## REST API

The tenant REST API supports the following operations.

- POST `/tenants` — Create tenant

  - Request body: JSON with required fields like `name`, `email`, `phone_number`, optional `external_id`, `country_code`, `tags`, etc.
  - Response: `201 Created` with created tenant resource.

- PATCH `/tenants/{id}` — Update tenant

  - Request body: JSON with fields to update (e.g. `name`, `active`, `status`, `email`, `phone_number`, `country_code`, `tags`).
  - Response: `200 OK` with updated tenant resource.

- DELETE `/tenants/{id}` — Delete tenant

  - Behavior: soft delete (set `active = false`)
  - Response: `204 No Content`.

- PATCH `/tenants/{id}/restore` — Restore soft deleted tenant

  - Behavior: restores tenant (set `active = true`)
  - Response: `200 OK Content`.

- GET `/tenants/{id}` — Get tenant by id

  - Response: `200 OK` with tenant resource or `404 Not Found`.

- GET `/tenants` — List tenants with filtering, pagination and sorting
  - Query parameters:
    - `per_page` (integer) — page size, example `per_page=100`.
    - `last_row_id` (integer) — cursor for keyset pagination, example `last_row_id=10`.
    - `order_by` (string) — column to order by (default `id` or `created_at`).
    - `sort_order` (string) — `ASC` or `DESC` (default `ASC`).
    - Filters: filterable columns include `name`, `external_id`, `created_at`, `updated_at`, `active`.
      - Example: `?per_page=100&last_row_id=10&order_by=created_at&sort_order=ASC&name=acme`
  - Response: `200 OK` with an array of tenants and pagination metadata.

Notes:

- All endpoints operate under tenant isolation where applicable. For tenant management endpoints you may still require admin privileges; authorization checks should be enforced at the application layer as well as via DB policies.
- Use keyset pagination (`last_row_id`) for scalable paging. Avoid OFFSET for large tables.
- Ensure audit logging captures who performed each action (actor, role, request_id, timestamp, before/after values).
