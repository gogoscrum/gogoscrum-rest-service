package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.testing.dto.TestCaseDetailsDto;
import com.shimi.gogoscrum.testing.utils.ListOfStepToStringConverter;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class TestCaseDetails extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 7913042228963433520L;
    private Long testCaseId;
    private Long componentId;
    private String name;
    @Enumerated(EnumType.STRING)
    private TestType type;
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;
    private String preconditions;
    @Convert(converter = ListOfStepToStringConverter.class)
    private List<TestStep> steps = new ArrayList<>();
    private Integer version;

    @Override
    public TestCaseDetailsDto toDto() {
        TestCaseDetailsDto dto = new TestCaseDetailsDto();
        BeanUtils.copyProperties(this, dto);

        return dto;
    }

    public TestCaseDetails() {
    }

    public TestCaseDetails(Long id) {
        this.id = id;
    }

    public String getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(String preconditions) {
        this.preconditions = preconditions;
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TestStep> steps) {
        this.steps = steps;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestType getType() {
        return type;
    }

    public void setType(TestType type) {
        this.type = type;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public final boolean equals(Object object) {
        if (!(object instanceof TestCaseDetails that)) return false;

        return testCaseId.equals(that.testCaseId) && Objects.equals(componentId, that.componentId) && name.equals(that.name) &&
                type == that.type && priority == that.priority && Objects.equals(preconditions, that.preconditions) &&
                Objects.equals(steps, that.steps) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        int result = testCaseId.hashCode();
        result = 31 * result + Objects.hashCode(componentId);
        result = 31 * result + name.hashCode();
        result = 31 * result + Objects.hashCode(type);
        result = 31 * result + priority.hashCode();
        result = 31 * result + Objects.hashCode(preconditions);
        result = 31 * result + Objects.hashCode(steps);
        result = 31 * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestCaseDetails{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", testCaseId=").append(testCaseId);
        sb.append(", type=").append(type);
        sb.append(", priority=").append(priority);
        sb.append(", preconditions='").append(preconditions).append('\'');
        sb.append(", steps=").append(steps);
        sb.append(", version=").append(version);
        sb.append(", componentId=").append(componentId);
        sb.append('}');
        return sb.toString();
    }
}
