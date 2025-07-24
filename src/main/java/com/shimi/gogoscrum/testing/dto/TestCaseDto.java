package com.shimi.gogoscrum.testing.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestCaseDetails;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class TestCaseDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -3561817253130353845L;
    private Long code;
    private Long projectId;
    private TestCaseDetailsDto details;
    private Integer latestVersion;

    @Override
    public TestCase toEntity() {
        TestCase entity = new TestCase();
        BeanUtils.copyProperties(this, entity);

        if (details != null) {
            entity.setDetails(details.toEntity());
        } else {
            entity.setDetails(new TestCaseDetails());
        }

        return entity;
    }

    public TestCaseDto() {
    }

    public TestCaseDto(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public TestCaseDetailsDto getDetails() {
        return details;
    }

    public void setDetails(TestCaseDetailsDto details) {
        this.details = details;
    }

    public Integer getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Integer latestVersion) {
        this.latestVersion = latestVersion;
    }
}
