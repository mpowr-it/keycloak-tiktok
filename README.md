# Keycloak social login provider for TikTok

This is a custom social login provider for Keycloak connecting with TikTok's OAuth2 API, which is not OIDC compatible.

## Install

Download `keycloak-tiktok-<version>.jar` from [Releases page](https://github.com/mpowr-it/keycloak-tiktok/releases).
Then deploy it into `$KEYCLOAK_HOME/providers` directory.

## Setup

### Register Your App with TikTok

- Go to the TikTok developer portal: https://developers.tiktok.com
- Create an app and note:
    - Client ID
    - Client Secret
    -
- Redirect URI – You will need to set this to Keycloak’s redirect URI for social logins. It will look like this:

```perl
https://<your-keycloak-domain>/realms/<your-realm>/broker/tiktok/endpoint
```

### Keycloak

1. Add `tiktok` Identity Provider in the realm which you want to configure.
2. In the `tiktok` identity provider page, set `Client Id` and `Client Secret`.
3. (Optional) Set Guild Id(s) to allow federation if you want.

## Source Build

Clone this repository and run `mvn package`.
You can see `keycloak-tiktok-<version>.jar` under `target` directory.

## Authors

- [Holger Woltersdorf](https://github.com/hollodotme)
- [Hiroyuki Wada](https://github.com/wadahiro)
- and contributors

