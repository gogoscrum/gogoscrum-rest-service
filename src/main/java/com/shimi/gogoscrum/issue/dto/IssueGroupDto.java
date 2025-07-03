package com.shimi.gogoscrum.issue.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gogoscrum.issue.model.IssueGroupStatus;
import com.shimi.gogoscrum.project.dto.ProjectDto;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class IssueGroupDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 4125411941997460964L;

    private String label;
    private ProjectDto project;
    private boolean builtIn = false;
    private short seq;
    private IssueGroupStatus status = IssueGroupStatus.IN_PROGRESS;

    @Override
    public IssueGroup toEntity() {
        IssueGroup entity = new IssueGroup();
        BeanUtils.copyProperties(this, entity, "project");

        if (this.project != null) {
            entity.setProject(this.project.toEntity());
        }

        return entity;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public IssueGroupStatus getStatus() {
        return status;
    }

    public void setStatus(IssueGroupStatus status) {
        this.status = status;
    }
}
