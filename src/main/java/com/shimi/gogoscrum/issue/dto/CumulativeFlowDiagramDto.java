package com.shimi.gogoscrum.issue.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CumulativeFlowDiagramDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2778546368389825838L;
    private Long sprintId;
    private Map<Long, StatusGroupLineDto> groupLines = new LinkedHashMap<>();

    public CumulativeFlowDiagramDto() {
    }

    public CumulativeFlowDiagramDto(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Map<Long, StatusGroupLineDto> getGroupLines() {
        return groupLines;
    }

    public void setGroupLines(
            Map<Long, StatusGroupLineDto> groupLines) {
        this.groupLines = groupLines;
    }

    public static class StatusGroupLineDto implements Serializable {
        private static final long serialVersionUID = -963930310985502091L;
        private Long groupId;
        private String groupLabel;
        private Map<String, DailyCountDto> dailyCounts = new LinkedHashMap<>();

        public StatusGroupLineDto() {
        }

        public StatusGroupLineDto(Long groupId, String groupLabel) {
            this.groupId = groupId;
            this.groupLabel = groupLabel;
        }

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public String getGroupLabel() {
            return groupLabel;
        }

        public void setGroupLabel(String groupLabel) {
            this.groupLabel = groupLabel;
        }

        public Map<String, DailyCountDto> getDailyCounts() {
            return dailyCounts;
        }

        public void setDailyCounts(
                Map<String, DailyCountDto> dailyCounts) {
            this.dailyCounts = dailyCounts;
        }
    }

    public static class DailyCountDto implements Serializable {
        private static final long serialVersionUID = -6441408436550242757L;
        private Long issueCount;
        private Double totalStoryPoints;

        public DailyCountDto() {
        }

        public DailyCountDto(Long issueCount, Double totalStoryPoints) {
            this.issueCount = issueCount;
            this.totalStoryPoints = totalStoryPoints;
        }

        public Long getIssueCount() {
            return issueCount;
        }

        public void setIssueCount(Long issueCount) {
            this.issueCount = issueCount;
        }

        public Double getTotalStoryPoints() {
            return totalStoryPoints;
        }

        public void setTotalStoryPoints(Double totalStoryPoints) {
            this.totalStoryPoints = totalStoryPoints;
        }
    }
}


