package com.lbell91.core.exceptions.messages;

public final class WorkflowExecutionMessages {

    public static final String TERMINATING_STATE = "Cannot apply event to terminating state '%s' in workflow '%s'.";
    public static final String NO_TRANSITION = "No transition defined for state '%s' with event '%s' in workflow '%s'.";
    public static final String TRANSITION_DEFINED = "Transition already defined fore state '%s' with event '%s'.";
    private WorkflowExecutionMessages() {
    }
    
}
