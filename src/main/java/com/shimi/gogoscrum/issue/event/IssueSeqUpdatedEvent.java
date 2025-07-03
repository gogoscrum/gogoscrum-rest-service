package com.shimi.gogoscrum.issue.event;

import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.event.EntityChangeEvent;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

public class IssueSeqUpdatedEvent extends ApplicationEvent {
    @Serial
    private static final long serialVersionUID = 5207346015585796467L;
    private Long sprintId;
    private User updatedBy;

    public IssueSeqUpdatedEvent(Object source) {
        super(source);
    }

    public IssueSeqUpdatedEvent(Object source, Long sprintId) {
        super(source);
        this.sprintId = sprintId;
    }

    public IssueSeqUpdatedEvent(Object source, Long sprintId, User updatedBy) {
        super(source);
        this.sprintId = sprintId;
        this.updatedBy = updatedBy;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
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
