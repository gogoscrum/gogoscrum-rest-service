package com.shimi.gogoscrum.project.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class InvitationFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 142849873497594675L;
    private Long projectId;
    private String code;
    private Boolean active;

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
}
