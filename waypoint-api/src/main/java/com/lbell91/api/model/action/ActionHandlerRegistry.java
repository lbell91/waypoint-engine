package com.lbell91.api.model.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActionHandlerRegistry<C>{
    
    private final Map<ActionId, ActionHandler<C>> handlers = new HashMap<>();

    public ActionHandlerRegistry<C> register(ActionId actionId, ActionHandler<C> handler) {
        Objects.requireNonNull(actionId, "actionId");
        Objects.requireNonNull(handler, "handler");

        var previous = handlers.putIfAbsent(actionId, handler);
        if (previous != null) {
            throw new IllegalStateException("Handler already registered for actionId " + actionId);
        }
        return this;
    }

    public ActionHandler<C> get(ActionId actionId) {
        Objects.requireNonNull(actionId, "actionId");
        var handler = handlers.get(actionId);
        if (handler == null) {
            throw new IllegalStateException("No handler registered for actionId " + actionId);
        }
        return handler;
    }
    
}
