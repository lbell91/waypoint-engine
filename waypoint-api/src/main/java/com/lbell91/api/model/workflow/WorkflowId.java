package com.lbell91.api.model.workflow;

import com.lbell91.api.exceptions.ApiIllegalArgumentException;

public record WorkflowId(String value) {
    public WorkflowId {
        if (value == null || value.isBlank()) {
            throw ApiIllegalArgumentException.workflowIdRequired();
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
