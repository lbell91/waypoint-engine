package com.lbell91.api.exceptions;

import com.lbell91.api.exceptions.messages.ActionExceptionMessages;
import com.lbell91.api.exceptions.messages.WorkflowExceptionMessages;

public class ApiIllegalArgumentException extends IllegalArgumentException {
    public ApiIllegalArgumentException(String message) {
        super(message);
    }

    public static ApiIllegalArgumentException actionIdRequired() {
        return new ApiIllegalArgumentException(
                ActionExceptionMessages.ACTION_ID_REQUIRED);
    }

    public static ApiIllegalArgumentException workflowIdRequired() {
        return new ApiIllegalArgumentException(
                WorkflowExceptionMessages.WORKFLOW_ID_REQUIRED);
    }

    public static ApiIllegalArgumentException workflowInvalidDefinition(String details) {
        return new ApiIllegalArgumentException(
                String.format(WorkflowExceptionMessages.WORKFLOW_INVALID_DEFINITION, details));
    }
    
}
