package com.lbell91.api.model;

import java.util.Objects;

public record StateEventKey <S, E>(S state, E event) {
    public StateEventKey {
        Objects.requireNonNull(state);
        Objects.requireNonNull(event);
    }
}