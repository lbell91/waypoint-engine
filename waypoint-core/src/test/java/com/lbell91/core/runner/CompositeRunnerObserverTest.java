package com.lbell91.core.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CompositeRunnerObserverTest {
    
    private static final class RecordingObserver<S, C> implements RunnerObserver<S, C> {
        private final String name;
        private final List<String> calls;

        private RecordingObserver(String name, List<String> calls) {
            this.name = name;
            this.calls = calls;
        }

        @Override public void onStepStart(com.lbell91.api.model.workflow.WorkflowId workflowId, int stepIndex, S state, C context) {
            calls.add(name + ":stepStart(" + stepIndex + "," + state + ")");
        }
    }

    @Test
    void onStepStart_callsAllObservers() {
        var calls = new ArrayList<String>();
        RunnerObserver<String, String> a = new RecordingObserver<>("A", calls);
        RunnerObserver<String, String> b = new RecordingObserver<>("B", calls);

        var mutable = new ArrayList<RunnerObserver<String, String>>();

        mutable.add(a);

        var composite = new CompositeRunnerObserver<>(mutable);

        mutable.add(b);

        composite.onStepStart(null, 1, "STATE", "CTX");

        assertEquals(List.of("A:stepStart(1,STATE)"), calls);

    }

    @Test
    void onStepStart_withMultipleObservers_callsAllObservers() {
        var calls = new ArrayList<String>();
        RunnerObserver<String, String> a = new RecordingObserver<>("A", calls);
        RunnerObserver<String, String> b = new RecordingObserver<>("B", calls);

        var composite = new CompositeRunnerObserver<>(List.of(a, b));

        composite.onStepStart(null, 1, "STATE", "CTX");

        assertEquals(List.of(
            "A:stepStart(1,STATE)",
            "B:stepStart(1,STATE)"
        ), calls);

    }
}
