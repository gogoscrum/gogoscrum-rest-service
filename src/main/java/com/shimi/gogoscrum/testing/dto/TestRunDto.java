package com.shimi.gogoscrum.testing.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.testing.model.TestRun;
import com.shimi.gogoscrum.testing.model.TestStepResult;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestRunDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 1653212101445935586L;
    private Long projectId;
    private Long testCaseId;
    private Long testCaseDetailsId;
    private Integer testCaseVersion;
    private Long testPlanId;
    private List<TestStepResult> stepResults = new ArrayList<>();
    private TestRun.TestRunStatus status;
    private String result;
    private List<FileDto> files = new ArrayList<>();

    @Override
    public TestRun toEntity() {
        TestRun entity = new TestRun();
        BeanUtils.copyProperties(this, entity);

        if (!CollectionUtils.isEmpty(this.files)){
            List<File> fileEntities = this.files.stream().map(FileDto::toEntity).collect(Collectors.toList());
            entity.setFiles(fileEntities);
        }

        return entity;
    }

    public TestRunDto() {
    }

    public TestRunDto(Long id) {
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

    public TestRun.TestRunStatus getStatus() {
        return status;
    }

    public void setStatus(TestRun.TestRunStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<FileDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }
}
