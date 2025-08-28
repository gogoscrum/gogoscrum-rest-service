package com.shimi.gogoscrum.user.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.service.FileService;
import com.shimi.gogoscrum.user.model.Preference;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.model.UserFilter;
import com.shimi.gogoscrum.user.repository.UserRepository;
import com.shimi.gogoscrum.user.repository.UserSpecs;
import com.shimi.gsf.core.exception.BaseServiceException;
import com.shimi.gsf.core.exception.EntityDuplicatedException;
import com.shimi.gsf.core.exception.EntityNotFoundException;
import com.shimi.gsf.core.exception.NoPermissionException;
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
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, UserFilter> implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository repository;
    @Autowired
    private FileService fileService;

    @Override
    protected UserRepository getRepository() {
        return repository;
    }

    @Override
    protected void beforeCreate(User user) {
        this.verifyUsername(user);
        this.verifyPassword(user.getPassword());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
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
                throw new BaseServiceException("usernameCannotChange", "Username cannot be changed to: " + user.getUsername(), HttpStatus.BAD_REQUEST);
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
     * @param id User ID
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
        } else {
            log.debug("Loaded user by username {}: {}", username, user);
            return user;
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
