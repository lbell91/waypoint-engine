package com.lbell91.api.validators;

import org.junit.jupiter.api.Test;

public class WorkflowValidatorTest {

    @Test
    void validate_workflow_definition() {
        var results = WorkflowValidator.validate(null);

        assert !results.isValid();
        assert results.errors().stream().anyMatch(e -> e.code().equals("WF_DEFINITION_IS_NULL"));
    }
    
}
