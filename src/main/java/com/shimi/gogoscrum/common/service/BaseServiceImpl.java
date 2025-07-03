package com.shimi.gogoscrum.common.service;

import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.model.Filter;
import com.shimi.gsf.core.model.TraceableEntity;
import com.shimi.gsf.core.service.GeneralServiceImpl;

/**
 * Base class for all service implementations in the application.
 * This class provides common functionality for services that deal with entities
 * that are traceable and have a filter.
 *
 * @param <T> the type of the entity
 * @param <K> the type of the filter
 */
public abstract class BaseServiceImpl<T extends TraceableEntity, K extends Filter> extends GeneralServiceImpl<T, K> {
    @Override
    protected User getCurrentUser() {
        com.shimi.gsf.core.model.User user = super.getCurrentUser();
        return user != null ? (User) user : null;
    }
}
