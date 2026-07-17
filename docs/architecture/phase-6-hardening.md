# Phase 6: cleanup and release hardening

Status: implemented

Date: 2026-07-17

## Outcome

Wateria now ships only the Kotlin, single-activity Compose application. The dormant
Java activities, services, job schedulers, dialogs, adapters, XML layouts, custom
fonts, animation payload, and their obsolete runtime dependencies have been removed.
The executable legacy migration contract remains covered by test-only fixtures; the
production reader and repair policy remain in `:data`.

## Runtime and dependency cleanup

- The manifest exposes only the Compose launcher and the non-exported reminder action
  receiver.
- ThreeTenABP, Lottie, AppCompat, Material Views, ConstraintLayout, RecyclerView, and
  legacy support-v4 are no longer packaged.
- Java time support continues through core-library desugaring at API 21.
- Stable drawable IDs and all 44 historical plant icons remain unchanged so a v1
  preference snapshot can still be migrated.
- Release builds now run code shrinking and resource shrinking.

## Privacy and security posture

Wateria remains local-first and has no account or application backend. Plant names,
watering dates, reminder settings, and tip progress stay in Room and DataStore. The
Firebase Analytics SDK has been removed. Firebase Crashlytics is disabled in debug
builds and enabled in release builds; Wateria does not set a user ID, add custom
keys, log plant data, or manually attach exceptions.

Crashlytics mapping upload is disabled for routine builds and CI. A signed Phase 7
artifact can opt in with `-Pwateria.uploadCrashlyticsMapping=true`, keeping an
ordinary `assembleRelease` read-only with respect to Firebase.

Backup rules include only the Room database, Wateria DataStore file, and the legacy
SharedPreferences file needed for upgrade recovery. Cloud restore requires client-
side encryption capability on Android 12 or later. WorkManager and Firebase internal
state are excluded. Cleartext network traffic is disabled, package visibility is
limited to Google Lens, notification actions use immutable pending intents, and their
receiver is not exported.

Google Lens remains an explicit external-app handoff to preserve the existing user
experience. Wateria does not transmit a plant name, image, identifier, or local
database content to Lens or to the Play Store.

The historical privacy notice in `ExternalAssets` predates this implementation and
overstates data collection, including location. It must be replaced and legally
reviewed before a store release; the engineering inventory above is the source of
truth for that update.

## Accessibility, adaptive UI, and localization

- Onboarding scrolls and reduces decorative image height on short screens or large
  text, keeping the final action reachable at 200% font scale.
- Plant cards and tip actions stack vertically when text is enlarged.
- Every top-level back action exposes a localized semantic description while
  decorative images remain hidden from accessibility services.
- Reminder channel, notification, and action strings now have Spanish translations.
- English and Spanish plural resources cover every quantity required by Android lint.
- Dark and light Compose color schemes retain Wateria's green/orange identity.

Instrumented checks cover large-text onboarding reachability, back-button semantics,
the notification permission in the merged manifest, non-exported reminder actions,
backup enablement, and cleartext-network denial.

## Asset and dependency attribution

The in-app license list reflects the shipped AndroidX/Compose, Kotlin, Hilt, Firebase,
and illustrated assets. The repository's historical metadata attributes the plant
icons and illustrations to Freepik contributors through Flaticon/Freepik but does not
retain individual creator names or original asset URLs. Those records must be
recovered or the affected assets replaced before distribution terms can be declared
fully verified.

Dependabot continues to monitor Gradle and GitHub Actions dependencies weekly. Phase 6
does not perform a broad version upgrade because the API-21-compatible Firebase line
and the completed migration need to be validated independently from dependency churn.

## Verification and Phase 7 handoff

The local gate is formatting and Detekt, all JVM tests, Android lint, debug assembly,
and a minified, resource-shrunk release assembly. Device validation includes the
instrumented Phase 6 suite plus the existing onboarding, reminder permission,
settings, tips, Lens fallback, and repeated WorkManager delivery checks.

Phase 7 owns release-candidate work that requires release authority or external
records: production signing, Play Console declarations and screenshots, final privacy
notice review/publication, complete asset provenance, upgrade testing from the store
artifact, and staged rollout/monitoring.
