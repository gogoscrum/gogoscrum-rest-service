package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.testing.dto.TestCaseDto;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class TestCase extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1794849626692644392L;
    private Long projectId;
    private Long code;
    @OneToOne
    @JoinColumn(name = "latest_details_id")
    private TestCaseDetails details;
    private boolean deleted = false;
    private Integer latestVersion;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "test_case_file",
            joinColumns = @JoinColumn(name = "test_case_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private List<File> files = new ArrayList<>();

    @Override
    public TestCaseDto toDto() {
        return this.toDto(false);
    }

    @Override
    public TestCaseDto toDto(boolean detailed) {
        TestCaseDto dto = new TestCaseDto();
        BeanUtils.copyProperties(this, dto);

        if (details != null) {
            dto.setDetails(details.toDto());
        }

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

    public TestCase() {
    }

    public TestCase(Long id) {
        this.id = id;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public TestCaseDetails getDetails() {
        return details;
    }

    public void setDetails(TestCaseDetails details) {
        this.details = details;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Integer latestVersion) {
        this.latestVersion = latestVersion;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public final boolean equals(Object object) {
        if (!(object instanceof TestCase testCase)) return false;

        return deleted == testCase.deleted && projectId.equals(testCase.projectId) && Objects.equals(code, testCase.code) &&
                details.equals(testCase.details);
    }

    @Override
    public int hashCode() {
        int result = projectId.hashCode();
        result = 31 * result + Objects.hashCode(code);
        result = 31 * result + details.hashCode();
        result = 31 * result + Boolean.hashCode(deleted);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestCase{");
        sb.append("id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", projectId=").append(projectId);
        sb.append('}');
        return sb.toString();
    }
}
