package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.testing.dto.TestCaseDetailsDto;
import com.shimi.gogoscrum.testing.utils.ListOfStepToStringConverter;
import com.shimi.gogoscrum.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
    private String description;
    @Enumerated(EnumType.STRING)
    private TestType type;
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;
    private String preconditions;
    @Convert(converter = ListOfStepToStringConverter.class)
    private List<TestStep> steps = new ArrayList<>();
    private Integer version;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User owner;
    private Boolean automated = Boolean.FALSE;

    @Override
    public TestCaseDetailsDto toDto() {
        TestCaseDetailsDto dto = new TestCaseDetailsDto();
        BeanUtils.copyProperties(this, dto);

        if (this.owner != null) {
            dto.setOwner(this.owner.toDto().normalize());
        }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Boolean getAutomated() {
        return automated;
    }

    public void setAutomated(Boolean automated) {
        this.automated = automated;
    }

    @Override
    public final boolean equals(Object object) {
        if (!(object instanceof TestCaseDetails that)) return false;

        return testCaseId.equals(that.testCaseId) && Objects.equals(componentId, that.componentId) && name.equals(that.name) &&
                type == that.type && priority == that.priority && Objects.equals(preconditions, that.preconditions) &&
                Objects.equals(steps, that.steps) && Objects.equals(version, that.version) && Objects.equals(description, that.description) &&
                Objects.equals(owner, that.owner) && Objects.equals(automated, that.automated);
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
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(owner);
        result = 31 * result + Objects.hashCode(automated);
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
        sb.append(", description='").append(description).append('\'');
        sb.append(", owner=").append(owner);
        sb.append(", automated=").append(automated);
        sb.append('}');
        return sb.toString();
    }
}
