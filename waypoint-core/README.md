
---

# waypoint-core

`waypoint-core` contains the **pure FSM execution and validation logic** for the Waypoint Engine.

It executes workflows defined by `waypoint-api` and enforces core invariants such as determinism and terminal state semantics.

---

## Purpose

This module is responsible for:
- Applying events to workflows deterministically
- Validating workflow definitions
- Enforcing execution rules (e.g. terminating states)
- Remaining completely side-effect free

It is the heart of the engine.

---

## StateMachineEngine

`StateMachineEngine` applies an event to a workflow definition:

```
applyResult(workflowDefinition, currentState, event, context)
```