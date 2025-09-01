package com.shimi.gogoscrum.doc.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.doc.dto.DocDto;
import jakarta.persistence.Entity;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class Doc extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -4046441547864016430L;
    private Long projectId;
    private String name;
    private String content;
    private Boolean publicAccess = Boolean.FALSE;

    @Override
    public DocDto toDto() {
        return this.toDto(false);
    }

    @Override
    public DocDto toDto(boolean detailed) {
        DocDto dto = new DocDto();
        BeanUtils.copyProperties(this, dto);
        if (this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto().normalize());
        }
        if (detailed && this.updatedBy != null) {
            dto.setUpdatedBy(this.updatedBy.toDto().normalize());
        }
        return dto;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Doc{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", projectId=").append(projectId);
        sb.append(", publicAccess=").append(publicAccess);
        sb.append('}');
        return sb.toString();
    }
}
