package com.shimi.gogoscrum.project.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.common.util.DateTimeUtil;
import com.shimi.gogoscrum.common.util.RandomToolkit;
import com.shimi.gogoscrum.project.model.Invitation;
import com.shimi.gogoscrum.project.model.InvitationFilter;
import com.shimi.gogoscrum.project.repository.InvitationRepository;
import com.shimi.gogoscrum.project.repository.InvitationSpecs;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.exception.BaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class InvitationServiceImpl extends BaseServiceImpl<Invitation, InvitationFilter> implements InvitationService {
    private static final Logger log = LoggerFactory.getLogger(InvitationServiceImpl.class);
    public static final int INVITATION_CODE_LENGTH = 12;
    public static final int INVITATION_CODE_MAX_TRY = 3;

    @Autowired
    InvitationRepository repository;
    @Autowired
    ProjectService projectService;

    @Override
    protected InvitationRepository getRepository() {
        return this.repository;
    }

    @Override
    protected void beforeCreate(Invitation invitation) {
        ProjectMemberUtils.checkDeveloper(projectService.get(invitation.getProjectId()), getCurrentUser());
        this.preprocess(invitation);
    }

    @Override
    protected void beforeUpdate(Long id, Invitation existingEntity, Invitation newEntity) {
        ProjectMemberUtils.checkDeveloper(projectService.get(newEntity.getProjectId()), getCurrentUser());
        this.preprocess(newEntity);
    }

    private void preprocess(Invitation invitation) {
        if (invitation.getId() == null) {
            invitation.setCode(this.generateNewCode());
        }

        if (invitation.getValidDays() == null || invitation.getValidDays() >= 9999 )  {
            invitation.setExpireTime(null);
        } else {
            invitation.setExpireTime(DateTimeUtil.daysLater(invitation.getValidDays()));
        }
    }

    private String generateNewCode() {
        for (int i = 0; i < INVITATION_CODE_MAX_TRY; i++) {
            String randomString = RandomToolkit.getRandomString(INVITATION_CODE_LENGTH);

            Invitation invitation = this.findByCode(randomString);

            if (invitation == null) {
                return randomString;
            } else if (log.isDebugEnabled()) {
                log.debug("Duplicated invitation code {} generated, will try again", randomString);
            }
        }

        throw new BaseServiceException("invitationCodeGenerationFail",
                "Failed to generate new invitation code after max tries", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Invitation findByCode(String invitationCode) {
        return repository.findByCode(invitationCode);
    }

    @Override
    public Long increaseJoinCount(Long id) {
        Invitation invitation = get(id);
        Long joinCount = Optional.ofNullable(invitation.getJoinCount()).orElse(0L);
        invitation.setJoinCount(joinCount + 1);
        this.repository.save(invitation);

        if (log.isDebugEnabled()) {
            log.debug("Increased joinCount of invitation {} to {}", id, invitation.getJoinCount());
        }

        return invitation.getJoinCount();
    }

    @Override
    protected Specification<Invitation> toSpec(InvitationFilter filter) {
        Specification<Invitation> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = InvitationSpecs.projectIdEqual(filter.getProjectId());
        } else {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "Project ID is required to query invitations",
                    HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.hasText(filter.getCode())) {
            Specification<Invitation> codeLike = InvitationSpecs.codeLike(filter.getCode());

            querySpec = querySpec.and(codeLike);
        }

        if (filter.getActive() != null) {
            Specification<Invitation> statusSpec = null;

            if (filter.getActive()) {
                statusSpec = InvitationSpecs.enabled().and(InvitationSpecs.notExpired());
            } else {
                statusSpec = InvitationSpecs.disabled().or(InvitationSpecs.expired());
            }

            querySpec = querySpec.and(statusSpec);
        }

        return querySpec;
    }

    @Override
    public Invitation disableInvitation(Long id) {
        return this.updatedInvitationStatus(id, Boolean.FALSE);
    }

    @Override
    public Invitation enableInvitation(Long id) {
        return this.updatedInvitationStatus(id, Boolean.TRUE);
    }

    private Invitation updatedInvitationStatus(Long id, Boolean status) {
        Invitation invitation = get(id);

        if (invitation.getEnabled() == null || !invitation.getEnabled().equals(status)) {
            User currentUser = getCurrentUser();

            invitation.setEnabled(status);
            invitation.setUpdateTraceInfo(currentUser);

            repository.save(invitation);

            log.info("Invitation {} enable status updated to {} by user {}", invitation, status, currentUser);
        } else {
            if (log.isDebugEnabled()) {
                log.info("Invitation {} enable status is already in {}", invitation, status);
            }
        }

        return invitation;
    }
}
