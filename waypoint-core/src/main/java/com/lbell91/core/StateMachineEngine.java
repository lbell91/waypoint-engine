package com.lbell91.core;

import java.util.Objects;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.transition.TransitionResult;
import com.lbell91.api.model.workflow.WorkflowDefinition;
import com.lbell91.core.exceptions.CoreIllegalStateException;

public class StateMachineEngine<S, E, C> {

    public TransitionResult<S> applyResult(WorkflowDefinition<S, E, C> workflowDefinition,
                                           S currentState,
                                           E event,
                                           C context) {
        Objects.requireNonNull(workflowDefinition);
        Objects.requireNonNull(currentState);
        Objects.requireNonNull(event);
        
        if (workflowDefinition.terminatingStates().contains(currentState)) {
            throw CoreIllegalStateException.terminatingState(workflowDefinition.id(), currentState);
        }

        var key = new StateEventKey<>(currentState, event);
        var result = workflowDefinition.transitionsTable().get(key);
        if (result == null) {
            throw CoreIllegalStateException.noTransition(workflowDefinition.id(), currentState, event);
        }
        return result;
    }
    
}
