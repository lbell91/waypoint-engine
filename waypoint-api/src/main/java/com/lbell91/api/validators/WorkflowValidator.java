package com.lbell91.api.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.lbell91.api.model.WorkflowDefinition;

public final class WorkflowValidator {

    public static <S, E, C> List<ValidationError> validate(WorkflowDefinition<S, E, C> workflowDefinition
    ) {
        var errors = new ArrayList<ValidationError>();
        if (workflowDefinition.id() == null) errors.add(ValidationError.of(ValidationErrorType.WF_ID_IS_NULL));
        if (workflowDefinition.initialState() == null) errors.add(ValidationError.of(ValidationErrorType.WF_INITIAL_STATE_IS_NULL));
        if (workflowDefinition.terminatingStates() == null) errors.add(ValidationError.of(ValidationErrorType.WF_TERMINATING_STATES_IS_NULL));
        if (workflowDefinition.transitionsTable() == null) errors.add(ValidationError.of(ValidationErrorType.WF_TRANSITIONS_TABLE_IS_NULL));
        
        return errors;
        
    }

    private WorkflowValidator() {}
}
