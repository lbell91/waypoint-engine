package com.lbell91.core.exceptions;

import com.lbell91.core.exceptions.messages.WorkflowRunnerMessages;

public class CoreIllegalArgumentException extends IllegalArgumentException {

    public CoreIllegalArgumentException(String message) {
        super(message);
    }

    public static CoreIllegalArgumentException maxStepsIllegalArgument(int maxSteps) {
        return new CoreIllegalArgumentException(
                String.format(WorkflowRunnerMessages.MAX_STEPS_ILLEGAL_ARGUMENT, maxSteps)
        );
    }
    
}
