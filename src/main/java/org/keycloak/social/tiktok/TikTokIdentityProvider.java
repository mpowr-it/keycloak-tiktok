package org.keycloak.social.tiktok;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.http.simple.SimpleHttp;
import org.keycloak.http.simple.SimpleHttpRequest;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;

import java.util.*;

/**
 * TikTokIdentityProvider is an implementation of the SocialIdentityProvider interface for TikTok.
 */
public class TikTokIdentityProvider extends AbstractOAuth2IdentityProvider<TikTokIdentityProviderConfig>
    implements SocialIdentityProvider<TikTokIdentityProviderConfig> {

    private static final Logger log = Logger.getLogger(TikTokIdentityProvider.class);

    private List<String> grantedScopes;

    public static final String AUTH_URL = "https://www.tiktok.com/v2/auth/authorize/";
    public static final String TOKEN_URL = "https://open.tiktokapis.com/v2/oauth/token/";
    public static final String USER_INFO_URL = "https://open.tiktokapis.com/v2/user/info/";
    public static final String REVOKE_ACCESS_URL = "https://open.tiktokapis.com/v2/oauth/revoke/";
    public static final String DEFAULT_SCOPES = "user.info.basic,user.info.profile";
    public static final Map<String, String> PROFILE_FIELDS = new HashMap<>() {{
        put("open_id", "user.info.basic");
        put("union_id", "user.info.basic");
        put("avatar_url", "user.info.basic");
        put("avatar_url_100", "user.info.basic");
        put("avatar_large_url", "user.info.basic");
        put("display_name", "user.info.basic");
        put("bio_description", "user.info.profile");
        put("profile_deep_link", "user.info.profile");
        put("is_verified", "user.info.profile");
        put("username", "user.info.profile");
        put("follower_count", "user.info.stats");
        put("following_count", "user.info.stats");
        put("likes_count", "user.info.stats");
        put("video_count", "user.info.stats");
    }};

    public TikTokIdentityProvider(KeycloakSession session, TikTokIdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(USER_INFO_URL);
    }

    /**
     * Get the federated identity from the token response.
     * Extracts the granted scopes from TikTok's token response so that only
     * fields covered by the user's actual consent are requested from the user info endpoint.
     *
     * @param response The raw token response from TikTok.
     * @return BrokeredIdentityContext
     */
    @Override
    public BrokeredIdentityContext getFederatedIdentity(String response) {
        String scope = extractTokenFromResponse(response, OAUTH2_PARAMETER_SCOPE);
        if (scope != null && !scope.isEmpty()) {
            this.grantedScopes = Arrays.asList(scope.split(","));
            log.debugf("Granted scopes from TikTok token response: %s", scope);
        } else {
            this.grantedScopes = Arrays.asList(getDefaultScopes().split(","));
            log.debug("No scope in token response, falling back to configured scopes.");
        }
        return super.getFederatedIdentity(response);
    }

    /**
     * Authenticate the token request to TikTok.
     *
     * @param tokenRequest The token request to authenticate.
     * @return SimpleHttpRequest
     * @see <a href="https://developers.tiktok.com/doc/oauth-user-access-token-management#1._fetch_an_access_token_using_an_authorization_code">Fetch an access token using an authorization code</a>
     */
    @Override
    public SimpleHttpRequest authenticateTokenRequest(final SimpleHttpRequest tokenRequest) {
        return tokenRequest
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cache-Control", "no-cache")
            .param("client_key", getConfig().getClientId())
            .param("client_secret", getConfig().getClientSecret());
    }

    /**
     * Create the authorization URL for TikTok.
     *
     * @param request The authentication request.
     * @return UriBuilder
     * @see <a href="https://developers.tiktok.com/doc/login-kit-web?enter_method=left_navigation#redirect_request_to_tiktok's_authorization_server">User Authorization</a>
     */
    @Override
    protected UriBuilder createAuthorizationUrl(AuthenticationRequest request) {
        UriBuilder builder = super.createAuthorizationUrl(request);
        builder.queryParam("client_key", getConfig().getClientId());
        builder.replaceQueryParam("scope", this.getDefaultScopes());

        log.debug("Authorization URL: " + builder.build().toString());

        return builder;
    }

    /**
     * Get the profile endpoint for validation.
     *
     * @param event The event builder.
     * @return String
     */
    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return USER_INFO_URL;
    }

    /**
     * Extract the identity from the profile returned by TikTok.
     *
     * @param event   The event builder.
     * @param profile The profile returned by TikTok.
     * @return BrokeredIdentityContext
     * @see <a href="https://developers.tiktok.com/doc/tiktok-api-v2-get-user-info">Get User Info</a>
     */
    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
        String unionId = getJsonProperty(profile, "union_id");
        String username = getJsonProperty(profile, "username");

        if (username == null || username.isEmpty()) {
            username = getJsonProperty(profile, "display_name");
        }
        if (username == null || username.isEmpty()) {
            username = unionId;
        }

        BrokeredIdentityContext user = new BrokeredIdentityContext(unionId, getConfig());
        user.setUsername(username);
        user.setEmail((username + "@tiktok.com").toLowerCase());
        user.setIdp(this);

        TikTokUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());

        return user;
    }

    /**
     * Get the federated identity from TikTok.
     *
     * @param accessToken The access token obtained from TikTok.
     * @return BrokeredIdentityContext
     * @see <a href="https://developers.tiktok.com/doc/tiktok-api-v2-get-user-info">Get User Info</a>
     */
    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        JsonNode profile;

        List<String> scopes = (grantedScopes != null) ? grantedScopes
            : Arrays.asList(getDefaultScopes().split(","));
        SimpleHttpRequest profileRequest = SimpleHttp.create(session).doGet(USER_INFO_URL)
            .header("Authorization", "Bearer " + accessToken)
            .param("fields", String.join(",", getFieldsFromScopes(scopes)));

        try {
            profile = profileRequest.asJson();
        } catch (Exception e) {
            throw new IdentityBrokerException("Could not obtain user profile from TikTok.", e);
        }

        if (profile.get("error") != null && !profile.get("error").get("code").asText().equals("ok")) {
            log.error("Error Code from TikTok: " + profile.get("error").get("code").asText());
            log.error("Error Message from TikTok: " + profile.get("error").get("message").asText());
        }

        if (profile.get("data") == null || profile.get("data").get("user") == null) {
            throw new IdentityBrokerException("Could not obtain user profile from TikTok.");
        }

        return extractIdentityFromProfile(null, profile.get("data").get("user"));
    }

    /**
     * Get the fields from the scopes defined in the configuration.
     *
     * @return String[] List of fields
     */
    protected List<String> getFieldsFromScopes(List<String> scopes) {
        List<String> fields = new ArrayList<>();

        // Query all fields defined by the given scopes
        for (String scope : scopes) {
            // add all keys from PROFILE_FIELDS to "fields" list that have the current "scope" as value
            fields.addAll(PROFILE_FIELDS.entrySet().stream()
                              .filter(entry -> entry.getValue().equals(scope.trim().toLowerCase()))
                              .map(Map.Entry::getKey)
                              .toList());
        }

        return fields;
    }

    /**
     * Get the scopes to request from TikTok.
     * Scopes added in the admin UI of Keycloak will be added to the default scopes.
     *
     * @return String Unique comma-separated list of scopes
     */
    @Override
    protected String getDefaultScopes() {
        if (getConfig().getDefaultScope() == null || getConfig().getDefaultScope().isEmpty()) {
            return DEFAULT_SCOPES;
        }

        ArrayList<String> scopes = new ArrayList<>(Arrays.asList(DEFAULT_SCOPES.split("(,|\\s)")));
        scopes.addAll(Arrays.asList(getConfig().getDefaultScope().trim().split("(,|\\s)")));

        // Remove duplicates and sort the scopes
        Set<String> uniqueScopes = new HashSet<>(scopes);
        ArrayList<String> newScopes = new ArrayList<>(uniqueScopes);
        Collections.sort(newScopes);

        return String.join(",", newScopes);
    }

    /**
     * Revoke the access token for the user session.
     *
     * @param session     Keycloak session
     * @param userSession User session to revoke the access token for
     * @param uriInfo     Uri information for the request
     * @param realm       Realm model
     */
    @Override
    public void backchannelLogout(KeycloakSession session, UserSessionModel userSession, UriInfo uriInfo, RealmModel realm) {
        String accessToken = userSession.getNote(FEDERATED_ACCESS_TOKEN);

        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("No access token found for user session: " + userSession.getId());
            return;
        }

        log.debug("Federated access_token: " + accessToken);

        SimpleHttpRequest revokeRequest = SimpleHttp.create(session).doPost(REVOKE_ACCESS_URL)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cache-Control", "no-cache")
            .param("client_key", getConfig().getClientId())
            .param("client_secret", getConfig().getClientSecret())
            .param("token", accessToken);

        try {
            JsonNode response = revokeRequest.asJson();
            guardRevokeRequestSucceeded(response);
            log.info("Successfully revoked access token for user session: " + userSession.getId());
        } catch (Exception e) {
            log.error("Failed to revoke access token for user session: " + userSession.getId(), e);
            throw new IdentityBrokerException("Failed to revoke access token for TikTok.", e);
        }
    }

    /**
     * Guard the revoke request response to ensure it succeeded.
     *
     * @param response The JSON response from the revoke request.
     * @throws IdentityBrokerException if the revoke request failed.
     */
    private void guardRevokeRequestSucceeded(JsonNode response) {
        if (response.get("error") != null) {
            String errorCode = response.get("error").get("code").asText();
            String errorMessage = response.get("error").get("message").asText();
            log.error("Error revoking access token: " + errorCode + " - " + errorMessage);
            throw new IdentityBrokerException("Failed to revoke access token for TikTok: " + errorMessage);
        }
    }
}
