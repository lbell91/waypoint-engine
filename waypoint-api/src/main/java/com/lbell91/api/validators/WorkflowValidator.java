package com.lbell91.api.validators;

import java.util.ArrayList;
import com.lbell91.api.model.WorkflowDefinition;

public final class WorkflowValidator {

    public static <S, E, C> ValidationResult validate(WorkflowDefinition<S, E, C> workflowDefinition
    ) {
        var errors = new ArrayList<ValidationError>();

        if (workflowDefinition == null) {
            errors.add(ValidationError.of(ValidationErrorType.WF_DEFINITION_IS_NULL));
            return new ValidationResult(errors);
        }

        if (workflowDefinition.id() == null) errors.add(ValidationError.of(ValidationErrorType.WF_ID_IS_NULL));
        if (workflowDefinition.initialState() == null) errors.add(ValidationError.of(ValidationErrorType.WF_INITIAL_STATE_IS_NULL));
        if (workflowDefinition.terminatingStates() == null) errors.add(ValidationError.of(ValidationErrorType.WF_TERMINATING_STATES_IS_NULL));
        if (workflowDefinition.transitionsTable() == null) errors.add(ValidationError.of(ValidationErrorType.WF_TRANSITIONS_TABLE_IS_NULL));
        
        if (workflowDefinition.transitionsTable() != null) {
            workflowDefinition.transitionsTable().forEach((key, result) -> {
                if (key == null) errors.add(ValidationError.of(ValidationErrorType.WF_TERMINATING_STATES_IS_NULL));
                if (result == null) errors.add(ValidationError.of(ValidationErrorType.WF_TRANSITIONS_TABLE_IS_NULL));
                if (key != null && workflowDefinition.terminatingStates() != null &&
                    workflowDefinition.terminatingStates().contains(key.state())) {
                    errors.add(ValidationError.of(ValidationErrorType.WF_TERMINATING_STATE_HAS_OUTGOING_TRANSITIONS));
                }
            
            });
        }

        return new ValidationResult(errors);
        
    }

    public static <S, E, C> void validatOrThrow(WorkflowDefinition<S, E, C> workflowDefinition) {
        var result = validate(workflowDefinition);

        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid workflow definition " + result.errors());
        }
    }

    private WorkflowValidator() {}
}
