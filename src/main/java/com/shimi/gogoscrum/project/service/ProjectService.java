package com.shimi.gogoscrum.project.service;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.project.dto.SprintVelocityDto;
import com.shimi.gogoscrum.project.model.ProjectFilter;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.project.model.ProjectMember;
import com.shimi.gogoscrum.project.model.ProjectMemberRole;
import com.shimi.gsf.core.model.EntityQueryResult;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface ProjectService extends GeneralService<Project, ProjectFilter> {
    String generateNextIssueCode(Long projectId);
    Project joinProject(String invitationCode);
    void quitProject(Long projectId);
    Project archive(Long id);
    Project unarchive(Long id);
    Project transferTo(Long projectId, Long userId, boolean quit);
    List<SprintVelocityDto> getProjectVelocity(Long projectId);
    ProjectMember createMember(ProjectMember projectMember);
    void deleteMember(Long memberId);
    ProjectMember updateMemberRole(Long memberId, ProjectMemberRole role);
    EntityQueryResult<ProjectMember> findMembersByInvitation(Long invitationId, int page, int pageSize);
    Project updateAvatar(Long projectId, File avatarFile);
    void deleteAvatar(Long projectId);
}
