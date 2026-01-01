package com.lbell91.core.exceptions;

import com.lbell91.api.model.workflow.WorkflowId;
import com.lbell91.core.exceptions.messages.WorkflowExecutionMessages;
import com.lbell91.core.exceptions.messages.WorkflowRunnerMessages;

public class CoreIllegalStateException extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    
    public CoreIllegalStateException(String message) {
        super(message);
    }

    public static CoreIllegalArgumentException actionNotDefinedForState(String actionId, String state, String workflowId) {
        return new CoreIllegalArgumentException(
                String.format(WorkflowExecutionMessages.ACTION_NOT_DEFINED_FOR_STATE, actionId, state, workflowId)
        );
    }

    public static CoreIllegalStateException terminatingState(WorkflowId workflowId, Object state) {
        return new CoreIllegalStateException(WorkflowExecutionMessages.TERMINATING_STATE.formatted(state, workflowId));
    }
    
    public static CoreIllegalStateException noTransition(WorkflowId workflowId, Object state, Object event) {
        return new CoreIllegalStateException(WorkflowExecutionMessages.NO_TRANSITION.formatted(state, event, workflowId));
    }

    public static CoreIllegalStateException maxStepsExceeded(int maxSteps, String workflowId) {
        return new CoreIllegalStateException(
                String.format(WorkflowRunnerMessages.MAX_STEPS_EXCEEDED, maxSteps, workflowId)
        );
    }

    public static CoreIllegalStateException noActionsForNonTerminatingState(String state, String workflowId) {
        return new CoreIllegalStateException(
                String.format(WorkflowRunnerMessages.NO_ACTIONS_FOR_NON_TERMINATING_STATE, state, workflowId)
        );
    }

    public static CoreIllegalStateException transitionDefined(Object state, Object event) {
        return new CoreIllegalStateException(WorkflowExecutionMessages.TRANSITION_DEFINED.formatted(state, event));
    }
}
