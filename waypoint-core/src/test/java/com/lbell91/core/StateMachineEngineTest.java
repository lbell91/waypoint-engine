package com.lbell91.core;

import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.TransitionResult;
import com.lbell91.api.model.WorkflowDefinition;
import com.lbell91.api.model.WorkflowId;
import com.lbell91.core.definition.WorkflowDefinitions;

public class StateMachineEngineTest {
    enum State {
        START,
        MIDDLE,
        END
    }

    enum Event {
        GO,
        STOP
    }

    record Context(String info) {
    }

    // Simple key class to represent state-event pairs for a transition table
    record Key(State state, Event event) {
        Key {
            Objects.requireNonNull(state);
            Objects.requireNonNull(event);
        }
    }


    @Test
    void apply_valid_event_should_transition_state() {
        var workflowDefinition = WorkflowDefinitions.<State,Event,Context>builder(new WorkflowId("test-workflow"), State.START)
            .terminating(State.END)
            .transition(State.START, Event.GO, State.MIDDLE)
            .transition(State.MIDDLE, Event.GO, State.END)
            .build();

        var engine = new StateMachineEngine<State, Event, Context>();

        var result = engine.applyResult(workflowDefinition, State.START, Event.GO, new Context("test"));
        assert result.nextState().equals(State.MIDDLE);
    }

    @Test
    void apply_event_on_terminating_state_should_throw() {
        var workflowDefinition = WorkflowDefinitions.<State,Event,Context>builder(new WorkflowId("test-workflow"), State.START)
            .terminating(State.END)
            .transition(State.START, Event.GO, State.MIDDLE)
            .transition(State.MIDDLE, Event.GO, State.END)
            .build();

        var engine = new StateMachineEngine<State, Event, Context>();

        try {
            engine.applyResult(workflowDefinition, State.END, Event.GO, new Context("test"));
            assert false;
        } catch (IllegalStateException e) {
            assert e.getMessage().contains("Cannot apply event to terminating state");
        }
    }
}
