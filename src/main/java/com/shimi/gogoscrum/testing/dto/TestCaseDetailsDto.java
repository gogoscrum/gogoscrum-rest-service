package com.shimi.gogoscrum.testing.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.testing.model.TestCaseDetails;
import com.shimi.gogoscrum.testing.model.TestStep;
import com.shimi.gogoscrum.testing.model.TestType;
import com.shimi.gogoscrum.testing.utils.ListOfStepToStringConverter;
import com.shimi.gogoscrum.user.dto.UserDto;
import jakarta.persistence.Convert;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class TestCaseDetailsDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 8008333803486109221L;
    private Long testCaseId;
    private String name;
    private String description;
    private TestType type;
    private Priority priority = Priority.NORMAL;
    private Long componentId;
    private String preconditions;
    @Convert(converter = ListOfStepToStringConverter.class)
    private List<TestStep> steps = new ArrayList<>();
    private Integer version;
    private UserDto owner;
    private Boolean automated = Boolean.FALSE;

    @Override
    public TestCaseDetails toEntity() {
        TestCaseDetails entity = new TestCaseDetails();
        BeanUtils.copyProperties(this, entity);

        if (this.owner != null) {
            entity.setOwner(this.owner.toEntity());
        }

        return entity;
    }

    public TestCaseDetailsDto() {
    }

    public TestCaseDetailsDto(Long id) {
        this.id = id;
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

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public Boolean getAutomated() {
        return automated;
    }

    public void setAutomated(Boolean automated) {
        this.automated = automated;
    }
}
