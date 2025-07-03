package com.shimi.gogoscrum.project.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.project.model.ProjectMember;
import com.shimi.gogoscrum.project.model.ProjectMemberRole;
import com.shimi.gogoscrum.user.dto.UserDto;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class ProjectMemberDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -8300853725752033328L;

    private ProjectDto project;
    private UserDto user;
    private ProjectMemberRole role;
    private ProjectMember.JoinChannel joinChannel;
    private Long invitationId;

    @Override
    public ProjectMember toEntity() {
        ProjectMember entity = new ProjectMember();
        BeanUtils.copyProperties(this, entity, "project", "user");
        entity.setProject(this.project.toEntity());
        entity.setUser(this.user.toEntity());
        return entity;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public ProjectMemberRole getRole() {
        return role;
    }

    public void setRole(ProjectMemberRole role) {
        this.role = role;
    }

    public ProjectMember.JoinChannel getJoinChannel() {
        return joinChannel;
    }

    public void setJoinChannel(ProjectMember.JoinChannel joinChannel) {
        this.joinChannel = joinChannel;
    }

    public Long getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }
}
