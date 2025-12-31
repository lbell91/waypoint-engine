package com.lbell91.core.exceptions;

import com.lbell91.api.model.WorkflowId;

public class WorkflowExecutionException extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    
    public WorkflowExecutionException(String message) {
        super(message);
    }

    public static WorkflowExecutionException terminatingState(WorkflowId workflowId, Object state) {
        return new WorkflowExecutionException(WorkflowExectuionMessages.TERMINATING_STATE.formatted(state, workflowId));
    }
    
    public static WorkflowExecutionException noTransition(WorkflowId workflowId, Object state, Object event) {
        return new WorkflowExecutionException(WorkflowExectuionMessages.NO_TRANSITION.formatted(state, event, workflowId));
    }
}
