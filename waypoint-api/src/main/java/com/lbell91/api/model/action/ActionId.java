package com.lbell91.api.model.action;

import java.util.Objects;

import com.lbell91.api.exceptions.ApiIllegalArgumentException;

public record ActionId(String value) {

    public ActionId {
        Objects.requireNonNull(value, "value");

        if (value.isBlank()) {
            throw ApiIllegalArgumentException.actionIdRequired();
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
