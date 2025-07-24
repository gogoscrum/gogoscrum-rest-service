package com.shimi.gogoscrum.issue.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.common.util.ListOfPriorityToStringConverter;
import com.shimi.gogoscrum.issue.util.ListOfIssueTypeToStringConverter;
import com.shimi.gogoscrum.common.util.ListOfLongToStringConverter;
import com.shimi.gogoscrum.common.util.ListOfOrderToStringConverter;
import com.shimi.gogoscrum.issue.dto.IssueFilterDto;
import com.shimi.gsf.core.model.Filter;
import org.springframework.beans.BeanUtils;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
public class IssueFilter extends BaseEntity implements Filter {
    @Serial
    private static final long serialVersionUID = 2806131752606175926L;
    private String name;
    private Long projectId;
    private String keyword;
    private int seq;
    @Convert(converter = ListOfLongToStringConverter.class)
    private List<Long> sprintIds;
    @Convert(converter = ListOfLongToStringConverter.class)
    private List<Long> groupIds;
    @Convert(converter = ListOfLongToStringConverter.class)
    private List<Long> componentIds;
    @Convert(converter = ListOfIssueTypeToStringConverter.class)
    private List<IssueType> types;
    @Convert(converter = ListOfPriorityToStringConverter.class)
    private List<Priority> priorities;
    @Convert(converter = ListOfLongToStringConverter.class)
    private List<Long> tagIds;
    @Convert(converter = ListOfLongToStringConverter.class)
    private List<Long> ownerIds;
    @Convert(converter = ListOfOrderToStringConverter.class)
    private List<Order> orders = new ArrayList<>();

    @Transient
    private int page = 1;
    @Transient
    private int pageSize = 10;
    @Transient
    private Boolean backlog;
    @Transient
    private String language;

    @Override
    public IssueFilterDto toDto() {
        IssueFilterDto dto = new IssueFilterDto();
        BeanUtils.copyProperties(this, dto);

        if (this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto().normalize());
        }

        return dto;
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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public int getPage() {
        return page > 1 ? page - 1 : 0;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Boolean getBacklog() {
        return backlog;
    }

    public void setBacklog(Boolean backlog) {
        this.backlog = backlog;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IssueFilter{");
        sb.append("projectId=").append(projectId);
        sb.append(", keyword='").append(keyword).append('\'');
        sb.append(", sprintIds=").append(sprintIds);
        sb.append(", groupIds=").append(groupIds);
        sb.append(", componentIds=").append(componentIds);
        sb.append(", types=").append(types);
        sb.append(", priorities=").append(priorities);
        sb.append(", tagIds=").append(tagIds);
        sb.append(", ownerIds=").append(ownerIds);
        sb.append(", page=").append(page);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
