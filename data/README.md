# Data module

Android-backed implementations of the repository contracts in `:domain` belong
here. Room, DataStore, WorkManager, migration adapters, and Hilt bindings may live
in this module; UI and feature presentation code may not.

Data implementation begins in Phase 3. Phase 2 establishes the module boundary,
tooling, dependencies, and Room schema export policy.
