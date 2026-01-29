package org.keycloak.social.tiktok;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class TikTokIdentityProviderFactory extends AbstractIdentityProviderFactory<TikTokIdentityProvider>
        implements SocialIdentityProviderFactory<TikTokIdentityProvider>, ServerInfoAwareProviderFactory {

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

    /**
     * Returns the list operational information for the TikTok identity provider.
     *
     * @return Map<String, String> Map of operational information
     */
    @Override
    public Map<String, String> getOperationalInfo() {
        Map<String, String> ret = new LinkedHashMap<>();
        ret.put("social_provider", "tiktok");
        ret.put("version", "1.2.0");
        ret.put("author", "MPOWR IT GmbH");
        ret.put("author_url", "https://mpowr.it");
        ret.put("project_url", "https://github.com/mpowr-it/keycloak-tiktok");

        return ret;
    }
}
