package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.testing.dto.TestRunDto;
import com.shimi.gogoscrum.testing.utils.ListOfStepResultToStringConverter;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class TestRun extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 6409453185785371608L;
    private Long projectId;
    private Long testCaseId;
    private Long testCaseDetailsId;
    private Integer testCaseVersion;
    private Long testPlanId;
    @Convert(converter = ListOfStepResultToStringConverter.class)
    private List<TestStepResult> stepResults = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private TestRunStatus status;
    private String result;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "test_run_file",
            joinColumns = @JoinColumn(name = "test_run_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private List<File> files = new ArrayList<>();

    @Override
    public TestRunDto toDto() {
        return this.toDto(false);
    }

    @Override
    public TestRunDto toDto(boolean detailed) {
        TestRunDto dto = new TestRunDto();
        BeanUtils.copyProperties(this, dto);

        if (this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto());
        }

        if (detailed) {
            if (!CollectionUtils.isEmpty(this.files)) {
                List<FileDto> fileDtos = this.files.stream().map(File::toDto).collect(Collectors.toList());
                dto.setFiles(fileDtos);
            }
        }

        return dto;
    }

    public TestRun() {
    }

    public TestRun(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Long getTestCaseDetailsId() {
        return testCaseDetailsId;
    }

    public void setTestCaseDetailsId(Long testCaseDetailsId) {
        this.testCaseDetailsId = testCaseDetailsId;
    }

    public Integer getTestCaseVersion() {
        return testCaseVersion;
    }

    public void setTestCaseVersion(Integer testCaseVersion) {
        this.testCaseVersion = testCaseVersion;
    }

    public Long getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(Long testPlanId) {
        this.testPlanId = testPlanId;
    }

    public List<TestStepResult> getStepResults() {
        return stepResults;
    }

    public void setStepResults(List<TestStepResult> stepResults) {
        this.stepResults = stepResults;
    }

    public TestRunStatus getStatus() {
        return status;
    }

    public void setStatus(TestRunStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestRun{");
        sb.append("projectId=").append(projectId);
        sb.append(", testCaseId=").append(testCaseId);
        sb.append(", testCaseDetailsId=").append(testCaseDetailsId);
        sb.append(", version=").append(testCaseVersion);
        sb.append(", testPlanId=").append(testPlanId);
        sb.append(", stepResults=").append(stepResults);
        sb.append(", status=").append(status);
        sb.append(", result='").append(result).append('\'');
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }

    public enum TestRunStatus {
        SKIPPED,
        BLOCKED,
        SUCCESS,
        FAILED
    }
}
