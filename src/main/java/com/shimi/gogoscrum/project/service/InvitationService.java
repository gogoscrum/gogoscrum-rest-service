package com.shimi.gogoscrum.project.service;

import com.shimi.gogoscrum.project.model.Invitation;
import com.shimi.gogoscrum.project.model.InvitationFilter;
import com.shimi.gsf.core.service.GeneralService;

public interface InvitationService extends GeneralService<Invitation, InvitationFilter> {
    Invitation findByCode(String invitationCode);
    Long increaseJoinCount(Long id);
    Invitation disableInvitation(Long id);
    Invitation enableInvitation(Long id);
}
