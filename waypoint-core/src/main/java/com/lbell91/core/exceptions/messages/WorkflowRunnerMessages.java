package com.lbell91.core.exceptions.messages;

public final class WorkflowRunnerMessages {


    public static final String MAX_STEPS_ILLEGAL_ARGUMENT = "Max steps must be greater than zero.";
    public static final String MAX_STEPS_EXCEEDED = "Max steps exceeded (%s) for workflow '%s'.";

    public static final String NO_ACTIONS_FOR_NON_TERMINATING_STATE = "No actions defined for non-terminating state '%s' in workflow '%s'.";

    private WorkflowRunnerMessages() {
    }
    
}
