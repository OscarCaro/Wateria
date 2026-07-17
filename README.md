# Wateria - Android App
_"Receive watering reminders for your plants and become a plant guru"_

![](https://github.com/OscarCaro/Wateria/blob/master/ExternalAssets/PlayStorePictures/Gr%C3%A1fico%20de%20Funciones.png)

Wateria is a free plant care assistant app that helps you and your plants thrive.

## Revamp status

The application is being modernized incrementally while the released v1.6 user
experience remains active. The current foundation uses Kotlin, Jetpack Compose,
Hilt, and a small Clean Architecture module split:

- `:app` owns Android entry points, the legacy UI, and the inactive Compose shell.
- `:domain` is a pure Kotlin boundary for business models, contracts, and use cases.
- `:data` will implement those contracts with Room, DataStore, and WorkManager.

Architecture decisions and the migration sequence are documented in
[`docs/architecture/phase-0-blueprint.md`](docs/architecture/phase-0-blueprint.md).
The compatibility rules protected during the migration are documented in
[`docs/architecture/legacy-v1-migration-contract.md`](docs/architecture/legacy-v1-migration-contract.md).

## Development

Use JDK 17 and the checked-in Gradle wrapper. The CI-equivalent local gate is:

```shell
./gradlew quality test lintDebug assembleDebug
```

## Download
Download it now at [Google Play Store](https://play.google.com/store/apps/details?id=com.wateria)

## License
This project is published under the **_No Licese_** GitHub License

This means, by default, that nobody else can copy, distribute, or modify this code.

However, as this project is published on GitHub (therefore under their [Terms of service](https://docs.github.com/en/github/site-policy/github-terms-of-service)), it is allowed to view and fork the repository.

For extended information, please refer to [choosealicense.com](https://choosealicense.com/no-permission/)
