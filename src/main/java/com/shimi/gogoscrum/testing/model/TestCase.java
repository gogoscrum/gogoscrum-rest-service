package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.testing.dto.TestCaseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Objects;

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
