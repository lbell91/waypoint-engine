package com.lbell91.api.model.action;

@FunctionalInterface
public interface ActionHandler<C> {
    ActionResult handle(ActionCommand<C> command, C context);
}