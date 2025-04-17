package org.keycloak.social.tiktok;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class TikTokIdentityProviderConfig extends OAuth2IdentityProviderConfig {

    public TikTokIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }

    public TikTokIdentityProviderConfig() {}

    public void setPrompt(String prompt) {
        getConfig().put("prompt", prompt);
    }
}
