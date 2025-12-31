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
    
    enum State { START, WORK, END, FAIL}

    record Ctx(String v) {}

    private static final ActionId BEGIN = new ActionId("begin");
    private static final ActionId DO_WORK = new ActionId("do-work");

    private static final ActionEvent completed(ActionId actionId, ActionResult result) {
        return new ActionCompleted(actionId, result);
    }

    @Test
    void runner_executes_actions_and_transitions_to_end() {
        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(new WorkflowId("wf_id"), State.START)
            .terminating(State.END)
            .terminating(State.FAIL)

            .onEntry(State.START, ActionCommand.of(BEGIN))
            .onEntry(State.WORK, ActionCommand.of(DO_WORK))
            
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

        var finalState = runner.runToCompletion(definition, new Ctx("context"));
        assertEquals(State.END, finalState);
    }

    @Test
    void runToCompletion_failure_path_should_end_in_FAIL() {
        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(new WorkflowId("wf_id"), State.START)
            .terminating(State.END)
            .terminating(State.FAIL)

            .onEntry(State.START, ActionCommand.of(BEGIN))
            .onEntry(State.WORK, ActionCommand.of(DO_WORK))
            
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

        var finalState = runner.runToCompletion(definition, new Ctx("context"));
        assertEquals(State.FAIL, finalState);
    }

    @Test
    void runToCompletion_no_actions_defined_should_throw_exception() {
        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(new WorkflowId("wf_id"), State.START)
            .terminating(State.END)
            .transition(State.START, completed(new ActionId("any"), ActionResult.SUCCESS), State.END)
            .build();

        var handlers = new ActionHandlerRegistry<Ctx>();
        var engine = new StateMachineEngine<State, ActionEvent, Ctx>();
        var runner = new WorkflowRunner<State, Ctx>(engine, handlers);

        assertThrows(IllegalStateException.class, () -> runner.runToCompletion(definition, new Ctx("x")));
    }
}
