package com.shimi.gogoscrum.testing.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestCaseDetails;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestCaseDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -3561817253130353845L;
    private Long code;
    private Long projectId;
    private TestCaseDetailsDto details;
    private Integer latestVersion;
    private List<FileDto> files = new ArrayList<>();

    @Override
    public TestCase toEntity() {
        TestCase entity = new TestCase();
        BeanUtils.copyProperties(this, entity);

        if (details != null) {
            entity.setDetails(details.toEntity());
        } else {
            entity.setDetails(new TestCaseDetails());
        }

        if (!CollectionUtils.isEmpty(this.files)){
            List<File> fileEntities = this.files.stream().map(FileDto::toEntity).collect(Collectors.toList());
            entity.setFiles(fileEntities);
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

    public List<FileDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }
}
