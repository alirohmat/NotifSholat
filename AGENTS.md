# Repo guide

## Local Setup
- Create `local.properties` file in the root directory.
- Add `sdk.dir=/path/to/your/android/sdk` to `local.properties`. Replace `/path/to/your/android/sdk` with your actual Android SDK path.

## Build
- Gradle wrapper: `./app/gradlew` (Gradle 8.5, JDK 17)
- Run from repo root:
  - `./app/gradlew assembleDebug`
  - `./app/gradlew assembleRelease`
- GitHub Actions runs same builds from `app/` working dir.

## Structure
- Single app module: `app/`
- Main code: `app/src/main/java/com/adzan/`
- Resources: `app/src/main/res/`
- App class: `com.adzan.AdzanApp`
- Services: `LocationService`, `PrayerTimeService`

## Non-obvious code state
- Codebase is Java, despite `build.gradle` including `kotlin-android`.
- `Gson` is a dependency, but the current code uses `org.json` for JSON parsing.

## API contract
- Base URL: `https://api.myquran.com/v3`
- City list: `GET /sholat/kota/semua`
- Prayer times: `GET /sholat/jadwal/{cityId}/{YYYY-M}`
- City IDs are opaque MD5 hashes; do not derive them.
- Month is dash-separated and 1-indexed.

## Runtime gotchas
- `LocationService` reverse-geocodes with `Geocoder`; null/IOException paths need handling.
- `PrayerTimeService` expects a `city_name` extra, then resolves the city ID via `ApiUtils.searchCityByName()`.
- Notification permission is required on Android 13+ before posting prayer notifications.

## Testing
- No unit tests currently present.
- `testImplementation` has JUnit 4.13.2; `androidTest` uses Espresso.
- No typecheck task exists; use the Gradle build as verification.

## Workflow
- Default kerja: asisten coding buat code, lalu push ke GitHub, lalu biar GitHub Actions build otomatis.
- Kalau user minta kerja code, anggap target akhir include commit/push dan verifikasi via GitHub Actions bila diminta.
