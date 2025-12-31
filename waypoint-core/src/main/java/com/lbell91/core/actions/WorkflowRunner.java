package com.lbell91.core.actions;

import java.util.Objects;

import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.action.ActionCompleted;
import com.lbell91.api.model.action.ActionEvent;
import com.lbell91.api.model.action.ActionHandlerRegistry;
import com.lbell91.api.model.action.ActionResult;
import com.lbell91.api.model.workflow.WorkflowDefinition;
import com.lbell91.core.StateMachineEngine;
import com.lbell91.core.exceptions.CoreIllegalArgumentException;
import com.lbell91.core.exceptions.CoreIllegalStateException;

public class WorkflowRunner<S, C> {

    private final StateMachineEngine<S, ActionEvent, C> engine;
    private final ActionHandlerRegistry<C> handlers;
    private final int maxSteps;

    public WorkflowRunner(StateMachineEngine<S, ActionEvent, C> engine,
                          ActionHandlerRegistry<C> handlers) {
        this(engine, handlers, 10_000);
    }

    public WorkflowRunner(StateMachineEngine<S, ActionEvent, C> engine,
                          ActionHandlerRegistry<C> handlers,
                          int maxSteps) {
        this.engine = Objects.requireNonNull(engine, "engine");
        this.handlers = Objects.requireNonNull(handlers, "handlers");
        if (maxSteps <= 0) throw CoreIllegalArgumentException.maxStepsIllegalArgument(maxSteps);
        this.maxSteps = maxSteps;
    }

    public RunStepResult<S, C> runStep(WorkflowDefinition<S, ActionEvent, C> workflowDefinition,
                                        S currentState,
                                        C context) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");
        Objects.requireNonNull(currentState, "currentState");
        
        if (workflowDefinition.terminatingStates().contains(currentState)) {
            throw CoreIllegalStateException.terminatingState(workflowDefinition.id(), currentState);
        }

        var actions = workflowDefinition.actionsForState(currentState, context);

        if (actions == null || actions.isEmpty()) {
            throw CoreIllegalStateException.noActionsForNonTerminatingState(currentState.toString(), workflowDefinition.id().toString());
        }

        ActionCommand<C> action = actions.get(0);
        var handler = handlers.get(action.actionId());

        ActionResult result = handler.handle(action, context);
        ActionEvent event = new ActionCompleted(action.actionId(), result);

        var transition = engine.applyResult(workflowDefinition, currentState, event, context);

        S nextState = transition.nextState();

        return new RunStepResult<>(currentState, nextState, action, event);
    }

    public S runToCompletion(WorkflowDefinition<S, ActionEvent, C> workflowDefinition, C context) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");

        S currentState = workflowDefinition.initialState();
        int steps = 0;

        while (!workflowDefinition.terminatingStates().contains(currentState)) {
            if (++steps > maxSteps) {
                throw CoreIllegalStateException.maxStepsExceeded(maxSteps, workflowDefinition.id().toString());
            }

            var step = runStep(workflowDefinition, currentState, context);
            currentState = step.toState();
        }

        return currentState;
    }
    
}
