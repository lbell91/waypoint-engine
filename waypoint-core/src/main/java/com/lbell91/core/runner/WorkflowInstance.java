package com.lbell91.core.runner;

import java.util.Objects;

import com.lbell91.api.model.workflow.WorkflowDefinition;
import com.lbell91.api.model.workflow.WorkflowId;

public final class WorkflowInstance<S, C> {
    
    private final WorkflowId workflowId;
    private S currentState;
    private final C context;
    private int stepIndex;

    public WorkflowInstance(WorkflowId workflowId, S initialState, int stepIndex, C context) {
        this.workflowId = Objects.requireNonNull(workflowId, "workflowId");
        this.currentState = Objects.requireNonNull(initialState, "initialState");
        this.context = context;
        this.stepIndex = stepIndex;
    }

    public static <S, E, C> WorkflowInstance<S, C> start(WorkflowDefinition<S, E, C> definition, C context) {
        Objects.requireNonNull(definition, "definition");
        return new WorkflowInstance<>(
            definition.id(),
            definition.initialState(),
            0,
            context
        );
    }

    public WorkflowId workflowId() {
        return workflowId;
    }

    public S currentState() {
        return currentState;
    }

    public C context() {
        return context;
    }

    public int stepIndex() {
        return stepIndex;
    }

    WorkflowInstance<S, C> next(S nextState) {
        return new WorkflowInstance<>(workflowId, Objects.requireNonNull(nextState, "nextState"), stepIndex + 1, context);
    }
}
