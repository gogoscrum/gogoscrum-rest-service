package com.shimi.gogoscrum.tag.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.tag.dto.TagDto;
import jakarta.persistence.Entity;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class Tag extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 6082728886362463850L;
    private String name;
    private Long projectId;
    private String color;

    @Override
    public TagDto toDto() {
        return this.toDto(false);
    }

    @Override
    public TagDto toDto(boolean detailed) {
        TagDto dto = new TagDto();
        BeanUtils.copyProperties(this, dto);

        if (detailed) {
            dto.setCreatedBy(this.createdBy.toDto());
        }

        return dto;
    }

    public Tag() {
    }

    public Tag(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tag {");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", projectId=").append(projectId);
        sb.append('}');
        return sb.toString();
    }
}
