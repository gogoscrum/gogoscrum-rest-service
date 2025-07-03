package com.shimi.gogoscrum.doc.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.doc.model.Doc;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class DocDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 1195928461096875059L;
    private Long projectId;
    private String name;
    private String content;

    @Override
    public Doc toEntity() {
        Doc entity = new Doc();
        BeanUtils.copyProperties(this, entity);
        return entity;
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
}
