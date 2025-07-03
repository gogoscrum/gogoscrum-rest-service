package com.shimi.gogoscrum.component.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.component.model.Component;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class ComponentDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -6929042842015574658L;

    private String name;
    private Long parentId;
    private String path;
    private Long projectId;
    private Integer seq = 0;
    private List<ComponentDto> children = new ArrayList<>();

    @Override
    public Component toEntity() {
        Component entity = new Component();
        BeanUtils.copyProperties(this, entity, "children");
        return entity;
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

    public List<ComponentDto> getChildren() {
        return children;
    }

    public void setChildren(List<ComponentDto> children) {
        this.children = children;
    }
}
