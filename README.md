# Wateria - Android App
_"Receive watering reminders for your plants and become a plant guru"_

![](https://github.com/OscarCaro/Wateria/blob/master/ExternalAssets/PlayStorePictures/Gr%C3%A1fico%20de%20Funciones.png)

Wateria is a free plant care assistant app that helps you and your plants thrive.

## Revamp status

The application is being modernized incrementally. The Compose application is now
the launcher on the revamp branch, backed by Kotlin, Hilt, Room, DataStore,
WorkManager, and a small Clean Architecture module split:

- `:app` owns the Compose entry point, presentation state, navigation, and the
  temporarily retained legacy UI.
- `:domain` is a pure Kotlin boundary for business models, contracts, and use cases.
- `:data` implements those contracts with Room, DataStore, legacy migration, and WorkManager.

Phase 5 completes the Compose parity surface: migration-gated plant management,
WorkManager reminders and actions, reactive settings, first-run onboarding, daily
tips, Google Lens, rating, about, and licenses. The related legacy source remains
temporarily available as a rollback boundary; Phase 6 removes it and focuses on
release hardening.

Architecture decisions and the migration sequence are documented in
[`docs/architecture/phase-0-blueprint.md`](docs/architecture/phase-0-blueprint.md).
The compatibility rules protected during the migration are documented in
[`docs/architecture/legacy-v1-migration-contract.md`](docs/architecture/legacy-v1-migration-contract.md).
The implemented persistence and migration design is recorded in
[`docs/architecture/phase-3-domain-data.md`](docs/architecture/phase-3-domain-data.md).
The active core-loop design and remaining parity boundary are recorded in
[`docs/architecture/phase-4-core-loop.md`](docs/architecture/phase-4-core-loop.md).
The completed parity surface and reminder pipeline are recorded in
[`docs/architecture/phase-5-feature-parity.md`](docs/architecture/phase-5-feature-parity.md).

## Development

Use JDK 17 and the checked-in Gradle wrapper. The CI-equivalent local gate is:

```shell
./gradlew spotlessApply quality
./gradlew test lintDebug assembleDebug
```

## Download
Download it now at [Google Play Store](https://play.google.com/store/apps/details?id=com.wateria)

## License
This project is published under the **_No Licese_** GitHub License

This means, by default, that nobody else can copy, distribute, or modify this code.

However, as this project is published on GitHub (therefore under their [Terms of service](https://docs.github.com/en/github/site-policy/github-terms-of-service)), it is allowed to view and fork the repository.

For extended information, please refer to [choosealicense.com](https://choosealicense.com/no-permission/)
