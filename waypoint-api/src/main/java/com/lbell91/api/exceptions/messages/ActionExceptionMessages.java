package com.lbell91.api.exceptions.messages;

public final class ActionExceptionMessages {

    public static final String ACTION_HANDLER_NOT_FOUND = "Action handler not found for action id '%s'.";
    public static final String ACTION_HANDLER_ALREADY_REGISTERED = "Action handler already registered for action id '%s'.";
    public static final String ACTION_ID_REQUIRED = "Action id must not be blank";

    private ActionExceptionMessages() {
    }
    
}
