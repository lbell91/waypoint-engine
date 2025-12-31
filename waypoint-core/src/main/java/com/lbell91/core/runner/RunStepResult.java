package com.lbell91.core.runner;

import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.action.ActionEvent;
import com.lbell91.api.model.workflow.WorkflowDefinition;

public record RunStepResult<S, C>(
    S fromState,
    S toState,
    ActionCommand<C> action,
    ActionEvent event
) {}
