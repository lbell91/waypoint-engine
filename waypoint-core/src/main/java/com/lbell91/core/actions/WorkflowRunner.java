package com.lbell91.core.actions;

import java.util.Objects;

import com.lbell91.api.model.action.ActionCompleted;
import com.lbell91.api.model.action.ActionEvent;
import com.lbell91.api.model.action.ActionHandlerRegistry;
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
        if (maxSteps <= 0) {
            throw CoreIllegalArgumentException.maxStepsIllegalArgument(maxSteps);
        }
        this.maxSteps = maxSteps;
    }

    public S runToCompletion(WorkflowDefinition<S, ActionEvent, C> workflowDefinition, C context) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");
        Objects.requireNonNull(context, "context");

        var currentState = workflowDefinition.initialState();
        int steps = 0;

        while (!workflowDefinition.terminatingStates().contains(currentState)) {
            if (++steps > maxSteps) {
                throw CoreIllegalStateException.maxStepsExceeded(maxSteps, workflowDefinition.id().toString());
            }

            var actions = workflowDefinition.actionsForState(currentState, context);

            if (actions.isEmpty()) {
                throw CoreIllegalStateException.noActionsForNonTerminatingState(currentState.toString(), workflowDefinition.id().toString());
            }

            for (var action : actions) {
                var handler = handlers.get(action.actionId());
                var result = handler.handle(action, context);
                
                var event = new ActionCompleted(action.actionId(), result);
                var next = engine.applyResult(workflowDefinition, currentState, event, context).nextState();

                currentState = next;

                break;
            }
        }

        return currentState;
    }
    
}
