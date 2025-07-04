package com.shimi.gogoscrum.socket;

import com.shimi.gsf.core.event.EntityChangeEvent;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an event that will be sent to front-end board page via WebSocket,
 * such as issue changes, sequence changes, or group changes.
 */
public class BoardChangedEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 4330358158143187841L;
    private Long projectId;
    private Long sprintId;
    private BoardEventType eventType;
    private EntityChangeEvent.ActionType actionType;
    private Long updatedByUserId;
    private String updatedByUserNickname;
    private String updatedByUserAvatar;
    private Long sourceId;
    private String sourceName;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public BoardEventType getEventType() {
        return eventType;
    }

    public void setEventType(BoardEventType eventType) {
        this.eventType = eventType;
    }

    public EntityChangeEvent.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(EntityChangeEvent.ActionType actionType) {
        this.actionType = actionType;
    }

    public Long getUpdatedByUserId() {
        return updatedByUserId;
    }

    public void setUpdatedByUserId(Long updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    public String getUpdatedByUserNickname() {
        return updatedByUserNickname;
    }

    public void setUpdatedByUserNickname(String updatedByUserNickname) {
        this.updatedByUserNickname = updatedByUserNickname;
    }

    public String getUpdatedByUserAvatar() {
        return updatedByUserAvatar;
    }

    public void setUpdatedByUserAvatar(String updatedByUserAvatar) {
        this.updatedByUserAvatar = updatedByUserAvatar;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public enum BoardEventType {
        ISSUE_CHANGED, ISSUE_SEQ_CHANGED, ISSUE_GROUP_CHANGED, ISSUE_GROUP_SEQ_CHANGED
    }
}
