package com.lbell91.core.definition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.TransitionResult;
import com.lbell91.api.model.WorkflowDefinition;
import com.lbell91.api.model.WorkflowId;
import com.lbell91.api.validators.WorkflowValidator;

public class WorkflowDefinitions {


    public static <S, E, C> Builder<S, E, C> builder(WorkflowId id, S initialState) {
        return new Builder<>(id, initialState);
    }

    public static final class Builder<S, E, C> {
        private final WorkflowId id;
        private final S initialState;
        private final Set<S> terminatingStates = new HashSet<>();
        private final Map<StateEventKey<S, E>, TransitionResult<S>> transitions = new HashMap<>();   

        Builder(WorkflowId id, S initialState) {
            this.id = Objects.requireNonNull(id, "id");
            this.initialState = Objects.requireNonNull(initialState, "initialState");
        }

        public Builder<S, E, C> terminating(S state) {
            this.terminatingStates.add(Objects.requireNonNull(state, "state"));
            return this;
        }

        public Builder<S, E, C> transition(S from, E event, S to) {
            Objects.requireNonNull(from, "from");
            Objects.requireNonNull(event, "event");
            Objects.requireNonNull(to, "to");

            var key = new StateEventKey<>(from, event);
            var previous = transitions.putIfAbsent(key, new TransitionResult<>(to));

            if (previous != null) {
                throw new IllegalStateException(
                    "Transition already defined for state " + 
                    from + 
                    " and event " + 
                    event);
            }


            return this;
        }

        public FromStep from(S from) {
            return new FromStep(from);
        }

        public final class FromStep {
            private final S from;

            FromStep(S from) {
                this.from = Objects.requireNonNull(from, "from");
            }

            public OnStep on(E event) {
                return new OnStep(from, event);
            }
            
        }

        public final class OnStep {

            private final S from;
            private final E event;

            OnStep(S from, E event) {
                this.from = Objects.requireNonNull(from, "from");
                this.event = Objects.requireNonNull(event, "event");
            }

            public Builder<S, E, C> to(S to) {
                return transition(from, event, to);
            }
        }

        public WorkflowDefinition<S, E, C> build() {
            var definition = new ImmutableWorkflowDefinition<S, E, C>(id, initialState, terminatingStates, transitions);

            WorkflowValidator.validateOrThrow(definition);

            return definition;
        }
    }

private WorkflowDefinitions() {}

}