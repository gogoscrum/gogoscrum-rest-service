package com.shimi.gogoscrum.issue.dto;

import java.io.Serial;
import java.io.Serializable;

public class IssueCountDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5698836391797575106L;
    private Long issueGroupId;
    private Long count;
    private Double storyPoints;

    public IssueCountDto() {
    }

    public IssueCountDto(Long issueGroupId, Long count, Double storyPoints) {
        this.issueGroupId = issueGroupId;
        this.count = count;
        this.storyPoints = storyPoints;
    }

    public Long getIssueGroupId() {
        return issueGroupId;
    }

    public void setIssueGroupId(Long issueGroupId) {
        this.issueGroupId = issueGroupId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Double storyPoints) {
        this.storyPoints = storyPoints;
    }
}
