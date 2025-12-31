package com.lbell91.api.exceptions;

import com.lbell91.api.exceptions.messages.ActionExceptionMessages;

public class ApiIllegalStateException extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    
    public ApiIllegalStateException(String message) {
        super(message);
    }

    public static ApiIllegalStateException actionHandlerNotFound(String actionId) {
        return new ApiIllegalStateException(
                String.format(ActionExceptionMessages.ACTION_HANDLER_NOT_FOUND, actionId));
    }

    public static ApiIllegalStateException actionHandlerAlreadyRegistered(String actionId) {
        return new ApiIllegalStateException(
                String.format(ActionExceptionMessages.ACTION_HANDLER_ALREADY_REGISTERED, actionId));
    }

}
