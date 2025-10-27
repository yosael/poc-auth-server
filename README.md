# poc-auth-server

`poc-auth-server` is an authentication and authorization service built with Spring Boot 3.5.x and Java 21.

It is responsible for:
- User registration (regular users only)
- Username/password login
- Generating signed JWT access tokens
- Exposing a JWKS endpoint so other services can validate those tokens
- Admin-only management endpoints (user search, role management, etc.)

This service is both:
1. An **auth server** (issues JWTs), and
2. A **resource server** (protects its own admin endpoints using those JWTs).

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Core Responsibilities](#core-responsibilities)
- [Data Model](#data-model)
- [Security Model](#security-model)
- [Key Management (RSA / JWKS)](#key-management-rsa--jwks)
- [Endpoints](#endpoints)
    - [Public](#public)
    - [Protected (Admin)](#protected-admin)
- [How JWT Works Here](#how-jwt-works-here)
- [Application Layers](#application-layers)
- [Profiles and Bootstrap Admin](#profiles-and-bootstrap-admin)
- [Configuration Notes](#configuration-notes)
- [Typical Flow](#typical-flow)
- [Hardening / Next Steps](#hardening--next-steps)

---

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA (PostgreSQL)
- JWT (RS256) using Nimbus JOSE
- BCrypt password hashing
- Gradle
- Lombok

---

## Core Responsibilities

### Authentication
- `/api/auth/login` authenticates a user with username/password and returns a JWT access token.

### Registration
- `/api/auth/register` creates a new user.
- The new user always gets the `USER` role.
- You cannot self-register as `ADMIN` (prevents privilege escalation).

### Authorization / RBAC
- JWTs include a `roles` claim like `["USER"]` or `["ADMIN","USER"]`.
- Admin-only endpoints require `ROLE_ADMIN`.

### Federation to other services
- The service exposes its public key at `/.well-known/jwks.json`.
- Other services (like `clients-api`) validate JWTs using that JWKS URL.

---

## Data Model

### `UserAccount`
- Fields:
    - `id` (UUID)
    - `username` (unique)
    - `passwordHash` (BCrypt)
    - `enabled` (boolean)
    - `roles` (many-to-many)

```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
@BatchSize(size = 50)
private Set<Role> roles;
```

### `Role`
- Fields:
    - `id` (UUID)
    - `name` (e.g., `ROLE_USER`, `ROLE_ADMIN`)

---
## Security Model

## registration
```POST /api/auth/register```
- Creates a new enabled user.
- Always assigns the `USER` role.
- Caller cannot assign `ADMIN` role.

This blocks privilege escalation via self-registration.

## login
```POST /api/auth/login```
- Authenticates user with username/password.
- Returns a signed JWT access token with `roles` claim.

Example Response:
```json 
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

## Key Management (RSA / JWKS)

This service signs JWTs with an RSA **private key** and exposes the matching **public key** so other services can verify those tokens.

### Why RSA?
- The auth server holds the private key and uses it to sign tokens.
- Other services never see the private key. They only need the public key to verify the signature.
- This means other services can trust tokens without calling this service every time.

### 1. Generate keys (local/dev)

```bash
# 1. Generate a private RSA key (PKCS#1)
openssl genrsa -out rsa_private.pem 2048

# 2. Convert that private key to PKCS#8 (this is the format the app expects)
openssl pkcs8 -topk8 -inform PEM -in rsa_private.pem -outform PEM -nocrypt -out rsa_private_pkcs8.pem

# 3. Extract the public key
openssl rsa -in rsa_private.pem -pubout -out rsa_public.pem
```

## Endpoints

### Public

#### `POST /api/auth/register`
Creates a new user account.

Request body:
```json
{
  "username": "newuser",
  "password": "cleartextpassword"
}
```

#### `POST /api/auth/login`
Authenticates a user and returns a JWT access token.
Request body:
```json
{
  "username": "existinguser",
  "password": "cleartextpassword"
}
```

### Protected (Admin)

All endpoints below require `ROLE_ADMIN` in the JWT.

#### `GET /api/admin/users`

Search users with optional filters (username, role, enabled).

Query parameters:
- `q` (optional)
- `role` (optional)
- `enabled` (optional)
- `page` (optional, default 0)
- `size` (optional, default 20)
- `sort` (optional, e.g., `username,asc`)

#### `PUT /api/admin/users/{id}/roles/{roleName}`
Assigns a role to a user.
Path parameters:
- `id`: User ID (UUID)
- `roleName`: Role name (e.g., `ADMIN`, `USER`)
- No request body.

#### `DELETE /api/admin/users/{id}/roles/{roleName}`
Removes a role from a user.
Path parameters:
- `id`: User ID (UUID)
- `roleName`: Role name (e.g., `ADMIN`, `USER`)
- No request body.
