package com.lbell91.core.definition;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.transition.TransitionResult;
import com.lbell91.api.model.workflow.WorkflowDefinition;
import com.lbell91.api.model.workflow.WorkflowId;

public class ImmutableWorkflowDefinition <S, E, C> implements WorkflowDefinition<S, E, C> {

    private final WorkflowId id;
    private final S initialState;
    private final Set<S> terminatingStates;
    private final Map<StateEventKey<S, E>, TransitionResult<S>> transitionsTable;

    public ImmutableWorkflowDefinition(WorkflowId id,
                                        S initialState,
                                        Set<S> terminatingStates,
                                        Map<StateEventKey<S, E>, TransitionResult<S>> transitionsTable) {

        this.id = Objects.requireNonNull(id, "id");
        this.initialState = Objects.requireNonNull(initialState, "initialState");
        this.terminatingStates = Objects.requireNonNull(terminatingStates, "terminatingStates");
        this.transitionsTable = Objects.requireNonNull(transitionsTable, "transitionsTable");
    }


	@Override
	public WorkflowId id() {
		return this.id;
	}

	@Override
	public S initialState() {
		return this.initialState;
	}

	@Override
	public Set<S> terminatingStates() {
		return this.terminatingStates;
	}

	@Override
	public Map<StateEventKey<S, E>, TransitionResult<S>> transitionsTable() {
		return this.transitionsTable;
	}

}
