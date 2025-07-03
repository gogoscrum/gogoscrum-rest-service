package com.shimi.gogoscrum.issue.event;

import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.event.EntityChangeEvent;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

public class GroupSeqUpdatedEvent extends ApplicationEvent {
    @Serial
    private static final long serialVersionUID = 8135104929334441558L;
    private Long projectId;
    private User updatedBy;

    public GroupSeqUpdatedEvent(Object source) {
        super(source);
    }

    public GroupSeqUpdatedEvent(Object source, Long projectId) {
        super(source);
        this.projectId = projectId;
    }

    public GroupSeqUpdatedEvent(Object source, Long projectId, User updatedBy) {
        super(source);
        this.projectId = projectId;
        this.updatedBy = updatedBy;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public EntityChangeEvent.ActionType getActionType() {
        return EntityChangeEvent.ActionType.UPDATE;
    }
}
