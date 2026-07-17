# Domain module

Pure Kotlin business models, repository contracts, validation, date rules, and use
cases belong here. This module must not depend on Android, Compose, Room, DataStore,
Firebase, `:app`, or `:data`.

The Phase 3 implementation includes stable plant/icon/interval value types,
derived watering status, repository and scheduling ports, injected time and ID
providers, validation, and business-action use cases. Android-specific concerns
remain outside this module.
