package com.lbell91.core.exceptions;

final class WorkflowExectuionMessages {

    static final String TERMINATING_STATE = "Cannot apply event to terminating state '%s' in workflow '%s'.";
    static final String NO_TRANSITION = "No transition defined for state '%s' with event '%s' in workflow '%s'.";

    private WorkflowExectuionMessages() {
    }
    
}
