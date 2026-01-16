package com.shimi.gogoscrum.user.service;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.user.model.Preference;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.model.UserFilter;
import com.shimi.gogoscrum.user.oauth.OauthProvider;
import com.shimi.gsf.core.model.EntityQueryResult;
import com.shimi.gsf.core.service.GeneralService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * UserService is an interface that extends GeneralService and UserDetailsService.
 * It provides methods for user management, including finding project mates,
 * updating user basics, verifying passwords, updating passwords, and updating user last login info.
 */
public interface UserService extends GeneralService<User, UserFilter>, UserDetailsService {
    User updateUserBasics(Long id, User user);

    void updatePassword(Long id, String oldPassword, String newPassword);

    void updateLastLoginInfo(User user);

    User updatePreference(Long userId, Preference preference);

    User updateAvatar(Long userId, File avatarFile);

    void deleteAvatar(Long userId);

    /**
     * Find users who are used to work with the current user in the same projects, filtered by keyword (nickname or username).
     * @param page the page number
     * @param pageSize the size of each page
     * @param keyword the keyword to filter users (optional)
     * @return the paginated result of users
     */
    EntityQueryResult<User> findProjectMates(int page, int pageSize, String keyword);

    /**
     * Get the list of configured 3rd-party OAuth providers.
     * @return the list of OAuth provider configurations
     */
    List<OauthProvider.ProviderConfig> getOauthProviders();

    /**
     * Get a specific OAuth provider by its unique name.
     * @param name the unique name of the OAuth provider
     * @return the OAuth provider instance
     */
    OauthProvider getOauthProvider(String name);

    /**
     * Retrieve user info from 3rd-party OAuth provider based on the provided OAuth information.
     * If the user is already existing, then log them in. If not, return a new user object
     * with the information retrieved from the provider, so that the caller can decide
     * whether to create a brand-new user or bind to an existing user.
     * @param oauthInfo the OAuth information from the callback
     * @return the user information retrieved from the provider or an existing user
     */
    User retrieveUser(OauthProvider.OauthInfo oauthInfo);

    /**
     * Create a new user or bind the OAuth info to an existing user based on the provided user object.
     * The user object should contain a flag indicating whether to bind to an existing user.
     * If binding, the user object should also contain the username and password of the existing user to bind.
     * @param user the user object containing the OAuth info and binding information
     * @return the created user
     */
    User createOrBindFromOauth(User user);

    /**
     * Unbind the OAuth binding from current user.
     * @param bindingId the ID of the UserBinding to unbind
     */
    void unbindOauth(Long bindingId);
}
