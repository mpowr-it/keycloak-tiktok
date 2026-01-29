# Keycloak social login provider for TikTok

This is a custom social login provider for Keycloak connecting with TikTok's OAuth2 API, which is not OIDC compatible.

## Compatibility

The following table shows the compatibility between Keycloak and this plugin versions:

| Keycloak Version   | Plugin Version |
|--------------------|----------------|
| >= 26.2.0 < 26.5.0 | <= 1.1.0       |
| >= 26.5.0          | > 1.1.0        |

## Install

Download `keycloak-tiktok-<version>.jar` from [Releases page](https://github.com/mpowr-it/keycloak-tiktok/releases).
Then deploy it into `$KEYCLOAK_HOME/providers` directory and restart Keycloak.

## Setup

### Register Your App with TikTok

- Go to the TikTok developer portal: https://developers.tiktok.com
- Create an app and note:
    - Client Key
    - Client Secret
- Redirect URI – You will need to set this to Keycloak’s redirect URI for social logins. It will look like this:
- Add the following scopes:
    - `user.info.basic` (default)
    - `user.info.profile`
    - `user.info.stats` (optional)

```perl
https://<your-keycloak-domain>/realms/<your-realm>/broker/tiktok/endpoint
```

The endpoint URL is shown in the [next step](#configure-tiktok-idp-in-keycloak).

### Configure TikTok IDP in Keycloak

1. Add `tiktok` Identity Provider in the realm which you want to configure.
2. In the `tiktok` identity provider page, set
    * `Client Id` — use the Client Key from your TikTok app and
    * `Client Secret` — use the Client Secret from your TikTok app.  
      ... and save the configuration.
3. If you need the user's stats, add the `user.info.stats` scope to the `Scopes` field under "Advanced Settings".
4. Enable "Store tokens"
5. Enable "Trust Email" — TikTok does not provide user emails, so the IDP will create it from the username like
   `<username>@tiktok.com`.
6. Set Sync Mode to `FORCE` if you want to sync the user data from TikTok every time the user logs in.

Save the configuration.

The TikTok button will be shown in the login page of the realm.

### User Profile Data

As [described here](https://developers.tiktok.com/doc/tiktok-api-v2-get-user-info) you'll get the following user profile
attributes depending on the requested scopes.

#### Scopes

**user.info.basic**:

* `open_id`
* `union_id` — Used as unique user-ID in Keycloak
* `avatar_url`
* `avatar_url_100`
* `avatar_large_url`
* `display_name`

**user.info.profile**:

* `bio_description`
* `profile_deep_link`
* `is_verified`
* `username` — Used as username and email (`<username>@tiktok.com`) in Keycloak

**user.info.stats**:

* `follower_count`
* `following_count`
* `likes_count`
* `video_count`

You can map all the values to your user profile in Keycloak using Mappers in the TikTok identity provider:

1. Go to the `Mappers` tab of the TikTok identity provider.
2. Click `Add mapper`.
3. Enter a name for the mapper (e.g. `Avatar URL`).
4. Select `FORCE` for Sync Mode to always sync the user data from TikTok.
5. Select `Attribute Importer` for the mapper type.
6. Select the TikTok attribute you want to map (e.g. `avatar_url`, see [above](#scopes)).
7. Select the user or custom attribute you want to map it to. (e.g. `avatar_url`).
8. Click `Save`.
9. Repeat for each attribute you want to map.

## Features

* Implements the user access token management for TikTok, see
  [TikTok OAuth documentation](https://developers.tiktok.com/doc/oauth-user-access-token-management)
* OpenID Connect (OIDC) compatible login flow
* OpenID Connect logout causes revoking the app access in the user's TikTok account
* Supports multiple scopes:
  - `user.info.basic` (default)
  - `user.info.profile` (default)
  - Further optional scopes like `user.info.stats`

## Source Build

Clone this repository and run `mvn package`.
You can see `keycloak-tiktok-<version>.jar` under `target` directory.

## Static checks

```bash
make check-all
```

## Tests

```bash
make test
```

## Authors

- [Holger Woltersdorf](https://github.com/hollodotme)
- [Hiroyuki Wada](https://github.com/wadahiro)
- and contributors
