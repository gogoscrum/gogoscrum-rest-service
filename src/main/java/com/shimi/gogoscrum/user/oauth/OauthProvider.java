package com.shimi.gogoscrum.user.oauth;

import org.pf4j.ExtensionPoint;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Interface for 3rd-party OAuth providers. Each implementation should handle the specifics of interacting
 * with a particular OAuth provider. This interface is an extension point for the PF4J plugin framework.
 */
public interface OauthProvider extends ExtensionPoint {
    /**
     * Get the configuration of the OAuth provider.
     * @return the provider configuration
     */
    ProviderConfig getConfig();

    /**
     * Get the unique name of the OAuth provider.
     * @return the provider name
     */
    String getName();

    /**
     * Get the login URL to redirect users for authentication.
     * @return the login URL
     */
    String getLoginUrl();

    /**
     * Retrieve user information from the OAuth provider using the provided OAuth info.
     * @param oauth the OAuth information from the callback
     * @return the user information retrieved from the provider
     */
    OauthUser retrieveUser(OauthInfo oauth);

    /**
     * Class representing the configuration of an OAuth provider.
     */
    class ProviderConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = -668716954025586325L;
        /**
         * Unique name of the OAuth provider, e.g., "github", "google".
         */
        private String name;

        /**
         * Display name of the OAuth provider, e.g., "GitHub", "Google".
         */
        private String displayName;
        /**
         * URL of the icon representing the OAuth provider.
         */
        private String iconUrl;
        /**
         * Languages supported by this OAuth provider, e.g., "en", "cn", check the front-end
         * project to the full list of supported languages.
         * If set, this provider will be shown only to users with the specified languages.
         * If not set, it will be shown to all users.
         */
        private List<String> languages;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public List<String> getLanguages() {
            return languages;
        }

        public void setLanguages(List<String> languages) {
            this.languages = languages;
        }
    }

    /**
     * Class representing OAuth information provided by the callback from 3rd-party OAuth provider.
     */
    class OauthInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = -7407255357051467910L;
        private String provider;
        private String code;
        private String state;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    /**
     * Interface representing a user retrieved from the OAuth provider.
     */
    interface OauthUser {
        String getProvider();
        String getUsername();
        String getAvatarUrl();
        String getExtUserId();
    }
}


