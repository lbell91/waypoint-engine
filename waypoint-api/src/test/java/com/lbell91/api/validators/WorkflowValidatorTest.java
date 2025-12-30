package com.lbell91.api.validators;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.lbell91.api.model.StateEventKey;
import com.lbell91.api.model.TransitionResult;
import com.lbell91.api.model.WorkflowDefinition;
import com.lbell91.api.model.WorkflowId;

public class WorkflowValidatorTest {

    @Test
    void validate_workflow_definition() {
        var results = WorkflowValidator.validate(null);

        assert !results.isValid();
        assert results.errors().stream().anyMatch(e -> e.code().equals("WF_DEFINITION_IS_NULL"));
    }

    @Test
    void validate_flag_outgoing_transitions_from_terminating_states() {
        var workflowDefinition = new WorkflowDefinition<String, String, String>() {
            @Override public WorkflowId id() {return new com.lbell91.api.model.WorkflowId("test-wf");}
            @Override public String initialState() {return "START";}
            @Override public Set<String> terminatingStates() {return Set.of("END");}
            @Override public Map<StateEventKey<String, String>, TransitionResult<String>> transitionsTable() {
                return Map.of(
                    new StateEventKey<>("END", "GO"), new TransitionResult<>("MIDDLE")
                );
            }
        };

        var results = WorkflowValidator.validate(workflowDefinition);

        assert !results.isValid();
        assert results.errors().stream().anyMatch(e -> e.code().equals("WF_TERMINATING_STATE_HAS_OUTGOING_TRANSITIONS"));
    }
    
}
