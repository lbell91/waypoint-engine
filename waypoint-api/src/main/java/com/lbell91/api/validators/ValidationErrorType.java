package com.lbell91.api.validators;

public enum ValidationErrorType {
    WF_ID_IS_NULL("WF_ID_IS_NULL", "Workflow id is null."),
    WF_INITIAL_STATE_IS_NULL("WF_INITIAL_STATE_IS_NULL", "Workflow initial state is null."),
    WF_TERMINATING_STATES_IS_NULL("WF_TERMINATING_STATES_IS_NULL", "Workflow terminating states is null."),
    WF_TRANSITIONS_TABLE_IS_NULL("WF_TRANSITIONS_TABLE_IS_NULL", "Workflow transitions table is null.");

    private final String errorCode;
    private final String message;

    ValidationErrorType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String code() {
        return errorCode;
    }

    public String message() {
        return message;
    }
}
