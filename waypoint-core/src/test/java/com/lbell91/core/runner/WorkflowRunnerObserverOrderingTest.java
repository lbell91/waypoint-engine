package com.lbell91.core.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.action.ActionCompleted;
import com.lbell91.api.model.action.ActionEvent;
import com.lbell91.api.model.action.ActionHandlerRegistry;
import com.lbell91.api.model.action.ActionId;
import com.lbell91.api.model.action.ActionResult;
import com.lbell91.api.model.workflow.WorkflowId;
import com.lbell91.core.StateMachineEngine;
import com.lbell91.core.definition.WorkflowDefinitions;

// Adjust this import if WorkflowRunner is still in com.lbell91.core.actions

class WorkflowRunnerObserverTest {

    enum State { START, WORK, END, FAIL }

    record Ctx(String v) {}

    private static final ActionId BEGIN = new ActionId("begin");
    private static final ActionId DO_WORK = new ActionId("do-work");

    private static ActionEvent completed(ActionId actionId, ActionResult result) {
        return new ActionCompleted(actionId, result);
    }

    @Test
    void observer_emits_hooks_in_order_and_calls_onCompleted() {
        List<String> calls = new ArrayList<>();

        RunnerObserver<State, Ctx> observer = new RunnerObserver<>() {
            @Override public void onStepStart(WorkflowId id, int stepIndex, State state, Ctx context) { calls.add("stepStart"); }
            @Override public void onActionPlanned(ActionCommand<Ctx> command) { calls.add("planned"); }
            @Override public void onActionExecuted(ActionCommand<Ctx> command, ActionEvent event) { calls.add("executed"); }
            @Override public void onTransitionApplied(State fromState, State toState, ActionEvent event) { calls.add("transitioned"); }
            @Override public void onCompleted(WorkflowId workflowId, State finalState, int stepIndex) { calls.add("completed"); }
            @Override public void onFailure(WorkflowId workflowId, int stepIndex, State state, RuntimeException exception) { calls.add("failed"); }
        };

        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(new WorkflowId("wf_id"), State.START)
                .terminating(State.END)
                .terminating(State.FAIL)

                .onEntry(State.START, ActionCommand.of(BEGIN))
                .onEntry(State.WORK, ActionCommand.of(DO_WORK))

                .transition(State.START, completed(BEGIN, ActionResult.SUCCESS), State.WORK)
                .transition(State.WORK, completed(DO_WORK, ActionResult.SUCCESS), State.END)
                .transition(State.WORK, completed(DO_WORK, ActionResult.FAILURE), State.FAIL)

                .build();

        var handlers = new ActionHandlerRegistry<Ctx>()
                .register(BEGIN, (command, context) -> ActionResult.SUCCESS)
                .register(DO_WORK, (command, context) -> ActionResult.SUCCESS);

        var engine = new StateMachineEngine<State, ActionEvent, Ctx>();
        var runner = new WorkflowRunner<State, Ctx>(engine, handlers, observer, 10_000);

        var finalState = runner.runToCompletion(definition, new Ctx("context"));
        assertEquals(State.END, finalState);

        // Two steps:
        // Step 1: START => WORK
        // Step 2: WORK => END
        assertEquals(
                List.of(
                        "stepStart", "planned", "executed", "transitioned",
                        "stepStart", "planned", "executed", "transitioned",
                        "completed"
                ),
                calls
        );
    }

    @Test
    void observer_calls_onFailure_and_rethrows_when_no_actions_defined() {
        List<String> calls = new ArrayList<>();

        RunnerObserver<State, Ctx> observer = new RunnerObserver<>() {
            @Override public void onStepStart(WorkflowId id, int stepIndex, State state, Ctx context) { calls.add("stepStart"); }
            @Override public void onFailure(WorkflowId workflowId, int stepIndex, State state, RuntimeException exception) { calls.add("failed"); }
            @Override public void onCompleted(WorkflowId workflowId, State finalState, int stepIndex) { calls.add("completed"); }
        };

        // Non-terminal START with no onEntry actions => runner should throw
        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(new WorkflowId("wf_id"), State.START)
                .terminating(State.END)
                // We don’t even need a transition; runner fails before applying engine
                .build();

        var handlers = new ActionHandlerRegistry<Ctx>();
        var engine = new StateMachineEngine<State, ActionEvent, Ctx>();
        var runner = new WorkflowRunner<State, Ctx>(engine, handlers, observer, 10_000);

        assertThrows(IllegalStateException.class, () -> runner.runToCompletion(definition, new Ctx("x")));

        // We should see stepStart, then failure; never completed
        assertEquals(List.of("stepStart", "failed"), calls);
    }
}
