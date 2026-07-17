# Phase 4: Migration-gated Compose core loop

Status: implemented

Date: 2026-07-17

## Outcome

Wateria now starts in a Hilt-enabled Compose activity and completes its core plant
care loop through the clean architecture boundaries established in Phases 2 and 3.
The legacy activities remain compiled but no longer own the launcher intent.

The active path supports:

- migration before any new repository state is shown;
- an empty state and reactively sorted plant list;
- a shared add/edit editor addressed by stable plant ID;
- all 44 legacy plant icons through semantic domain keys;
- independent watering interval and next-watering controls;
- watering from the list;
- confirmed deletion from the editor; and
- English and Spanish presentation resources.

## Startup safety

`MainActivity` renders `WateriaBootstrapRoute` before navigation is created. The
bootstrap ViewModel invokes the `LegacyMigration` interface and admits only
`Completed` or `AlreadyComplete` results to the application.

An invalid or unverifiable legacy payload produces a blocking recovery screen with
a retry action. The plant list is not exposed in that state, so an apparent empty
installation cannot overwrite data that still exists in legacy preferences. The
underlying migration remains idempotent and continues to leave legacy preferences
untouched.

## Presentation boundaries

The Compose screens depend on domain use cases, not Room, DataStore, or legacy
classes. ViewModels expose immutable `StateFlow` state and buffered one-off effects.
Navigation owns the `NavController`; screens receive callbacks carrying `PlantId`.

The list recalculates `Upcoming`, `DueToday`, and `Overdue` presentation from the
injected date whenever the screen resumes. Repository ordering remains next watering
date, display name, then stable ID. Watering updates Room and the reactive flow moves
the card without adapter-position bookkeeping.

The editor keeps draft fields in `SavedStateHandle`. It trims names on save, rejects
blank names and names over 50 characters, and permits duplicate display names because
identity is the UUID. Watering intervals remain 1 through 40 days and the upcoming
watering offset remains 0 through 40 days for legacy parity.

## Brand and accessibility baseline

The screens use the approved green/orange Material 3 color scheme while retaining
the original illustration and full icon set. Buttons and icon choices expose semantic
labels, plant rows use stable keys, destructive actions require confirmation, and
dark color tokens remain available through the shared theme.

## Verification

Phase 4 adds JVM coverage for:

- bootstrap completion, failure, and retry;
- list sorting, derived watering states, watering, and stable-ID deletion;
- add, edit, validation, draft restoration, and delete editor flows;
- exact one-to-one coverage of the 44 domain icon keys; and
- the approved 50-character plant-name boundary.

The repository gates remain `quality` followed separately by `test lintDebug
assembleDebug` to avoid Android resource generation racing formatting checks.

## Deliberate Phase 5 boundary

Settings, notification delivery/actions, onboarding, tips, the middle action sheet,
Google Lens, rate-app behavior, about, and licenses have not yet moved to Compose.
The settings destination is visibly marked as pending rather than imitating a
completed feature. Legacy code and dependencies are retained until these paths reach
parity and their replacements pass tests.

The WorkManager scheduler foundation exists, but its workers intentionally remain
delivery placeholders until Phase 5 connects notification channels, actions, snooze,
permission handling, and rescheduling. This branch is therefore a development
milestone, not a parity release candidate.
