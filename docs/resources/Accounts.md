## REST API

The accounts REST API supports the following operations.

- POST `/accounts` — Creates an account.

  - Request body: JSON with required fields like `name`, optional `tags`, etc.
  - Response: `201 Created` with created account resource.

- PATCH `/accounts/{id}` — Update an account

  - Request body: JSON with fields to update (e.g. `name`, `active`, `status`, `email`, `phone_number`, `country_code`, `tags`).
  - Response: `200 OK` with updated account resource.

- DELETE `/accounts/{id}` — Delete account

  - Behavior: soft delete (set `active = false`)
  - Response: `204 No Content`.

- PATCH `/accounts/{id}/restore` — Restore soft deleted account

  - Behavior: restores account (set `active = true`)
  - Response: `200 OK Content`.

- GET `/accounts/{id}` — Get account by id

  - Response: `200 OK` with account resource or `404 Not Found`.

- GET `/accounts` — List accounts with filtering, pagination and sorting
  - Query parameters:
    - `per_page` (integer) — page size, example `per_page=100`.
    - `last_row_id` (integer) — cursor for keyset pagination, example `last_row_id=10`.
    - `order_by` (string) — column to order by (default `id` or `created_at`).
    - `sort_order` (string) — `ASC` or `DESC` (default `ASC`).
    - Filters: filterable columns include `name`, `created_at`, `updated_at`, `active`.
      - Example: `?per_page=100&last_row_id=10&order_by=created_at&sort_order=ASC&name=acme`
  - Response: `200 OK` with an array of accounts and pagination metadata.
