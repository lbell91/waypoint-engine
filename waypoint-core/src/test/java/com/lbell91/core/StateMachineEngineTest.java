package com.lbell91.core;

import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.lbell91.api.model.TransitionResult;

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
        var transtions = Map.of(
                new Key(State.START, Event.GO), 
                new TransitionResult<>(State.MIDDLE),

                new Key(State.MIDDLE, Event.GO), 
                new TransitionResult<>(State.END)
        );

        var engine = new StateMachineEngine<State, Event, Context>(
                (state, event, context) -> {
                    var key = new Key(state, event);
                    var result = transtions.get(key);
                    if (result == null) {
                        throw new IllegalStateException("No transition defined for state " + state + " and event " + event);
                    }
                    return result;
                });

        var result = engine.applyEvent(State.START, Event.GO, new Context("test"));
        assert result.nextState() == State.MIDDLE;
    }
}
