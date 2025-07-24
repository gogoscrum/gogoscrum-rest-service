package com.shimi.gogoscrum.issue.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.issue.model.IssueFilter;
import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.issue.model.IssueType;
import com.shimi.gsf.core.model.Filter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class IssueFilterDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -4578479425878450254L;
    private String name;
    private Long projectId;
    private String keyword;
    private Boolean backlog;
    private List<Long> sprintIds;
    private List<Long> groupIds;
    private List<Long> componentIds;
    private List<IssueType> types;
    private List<Priority> priorities;
    private List<Long> tagIds;
    private List<Long> ownerIds;
    private int page = 1;
    private int pageSize = 10;
    private List<Filter.Order> orders = new ArrayList<>();
    private String language;

    @Override
    public IssueFilter toEntity() {
        IssueFilter entity = new IssueFilter();
        BeanUtils.copyProperties(this, entity);

        return entity;
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

    public Boolean getBacklog() {
        return backlog;
    }

    public void setBacklog(Boolean backlog) {
        this.backlog = backlog;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Long> getSprintIds() {
        return sprintIds;
    }

    public void setSprintIds(List<Long> sprintIds) {
        this.sprintIds = sprintIds;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<Long> getComponentIds() {
        return componentIds;
    }

    public void setComponentIds(List<Long> componentIds) {
        this.componentIds = componentIds;
    }

    public List<IssueType> getTypes() {
        return types;
    }

    public void setTypes(List<IssueType> types) {
        this.types = types;
    }

    public List<Priority> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<Priority> priorities) {
        this.priorities = priorities;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<Long> getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(List<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Filter.Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Filter.Order> orders) {
        this.orders = orders;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
