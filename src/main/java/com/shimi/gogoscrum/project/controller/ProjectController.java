package com.shimi.gogoscrum.project.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.project.dto.InvitationDto;
import com.shimi.gogoscrum.project.dto.ProjectDto;
import com.shimi.gogoscrum.project.dto.ProjectMemberDto;
import com.shimi.gogoscrum.project.dto.SprintVelocityDto;
import com.shimi.gogoscrum.project.model.*;
import com.shimi.gogoscrum.project.service.InvitationService;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.dto.Dto;
import com.shimi.gsf.core.dto.DtoQueryResult;
import com.shimi.gsf.core.model.EntityQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/projects")
@CrossOrigin
@Tag(name = "Project", description = "Project management")
@RolesAllowed({User.ROLE_USER})
public class ProjectController extends BaseController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private InvitationService invitationService;

    @Operation(summary = "Create a new project")
    @PostMapping
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        Project project = projectDto.toEntity();
        Project savedProject = projectService.create(project);
        return savedProject.toDto(true);
    }

    @Operation(summary = "Search projects")
    @Parameters({@Parameter(name = "filter", description = "Project search filter")})
    @GetMapping
    public DtoQueryResult<Dto> search(ProjectFilter filter) {
        EntityQueryResult<Project> queryResult = projectService.search(Objects.requireNonNullElse(filter, new ProjectFilter()));
        return queryResult.toDto();
    }

    @Operation(summary = "Get a project")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @GetMapping("/{id}")
    public ProjectDto get(@PathVariable Long id) {
        Project project = projectService.get(id);
        return project.toDto(true);
    }

    @Operation(summary = "Update a project")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @PutMapping("/{id}")
    public ProjectDto update(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        Project updatedProject = projectService.update(id, projectDto.toEntity());
        return updatedProject.toDto(true);
    }

    @Operation(summary = "Delete a project")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }

    @Operation(summary = "Archive a project")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @PostMapping("/{id}/archive")
    public ProjectDto archive(@PathVariable Long id) {
        return projectService.archive(id).toDto(true);
    }

    @Operation(summary = "Unarchive a project")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @DeleteMapping("/{id}/archive")
    public ProjectDto unarchive(@PathVariable Long id) {
        return projectService.unarchive(id).toDto(true);
    }

    @Operation(summary = "Create a project invitation")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @PostMapping("/{id}/invitations")
    public InvitationDto invite(@PathVariable Long id, @RequestBody InvitationDto invitationDto) {
        return invitationService.create(invitationDto.toEntity()).toDto();
    }

    @Operation(summary = "Update a project invitation")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "invitationId", description = "The invitation ID")
    })
    @PutMapping("/{id}/invitations/{invitationId}")
    public InvitationDto updateInvitation(@PathVariable Long id, @PathVariable Long invitationId, @RequestBody InvitationDto invitationDto) {
        return invitationService.update(invitationId, invitationDto.toEntity()).toDto();
    }

    @Operation(summary = "Join a project via invitation")
    @Parameters({@Parameter(name = "invitationCode", description = "The invitation code")})
    @PostMapping("/invitations/{invitationCode}")
    public ProjectDto joinProject(@PathVariable String invitationCode) {
        Project project = projectService.joinProject(invitationCode);
        return project.toDto(true);
    }

    @Operation(summary = "Search project invitations")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @GetMapping("/{id}/invitations")
    public DtoQueryResult<Dto> searchInvitations(@PathVariable Long id, InvitationFilter filter) {
        filter.setProjectId(id);
        EntityQueryResult<Invitation> queryResult = invitationService.search(filter);
        return queryResult.toDto();
    }

    @Operation(summary = "Disable an invitation")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "invitationId", description = "The invitation ID")
    })
    @DeleteMapping("/{id}/invitations/{invitationId}/enable")
    public InvitationDto disableInvitation(@PathVariable Long id, @PathVariable Long invitationId) {
        Invitation invitation = invitationService.disableInvitation(invitationId);
        return invitation.toDto();
    }

    @Operation(summary = "Enable an invitation")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "invitationId", description = "The invitation ID")
    })
    @PostMapping("/{id}/invitations/{invitationId}/enable")
    public InvitationDto enableInvitation(@PathVariable Long id, @PathVariable Long invitationId) {
        Invitation invitation = invitationService.enableInvitation(invitationId);
        return invitation.toDto();
    }

    @Operation(summary = "Transfer project to new owner")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "userId", description = "User ID of new owner"),
            @Parameter(name = "quit", description = "If to quit the project"),
    })
    @PutMapping("/{id}/owner")
    public ProjectDto transfer(@PathVariable Long id, @RequestParam Long userId, @RequestParam(required = false) boolean quit) {
        Project project = projectService.transferTo(id, userId, quit);
        return project.toDto(true);
    }

    @Operation(summary = "Create new project member")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @PostMapping("/{id}/members")
    public ProjectMemberDto createMember(@PathVariable Long id, @RequestBody ProjectMemberDto projectMemberDto) {
        ProjectMember projectMember = projectMemberDto.toEntity();
        projectMember.setJoinChannel(ProjectMember.JoinChannel.MANUAL);
        ProjectMember savedProjectMember = projectService.createMember(projectMember);
        return savedProjectMember.toDto(true);
    }

    @Operation(summary = "Delete a project member")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "memberId", description = "The member ID")})
    @DeleteMapping("/{id}/members/{memberId}")
    public void deleteMember(@PathVariable Long id, @PathVariable Long memberId) {
        projectService.deleteMember(memberId);
    }

    @Operation(summary = "Quit a project")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @DeleteMapping("/{id}/members/myself")
    public void quitProject(@PathVariable Long id) {
        projectService.quitProject(id);
    }

    @Operation(summary = "Set a member as guest")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "memberId", description = "Project member ID")
    })
    @PutMapping("/{id}/members/{memberId}/guest")
    public ProjectMemberDto setAsGuest(@PathVariable Long memberId) {
        return projectService.updateMemberRole(memberId, ProjectMemberRole.GUEST).toDto(true);
    }

    @Operation(summary = "Set a member as developer")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "memberId", description = "Project member ID")
    })
    @PutMapping("/{id}/members/{memberId}/developer")
    public ProjectMemberDto setAsDeveloper(@PathVariable Long memberId) {
        return projectService.updateMemberRole(memberId, ProjectMemberRole.DEVELOPER).toDto(true);
    }

    @Operation(summary = "Get project members by invitation")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "invitationId", description = "The invitation ID"),
            @Parameter(name = "page", description = "The page number, starting from 1"),
            @Parameter(name = "pageSize", description = "The page size")})
    @GetMapping("/{id}/members")
    public DtoQueryResult<Dto> getMembers(
            @PathVariable Long id,
            @RequestParam(value = "invitationId") Long invitationId,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize) {
        EntityQueryResult<ProjectMember> queryResult = projectService.findMembersByInvitation(invitationId, page - 1, pageSize);
        return queryResult.toDto();
    }

    @Operation(summary = "Update project avatar")
    @Parameters({
            @Parameter(name = "id", description = "The project ID"),
            @Parameter(name = "fileDto", description = "Avatar image file")})
    @PutMapping("/{id}/avatar")
    public ProjectDto updateAvatar(@PathVariable Long id, @RequestBody FileDto fileDto) {
        Project project = projectService.updateAvatar(id, fileDto.toEntity());
        return project.toDto();
    }

    @Operation(summary = "Delete project avatar")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @DeleteMapping("/{id}/avatar")
    public void deleteAvatar(@PathVariable Long id) {
        projectService.deleteAvatar(id);
    }

    @Operation(summary = "Get team velocity of a project")
    @Parameters({@Parameter(name = "id", description = "The project ID")})
    @GetMapping("/{id}/velocity")
    public List<SprintVelocityDto> getProjectVelocity(@PathVariable Long id) {
        return projectService.getProjectVelocity(id);
    }
}
