package com.lbell91.api.model.workflow;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.transition.TransitionResult;

public interface WorkflowDefinition<S, E, C> {
    WorkflowId id();
    S initialState();
    Set<S> terminatingStates();
    Map<StateEventKey<S, E>, TransitionResult<S>> transitionsTable();
    
    default List<ActionCommand<C>> actionsForState(S state, C context) {
        return List.of();
    }
}
