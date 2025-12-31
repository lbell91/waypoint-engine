package com.lbell91.api.model.workflow;

public record WorkflowId(String value) {
    public WorkflowId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WorkflowId cannot be null or blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
