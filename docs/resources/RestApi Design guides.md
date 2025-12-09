# REST API Design Guides

This document captures API design rules, conventions, and examples used by this
project. It is intended as a concise reference for implementers and reviewers.
For full API details, refer to the OpenAPI/Swagger specification.

## Purpose & Scope

- Document common API patterns, conventions, response formats, and error handling.

## Content Type (Request Body)

- All APIs accept and return JSON (`application/json`) unless explicitly noted.

## Versioning

- Expose API version in the URL, for example: `/v1/tenants`.

## Naming & Resource Conventions

- Resource names are plural and use `kebab-case` when multi-word, e.g. `/tenants`, `/api-keys`.
- Use nouns for resources (avoid verbs in path names).
- Use path parameters for resource identity: `/tenants/{id}`.
- Avoid deep nesting; prefer at most two levels of nesting when necessary.

## HTTP Methods Mapping

Use the standard HTTP verbs with clear semantics:

- POST — Create a resource. Returns `201 Created` with a `Location` header for the created resource. Support idempotency for retries (see below).
- GET — Retrieve resource(s). `GET` is safe and idempotent and must not have side effects.
  - `GET /tenants` — list
  - `GET /tenants/{id}` — retrieve single
  - `200 OK` for success, `404 Not Found` when a resource does not exist or is inaccessible.
- PATCH — Partial update. Returns `200 OK` with the updated resource or `404` if not found.
- PUT — Replace resource (use sparingly). Returns `200 OK` or `404` if not found.
- DELETE — Logical delete (soft-delete).

Behavioral notes:

- POST should return `201` on create. If the client provides an idempotency key and the resource already exists, return the existing resource with `200 OK`.
- DELETE should be idempotent: deleting an already-deleted resource should still return a successful status (e.g. `204 No Content` or `200 OK`).

## Pagination

- Prefer keyset pagination for scale. Support `last_row_id` (cursor) and `per_page` (page size).
- For the first page `last_row_id` may be omitted or set to `0`.
- Enforce server-side bounds on `per_page` (e.g. max 100).

Example paginated response:

```json
{
  "data": [],
  "meta": {
    "last_row_id": 10,
    "has_more": true,
    "per_page": 100
  }
}
```

## Filtering & Sorting

- Only allow filtering on a small, documented set of columns to avoid expensive scans.
- String filters commonly support "starts with" semantics.
- Date/time filters should support range operators (`>`, `>=`, `<`, `<=`, `=`).
- Boolean filters support `true`/`false`.
- `order_by` (or `sort_by`) must be whitelisted server-side; `sort_order` can be `ASC` or `DESC`.

## Common Response Codes

- `201 Created` — resource created.
- `200 OK` — successful retrieval or update.
- `204 No Content` — successful delete with no response body.
- `400 Bad Request` — validation or client error.
- `401 Unauthorized` — authentication required or failed.
- `403 Forbidden` — authenticated but not authorized.
- `404 Not Found` — resource not found.
- `409 Conflict` — resource conflict (e.g., uniqueness violation).
- `429 Too Many Requests` — rate limited.
- `500 Internal Server Error` — server error.

## Validation & Idempotency

- Validate inputs at the API boundary and return `400 Bad Request` with structured errors on validation failure.
- Support idempotency for create operations via an `Idempotency-Key` header.

## Error Handling

Return structured error responses. Example:

```json
{
  "detail": "Explains the error.",
  "errors": [
    {
      "field": "name",
      "type": "https://api.example.com/errors#duplicate-entity",
      "description": "Entity identified by 'name' with value 'acme' already exists."
    }
  ],
  "instance": "/tenants",
  "request_id": "2ebd96ca-e898-48fa-97ab-b1b32979da8d",
  "status": 400,
  "timestamp": "2025-11-16T10:30:03.224Z",
  "title": "Validation Error",
  "type": "https://api.example.com/errors#validation-error"
}
```

Notes:

- `timestamp`: time at which the server returned the error (UTC).
- `errors` array is optional.
- `request_id`: server-generated UUID to help trace requests in logs and support requests.
- `status`: same as the HTTP status code returned.
- `type`: URL that documents the error type.

## Security & Authorization

- Use HTTPS for all endpoints.
- Authenticate requests using JWT, API keys (via Authorization: Bearer header), or `Cookie` header.
- Enforce authorization in the application layer via roles and permissions.

## Rate Limiting & Throttling

- Apply per-client and global rate limits.
- Return `429 Too Many Requests` with a `Retry-After` header and optional rate-limit headers (`X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`, `X-RateLimit-Used`).

## Request Headers

- `X-Tenant-Id` (required for tenant-scoped requests, optional for tenant creation): the tenant identifier used to scope the request on the server side (numeric `id`).
- `X-Account-Id` (optional): identifier for the account or organization that owns the resource.
- `X-User-Id` (required): identifier of the acting user who initiated the request; used for authentication and authorization.

## Response Headers

- `X-Request-Id` (UUID) in all responses for traceability.
- `X-RateLimit-Limit` — allowed requests in the current window.
- `X-RateLimit-Remaining` — requests remaining in the current window.
- `X-RateLimit-Reset` — Unix timestamp when the window resets.
- `X-RateLimit-Used` — requests already used (optional).
- `Content-Type: application/json`

## OpenAPI / Documentation

- Maintain an OpenAPI (Swagger) specification for all endpoints and keep it in sync with implementation. Publish the spec URL in this document.
