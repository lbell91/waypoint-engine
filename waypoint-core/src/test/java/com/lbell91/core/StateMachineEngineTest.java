package com.lbell91.core;

import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.TransitionResult;
import com.lbell91.api.model.WorkflowDefinition;
import com.lbell91.api.model.WorkflowId;

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
        var transitions = Map.of(
            new StateEventKey<>(State.START, Event.GO), new TransitionResult<>(State.MIDDLE),
            new StateEventKey<>(State.MIDDLE, Event.GO), new TransitionResult<>(State.END)
        );

        var workflowDefinition = new WorkflowDefinition<State,Event,Context>() {
            @Override public String id() {return new String("test-workflow");}
            @Override public State initialState() {return State.START;}
            @Override public java.util.Set<State> terminatingStates() {return java.util.Set.of(State.END);}
            @Override public Map<StateEventKey<State, Event>, TransitionResult<State>> transitionsTable() {return transitions;}
        };

        var engine = new StateMachineEngine<State, Event, Context>();

        var result = engine.applyResult(workflowDefinition, State.START, Event.GO, new Context("test"));
        assert result.nextState().equals(State.MIDDLE);
    }
}
