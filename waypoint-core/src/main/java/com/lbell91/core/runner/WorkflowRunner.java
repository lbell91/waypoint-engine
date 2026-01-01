package com.lbell91.core.runner;

import java.util.List;
import java.util.Objects;

import javax.swing.Action;

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
                                       C context,
                                       ActionCommand<C> actionToExecute,
                                       int stepIndex) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");
        Objects.requireNonNull(currentState, "currentState");
        Objects.requireNonNull(actionToExecute, "actionToExecute");

        return runStepInternal(workflowDefinition, currentState, context, actionToExecute, stepIndex);
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

            var actions = workflowDefinition.actionsForState(currentState, context);

            if (actions == null || actions.isEmpty()) {
                RuntimeException ex = CoreIllegalStateException.noActionsForNonTerminatingState(
                        currentState.toString(),
                        workflowDefinition.id().toString()
                );
                observer.onFailure(workflowDefinition.id(), steps, currentState, ex);
                throw ex;
            }

            if (actions.size() != 1) {
                // Don't guess. This method is only for "automatic" workflows.
                RuntimeException ex = new IllegalStateException(
                        "Cannot auto-run: multiple actions available. workflowId=" + workflowDefinition.id()
                                + ", state=" + currentState
                                + ", actionCount=" + actions.size()
                );
                observer.onFailure(workflowDefinition.id(), steps, currentState, ex);
                throw ex;
            }

            var step = runStepInternal(workflowDefinition, currentState, context, actions.get(0), steps);
            currentState = step.toState();
        }

        observer.onCompleted(workflowDefinition.id(), currentState, steps);
        return currentState;
    }

    private RunStepResult<S, C> runStepInternal(WorkflowDefinition<S, ActionEvent, C> workflowDefinition,
                                                S currentState,
                                                C context,
                                                ActionCommand<C> actionToExecute,
                                                int stepIndex) {
        try {
            observer.onStepStart(workflowDefinition.id(), stepIndex, currentState, context);

            if (workflowDefinition.terminatingStates().contains(currentState)) {
                throw CoreIllegalStateException.terminatingState(workflowDefinition.id(), currentState);
            }

            var actions = workflowDefinition.actionsForState(currentState, context);

            if (actions == null || actions.isEmpty()) {
                throw CoreIllegalStateException.noActionsForNonTerminatingState(
                        currentState.toString(),
                        workflowDefinition.id().toString()
                );
            }

            if (!actions.contains(actionToExecute)) {
                throw CoreIllegalStateException.actionNotDefinedForState(
                        actionToExecute.actionId().toString(),
                        currentState.toString(),
                        workflowDefinition.id().toString()
                );
            }

            observer.onActionPlanned(actionToExecute);

            var handler = handlers.get(actionToExecute.actionId());

            ActionResult result = handler.handle(actionToExecute, context);
            ActionEvent event = new ActionCompleted(actionToExecute.actionId(), result);
            observer.onActionExecuted(actionToExecute, event);

            var transition = engine.applyResult(workflowDefinition, currentState, event, context);
            S nextState = transition.nextState();
            observer.onTransitionApplied(currentState, nextState, event);

            return new RunStepResult<>(currentState, nextState, actionToExecute, event);
        } catch (RuntimeException ex) {
            observer.onFailure(workflowDefinition.id(), stepIndex, currentState, ex);
            throw ex;
        }
    }

    public List<ActionCommand<C>> getAvailableActions(WorkflowDefinition<S, ActionEvent, C> workflowDefinition,
                                                            WorkflowInstance<S, C> instance) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");
        Objects.requireNonNull(instance, "instance");

        return workflowDefinition.actionsForState(instance.currentState(), instance.context());
    }

    public RunStepResult<S, C> runStep(WorkflowDefinition<S, ActionEvent, C> workflowDefinition,
                                       WorkflowInstance<S, C> instance,
                                       ActionCommand<C> actionToExecute) {
        Objects.requireNonNull(workflowDefinition, "workflowDefinition");
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(actionToExecute, "actionToExecute");
        
        int nextStepIndex = instance.stepIndex() + 1;

        RunStepResult<S, C> step = runStep(
                workflowDefinition,
                instance.currentState(),
                instance.context(),
                actionToExecute,
                nextStepIndex
        );

        return step;
    }
}
