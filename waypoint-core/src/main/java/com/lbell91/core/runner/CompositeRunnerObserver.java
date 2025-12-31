package com.lbell91.core.runner;

import com.lbell91.api.model.workflow.WorkflowId;
import java.util.List;
import java.util.Objects;

public final class CompositeRunnerObserver<S, C> implements RunnerObserver<S, C> {
    private final List<RunnerObserver<S, C>> observers;
    public CompositeRunnerObserver(List<RunnerObserver<S, C>> observers) {
        Objects.requireNonNull(observers, "observers");

        observers.forEach(o -> Objects.requireNonNull(o, "observer") );
        this.observers = List.copyOf(observers);
    }

    @Override public void onStepStart(WorkflowId workflowId, int stepIndex, S state, C context) {
        observers.forEach(o -> o.onStepStart(workflowId, stepIndex, state, context));
    }

    @Override public void onActionPlanned(com.lbell91.api.model.action.ActionCommand<C> command) {
        observers.forEach(o -> o.onActionPlanned(command));
    }

    @Override public void onActionExecuted(com.lbell91.api.model.action.ActionCommand<C> command,
                                          com.lbell91.api.model.action.ActionEvent event) {
        observers.forEach(o -> o.onActionExecuted(command, event));
    }

    @Override public void onTransitionApplied(S fromState, S toState, com.lbell91.api.model.action.ActionEvent event) {
        observers.forEach(o -> o.onTransitionApplied(fromState, toState, event));
    }

    @Override public void onCompleted(WorkflowId workflowId, S finalState, int stepIndex) {
        observers.forEach(o -> o.onCompleted(workflowId, finalState, stepIndex));
    }

    @Override public void onFailure(WorkflowId workflowId, int stepIndex, S state, RuntimeException exception) {
        observers.forEach(o -> o.onFailure(workflowId, stepIndex, state, exception));
    }
}