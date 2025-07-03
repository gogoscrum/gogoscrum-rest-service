package com.shimi.gogoscrum.sprint.model;

import com.shimi.gogoscrum.sprint.util.IssueCountsToStringJpaConverter;
import com.shimi.gogoscrum.issue.dto.IssueCountDto;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class SprintIssueCount implements Serializable {
    @Serial
    private static final long serialVersionUID = 6838936961039626976L;
    @Id
    private Long sprintId;
    @Convert(converter = IssueCountsToStringJpaConverter.class)
    private Map<String, List<IssueCountDto>> dailyCounts = new LinkedHashMap<>();

    public SprintIssueCount() {
    }

    public SprintIssueCount(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Map<String, List<IssueCountDto>> getDailyCounts() {
        return dailyCounts;
    }

    public void setDailyCounts(
            Map<String, List<IssueCountDto>> dailyCounts) {
        this.dailyCounts = dailyCounts;
    }
}
