package com.shimi.gogoscrum.component.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.component.dto.ComponentDto;
import com.shimi.gogoscrum.user.dto.UserDto;
import jakarta.persistence.Entity;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;

@Entity
public class Component extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -111600323162979153L;

    private String name;
    private Long parentId;
    private String path;
    private Long projectId;
    private Integer seq = 0;

    @Override
    public ComponentDto toDto() {
        ComponentDto dto = new ComponentDto();
        BeanUtils.copyProperties(this, dto);

        if (createdBy != null) {
            dto.setCreatedBy(createdBy.toDto().normalize());
        }

        return dto;
    }

    public Component() {
    }

    public Component(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return (StringUtils.hasText(path) ? path : "/") + id + "/";
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Component{");
        sb.append("name='").append(name).append('\'');
        sb.append(", parentId=").append(parentId);
        sb.append(", path='").append(path).append('\'');
        sb.append(", projectId=").append(projectId);
        sb.append(", seq=").append(seq);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
