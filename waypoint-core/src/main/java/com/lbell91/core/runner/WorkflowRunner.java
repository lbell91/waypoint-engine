package com.lbell91.core.runner;

import java.util.List;
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
    private final RunnerObserver<S, C> observer;
    private final int maxSteps;

    public WorkflowRunner(StateMachineEngine<S, ActionEvent, C> engine,
                          ActionHandlerRegistry<C> handlers,
                          RunnerObserver<S, C> observer,
                          int maxSteps) {
        this.engine = Objects.requireNonNull(engine, "engine");
        this.handlers = Objects.requireNonNull(handlers, "handlers");
        this.observer = Objects.requireNonNull(observer, "observer");
        if (maxSteps <= 0) throw CoreIllegalArgumentException.maxStepsIllegalArgument(maxSteps);
        this.maxSteps = maxSteps;
    }

    public WorkflowRunner(StateMachineEngine<S, ActionEvent, C> engine,
                          ActionHandlerRegistry<C> handlers) {
        this(engine, handlers, new NoopRunnerObserver<>(), 10_000);
    }

    public WorkflowRunner(StateMachineEngine<S, ActionEvent, C> engine,
                          ActionHandlerRegistry<C> handlers,
                          int maxSteps) {
        this(engine, handlers, new NoopRunnerObserver<>(), maxSteps);
    }

    public WorkflowRunner(StateMachineEngine<S, ActionEvent, C> engine,
                          ActionHandlerRegistry<C> handlers,
                          RunnerObserver<S, C> observer) {
        this(engine, handlers, observer, 10_000);
    }

    public WorkflowRunner(StateMachineEngine<S, ActionEvent, C> engine,
                          ActionHandlerRegistry<C> handlers,
                          List<RunnerObserver<S, C>> observers,
                          int maxSteps) {
        this(engine, handlers, new CompositeRunnerObserver<>(observers), maxSteps);
    }

    public RunStepResult<S, C> runStep(WorkflowDefinition<S, ActionEvent, C> workflowDefinition,
                                        S currentState,
                                        C context) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");
        Objects.requireNonNull(currentState, "currentState");
        
        return runStepInternal(workflowDefinition, currentState, context, 0);
    }

    public S runToCompletion(WorkflowDefinition<S, ActionEvent, C> workflowDefinition, C context) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");

        S currentState = workflowDefinition.initialState();
        int steps = 0;
            while (!workflowDefinition.terminatingStates().contains(currentState)) {
                if (++steps > maxSteps) {
                    RuntimeException ex = CoreIllegalStateException.maxStepsExceeded(maxSteps, workflowDefinition.id().toString());
                    observer.onFailure(workflowDefinition.id(), steps, currentState, ex);
                    throw ex;
                }

                RunStepResult<S, C> step = runStepInternal(workflowDefinition, currentState, context, steps);
                currentState = step.toState();
            }

            observer.onCompleted(workflowDefinition.id(), currentState, steps);

            return currentState;
        
    }

    

    private RunStepResult<S, C> runStepInternal(WorkflowDefinition<S, ActionEvent, C> workflowDefinition,
                                               S currentState,
                                               C context,
                                               int stepIndex) {
        try {
            observer.onStepStart(workflowDefinition.id(), stepIndex, currentState, context);

            if (workflowDefinition.terminatingStates().contains(currentState)) {
                throw CoreIllegalStateException.terminatingState(workflowDefinition.id(), currentState);
            }

            var actions = workflowDefinition.actionsForState(currentState, context);

            if (actions == null || actions.isEmpty()) {
                throw CoreIllegalStateException.noActionsForNonTerminatingState(currentState.toString(), workflowDefinition.id().toString());
            }

            ActionCommand<C> action = actions.get(0);
            observer.onActionPlanned(action);

            var handler = handlers.get(action.actionId());

            ActionResult result = handler.handle(action, context);
            ActionEvent event = new ActionCompleted(action.actionId(), result);
            observer.onActionExecuted(action, event);

            var transition = engine.applyResult(workflowDefinition, currentState, event, context);
            S nextState = transition.nextState();
            observer.onTransitionApplied(currentState, nextState, event);

            return new RunStepResult<>(currentState, nextState, action, event);
        } catch (RuntimeException ex) {
            observer.onFailure(workflowDefinition.id(), stepIndex, currentState, ex);
            throw ex;
        }
    }
    
}
