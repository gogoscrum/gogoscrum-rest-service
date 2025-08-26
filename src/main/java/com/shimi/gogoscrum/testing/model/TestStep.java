package com.shimi.gogoscrum.testing.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class TestStep implements Serializable {
    @Serial
    private static final long serialVersionUID = -4713785913676599219L;
    private String description;
    private String expectation;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectation() {
        return expectation;
    }

    public void setExpectation(String expectation) {
        this.expectation = expectation;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        TestStep testStep = (TestStep) object;
        return Objects.equals(description, testStep.description) && Objects.equals(expectation, testStep.expectation);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestStep{");
        sb.append("description='").append(description).append('\'');
        sb.append(", expectation='").append(expectation).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
