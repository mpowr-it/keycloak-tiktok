package org.keycloak.social.tiktok;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.messages.Messages;

import java.util.Set;

public class TikTokIdentityProvider extends AbstractOAuth2IdentityProvider<TikTokIdentityProviderConfig>
        implements SocialIdentityProvider<TikTokIdentityProviderConfig> {

    private static final Logger log = Logger.getLogger(TikTokIdentityProvider.class);

    public static final String AUTH_URL = "https://www.tiktok.com/v2/auth/authorize/";
    public static final String TOKEN_URL = "https://open.tiktokapis.com/v2/oauth/token/";
    public static final String USER_INFO_URL = "https://open.tiktokapis.com/v2/user/info/";
    public static final String DEFAULT_SCOPE = "user.info.basic user.info.profile";

    public TikTokIdentityProvider(KeycloakSession session, TikTokIdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(USER_INFO_URL);
    }

    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return USER_INFO_URL;
    }

    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
        BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(profile, "id"), getConfig());

        String username = getJsonProperty(profile, "username");
        String discriminator = getJsonProperty(profile, "discriminator");

        if (!"0".equals(discriminator)) {
            username += "#" + discriminator;
        }

        user.setUsername(username);
        user.setEmail(getJsonProperty(profile, "email"));
        user.setIdp(this);

        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());

        return user;
    }

    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        log.debug("doGetFederatedIdentity()");
        JsonNode profile = null;
        try {
            profile = SimpleHttp.doGet(USER_INFO_URL, session).header("Authorization", "Bearer " + accessToken).asJson();
        } catch (Exception e) {
            throw new IdentityBrokerException("Could not obtain user profile from TikTok.", e);
        }

        return extractIdentityFromProfile(null, profile);
    }

    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }
}
