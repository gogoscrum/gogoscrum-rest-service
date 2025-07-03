package com.shimi.gogoscrum.project.repository;

import com.shimi.gogoscrum.project.model.Invitation;
import com.shimi.gsf.core.repository.GeneralRepository;

public interface InvitationRepository extends GeneralRepository<Invitation> {
    Invitation findByCode(String invitationCode);
}
