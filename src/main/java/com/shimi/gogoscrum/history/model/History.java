package com.shimi.gogoscrum.history.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.history.dto.HistoryDto;
import com.shimi.gsf.core.event.EntityChangeEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class History extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 2341736350562167835L;

    private String entityType;
    private Long entityId;
    @Enumerated(EnumType.STRING)
    private EntityChangeEvent.ActionType actionType;
    private String details;

    @Override
    public HistoryDto toDto() {
        return this.toDto(true);
    }

    @Override
    public HistoryDto toDto(boolean detailed) {
        HistoryDto dto = new HistoryDto();
        BeanUtils.copyProperties(this, dto);
        if (this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto().normalize());
        }

        return dto;
    }

    public History() {
    }

    public History(Long id) {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("History{");
        sb.append("id=").append(id);
        sb.append(", entityType='").append(entityType).append('\'');
        sb.append(", entityId=").append(entityId);
        sb.append(", actionType=").append(actionType);
        sb.append(", details='").append(details).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
