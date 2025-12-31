package com.lbell91.api.model.action;

import java.util.Map;
import java.util.Objects;

public record ActionCommand<C>(ActionId actionId, Map<String, Object> parameters) {
    
    public ActionCommand {
        Objects.requireNonNull(actionId, "actionId");
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public static <C> ActionCommand<C> of(ActionId actionId) {
        return new ActionCommand<>(actionId, Map.of());
    }

    public static <C> ActionCommand<C> of(ActionId actionId, Map<String, Object> parameters) {
        return new ActionCommand<>(actionId, parameters);
    }
}
