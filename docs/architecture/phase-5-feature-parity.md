# Phase 5: Compose feature parity

Status: implemented

Date: 2026-07-17

## Outcome

Wateria's remaining user-facing legacy flows now have Compose replacements. The
single-activity revamp path covers reminders, settings, onboarding, plant-care tips,
the familiar middle tools menu, Google Lens, rating, about, and licenses while
preserving the approved green/orange identity.

The legacy activities and services remain in source only as a temporary rollback
boundary. They are not launcher or reminder entry points.

## Reminder delivery

The reminder pipeline is owned by WorkManager and Hilt:

1. `WorkManagerReminderScheduler` schedules unique one-time work for the next
   persisted reminder time.
2. `DailyReminderWorker` reloads current settings and due plants instead of using a
   serialized plant snapshot.
3. The notification publisher creates the watering channel and presents either one
   plant with a stable-ID water action or a count of multiple due plants.
4. Snooze uses a separate unique work stream and the persisted 1–23 hour duration.
5. Water and snooze actions enter through an internal, non-exported receiver.
6. A completed daily run appends its successor. User changes still replace pending
   work, so a running worker cannot cancel itself while scheduling tomorrow.

Disabling reminders, deleting all plants, and handling stale empty results clear any
displayed reminder. Notification permission is explained in onboarding and settings;
Android 13 or later requests the runtime permission only through those UI paths.

## Settings and first run

The settings screen observes DataStore-backed state and supports:

- enabling and disabling reminders;
- a 24-hour reminder time picker;
- snooze durations from 1 through 23 hours;
- notification permission recovery through the Android prompt or app settings;
- confirmed deletion of all plants; and
- navigation to about and license information.

Onboarding is versioned, blocks accidental dismissal, and records completion only
after the final page. Existing users whose legacy first-run flow was completed do not
see it again; fresh installs receive the three-page flow before the notification
permission request.

## Tips and external actions

The seven-tip rotation uses `LocalDate`, advances once per full calendar day, and
reports the actual time until the next local midnight. Tip wording was rewritten to
remove unsupported fertilizer, humidity, mental-health, and air-purification claims.

The bottom navigation preserves the recognizable settings / tools / add layout. The
tools sheet exposes Google Lens, rate-app, and tip actions. Lens checks Android package
visibility and offers a Play Store fallback when unavailable. Rating tries the Play
Store first and then a browser. Neither external integration enters the domain layer.

## Architecture boundaries

- Feature ViewModels depend on domain use cases and immutable state.
- `:domain` remains free of Android APIs.
- Notification construction and WorkManager adapters live in `:data`.
- Android permission, receiver, and external-intent entry points live in `:app`.
- WorkManager is initialized on demand with `HiltWorkerFactory`.
- Notification actions carry stable UUID strings, never parcelled plant snapshots.

## Verification

Phase 5 adds coverage for settings state and effects, onboarding completion, tip date
rollover and rotation, Lens/rating outcomes, notification worker behavior, and
successor-work policy. The repository passes Detekt, Spotless, all JVM tests, Android
lint, and debug assembly.

On an Android emulator, the verified flows include fresh onboarding, the Android 13+
notification prompt, settings, tips, the tools sheet, and the Lens unavailable
fallback. The daily WorkManager job was force-run twice: each Hilt worker execution
completed and advanced the namespaced JobScheduler entry from job 0 to 1 to 2 without
a worker stop, WorkManager error, or Android runtime crash.

## Phase 6 boundary

The next phase is cleanup and release hardening rather than another feature rewrite:

- remove retained legacy activities, services, layouts, and obsolete dependencies;
- remove ThreeTenABP after no legacy code requires it;
- audit analytics and crash-reporting privacy behavior;
- add instrumented Compose, accessibility, and notification-device coverage; and
- complete release signing, minification, upgrade, and store-readiness checks.
