package com.shimi.gogoscrum.user.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.common.util.RandomToolkit;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.service.FileService;
import com.shimi.gogoscrum.user.model.Preference;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.model.UserBinding;
import com.shimi.gogoscrum.user.model.UserFilter;
import com.shimi.gogoscrum.user.oauth.OauthProvider;
import com.shimi.gogoscrum.user.repository.UserBindingRepository;
import com.shimi.gogoscrum.user.repository.UserRepository;
import com.shimi.gogoscrum.user.repository.UserSpecs;
import com.shimi.gsf.core.exception.*;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, UserFilter> implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository repository;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserBindingRepository bindRepository;
    @Autowired
    private PluginManager pluginManager;
    private final Map<String, OauthProvider> oauthProvidersMap = new HashMap<>();

    @PostConstruct
    private void initPlugins() {
        List<OauthProvider> oauthProviderPlugins = pluginManager.getExtensions(OauthProvider.class);

        if (!oauthProviderPlugins.isEmpty()) {
            oauthProvidersMap.putAll(oauthProviderPlugins.stream().collect(
                    Collectors.toMap(OauthProvider::getName, p -> p, (p1, p2) -> p1)
            ));

            log.info("Loaded {} 3rd-party OAuth provider plugins: {}", oauthProviderPlugins.size(),
                    String.join(", ", oauthProvidersMap.keySet()));
        } else {
            log.debug("No 3rd-party OAuth provider plugins found");
        }
    }

    @Override
    protected UserRepository getRepository() {
        return repository;
    }

    @Override
    protected void beforeCreate(User user) {
        // For normal user registration, username and password are required;
        // For OAuth user creation, password will be empty and username will be generated
        if (CollectionUtils.isEmpty(user.getBindings())) {
            this.verifyUsername(user);
            this.verifyPassword(user.getPassword());
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        } else {
            user.setUsername(RandomToolkit.getRandomString(16));
            user.setPassword(null);
        }
    }

    private void verifyUsername(User user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new BaseServiceException("usernameRequired", "Username is required", HttpStatus.BAD_REQUEST);
        }

        User userByUsername = repository.findByUsername(user.getUsername());

        if (userByUsername != null && !userByUsername.getId().equals(user.getId())) {
            throw new EntityDuplicatedException(ErrorCode.DUPLICATED_USERNAME, "Username already exists: " + userByUsername.getUsername());
        }

        if (user.getId() != null) {
            User userById = super.get(user.getId());

            if (userById != null && !userById.getUsername().equals(user.getUsername())) {
                throw new BaseServiceException("usernameCannotChange", "Username cannot be changed to: " + user.getUsername(),
                        HttpStatus.BAD_REQUEST);
            }
        }

        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
    }

    private void verifyPassword(String pwd) {
        if (!StringUtils.hasText(pwd)) {
            throw new BaseServiceException("passwordRequired", "Password is required", HttpStatus.BAD_REQUEST);
        }

        if (pwd.length() < 6) {
            throw new BaseServiceException("passwordTooShort", "Password must be at least 6 characters long", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Update user basic information. For now only nickname can be updated.
     * @param id   User ID
     * @param user User object with updated information
     * @return Updated User object
     */
    @Override
    public User updateUserBasics(Long id, User user) {
        User currentUser = getCurrentUser();
        if (!id.equals(currentUser.getId())) {
            throw new NoPermissionException("User can only update their own information");
        }

        User existingUser = get(id);

        existingUser.setNickname(user.getNickname());

        User updatedUser = repository.save(existingUser);
        log.info("User basic info updated: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public User updateAvatar(Long userId, File avatarFile) {
        if (!Objects.equals(userId, getCurrentUser().getId())) {
            throw new NoPermissionException("You can only update your own avatar");
        }

        User user = get(userId);

        Long oldFileId = null;
        if (user.getAvatar() != null) {
            oldFileId = user.getAvatar().getId();
        }

        // Create new avatar file and link it to user
        user.setAvatar(fileService.create(avatarFile));
        user.setUpdateTraceInfo(getCurrentUser());
        User updatedUser = repository.save(user);

        // Delete old avatar file if exists
        if (oldFileId != null) {
            fileService.delete(oldFileId);
        }

        log.info("User {} avatar updated", userId);

        return updatedUser;
    }

    @Override
    public void deleteAvatar(Long userId) {
        if (!Objects.equals(userId, getCurrentUser().getId())) {
            throw new NoPermissionException("You can only delete your own avatar");
        }

        User user = get(userId);
        if (user.getAvatar() != null) {
            Long fileId = user.getAvatar().getId();
            // Unlink the avatar from user first, then delete the file
            user.setAvatar(null);
            user.setUpdateTraceInfo(getCurrentUser());
            repository.save(user);
            fileService.delete(fileId);
            log.info("User {} avatar deleted", userId);
        } else {
            log.warn("User {} has no avatar to delete", userId);
        }
    }

    @Override
    public User update(Long id, User user) {
        throw new BaseServiceException("notAllowed", "User update method is not allowed. Use updateUserBasics instead.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public boolean checkPassword(Long id, String oldPassword) {
        User user = get(id);
        return BCrypt.checkpw(oldPassword, user.getPassword());
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        this.verifyPassword(newPassword);

        if (!userId.equals(getCurrentUser().getId())) {
            throw new NoPermissionException("User can only update their own password");
        }

        User user = this.get(userId);
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BaseServiceException(ErrorCode.WRONG_PASSWORD, "Incorrect old password", HttpStatus.PRECONDITION_FAILED);
        }

        String encodePassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encodePassword);
        this.repository.save(user);
        log.info("Password updated for user {}", userId);
    }

    @Override
    public User updatePreference(Long userId, Preference preference) {
        User user = get(userId);
        user.setPreference(preference);
        user.setUpdateTraceInfo(getCurrentUser());

        User savedUser = repository.save(user);
        log.info("Preference updated for user {}: {}", userId, preference);
        return savedUser;
    }

    @EventListener(InteractiveAuthenticationSuccessEvent.class)
    public void userLoggedIn(InteractiveAuthenticationSuccessEvent event) {
        User user = (User) event.getAuthentication().getPrincipal();

        if (log.isDebugEnabled()) {
            log.debug("User logged in via {}: {}", event.getGeneratedBy().getSimpleName(), user);
        }

        this.updateLastLoginInfo(user);
    }

    @Override
    public void updateLastLoginInfo(User user) {
        User existingUser = this.get(user.getId());

        existingUser.setLastLoginIp(user.getLastLoginIp());
        existingUser.setLastLoginTime(new Date());

        User updatedUser = repository.save(existingUser);

        if (log.isDebugEnabled()) {
            log.debug("Updated user's last login info: {}", updatedUser);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = repository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("Cannot find user by username \"" + username + "\"");
        } else if (!StringUtils.hasText(user.getPassword())) {
            throw new BaseServiceException("pwdNotSet", "User has no password set. Please use social login.", HttpStatus.PRECONDITION_FAILED);
        } else {
            log.debug("Loaded user by username {}: {}", username, user);
            return user;
        }
    }

    @Override
    public List<OauthProvider.ProviderConfig> getOauthProviders() {
        return this.oauthProvidersMap.values().stream().map(OauthProvider::getConfig).toList();
    }

    @Override
    public OauthProvider getOauthProvider(String name) {
        if (!this.oauthProvidersMap.containsKey(name)) {
            throw new BadRequestException("Unsupported OAuth provider: " + name);
        }
        return this.oauthProvidersMap.get(name);
    }

    public User retrieveUser(OauthProvider.OauthInfo oauthInfo) {
        OauthProvider oauthProvider = this.oauthProvidersMap.get(oauthInfo.getProvider());
        if (oauthProvider == null) {
            throw new BadRequestException("Unsupported OAuth provider: " + oauthInfo.getProvider());
        }
        OauthProvider.OauthUser oauthUser = oauthProvider.retrieveUser(oauthInfo);
        return this.parseUser(oauthUser);
    }

    private User parseUser(OauthProvider.OauthUser oauthUser) {
        UserBinding binding = bindRepository.getByProviderAndExtUserId(oauthUser.getProvider(), oauthUser.getExtUserId());
        User user = null;

        if (binding != null) {
            // Existing user found via binding, fetch full user details
            user = this.get(binding.getUser().getId());
        } else {
            // No existing user, return a new one (but not saved in DB yet) with binding
            user = new User();

            binding = new UserBinding();
            binding.setProvider(oauthUser.getProvider());
            binding.setExtUserId(oauthUser.getExtUserId());
            binding.setUser(user);

            user.getBindings().add(binding);
            user.setNickname(oauthUser.getUsername());
            // Note: The avatar file need to be copied into file storage when creating the user
            File avatarFile= new File();
            avatarFile.setFullPath(oauthUser.getAvatarUrl());
            user.setAvatar(avatarFile);
        }

        return user;
    }

    public User createOrBindFromOauth(User user) {
        UserBinding binding = user.getBindings().getFirst();
        this.verifyBinding(binding);
        User targetUser = null;

        if (user.isBindToExistingUser()) {
            targetUser = repository.findByUsername(user.getUsername());

            if (targetUser == null) {
                throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot find user by username: " + user.getUsername());
            } else if (!StringUtils.hasText(targetUser.getPassword())) {
                throw new BaseServiceException(ErrorCode.WRONG_PASSWORD, "The target user has no password set. Cannot bind to OAuth account.", HttpStatus.PRECONDITION_FAILED);
            }

            if (!BCrypt.checkpw(user.getPassword(), targetUser.getPassword())) {
                throw new BaseServiceException(ErrorCode.WRONG_PASSWORD, "Wrong password", HttpStatus.PRECONDITION_FAILED);
            }

            binding.setUser(targetUser);
        } else {
            targetUser = this.create(user);
            log.info("Created new user from {} OAuth: {}", binding.getProvider(), targetUser);
            binding.setUser(targetUser);
        }

        binding.setId(null);
        binding.setAllTraceInfo(targetUser);
        bindRepository.save(binding);
        log.info("Created new OAuth binding from {} to user: {}", binding.getProvider(), targetUser);

        return targetUser;
    }

    private void verifyBinding(UserBinding binding) {
        if (!StringUtils.hasText(binding.getProvider())) {
            throw new BadRequestException("Bad binding info, OAuth provider is missing");
        }

        if (!StringUtils.hasText(binding.getExtUserId())) {
            throw new BadRequestException("Bad binding info, external user ID is missing");
        }

        if (!this.oauthProvidersMap.containsKey(binding.getProvider())) {
            throw new BadRequestException("Bad binding info, unsupported OAuth provider: " + binding.getProvider());
        }

        UserBinding existingBind = bindRepository.getByProviderAndExtUserId(binding.getProvider(), binding.getExtUserId());

        if (existingBind != null) {
            throw new EntityDuplicatedException("This " + binding.getProvider() + " account is already linked to another user");
        }
    }

    @Override
    protected Specification<User> toSpec(UserFilter filter) {
        Specification<User> querySpec = null;

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<User> nameLike = UserSpecs.nicknameLike(keyword).or(UserSpecs.usernameLike(keyword));

            try {
                Long id = Long.parseLong(filter.getKeyword());
                querySpec = nameLike.or(UserSpecs.idEquals(id));
            } catch (NumberFormatException e) {
                querySpec = nameLike;
            }
        }

        if (filter.getEnabled() != null) {
            Specification<User> isEnabled = UserSpecs.enabledEquals(filter.getEnabled());

            querySpec = querySpec == null ? isEnabled : querySpec.and(isEnabled);
        }

        return querySpec;
    }
}
