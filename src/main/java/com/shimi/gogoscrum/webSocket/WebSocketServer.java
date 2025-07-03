package com.shimi.gogoscrum.webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.shimi.gogoscrum.issue.event.GroupSeqUpdatedEvent;
import com.shimi.gogoscrum.issue.event.IssueSeqUpdatedEvent;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.event.EntityChangeEvent;
import com.shimi.gsf.core.model.Entity;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.EOFException;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket server for handling real-time updates on sprint boards.
 * It listens to events related to issues and issue groups, and broadcasts changes to connected clients.
 */
@Component
@ServerEndpoint("/websocket/{projectId}/{sprintId}")
public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static final Map<String, Long> sessionSprintMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> sessionProjectMap = new ConcurrentHashMap<>();
    private static final Map<Long, List<Session>> sprintSessionPool = new ConcurrentHashMap<>();
    private static final Map<Long, List<Session>> projectSessionPool = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("projectId") Long projectId, @PathParam("sprintId") Long sprintId) {
        addSessionIntoPool(session, projectId, sprintId);
        int joinedCount = onlineCount.incrementAndGet();

        if (log.isDebugEnabled()) {
            log.debug("New websocket connected to sprint board {} by session {} from user {}, total online users is {} now", sprintId,
                    session.getId(), getUserFromSession(session), joinedCount);
        }
    }

    private User getUserFromSession(Session session) {
        Principal userPrincipal = session.getUserPrincipal();
        if(userPrincipal != null) {
            return (User) ((Authentication) userPrincipal).getPrincipal();
        } else {
            return null;
        }
    }

    @OnClose
    public void onClose(Session session) {
        this.removeSessionFromPool(session);
        int joinedCount = onlineCount.decrementAndGet();

        if (log.isDebugEnabled()) {
            log.debug("Websocket connection closed by session {} from user {},  total online users is {} now",
                    session.getId(), getUserFromSession(session), joinedCount);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable instanceof EOFException) {
            // Client disconnected abruptly
            if (log.isDebugEnabled()) {
                log.debug("Websocket connection error occurred by session {} from user {}",
                        session.getId(), getUserFromSession(session));
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Websocket error in session {} from user {}: {}", session.getId(), getUserFromSession(session), throwable.getMessage(), throwable);
            }
        }

        try {
            session.close();
        } catch (IOException e) {
            log.error("Failed to close websocket session {}: {}", session.getId(), e.getMessage(), e);
        }
    }

    @EventListener
    public void onIssueOrGroupChange(EntityChangeEvent event) {
        Entity entity = Objects.requireNonNullElse(event.getUpdatedEntity(), event.getPreviousEntity());

        if (entity instanceof IssueGroup) {
            this.onGroupChanged(event);
        } else if (entity instanceof Issue) {
            this.onIssueChanged(event);
        }
    }

    private void onIssueChanged(EntityChangeEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Issue change event received: {}", event);
        }

        Issue issue = (Issue)Objects.requireNonNullElse(event.getUpdatedEntity(), event.getPreviousEntity());

        // Notify the board of the issue's current sprint
        BoardChangedEvent boardEvent = toBoardEvent(issue, event.getActionType());
        broadcastBoardEvent(boardEvent);

        if (event.getActionType().equals(EntityChangeEvent.ActionType.UPDATE)) {
            Issue oldIssue = (Issue)event.getPreviousEntity();
            // If the issue's sprint is changed, then notify the board of the old sprint
            if (!Objects.equals(issue.getSprint().getId(), oldIssue.getSprint().getId())) {
                boardEvent.setSprintId(oldIssue.getSprint().getId());
                broadcastBoardEvent(boardEvent);
            }
        }
    }

    private void onGroupChanged(EntityChangeEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Issue group change event received: {}", event);
        }

        IssueGroup issueGroup = (IssueGroup)Objects.requireNonNullElse(event.getUpdatedEntity(), event.getPreviousEntity());
        broadcastBoardEvent(toBoardEvent(issueGroup, event.getActionType()));
    }

    @EventListener
    public void onIssueSeqChanged(IssueSeqUpdatedEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Issue sequence change event received: {}", event);
        }

        broadcastBoardEvent(toBoardEvent(event));
    }

    @EventListener
    public void onGroupSeqChanged(GroupSeqUpdatedEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Issue group sequence change event received: {}", event);
        }

        broadcastBoardEvent(toBoardEvent(event));
    }

    private BoardChangedEvent toBoardEvent(Issue issue, EntityChangeEvent.ActionType actionType) {
        BoardChangedEvent boardEvent = new BoardChangedEvent();
        User updatedBy = issue.getUpdatedBy();

        boardEvent.setSprintId(issue.getSprint() != null ? issue.getSprint().getId() : null);
        boardEvent.setEventType(BoardChangedEvent.BoardEventType.ISSUE_CHANGED);
        boardEvent.setActionType(actionType);
        boardEvent.setSourceId(issue.getId());
        boardEvent.setSourceName(issue.getProject().getCode() + "-" + issue.getCode());
        boardEvent.setUpdatedByUserId(updatedBy.getId());
        boardEvent.setUpdatedByUserNickname(updatedBy.getNickname());
        boardEvent.setUpdatedByUserAvatar(updatedBy.getAvatarUrl());

        return boardEvent;
    }

    private BoardChangedEvent toBoardEvent(IssueSeqUpdatedEvent seqEvent) {
        BoardChangedEvent boardEvent = new BoardChangedEvent();

        IssueGroup issueGroup = (IssueGroup) seqEvent.getSource();
        User updatedBy = seqEvent.getUpdatedBy();

        boardEvent.setSprintId(seqEvent.getSprintId());
        boardEvent.setEventType(BoardChangedEvent.BoardEventType.ISSUE_SEQ_CHANGED);
        boardEvent.setActionType(seqEvent.getActionType());
        boardEvent.setSourceId(issueGroup.getId());
        boardEvent.setSourceName(issueGroup.getLabel());
        boardEvent.setUpdatedByUserId(updatedBy.getId());
        boardEvent.setUpdatedByUserNickname(updatedBy.getNickname());
        boardEvent.setUpdatedByUserAvatar(updatedBy.getAvatarUrl());

        return boardEvent;
    }

    private BoardChangedEvent toBoardEvent(IssueGroup group, EntityChangeEvent.ActionType actionType) {
        BoardChangedEvent boardEvent = new BoardChangedEvent();
        User updatedBy = group.getUpdatedBy();

        boardEvent.setProjectId(group.getProject().getId());
        boardEvent.setEventType(BoardChangedEvent.BoardEventType.ISSUE_GROUP_CHANGED);
        boardEvent.setActionType(actionType);
        boardEvent.setSourceId(group.getId());
        boardEvent.setSourceName(group.getLabel());
        boardEvent.setUpdatedByUserId(updatedBy.getId());
        boardEvent.setUpdatedByUserNickname(updatedBy.getNickname());
        boardEvent.setUpdatedByUserAvatar(updatedBy.getAvatarUrl());

        return boardEvent;
    }

    private BoardChangedEvent toBoardEvent(GroupSeqUpdatedEvent seqEvent) {
        BoardChangedEvent boardEvent = new BoardChangedEvent();
        User updatedBy = seqEvent.getUpdatedBy();

        boardEvent.setProjectId(seqEvent.getProjectId());
        boardEvent.setEventType(BoardChangedEvent.BoardEventType.ISSUE_GROUP_SEQ_CHANGED);
        boardEvent.setActionType(seqEvent.getActionType());
        boardEvent.setUpdatedByUserId(updatedBy.getId());
        boardEvent.setUpdatedByUserNickname(updatedBy.getNickname());
        boardEvent.setUpdatedByUserAvatar(updatedBy.getAvatarUrl());

        return boardEvent;
    }

    private void broadcastBoardEvent(BoardChangedEvent boardChangedEvent) {
        Long sprintId = boardChangedEvent.getSprintId();
        Long projectId = boardChangedEvent.getProjectId();

        // There are two types of board event, one will only affect a single sprint board (e.g. issue and issue seq changes),
        // the other will affect the boards of all sprints in a project (e.g. issue group and group seq changes).

        if (sprintId != null && sprintSessionPool.containsKey(sprintId)) {
            this.sendBoardEvent(sprintSessionPool.get(sprintId), boardChangedEvent);
        } else if (projectId != null && projectSessionPool.containsKey(projectId)) {
            this.sendBoardEvent(projectSessionPool.get(projectId), boardChangedEvent);
        }
    }

    private void sendBoardEvent(List<Session> sessions, BoardChangedEvent boardEvent) {
        try {
            // To ensure the snowflake ID is serialized as a string
            ObjectMapper mapper = new ObjectMapper().registerModule(
                    new SimpleModule().addSerializer(Long.class, new ToStringSerializer()));
            String msgBody = mapper.writeValueAsString(boardEvent);
            sessions.forEach(session -> {
                User userFromSession = getUserFromSession(session);
                Long updatedByUserId = boardEvent.getUpdatedByUserId();

                if (userFromSession != null && !userFromSession.getId().equals(updatedByUserId)) {
                    // Don't need to notify the user himself/herself who made the change
                    sendMsg(session, msgBody);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error caught while parsing BoardChangedEvent to string:", e);
        }
    }

    public void sendMsg(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);

            if (log.isTraceEnabled()) {
                log.trace("Websocket msg sent to session {} for user: {}", session.getId(), getUserFromSession(session));
            }
        } catch (IOException e) {
            log.error("Failed to send websocket message:", e);
        }
    }

    private void addSessionIntoPool(Session session, Long projectId,  Long sprintId) {
        sessionSprintMap.put(session.getId(), sprintId);
        sessionProjectMap.put(session.getId(), projectId);

        if (!sprintSessionPool.containsKey(sprintId)) {
            List<Session> sessions = new CopyOnWriteArrayList<>(Arrays.asList(session));
            sprintSessionPool.put(sprintId, sessions);
        } else {
            sprintSessionPool.get(sprintId).add(session);
        }

        if (!projectSessionPool.containsKey(projectId)) {
            List<Session> sessions = new CopyOnWriteArrayList<>(Arrays.asList(session));
            projectSessionPool.put(projectId, sessions);
        } else {
            projectSessionPool.get(projectId).add(session);
        }
    }

    private void removeSessionFromPool(Session session) {
        Long sprintId = sessionSprintMap.get(session.getId());
        Long projectId = sessionProjectMap.get(session.getId());

        if (sprintId != null && sprintSessionPool.containsKey(sprintId)) {
            sessionSprintMap.remove(session.getId());
            sprintSessionPool.get(sprintId).remove(session);
        }

        if (projectId != null && projectSessionPool.containsKey(projectId)) {
            sessionProjectMap.remove(session.getId());
            projectSessionPool.get(projectId).remove(session);
        }
    }
}
