package com.shimi.gogoscrum.common.util;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.exception.NoPermissionException;

import java.util.Objects;

public class PermissionUtil {
    private PermissionUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static void checkOwnership(BaseEntity entity, User currentUser) {
        if (!Objects.equals(entity.getCreatedBy().getId(), currentUser.getId())) {
            throw new NoPermissionException("You are not the owner the specified entity.");
        }
    }
}
