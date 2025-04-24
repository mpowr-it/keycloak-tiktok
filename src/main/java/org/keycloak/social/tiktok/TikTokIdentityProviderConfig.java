package org.keycloak.social.tiktok;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

public class TikTokIdentityProviderConfig extends OAuth2IdentityProviderConfig {

    public TikTokIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }

    public TikTokIdentityProviderConfig() {
    }
}
