package com.shimi.gogoscrum.common.model;

import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.model.SnowflakeIdTraceableEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

/**
 * Base class for all entities in the application.
 * This class contains common fields and methods that are shared across all entities.
 */
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseEntity extends SnowflakeIdTraceableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    protected User createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    protected User updatedBy;

    @Override
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(com.shimi.gsf.core.model.User createdBy) {
        if (createdBy instanceof User) {
            this.createdBy = (User) createdBy;
        }
    }

    @Override
    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(com.shimi.gsf.core.model.User updatedBy) {
        if (updatedBy instanceof User) {
            this.updatedBy = (User) updatedBy;
        }
    }
}
