package org.keycloak.social.tiktok;

import org.junit.jupiter.api.Test;

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
}
