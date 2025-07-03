package com.shimi.gogoscrum.history.model;

import com.shimi.gsf.core.event.EntityChangeEvent;
import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class HistoryFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 1596366796096110821L;
    private String entityType;
    private Long entityId;
    private EntityChangeEvent.ActionType actionType;

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public EntityChangeEvent.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(EntityChangeEvent.ActionType actionType) {
        this.actionType = actionType;
    }
}
