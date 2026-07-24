# AGENTS.md

This file provides guidance to coding agents when working with code in this repository.

## Project Overview

Keycloak TikTok is a Java plugin that adds TikTok as a social login (identity provider) to Keycloak. It implements TikTok's OAuth2 flow, user profile fetching, attribute mapping, and access token revocation on logout.

## Build & Test Commands

```bash
make build        # Clean build: mvn clean package
make test         # Run tests: mvn test
make check-all    # Validate version consistency across pom.xml and TikTokIdentityProviderFactory.java
make install      # Build and copy JAR to local Keycloak providers directory
```

## Architecture

**Keycloak SPI plugin** using Java 17, built with Maven, targeting Keycloak 26.5.0+.

Four source files in `src/main/java/org/keycloak/social/tiktok/`:

- **TikTokIdentityProvider** — Core OAuth2 flow implementation. Extends `AbstractOAuth2IdentityProvider`. Handles authorization URL construction (uses `client_key` instead of standard `client_id`), token exchange, user info fetching, scope-to-field mapping, and backchannel logout via TikTok's revocation endpoint. Generates placeholder emails as `<username>@tiktok.com` since TikTok doesn't expose user emails.

- **TikTokIdentityProviderFactory** — Registers the provider with Keycloak under ID `"tiktok"`. Also exposes plugin version and author via `ServerInfoAwareProviderFactory`. **Version string here must match pom.xml** (enforced by `scripts/check_version.sh`).

- **TikTokIdentityProviderConfig** — Thin wrapper around `OAuth2IdentityProviderConfig`.

- **TikTokUserAttributeMapper** — Extends `AbstractJsonUserAttributeMapper` to allow Keycloak admins to map TikTok profile fields to user attributes.

**Service registration** via `META-INF/services/` files (Java ServiceLoader pattern required by Keycloak SPI).

**Tests** are in the non-standard directory `src/main/test/` (configured in pom.xml via `testSourceDirectory`). Uses JUnit Jupiter 5.

## Key Conventions

- **Version sync:** The version in `pom.xml` and the `VERSION` constant in `TikTokIdentityProviderFactory.java` must always match. Run `make check-all` to verify.
- **Semantic release:** Releases are automated via `.releaserc` on tag push (pattern `v*.*.*`). The CI builds on push to `main` and on PRs.
- **TikTok API quirks:** TikTok's OAuth2 deviates from standard — uses `client_key` parameter instead of `client_id`, requires Bearer token auth for user info, and nests user data under `data.user` in responses.
