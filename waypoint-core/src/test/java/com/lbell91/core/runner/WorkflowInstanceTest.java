package com.lbell91.core.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.action.ActionCompleted;
import com.lbell91.api.model.action.ActionEvent;
import com.lbell91.api.model.action.ActionHandlerRegistry;
import com.lbell91.api.model.action.ActionId;
import com.lbell91.api.model.action.ActionResult;
import com.lbell91.core.definition.WorkflowDefinitions;

public class WorkflowInstanceTest {
    
    enum State {HOME, SELL }
    record Ctx(String v) {}

    private static final ActionId SELL = new ActionId("sell");
    private static final ActionEvent completed(ActionId actionId, ActionResult result) {
        return new ActionCompleted(actionId, result);
    }

    @Test
    void instance_tracks_state_and_stepIndex_across_steps() {
        var definition = WorkflowDefinitions.<State, ActionEvent, Ctx>builder(new com.lbell91.api.model.workflow.WorkflowId("wf-id"), State.HOME)
                .terminating(State.SELL)

                .onEntry(State.HOME, ActionCommand.<Ctx>of(SELL))

                .transition(State.HOME, completed(SELL, ActionResult.SUCCESS), State.SELL)
                .build();

        var handlers = new ActionHandlerRegistry<Ctx>()
                .register(SELL, (command, context) -> ActionResult.SUCCESS);
        var runner = new WorkflowRunner<State, Ctx>(new com.lbell91.core.StateMachineEngine<>(), handlers);
        var instance = WorkflowInstance.start(definition, new Ctx("context"));

        assertEquals(State.HOME, instance.currentState());
        assertEquals(0, instance.stepIndex());

        var step = runner.runStep(definition, instance, ActionCommand.of(SELL));

        WorkflowInstance<State, Ctx> next = instance.next(step.toState());
        
        assertEquals(State.HOME, instance.currentState());
        assertEquals(0, instance.stepIndex())
        ;
        assertEquals(State.SELL, next.currentState());
        assertEquals(1, next.stepIndex());
    }
}
