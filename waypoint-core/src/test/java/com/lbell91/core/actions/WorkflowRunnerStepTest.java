package com.lbell91.core.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.Action;

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

public class WorkflowRunnerStepTest {
    

    enum State { START, WORK, END, FAIL }

    record Ctx(String info) {}

    private static final ActionId BEGIN = new ActionId("begin");
    private static final ActionId PROCESS = new ActionId("process");

    private static ActionEvent completed(ActionId actionId, ActionResult result) {
        return new ActionCompleted(actionId, result);
    }

    @Test
    void runStep_executes_once_action_and_transitions_once() {
        var workflowId = new WorkflowId("wf-id");

        var def = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(workflowId, State.START)
            .terminating(State.END)
            .terminating(State.FAIL)

            .onEntry(State.START, ActionCommand.of(BEGIN))
            .onEntry(State.WORK, ActionCommand.of(PROCESS))

            .transition(State.START, completed(BEGIN, ActionResult.SUCCESS), State.WORK)
            .transition(State.START, completed(BEGIN, ActionResult.FAILURE), State.FAIL)

            .transition(State.WORK, completed(PROCESS, ActionResult.SUCCESS), State.END)
            .transition(State.WORK, completed(PROCESS, ActionResult.FAILURE), State.FAIL)
            .build();

        var handlers = new ActionHandlerRegistry<Ctx>()
            .register(BEGIN, (action, ctx) -> ActionResult.SUCCESS)
            .register(PROCESS, (action, ctx) -> ActionResult.SUCCESS);

        var engine = new StateMachineEngine<State, ActionEvent, Ctx>();
        var runner = new WorkflowRunner<State, Ctx>(engine, handlers);

        var step1 = runner.runStep(def, State.START, new Ctx("context-1"));
        assertEquals(State.START, step1.fromState());
        assertEquals(State.WORK, step1.toState());
        assertEquals(BEGIN, step1.action().actionId());
        assertEquals(new ActionCompleted(BEGIN, ActionResult.SUCCESS), step1.event());

        var step2 = runner.runStep(def, step1.toState(), new Ctx("context-2"));
        assertEquals(State.WORK, step2.fromState());
        assertEquals(State.END, step2.toState());
        assertEquals(PROCESS, step2.action().actionId());
        assertEquals(new ActionCompleted(PROCESS, ActionResult.SUCCESS), step2.event());
    }
}
