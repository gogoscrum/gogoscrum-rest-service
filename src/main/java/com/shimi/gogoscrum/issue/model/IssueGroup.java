package com.shimi.gogoscrum.issue.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.issue.dto.IssueGroupDto;
import com.shimi.gogoscrum.project.model.Project;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class IssueGroup extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 6234533279589155880L;

    private String label;
    private boolean builtIn;
    private short seq;
    @Enumerated(EnumType.STRING)
    private IssueGroupStatus status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Override
    public IssueGroupDto toDto() {
        return this.toDto(false);
    }

    public IssueGroupDto toDto(boolean detail) {
        IssueGroupDto dto = new IssueGroupDto();
        BeanUtils.copyProperties(this, dto, "project");

        if (detail && this.project != null) {
            dto.setProject(this.project.toDto());
        }

        return dto;
    }

    public IssueGroup() {
    }

    public IssueGroup(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    public IssueGroupStatus getStatus() {
        return status;
    }

    public void setStatus(IssueGroupStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "IssueGroup{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", status=" + status +
                '}';
    }
}
