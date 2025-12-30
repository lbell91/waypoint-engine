package com.lbell91.core;

import com.lbell91.api.model.TransitionResult;

public class StateMachineEngine<S, E, C> {

    private TriFunction<S, E, C, TransitionResult<S>> resolver;


    public StateMachineEngine(
            TriFunction<S, E, C, TransitionResult<S>> resolver) {
        this.resolver = resolver;
    }

    TransitionResult<S> applyEvent(S state, E event, C context) {
        return resolver.apply(state, event, context);
    }

    @FunctionalInterface
    interface TriFunction<S, E, C, R> {
        R apply(S s, E e, C c);
    }
    
}
