package com.lbell91.core.runner;

import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.action.ActionEvent;
import com.lbell91.api.model.workflow.WorkflowId;

public interface RunnerObserver<S, C> {
    default void onStepStart(WorkflowId workflowId, int stepIndex, S state, C context) {}
    default void onActionPlanned(ActionCommand<C> command) {}
    default void onActionExecuted(ActionCommand<C> command, ActionEvent event) {}
    default void onTransitionApplied(S fromState, S toState, ActionEvent event) {}
    default void onCompleted(WorkflowId workflowId, S finalState, int stepIndex) {}
    default void onFailure(WorkflowId workflowId, int stepIndex, S state, RuntimeException exception) {}
}
