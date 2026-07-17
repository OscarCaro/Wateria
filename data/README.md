# Data module

Android-backed implementations of the repository contracts in `:domain` belong
here. Room, DataStore, WorkManager, migration adapters, and Hilt bindings may live
in this module; UI and feature presentation code may not.

The Phase 3 implementation provides the Room plant repository, DataStore settings,
the idempotent v1.6 migration, and unique WorkManager scheduling adapters. These
components are deliberately not connected to the active legacy UI; activation
occurs through the Compose bootstrap flow in a later phase.
