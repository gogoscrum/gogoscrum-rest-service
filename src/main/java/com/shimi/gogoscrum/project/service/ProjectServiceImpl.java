package com.shimi.gogoscrum.project.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.common.util.DateTimeUtil;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.TargetType;
import com.shimi.gogoscrum.file.service.FileService;
import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gogoscrum.issue.model.IssueGroupStatus;
import com.shimi.gogoscrum.issue.service.IssueGroupService;
import com.shimi.gogoscrum.project.dto.SprintVelocityDto;
import com.shimi.gogoscrum.project.model.*;
import com.shimi.gogoscrum.project.repository.ProjectMemberRepository;
import com.shimi.gogoscrum.project.repository.ProjectRepository;
import com.shimi.gogoscrum.project.repository.ProjectSpecs;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.sprint.service.SprintService;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.event.EntityChangeEvent;
import com.shimi.gsf.core.exception.BaseServiceException;
import com.shimi.gsf.core.exception.EntityNotFoundException;
import com.shimi.gsf.core.exception.NoPermissionException;
import com.shimi.gsf.core.model.Entity;
import com.shimi.gsf.core.model.EntityQueryResult;
import com.shimi.gsf.util.PageQueryResultConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project, ProjectFilter> implements ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);
    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectMemberRepository memberRepository;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private IssueGroupService issueGroupService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private FileService fileService;

    @Override
    protected ProjectRepository getRepository() {
        return repository;
    }

    @Override
    public Project get(Long id) {
        Project project = super.get(id);
        ProjectMemberUtils.checkMember(project, getCurrentUser());
        return project;
    }

    @Override
    protected void beforeCreate(Project project) {
        project.setLastIssueSeq(0L);

        if (StringUtils.hasText(project.getName())) {
            if (project.getStartDate() != null) {
                project.setStartDate(DateTimeUtil.getBeginningOfDay(project.getStartDate()));
            }

            if (project.getEndDate() != null) {
                project.setEndDate(DateTimeUtil.getEndingOfDay(project.getEndDate()));
            }
        } else {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "The project name cannot be empty",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void afterCreate(Project project) {
        // Set current user as the project owner
        ProjectMember owner = new ProjectMember(project, getCurrentUser(), ProjectMemberRole.OWNER);
        owner.setJoinChannel(ProjectMember.JoinChannel.CREATOR);
        owner.setAllTraceInfo(getCurrentUser());
        project.getProjectMembers().add(memberRepository.save(owner));

        // Create the project backlog
        this.sprintService.createBacklog(project);

        // Create built-in issue groups
        this.createBuiltInGroups(project);
    }

    private void createBuiltInGroups(Project project) {
        List<IssueGroup> defaultIssueGroups = new ArrayList<>();
        short seq = 0;
        for (IssueGroupStatus status : IssueGroupStatus.values()) {
            defaultIssueGroups.add(this.newBuiltInGroup(project, seq++, status));
        }
        List<IssueGroup> savedIssueGroups = this.issueGroupService.saveAll(defaultIssueGroups);
        project.getIssueGroups().addAll(savedIssueGroups);

        if (log.isDebugEnabled()) {
            log.debug("Created built-in issue groups for project {}: {}", project.getId(), savedIssueGroups);
        }
    }

    private IssueGroup newBuiltInGroup(Project project, short seq, IssueGroupStatus status) {
        IssueGroup issueGroup = new IssueGroup();

        issueGroup.setProject(project);
        issueGroup.setLabel(status.toString().replace("_", " "));
        issueGroup.setAllTraceInfo(getCurrentUser());
        issueGroup.setBuiltIn(true);
        issueGroup.setSeq(seq);
        issueGroup.setStatus(status);

        return issueGroup;
    }

    @Override
    protected void beforeUpdate(Long projectId, Project existingProject, Project project) {
        ProjectMemberUtils.checkOwner(existingProject, getCurrentUser());

        if (!StringUtils.hasText(project.getName())) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "The project name cannot be empty",
                    HttpStatus.BAD_REQUEST);
        }

        if (project.getStartDate() != null) {
            project.setStartDate(DateTimeUtil.getBeginningOfDay(project.getStartDate()));
        }

        if (project.getEndDate() != null) {
            project.setEndDate(DateTimeUtil.getEndingOfDay(project.getEndDate()));
        }
    }

    @Override
    protected void afterUpdate(Long projectId, Project existingProject, Project updatedProject) {
        updatedProject.setProjectMembers(existingProject.getProjectMembers());
    }

    protected String[] getUpdateIgnoredProps() {
        return new String[]{"id", "avatar", "lastIssueSeq", "issueGroups", "createdTime", "createdBy", "fileCount", "totalFileSize"};
    }

    @Override
    public String generateNextIssueCode(Long projectId) {
        Project project = get(projectId);

        Long nextIssueSeq = project.getLastIssueSeq() + 1;
        String nextIssueCode = String.valueOf(nextIssueSeq);

        project.setLastIssueSeq(nextIssueSeq);
        repository.save(project);

        if (log.isDebugEnabled()) {
            log.debug("New issue seq {} generated for project {}", nextIssueCode, projectId);
        }

        return nextIssueCode;
    }

    @Override
    protected Specification<Project> toSpec(ProjectFilter filter) {
        User currentUser = getCurrentUser();

        // A user can only see projects they are a member of
        Specification<Project> querySpec = ProjectSpecs.isProjectMember(currentUser);

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<Project> nameLike = ProjectSpecs.codeLike(keyword).or(ProjectSpecs.nameLike(keyword));
            querySpec = querySpec.and(nameLike);
        }

        if (filter.getDeleted() != null) {
            Specification<Project> deletedSpec = ProjectSpecs.deletedEquals(filter.getDeleted());

            querySpec = querySpec.and(deletedSpec);
        }

        if (filter.getArchived() != null) {
            Specification<Project> archivedSpec = ProjectSpecs.archivedEquals(filter.getArchived());

            querySpec = querySpec.and(archivedSpec);
        }

        return querySpec;
    }

    @Override
    public void delete(Long id) {
        Project project = get(id);
        ProjectMemberUtils.checkOwner(project, getCurrentUser());
        project.setDeleted(true);
        project.setUpdateTraceInfo(getCurrentUser());
        repository.save(project);
        log.info("Soft deleted Project: {}", project);
    }

    @Override
    public Project archive(Long id) {
        Project project = get(id);
        ProjectMemberUtils.checkOwner(project, getCurrentUser());
        project.setArchived(true);
        project.setUpdateTraceInfo(getCurrentUser());
        Project updatedProject = repository.save(project);
        log.info("Archived Project: {}", project);
        return updatedProject;
    }

    @Override
    public Project unarchive(Long id) {
        Project project = get(id);
        ProjectMemberUtils.checkOwner(project, getCurrentUser());

        project.setArchived(false);
        Project updatedProject = repository.save(project);
        log.info("Unarchived Project: {}", project);
        return updatedProject;
    }

    @Override
    public Project joinProject(String invitationCode) {
        Invitation invitation = invitationService.findByCode(invitationCode);

        if (invitation == null || Boolean.FALSE.equals(invitation.getEnabled()) ||
                (invitation.getExpireTime() != null && invitation.getExpireTime().before(new Date()))) {
            throw new BaseServiceException(ErrorCode.INVALID_INVITATION,
                    "Project invitation doesn't exist, expired or disabled", HttpStatus.NOT_ACCEPTABLE);
        }

        Project project = super.get(invitation.getProjectId());
        User currentUser = getCurrentUser();

        if (ProjectMemberUtils.isMember(project, currentUser)) {
            Map<String, Object> extendValues = new HashMap<>();
            extendValues.put("projectId", invitation.getProjectId());
            throw new BaseServiceException(ErrorCode.ALREADY_IN_PROJECT, "You're already in the project",
                    HttpStatus.NOT_ACCEPTABLE, extendValues);
        }

        ProjectMember projectMember = new ProjectMember(project, currentUser);

        projectMember.setRole(ProjectMemberRole.valueOf(invitation.getInvitationType().toString()));
        projectMember.setJoinChannel(ProjectMember.JoinChannel.INVITATION);
        projectMember.setInvitationId(invitation.getId());
        projectMember.setAllTraceInfo(currentUser);

        project.getProjectMembers().add(memberRepository.save(projectMember));

        log.info("User {} joined project {} via invitation {}", currentUser.getId(), project.getId(), invitation.getId());

        invitationService.increaseJoinCount(invitation.getId());

        return project;
    }

    @Override
    public void quitProject(Long projectId) {
        User currentUser = getCurrentUser();

        // Check if the user is a member of the project
        ProjectMember member = memberRepository.findByProjectIdAndUserId(projectId, currentUser.getId());
        if (member == null) {
            throw new NoPermissionException("You are not a member of the project");
        }

        // If the user is the owner, they cannot quit unless they transfer ownership
        if (ProjectMemberRole.OWNER.equals(member.getRole())) {
            throw new NoPermissionException("Project owner cannot quit the project without transferring ownership");
        }

        // Remove the member from the project
        memberRepository.delete(member);
        log.info("User {} quit project {}", currentUser.getId(), projectId);
    }

    @Override
    public List<SprintVelocityDto> getProjectVelocity(Long projectId) {
        Project project = this.get(projectId);
        List<SprintVelocityDto> velocities = this.repository.getProjectVelocity(projectId);

        Map<Long, SprintVelocityDto> velocityDtoMap = velocities.stream()
                .collect(Collectors.toMap(SprintVelocityDto::getSprintId, Function.identity()));

        List<Sprint> sprints = project.getSprints();
        Collections.reverse(sprints);

        return sprints.stream().filter(sprint -> !sprint.getBacklog()).map(sprint -> Optional.ofNullable(velocityDtoMap.get(sprint.getId()))
                .orElse(new SprintVelocityDto(sprint.getId(), sprint.getName(), sprint.getStartDate(), sprint.getEndDate(), 0L, 0D))).toList();
    }

    @Override
    public Project transferTo(Long projectId, Long newOwnerUserId, boolean quit) {
        Project project = this.get(projectId);
        ProjectMember oldOwnerMember = project.getOwnerMember();
        Long oldOwnerUserId = oldOwnerMember.getUser().getId();
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(oldOwnerUserId)) {
            ProjectMember newOwnerMember = project.getMemberByUserId(newOwnerUserId);

            if (newOwnerMember != null) {
                newOwnerMember.setRole(ProjectMemberRole.OWNER);
                newOwnerMember.setUpdateTraceInfo(currentUser);
                memberRepository.save(newOwnerMember);
                log.info("Project {} transferred from original owner {} to new owner {}", projectId, oldOwnerUserId, newOwnerUserId);

                if (quit) {
                    memberRepository.delete(oldOwnerMember);
                    log.info("Old owner {} quit project {}", oldOwnerUserId, projectId);
                } else {
                    oldOwnerMember.setRole(ProjectMemberRole.DEVELOPER);
                    oldOwnerMember.setUpdateTraceInfo(currentUser);
                    memberRepository.save(oldOwnerMember);
                    log.info("Updated project {} owner {} to {}", projectId, oldOwnerUserId, oldOwnerMember.getRole());
                }

                return this.get(projectId);
            } else {
                throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "The specified new owner is not a member of the project",
                        HttpStatus.PRECONDITION_FAILED);
            }
        } else {
            throw new NoPermissionException("You are not the owner of the project");
        }
    }

    public ProjectMember createMember(ProjectMember member) {
        ProjectMemberUtils.checkDeveloper(get(member.getProject().getId()), getCurrentUser());

        // Check if the user is already a member of the project
        ProjectMember existingMember =
                memberRepository.findByProjectIdAndUserId(member.getProject().getId(), member.getUser().getId());
        if (existingMember != null) {
            throw new BaseServiceException(ErrorCode.ALREADY_IN_PROJECT, "User is already a member of the project",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        member.setAllTraceInfo(getCurrentUser());
        ProjectMember savedMember = memberRepository.save(member);
        log.info("Project member created: {}", savedMember);
        return member;
    }

    @Override
    public void deleteMember(Long memberId) {
        ProjectMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Project member not found by id: " + memberId));

        // Current user must be a member of the project
        ProjectMemberUtils.checkMember(member.getProject(), getCurrentUser());

        // Project owner cannot be deleted
        if (ProjectMemberRole.OWNER.equals(member.getRole())) {
            throw new NoPermissionException("Project owner cannot be deleted");
        }

        // Only project owner can delete other members
        if (!member.getUser().getId().equals(getCurrentUser().getId())) {
            ProjectMemberUtils.checkOwner(member.getProject(), getCurrentUser());
        }

        // Developers and guests can quit project by themselves
        memberRepository.delete(member);
        log.info("Project member deleted: {}", member);
    }

    @Override
    public ProjectMember updateMemberRole(Long memberId, ProjectMemberRole role) {
        ProjectMember result = null;

        Optional<ProjectMember> memberOptional = memberRepository.findById(memberId);

        if (memberOptional.isPresent()) {
            ProjectMember member = memberOptional.get();
            ProjectMemberRole oldRole = member.getRole();

            ProjectMemberUtils.checkOwner(member.getProject(), getCurrentUser());

            if (role != null && !Objects.equals(member.getRole(), role)) {
                member.setRole(role);
                member.setUpdateTraceInfo(getCurrentUser(true));
                result = memberRepository.save(member);

                log.debug("Project member {} role updated from {} to {}", memberId, oldRole, role);
            } else {
                result = member;
                if (log.isDebugEnabled()) {
                    log.debug("Project member {} role {} not changed, request will be ignored.", memberId, role);
                }
            }
        } else {
            throw new EntityNotFoundException("Project member not found by id: " + memberId);
        }

        return result;
    }

    @Override
    public EntityQueryResult<ProjectMember> findMembersByInvitation(Long invitationId, int page, int pageSize) {
        Page<ProjectMember> members = memberRepository.findByInvitationId(invitationId, PageRequest.of(page, pageSize, Sort.Direction.DESC, "id"));
        return PageQueryResultConverter.toQueryResult(members);
    }

    @Override
    public Project updateAvatar(Long projectId, File avatarFile) {
        Project project = get(projectId);
        ProjectMemberUtils.checkOwner(project, getCurrentUser());

        Long oldFileId = null;
        if (project.getAvatar() != null) {
            oldFileId = project.getAvatar().getId();
        }

        // Create new avatar file and link it to the project
        File savedAvatar = fileService.create(avatarFile);
        project.setAvatar(savedAvatar);
        project.setUpdateTraceInfo(getCurrentUser());
        Project updatedProject = repository.save(project);

        // Delete old avatar file if exists
        if (oldFileId != null) {
            fileService.delete(oldFileId);
        }

        log.info("Project {} avatar file updated to {}", projectId, savedAvatar.getId());
        this.updateFileCountAndTotalSize(projectId, 1, avatarFile.getSize());
        return updatedProject;
    }

    @Override
    public void deleteAvatar(Long projectId) {
        Project project = get(projectId);
        ProjectMemberUtils.checkOwner(project, getCurrentUser());

        File avatarFile = project.getAvatar();
        if (avatarFile != null) {
            Long fileId = avatarFile.getId();
            // Unlink the avatar from project first, then delete the file
            project.setAvatar(null);
            project.setUpdateTraceInfo(getCurrentUser());
            repository.save(project);
            fileService.delete(fileId);
            log.info("Project {} avatar file {} deleted.", projectId, fileId);
            this.updateFileCountAndTotalSize(projectId, -1, -avatarFile.getSize());
        } else {
            log.warn("Project {} has no avatar file to delete", projectId);
        }
    }

    /**
     * Handles file creation, updating and deletion events to update the project file count and total size.
     * This method is transactional and runs in a separate transaction to ensure thread safety and data integrity.
     * @param event The entity change event containing the file information.
     */
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onFileChanged(EntityChangeEvent event) {
        Entity entity = Objects.requireNonNullElse(event.getUpdatedEntity(), event.getPreviousEntity());
        EntityChangeEvent.ActionType actionType = event.getActionType();

        // Only issue attachments and project files creation or deletion events need to be handled
        if (!(entity instanceof File file)
                || file.getFolder()
                || file.getProjectId() == null
                || Objects.equals(file.getTargetType(), TargetType.PROJECT_AVATAR)
                || EntityChangeEvent.ActionType.UPDATE.equals(actionType)) {
            return;
        }

        Long projectId = file.getProjectId();
        this.updateFileCountAndTotalSize(projectId,
                EntityChangeEvent.ActionType.CREATE.equals(actionType) ? 1 : -1,
                EntityChangeEvent.ActionType.CREATE.equals(actionType) ? file.getSize() : -file.getSize());

        if (log.isDebugEnabled()) {
            log.debug("Project {} file count and total file size refreshed.", projectId);
        }
    }

    /**
     * Updates the file count and total size of a project. Thread safety is guaranteed by this repository implementation,
     * otherwise it may cause data overring by multiple threads.
     *
     * @param projectId   The ID of the project to update.
     * @param fileCountDiff The difference in file count (can be negative).
     * @param sizeDiff    The difference in total file size (can be negative).
     */
    private void updateFileCountAndTotalSize(Long projectId, long fileCountDiff, long sizeDiff) {
        repository.updateFileCountAndTotalSize(projectId, fileCountDiff, sizeDiff);
        log.debug("Project {} file count diff {}, total size diff {}", projectId, fileCountDiff, sizeDiff);
    }
}
