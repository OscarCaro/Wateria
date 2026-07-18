# Phase 7A: legacy migration certification

Status: certified on debug-key upgrade paths; production-key confirmation deferred

Date: 2026-07-18

## Scope

Phase 7A certifies the local-data upgrade boundary independently from publication.
Production signing, Play Console configuration, store declarations, internal-track
distribution, and staged rollout remain deferred until the final release candidate.

The automated suite installs the v1.6 debug APK from `develop`, writes fixtures
through the legacy package identity into its real default `SharedPreferences`, and
replaces it with the revamped debug APK. Both APKs use `com.wateria.debug`, version
code 10, and the same local debug signing identity. The replacement uses
`adb install -r`; it does not uninstall or clear the application between the legacy
and revamped APKs.

## Certified scenarios

The suite runs each scenario from a clean debug-package installation:

1. A malformed whole plant payload enters recovery without creating an empty,
   overwriteable plant list.
2. Missing legacy preferences complete as a valid empty migration with defaults.
3. All 44 historical resource IDs migrate to distinct semantic icon keys without an
   unknown fallback.
4. A representative two-plant installation preserves Unicode names, watering dates,
   frequencies, reminder settings, onboarding state, and tip progress.

Every successful scenario executes migration twice and asserts that the second run is
a no-op. Every scenario asserts that the raw legacy preferences remain untouched.
The representative scenario launches the production Compose bootstrap before storage
verification, proving that normal application startup initiates migration. It checks
the migrated tip index and home screen before inspecting the resulting Room and
DataStore files. The malformed scenario verifies the blocking recovery screen after
the storage-level failure assertions.

The existing JVM fixture suite remains the exhaustive boundary suite for missing and
wrongly typed fields, invalid dates, unknown icons, duplicate plants, invalid settings,
partial records, and interruption after the Room write. Phase 7A complements those
tests with the Android package replacement and persistent-storage boundary.

## Running the suite

Start an emulator or connect an expendable test device, then run:

```shell
./scripts/validate-legacy-upgrade.sh
```

The script deletes only `com.wateria.debug` and `com.wateria.debug.test` between
scenarios. It never touches the production package `com.wateria`. Select a device with
`ANDROID_SERIAL`; select another Git baseline with `WATERIA_LEGACY_REF`; or validate a
specific legacy artifact with `WATERIA_LEGACY_APK`.

Example:

```shell
ANDROID_SERIAL=emulator-5554 WATERIA_LEGACY_APK=/path/to/legacy.apk \
    ./scripts/validate-legacy-upgrade.sh
```

## Evidence

On 2026-07-18 the complete suite passed against legacy commit `8981cb8` and the
revamped Phase 6.5 baseline `b797b18` on both available representative platform
families:

| Device | API | Storage scenarios | UI scenarios | Result |
|---|---:|---:|---:|---|
| Temporary phone AVD | 29 | 4 | 2 | Passed |
| `Phone` (`sdk_gphone64_arm64`) | 34 | 4 | 2 | Passed |

The temporary API 29 AVD was created from the already-installed local system image
and removed after the pass. The normal API 34 AVD was not modified beyond the debug
packages managed by the suite. The script leaves the representative upgraded debug
installation on the device after a successful run.

These debug-key passes prove migration behavior, package-data retention, production
bootstrap integration, and Android storage compatibility across the tested API
levels. The final release candidate must repeat the representative upgrade with the
published store artifact and matching production signing key before rollout.
