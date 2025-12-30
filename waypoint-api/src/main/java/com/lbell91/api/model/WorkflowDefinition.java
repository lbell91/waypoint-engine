package com.lbell91.api.model;

import java.util.Map;
import java.util.Set;

public interface WorkflowDefinition<S, E, C> {
    String id();
    S initialState();
    Set<S> terminatingStates();
    Map<StateEventKey<S, E>, TransitionResult<S>> transitionsTable();
    
}
