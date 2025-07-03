package com.shimi.gogoscrum.project.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.project.dto.ProjectMemberDto;
import com.shimi.gogoscrum.user.model.User;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class ProjectMember extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 3968021065061271934L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProjectMemberRole role;

    @Enumerated(EnumType.STRING)
    private JoinChannel joinChannel;

    private Long invitationId;

    public ProjectMember() {
    }

    public ProjectMember(Project project, User user) {
        super();
        this.project = project;
        this.user = user;
    }

    public ProjectMember(Project project, User user, ProjectMemberRole role) {
        super();
        this.project = project;
        this.user = user;
        this.role = role;
    }

    @Override
    public ProjectMemberDto toDto() {
        return this.toDto(false);
    }

    @Override
    public ProjectMemberDto toDto(boolean detailed) {
        ProjectMemberDto dto = new ProjectMemberDto();
        BeanUtils.copyProperties(this, dto, "project", "user");
        dto.setUser(user.toDto());
        if (detailed) {
            dto.setProject(project.toDto());
        }
        return dto;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProjectMemberRole getRole() {
        return role;
    }

    public void setRole(ProjectMemberRole role) {
        this.role = role;
    }

    public JoinChannel getJoinChannel() {
        return joinChannel;
    }

    public void setJoinChannel(JoinChannel joinChannel) {
        this.joinChannel = joinChannel;
    }

    public Long getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    @Override
    public String toString() {
        return "ProjectMember{" +
                "project=" + project.getId() +
                ", user=" + user.getId() +
                ", type=" + role +
                '}';
    }

    public enum JoinChannel {
        CREATOR, MANUAL, INVITATION
    }
}
