package com.shimi.gogoscrum.history.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.history.model.History;
import com.shimi.gsf.core.event.EntityChangeEvent;

import java.io.Serial;

public class HistoryDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -9211521244422382723L;

    private String entityType;
    private Long entityId;
    private EntityChangeEvent.ActionType actionType;
    private String details;

    @Override
    public History toEntity() {
        throw new RuntimeException("Not supported action");
    }

    public HistoryDto() {
    }

    public HistoryDto(Long id) {
        this.id = id;
    }

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
