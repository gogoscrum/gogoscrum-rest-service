package com.shimi.gogoscrum.testing.model;

import java.io.Serial;
import java.io.Serializable;

public class TestStepResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 2284627115155828938L;
    private TestRun.TestRunStatus status;
    private String result;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestStepResult{");
        sb.append("status=").append(status);
        sb.append(", result='").append(result).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
