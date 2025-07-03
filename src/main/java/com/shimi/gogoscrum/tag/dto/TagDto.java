package com.shimi.gogoscrum.tag.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.tag.model.Tag;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class TagDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 684617821918188657L;
    private String name;
    private Long projectId;
    private String color;

    @Override
    public Tag toEntity() {
        Tag entity = new Tag();
        BeanUtils.copyProperties(this, entity);

        return entity;
    }

    public TagDto() {
    }

    public TagDto(Long id) {
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
}
