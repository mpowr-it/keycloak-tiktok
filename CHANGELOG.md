# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v1.1.0.html).

## [1.2.0] - 2026-01-29

[1.2.0]: https://github.com/mpowr-it/keycloak-tiktok/compare/v1.1.0...v1.2.0

### Added

* Support for partially granted scopes, when multiple scopes were requested but not all were granted by the user
* Compatibility information between Keycloak and plugin versions in [README.md](README.md)

### Upgraded

* Identity provider implementation to be compatible with Keycloak >= 26.5.0 codebase

## [1.1.0] - 2025-06-13

[1.1.0]: https://github.com/mpowr-it/keycloak-tiktok/compare/v1.0.0...v1.1.0

### Added

* Revoke app access in the user's TikTok account when the user logs out of Keycloak

### Fixed

* The `scope` parameter in authorization URL was not built correctly and caused missing scope confirmations on TikTok
  login page

## [1.0.0] - 2025-04-24

[1.0.0]: https://github.com/mpowr-it/keycloak-tiktok/tree/v1.0.0

Initial release of the TikTok Identity Provider for Keycloak.

### Added

* Optional `user.info.stats` scope to get user stats
* Requesting all fields from selected scopes dynamically
