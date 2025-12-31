package com.lbell91.api.model.action;

import java.util.Objects;

public record ActionCompleted(ActionId actionId, ActionResult result) implements ActionEvent  {
    
    public ActionCompleted {
        Objects.requireNonNull(actionId, "actionId");
        Objects.requireNonNull(result, "result");
    }
}
