package com.lbell91.api.model.action;

import java.util.Objects;

public record ActionId(String value) {

    public ActionId {
        Objects.requireNonNull(value, "value");

        if (value.isBlank()) {
            throw new IllegalArgumentException("ActionId must not be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
