package com.shimi.gogoscrum.project.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class InvitationFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 142849873497594675L;
    private Long projectId;
    private String code;
    private Boolean active;
    private List<Long> creators;
    private List<InvitationType> invitationTypes;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Long> getCreators() {
        return creators;
    }

    public void setCreators(List<Long> creators) {
        this.creators = creators;
    }

    public List<InvitationType> getInvitationTypes() {
        return invitationTypes;
    }

    public void setInvitationTypes(List<InvitationType> invitationTypes) {
        this.invitationTypes = invitationTypes;
    }
}
