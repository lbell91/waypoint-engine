# Waypoint Engine

Waypoint is an embeddable workflow engine for executing long-running business processes as explicit state machines.  
It focuses on durability, observability, and build-time safety rather than distributed orchestration.

## Requirements
- Java 21
- Gradle Wrapper (included)

## Project Structure
- `waypoint-api` — Public workflow contracts (states, events, actions)
- `waypoint-core` — State machine engine and execution logic
- `waypoint-persist` — Persistence abstractions and implementations
- `build-logic` — Gradle convention plugins and build standards

## Build
Run the full build (used by CI):

```
./gradlew ci
```

Clean all build outputs:

```
./gradlew clean
```

## Tests
Run tests for a single module:

```
./gradlew :waypoint-core:test
```