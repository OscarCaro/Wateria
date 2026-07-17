# Phase 3 domain and data implementation

Status: implemented behind the active v1.6 UI

## Boundaries

- `:domain` is pure Kotlin/JDK code. It owns plant and settings models, validation,
  derived watering status, repository ports, scheduling ports, time/ID providers,
  and business use cases.
- `:data` owns Room, Preferences DataStore, the v1.6 reader and migration, and
  WorkManager scheduling.
- `:app` still launches the legacy `MainActivity`. No migration or new persistence
  write is triggered by the released UI during this phase.

## Persistence

Room database `wateria.db`, schema version 1, stores:

- UUID plant ID;
- the legacy name verbatim until a later user edit;
- semantic icon key rather than a drawable integer;
- validated watering interval;
- next-watering epoch day;
- deterministic creation and update timestamps for migrated records.

Preferences DataStore `wateria.preferences_pb` stores reminder, onboarding, tip,
and migration state. The generated Room schema is committed under `data/schemas`.

## Migration guarantees

`LegacyMigrationRunner` implements the approved `not_started`, `in_progress`,
`complete`, and `failed` state machine. It:

1. reads default SharedPreferences without modifying them;
2. retains one persisted migration timestamp across retries;
3. parses each plant independently;
4. creates deterministic UUIDs from array position and raw legacy fields;
5. maps all 44 frozen drawable IDs to stable icon keys;
6. repairs invalid fields according to the approved contract;
7. upserts Room records transactionally;
8. writes converted settings to DataStore;
9. reads both stores back before marking migration complete;
10. records only non-sensitive counts.

An invalid whole payload enters the failed recovery state and does not clear Room
or alter the raw legacy preferences. A retry after an interrupted Room write uses
the same IDs, repaired dates, and timestamps, so it cannot duplicate records.

## Reminder boundary

The scheduler uses separate unique WorkManager streams for daily and snoozed work.
It calculates the next local-time occurrence with time-zone and daylight-saving
transitions respected. Phase 5 will connect worker execution to notification
delivery; the scheduler is currently inactive because no legacy screen calls it.

## Verification

The JVM suite covers:

- domain ordering, watering states, validation, and orchestration;
- the original Phase 1 icon and malformed-payload fixtures;
- Room repository mapping, sorting, and timestamps;
- DataStore settings conversion;
- successful, failed, repeated, and interrupted migrations;
- unique WorkManager scheduling and daylight-saving calculations.
