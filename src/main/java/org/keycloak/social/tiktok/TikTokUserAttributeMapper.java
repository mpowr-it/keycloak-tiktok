package org.keycloak.social.tiktok;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

public class TikTokUserAttributeMapper extends AbstractJsonUserAttributeMapper {

    private static final String[] cp = new String[]{TikTokIdentityProviderFactory.PROVIDER_ID};

    @Override
    public String[] getCompatibleProviders() {
        return cp;
    }

    @Override
    public String getId() {
        return "tiktok-user-attribute-mapper";
    }
}
