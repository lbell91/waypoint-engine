package com.lbell91.core.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

class WorkflowRunnerTest {

    enum State { START, WORK, END, FAIL }

    record Ctx(String v) {}

    private static final ActionId BEGIN = new ActionId("begin");
    private static final ActionId DO_WORK = new ActionId("do-work");

    private static ActionEvent completed(ActionId actionId, ActionResult result) {
        return new ActionCompleted(actionId, result);
    }

    @Test
    void runner_executes_actions_and_transitions_to_end() {
        var workflowId = new WorkflowId("wf_id");

        var beginCmd = ActionCommand.<Ctx>of(BEGIN);
        var doWorkCmd = ActionCommand.<Ctx>of(DO_WORK);

        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(workflowId, State.START)
                .terminating(State.END)
                .terminating(State.FAIL)

                .onEntry(State.START, beginCmd)
                .onEntry(State.WORK, doWorkCmd)

                .transition(State.START, completed(BEGIN, ActionResult.SUCCESS), State.WORK)
                .transition(State.START, completed(DO_WORK, ActionResult.FAILURE), State.FAIL)

                .transition(State.WORK, completed(DO_WORK, ActionResult.SUCCESS), State.END)
                .transition(State.WORK, completed(DO_WORK, ActionResult.FAILURE), State.FAIL)

                .build();

        var handlers = new ActionHandlerRegistry<Ctx>()
                .register(BEGIN, (command, context) -> ActionResult.SUCCESS)
                .register(DO_WORK, (command, context) -> ActionResult.SUCCESS);

        var engine = new StateMachineEngine<State, ActionEvent, Ctx>();
        var runner = new WorkflowRunner<State, Ctx>(engine, handlers);

        // runToCompletion now requires the action to execute for the whole run.
        // In this workflow, step 1 must use BEGIN, step 2 must use DO_WORK.
        // So we drive the two steps explicitly.
        var ctx = new Ctx("context");

        var step1 = runner.runStep(definition, State.START, ctx, beginCmd, 1);
        assertEquals(State.WORK, step1.toState());

        var step2 = runner.runStep(definition, step1.toState(), ctx, doWorkCmd, 2);
        assertEquals(State.END, step2.toState());
    }

    @Test
    void runStep_failure_path_should_end_in_FAIL() {
        var workflowId = new WorkflowId("wf_id");

        var beginCmd = ActionCommand.<Ctx>of(BEGIN);
        var doWorkCmd = ActionCommand.<Ctx>of(DO_WORK);

        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(workflowId, State.START)
                .terminating(State.END)
                .terminating(State.FAIL)

                .onEntry(State.START, beginCmd)
                .onEntry(State.WORK, doWorkCmd)

                .transition(State.START, completed(BEGIN, ActionResult.SUCCESS), State.WORK)
                .transition(State.START, completed(DO_WORK, ActionResult.FAILURE), State.FAIL)

                .transition(State.WORK, completed(DO_WORK, ActionResult.SUCCESS), State.END)
                .transition(State.WORK, completed(DO_WORK, ActionResult.FAILURE), State.FAIL)

                .build();

        var handlers = new ActionHandlerRegistry<Ctx>()
                .register(BEGIN, (command, context) -> ActionResult.SUCCESS)
                .register(DO_WORK, (command, context) -> ActionResult.FAILURE);

        var engine = new StateMachineEngine<State, ActionEvent, Ctx>();
        var runner = new WorkflowRunner<State, Ctx>(engine, handlers);

        var ctx = new Ctx("context");

        var step1 = runner.runStep(definition, State.START, ctx, beginCmd, 1);
        assertEquals(State.WORK, step1.toState());

        var step2 = runner.runStep(definition, step1.toState(), ctx, doWorkCmd, 2);
        assertEquals(State.FAIL, step2.toState());
    }

    @Test
    void runStep_no_actions_defined_should_throw_exception() {
        var workflowId = new WorkflowId("wf_id");

        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(workflowId, State.START)
                .terminating(State.END)
                .transition(State.START, completed(new ActionId("any"), ActionResult.SUCCESS), State.END)
                .build();

        var handlers = new ActionHandlerRegistry<Ctx>();
        var engine = new StateMachineEngine<State, ActionEvent, Ctx>();
        var runner = new WorkflowRunner<State, Ctx>(engine, handlers);

        var dummyCmd = ActionCommand.<Ctx>of(new ActionId("dummy"));

        assertThrows(IllegalStateException.class, () ->
                runner.runStep(definition, State.START, new Ctx("x"), dummyCmd, 1)
        );
    }
}
