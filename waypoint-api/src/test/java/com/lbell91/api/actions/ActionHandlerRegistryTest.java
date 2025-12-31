package com.lbell91.api.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.lbell91.api.model.action.ActionCommand;
import com.lbell91.api.model.action.ActionHandlerRegistry;
import com.lbell91.api.model.action.ActionId;
import com.lbell91.api.model.action.ActionResult;

class ActionHandlerRegistryTest {
    

    @Test
    void get_registered_handler_should_return_handler() {
        var registry = new ActionHandlerRegistry<String>();
        var actionId = new ActionId("do-something");

        registry.register(actionId, (command, context) -> ActionResult.SUCCESS);
        
        var handler = registry.get(actionId);
        var result = handler.handle(ActionCommand.<String>of(actionId), "ctx");

        assertEquals(ActionResult.SUCCESS, result);
    }

    @Test
    void get_unregistered_handler_should_throw_exception() {
        var registry = new ActionHandlerRegistry<String>();
        

        assertThrows(IllegalStateException.class, () -> registry.get(new ActionId("missing")));
    }

}
