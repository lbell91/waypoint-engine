# waypoint-api

`waypoint-api` defines the **domain contracts** for the Waypoint Engine.

This module contains only *pure abstractions and value types* that describe
a deterministic finite state machine (FSM). It has **no engine logic**, **no persistence**, and **no side effects**.

All other modules (`waypoint-core`, `waypoint-persist`, etc.) depend on this module.

---

## Purpose

The API module exists to:
- Define what a workflow *is*
- Define how transitions are identified
- Define the result of applying an event
- Provide stable contracts that can be validated, executed, and persisted

It is intentionally minimal and framework-agnostic.

---

## Core Concepts

### WorkflowDefinition

A `WorkflowDefinition<S,E,C>` describes a finite state machine:

- An identifier (`id`)
- An initial state
- A set of terminating states
- A deterministic transition table keyed by `(state, event)`