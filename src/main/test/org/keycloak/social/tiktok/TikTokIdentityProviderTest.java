package org.keycloak.social.tiktok;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.keycloak.broker.provider.BrokeredIdentityContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TikTokIdentityProviderTest {

    @Test
    public void testGetFieldsFromDefaultScopes() {
        // Create an instance of TikTokIdentityProvider with mocked data
        TikTokIdentityProviderConfig config = new TikTokIdentityProviderConfig();
        TikTokIdentityProvider provider = new TikTokIdentityProvider(null, config);

        // Define the input scopes
        List<String> scopes = List.of("user.info.basic", "user.info.profile");

        assertEquals(String.join(",", scopes), provider.getDefaultScopes());
        assertEquals(TikTokIdentityProvider.DEFAULT_SCOPES, provider.getDefaultScopes());

        // Call the method under test
        List<String> fields = provider.getFieldsFromScopes(scopes);

        // Verify the expected fields
        List<String> expectedFields = Arrays.asList(
            "open_id",
            "union_id",
            "avatar_url",
            "avatar_url_100",
            "avatar_large_url",
            "display_name",
            "bio_description",
            "profile_deep_link",
            "is_verified",
            "username"
        );

        Collections.sort(expectedFields);
        Collections.sort(fields);

        assertEquals(expectedFields, fields);
    }

    @Test
    public void testGetFieldsFromExtendedScopes() {
        // Create an instance of TikTokIdentityProvider with mocked data
        TikTokIdentityProviderConfig config = new TikTokIdentityProviderConfig();
        // Additional scope set by the user in Admin UI
        config.setDefaultScope("user.info.stats");

        TikTokIdentityProvider provider = new TikTokIdentityProvider(null, config);

        // Define the input scopes
        List<String> scopes = List.of("user.info.basic", "user.info.profile", "user.info.stats");

        assertEquals(String.join(",", scopes), provider.getDefaultScopes());
        assertNotEquals(TikTokIdentityProvider.DEFAULT_SCOPES, provider.getDefaultScopes());

        // Call the method under test
        List<String> fields = provider.getFieldsFromScopes(scopes);

        // Verify the expected fields
        List<String> expectedFields = Arrays.asList(
            "open_id",
            "union_id",
            "avatar_url",
            "avatar_url_100",
            "avatar_large_url",
            "display_name",
            "bio_description",
            "profile_deep_link",
            "is_verified",
            "username",
            "follower_count",
            "following_count",
            "likes_count",
            "video_count"
        );

        Collections.sort(expectedFields);
        Collections.sort(fields);

        assertEquals(expectedFields, fields);
    }

    @Test
    public void testGetFieldsFromBasicScopeOnly() {
        TikTokIdentityProviderConfig config = new TikTokIdentityProviderConfig();
        TikTokIdentityProvider provider = new TikTokIdentityProvider(null, config);

        // Simulate a user who only granted user.info.basic
        List<String> scopes = List.of("user.info.basic");
        List<String> fields = provider.getFieldsFromScopes(scopes);

        List<String> expectedFields = Arrays.asList(
            "open_id",
            "union_id",
            "avatar_url",
            "avatar_url_100",
            "avatar_large_url",
            "display_name"
        );

        Collections.sort(expectedFields);
        Collections.sort(fields);

        assertEquals(expectedFields, fields);
    }

    @Test
    public void testExtractIdentityUsesUsernameWhenAvailable() throws Exception {
        TikTokIdentityProviderConfig config = new TikTokIdentityProviderConfig();
        TikTokIdentityProvider provider = new TikTokIdentityProvider(null, config);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode profile = mapper.readTree(
            "{\"union_id\":\"uid123\",\"username\":\"testuser\",\"display_name\":\"Test User\"}"
        );

        BrokeredIdentityContext context = provider.extractIdentityFromProfile(null, profile);

        assertEquals("testuser", context.getUsername());
        assertEquals("testuser@tiktok.com", context.getEmail());
        assertEquals("uid123", context.getId());
    }

    @Test
    public void testExtractIdentityFallsBackToDisplayName() throws Exception {
        TikTokIdentityProviderConfig config = new TikTokIdentityProviderConfig();
        TikTokIdentityProvider provider = new TikTokIdentityProvider(null, config);

        ObjectMapper mapper = new ObjectMapper();
        // No username field — user.info.profile was not granted
        JsonNode profile = mapper.readTree(
            "{\"union_id\":\"uid123\",\"display_name\":\"Test User\"}"
        );

        BrokeredIdentityContext context = provider.extractIdentityFromProfile(null, profile);

        assertEquals("test user", context.getUsername());
        assertEquals("test user@tiktok.com", context.getEmail());
        assertEquals("uid123", context.getId());
    }

    @Test
    public void testExtractIdentityFallsBackToUnionId() throws Exception {
        TikTokIdentityProviderConfig config = new TikTokIdentityProviderConfig();
        TikTokIdentityProvider provider = new TikTokIdentityProvider(null, config);

        ObjectMapper mapper = new ObjectMapper();
        // Neither username nor display_name available
        JsonNode profile = mapper.readTree(
            "{\"union_id\":\"uid123\"}"
        );

        BrokeredIdentityContext context = provider.extractIdentityFromProfile(null, profile);

        assertEquals("uid123", context.getUsername());
        assertEquals("uid123@tiktok.com", context.getEmail());
        assertEquals("uid123", context.getId());
    }
}
