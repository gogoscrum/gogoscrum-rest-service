package com.shimi.gogoscrum.user.service;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.user.model.Preference;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.model.UserFilter;
import com.shimi.gsf.core.service.GeneralService;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * UserService is an interface that extends GeneralService and UserDetailsService.
 * It provides methods for user management, including finding project mates,
 * updating user basics, verifying passwords, updating passwords, and updating user last login info.
 */
public interface UserService extends GeneralService<User, UserFilter>, UserDetailsService {
    User updateUserBasics(Long id, User user);

    boolean checkPassword(Long id, String oldPassword);

    void updatePassword(Long id, String oldPassword, String newPassword);

    void updateLastLoginInfo(User user);

    User updatePreference(Long userId, Preference preference);

    User updateAvatar(Long userId, File avatarFile);

    void deleteAvatar(Long userId);
}
