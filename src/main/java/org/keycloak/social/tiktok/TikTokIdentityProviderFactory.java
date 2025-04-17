package org.keycloak.social.tiktok;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class TikTokIdentityProviderFactory extends AbstractIdentityProviderFactory<TikTokIdentityProvider>
        implements SocialIdentityProviderFactory<TikTokIdentityProvider> {

    public static final String PROVIDER_ID = "tiktok";

    @Override
    public String getName() {
        return "TikTok";
    }

    @Override
    public TikTokIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new TikTokIdentityProvider(session, new TikTokIdentityProviderConfig(model));
    }

    @Override
    public TikTokIdentityProviderConfig createConfig() {
        return new TikTokIdentityProviderConfig();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
