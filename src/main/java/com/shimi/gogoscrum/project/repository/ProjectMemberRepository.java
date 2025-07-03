package com.shimi.gogoscrum.project.repository;

import com.shimi.gogoscrum.project.model.ProjectMember;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectMemberRepository extends GeneralRepository<ProjectMember> {
    ProjectMember findByProjectIdAndUserId(Long projectId, Long userId);
    Page<ProjectMember> findByInvitationId(Long invitationId, Pageable pageable);
}
