package com.lbell91.core.definition;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.Action;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.transition.TransitionResult;
import com.lbell91.api.model.workflow.WorkflowDefinition;
import com.lbell91.api.model.workflow.WorkflowId;

public class ImmutableWorkflowDefinition <S, E, C> implements WorkflowDefinition<S, E, C> {

    private final WorkflowId id;
    private final S initialState;
    private final Set<S> terminatingStates;
    private final Map<StateEventKey<S, E>, TransitionResult<S>> transitionsTable;
    private final Map<S, List<ActionCommand<C>>> actionsByState;
    public ImmutableWorkflowDefinition(WorkflowId id,
                                        S initialState,
                                        Set<S> terminatingStates,
                                        Map<StateEventKey<S, E>, TransitionResult<S>> transitionsTable,
                                        Map<S, List<ActionCommand<C>>> actionsByState) {
        this.id = Objects.requireNonNull(id, "id");
        this.initialState = Objects.requireNonNull(initialState, "initialState");
        this.terminatingStates = Objects.requireNonNull(terminatingStates, "terminatingStates");
        this.transitionsTable = Objects.requireNonNull(transitionsTable, "transitionsTable");
        this.actionsByState = actionsByState.entrySet().stream()
                                        .collect(
                                            java.util.stream.Collectors.toUnmodifiableMap(
                                                Map.Entry::getKey,
                                                e -> List.copyOf(e.getValue())
                                            )
                                        );
    }


	@Override
	public WorkflowId id() {
		return this.id;
	}

    @Override
    public List<ActionCommand<C>> actionsForState(S state, C context) {
        return this.actionsByState.getOrDefault(state, List.of());
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
