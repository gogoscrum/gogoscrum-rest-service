package com.shimi.gogoscrum.testing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.common.model.Priority;
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
        private LinkedHashMap<TestRun.TestRunStatus, Long> caseByStatusSummary;
        private LinkedHashMap<Long, Long> caseByComponentSummary;
        private LinkedHashMap<TestType, Long> caseByTypeSummary;
        private LinkedHashMap<Long, Long> caseByExecutorSummary;

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

        public LinkedHashMap<TestRun.TestRunStatus, Long> getCaseByStatusSummary() {
            return caseByStatusSummary;
        }

        public void setCaseByStatusSummary(LinkedHashMap<TestRun.TestRunStatus, Long> caseByStatusSummary) {
            this.caseByStatusSummary = caseByStatusSummary;
        }

        public LinkedHashMap<Long, Long> getCaseByComponentSummary() {
            return caseByComponentSummary;
        }

        public void setCaseByComponentSummary(LinkedHashMap<Long, Long> caseByComponentSummary) {
            this.caseByComponentSummary = caseByComponentSummary;
        }

        public LinkedHashMap<TestType, Long> getCaseByTypeSummary() {
            return caseByTypeSummary;
        }

        public void setCaseByTypeSummary(LinkedHashMap<TestType, Long> caseByTypeSummary) {
            this.caseByTypeSummary = caseByTypeSummary;
        }

        public LinkedHashMap<Long, Long> getCaseByExecutorSummary() {
            return caseByExecutorSummary;
        }

        public void setCaseByExecutorSummary(LinkedHashMap<Long, Long> caseByExecutorSummary) {
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

        /**
         * Calculates the pass rate of test cases as a percentage, which is the ratio of
         * successful test cases to the total number of test cases.
         *
         * @return the pass rate as a float, or 0 if there are no cases or no executed cases.
         */
        public int getCasePassRate() {
            if (caseCount == 0 || executedCaseCount == 0 || caseByStatusSummary == null
                    || caseByStatusSummary.isEmpty() || !caseByStatusSummary.containsKey(TestRun.TestRunStatus.SUCCESS)) {
                return 0;
            }
            return Math.round((float) caseByStatusSummary.get(TestRun.TestRunStatus.SUCCESS) / caseCount * 100);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BugSummary {
        private Long bugCount = 0L;
        private List<Long> bugIds = new ArrayList<>();
        private LinkedHashMap<Priority, Long> bugByPrioritySummary;
        private LinkedHashMap<String, Long> bugByStatusSummary;
        private LinkedHashMap<Long, Long> bugByCreatorSummary;
        private LinkedHashMap<Long, Long> bugByAssigneeSummary;
        private LinkedHashMap<Long, Long> bugByComponentSummary;

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

        public Map<Priority, Long> getBugByPrioritySummary() {
            return bugByPrioritySummary;
        }

        public void setBugByPrioritySummary(LinkedHashMap<Priority, Long> bugByPrioritySummary) {
            this.bugByPrioritySummary = bugByPrioritySummary;
        }

        public LinkedHashMap<String, Long> getBugByStatusSummary() {
            return bugByStatusSummary;
        }

        public void setBugByStatusSummary(LinkedHashMap<String, Long> bugByStatusSummary) {
            this.bugByStatusSummary = bugByStatusSummary;
        }

        public LinkedHashMap<Long, Long> getBugByCreatorSummary() {
            return bugByCreatorSummary;
        }

        public void setBugByCreatorSummary(LinkedHashMap<Long, Long> bugByCreatorSummary) {
            this.bugByCreatorSummary = bugByCreatorSummary;
        }

        public LinkedHashMap<Long, Long> getBugByAssigneeSummary() {
            return bugByAssigneeSummary;
        }

        public void setBugByAssigneeSummary(LinkedHashMap<Long, Long> bugByAssigneeSummary) {
            this.bugByAssigneeSummary = bugByAssigneeSummary;
        }

        public LinkedHashMap<Long, Long> getBugByComponentSummary() {
            return bugByComponentSummary;
        }

        public void setBugByComponentSummary(LinkedHashMap<Long, Long> bugByComponentSummary) {
            this.bugByComponentSummary = bugByComponentSummary;
        }
    }
}
