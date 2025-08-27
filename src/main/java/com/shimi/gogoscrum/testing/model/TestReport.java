package com.shimi.gogoscrum.testing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.testing.dto.TestReportDto;
import com.shimi.gogoscrum.testing.utils.BugSummaryToStringConverter;
import com.shimi.gogoscrum.testing.utils.CaseSummaryToStringConverter;
import com.shimi.gogoscrum.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.*;

@Entity
public class TestReport extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1207962867741861845L;
    private Long projectId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_plan_id")
    private TestPlan testPlan;
    @Convert(converter = CaseSummaryToStringConverter.class)
    private CaseSummary caseSummary;
    @Convert(converter = BugSummaryToStringConverter.class)
    private BugSummary bugSummary;

    @Override
    public TestReportDto toDto() {
        return this.toDto(false);
    }

    @Override
    public TestReportDto toDto(boolean detailed) {
        TestReportDto dto = new TestReportDto();
        BeanUtils.copyProperties(this, dto);

        if (this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto());
        }

        if (this.owner != null) {
            dto.setOwner(this.owner.toDto().normalize());
        }

        if (this.testPlan != null) {
            dto.setTestPlan(this.testPlan.toDto().normalize());
        }

        return dto;
    }

    public TestReport() {
    }

    public TestReport(Long id) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CaseSummary getCaseSummary() {
        return caseSummary;
    }

    public void setCaseSummary(CaseSummary caseSummary) {
        this.caseSummary = caseSummary;
    }

    public BugSummary getBugSummary() {
        return bugSummary;
    }

    public void setBugSummary(BugSummary bugSummary) {
        this.bugSummary = bugSummary;
    }

    public TestPlan getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestReport{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", projectId=").append(projectId);
        sb.append('}');
        return sb.toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CaseSummary {
        private List<Long> caseIds = new ArrayList<>();
        private Long caseCount = 0L;
        private Long executedCaseCount = 0L;
        private Long executionRecordCount = 0L;
        private List<SummaryEntry> caseByStatusSummary;
        private List<SummaryEntry> caseByComponentSummary;
        private List<SummaryEntry> caseByTypeSummary;
        private List<SummaryEntry> caseByExecutorSummary;

        public List<Long> getCaseIds() {
            return caseIds;
        }

        public void setCaseIds(List<Long> caseIds) {
            this.caseIds = caseIds;
        }

        public Long getCaseCount() {
            return caseCount;
        }

        public void setCaseCount(Long caseCount) {
            this.caseCount = caseCount;
        }

        public Long getExecutedCaseCount() {
            return executedCaseCount;
        }

        public void setExecutedCaseCount(Long executedCaseCount) {
            this.executedCaseCount = executedCaseCount;
        }

        public Long getExecutionRecordCount() {
            return executionRecordCount;
        }

        public void setExecutionRecordCount(Long executionRecordCount) {
            this.executionRecordCount = executionRecordCount;
        }

        public List<SummaryEntry> getCaseByStatusSummary() {
            return caseByStatusSummary;
        }

        public void setCaseByStatusSummary(List<SummaryEntry> caseByStatusSummary) {
            this.caseByStatusSummary = caseByStatusSummary;
        }

        public List<SummaryEntry> getCaseByComponentSummary() {
            return caseByComponentSummary;
        }

        public void setCaseByComponentSummary(List<SummaryEntry> caseByComponentSummary) {
            this.caseByComponentSummary = caseByComponentSummary;
        }

        public List<SummaryEntry> getCaseByTypeSummary() {
            return caseByTypeSummary;
        }

        public void setCaseByTypeSummary(List<SummaryEntry> caseByTypeSummary) {
            this.caseByTypeSummary = caseByTypeSummary;
        }

        public List<SummaryEntry> getCaseByExecutorSummary() {
            return caseByExecutorSummary;
        }

        public void setCaseByExecutorSummary(List<SummaryEntry> caseByExecutorSummary) {
            this.caseByExecutorSummary = caseByExecutorSummary;
        }

        /**
         * Calculates the test case execution rate as a percentage, which is the ratio of
         * executed test cases to the total number of test cases.
         * @return the execution rate as an integer, or 0 if there are no cases or no executed cases.
         */
        public int getCaseTestRate() {
            if (caseCount == 0 || executedCaseCount == 0) {
                return 0;
            }
            return Math.round((float) executedCaseCount / caseCount * 100);
        }

        public int getFailedCaseCount() {
            if (caseByStatusSummary == null || caseByStatusSummary.isEmpty()) {
                return 0;
            }
            Optional<SummaryEntry> failedEntry = caseByStatusSummary.stream().filter(entry ->
                    Objects.equals(entry.getKey(), TestRun.TestRunStatus.FAILED.name())).findFirst();
            return failedEntry.map(SummaryEntry::getValue).orElse(0L).intValue();
        }

        /**
         * Calculates the pass rate of test cases as a percentage, which is the ratio of
         * successful test cases to the total number of test cases.
         *
         * @return the pass rate as a float, or 0 if there are no cases or no executed cases.
         */
        public int getCasePassRate() {
            // try to find the SUCCESS entry in caseByStatusSummary
            Optional<SummaryEntry> successEntry = caseByStatusSummary.stream().filter(entry ->
                    Objects.equals(entry.getKey(), TestRun.TestRunStatus.SUCCESS.name())).findFirst();

            if (caseCount == 0 || executedCaseCount == 0 || caseByStatusSummary == null
                    || caseByStatusSummary.isEmpty() || successEntry.isEmpty()) {
                return 0;
            }
            return Math.round((float) successEntry.get().getValue() / caseCount * 100);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BugSummary {
        private Long bugCount = 0L;
        private List<Long> bugIds = new ArrayList<>();
        private List<SummaryEntry> bugByPrioritySummary;
        private List<SummaryEntry> bugByStatusSummary;
        private List<SummaryEntry> bugByCreatorSummary;
        private List<SummaryEntry> bugByAssigneeSummary;
        private List<SummaryEntry> bugByComponentSummary;

        public Long getBugCount() {
            return bugCount;
        }

        public void setBugCount(Long bugCount) {
            this.bugCount = bugCount;
        }

        public List<Long> getBugIds() {
            return bugIds;
        }

        public void setBugIds(List<Long> bugIds) {
            this.bugIds = bugIds;
        }

        public List<SummaryEntry> getBugByPrioritySummary() {
            return bugByPrioritySummary;
        }

        public void setBugByPrioritySummary(List<SummaryEntry> bugByPrioritySummary) {
            this.bugByPrioritySummary = bugByPrioritySummary;
        }

        public List<SummaryEntry> getBugByStatusSummary() {
            return bugByStatusSummary;
        }

        public void setBugByStatusSummary(List<SummaryEntry> bugByStatusSummary) {
            this.bugByStatusSummary = bugByStatusSummary;
        }

        public List<SummaryEntry> getBugByCreatorSummary() {
            return bugByCreatorSummary;
        }

        public void setBugByCreatorSummary(List<SummaryEntry> bugByCreatorSummary) {
            this.bugByCreatorSummary = bugByCreatorSummary;
        }

        public List<SummaryEntry> getBugByAssigneeSummary() {
            return bugByAssigneeSummary;
        }

        public void setBugByAssigneeSummary(List<SummaryEntry> bugByAssigneeSummary) {
            this.bugByAssigneeSummary = bugByAssigneeSummary;
        }

        public List<SummaryEntry> getBugByComponentSummary() {
            return bugByComponentSummary;
        }

        public void setBugByComponentSummary(List<SummaryEntry> bugByComponentSummary) {
            this.bugByComponentSummary = bugByComponentSummary;
        }
    }

    public static class SummaryEntry {
        private String key;
        private Long value;

        public SummaryEntry() {
        }

        public SummaryEntry(String key, Long value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}
