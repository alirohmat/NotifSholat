# Repo guide

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
- `LocationService.java` is now cleaned, but `MainActivity.java` remains duplicated/broken. Clean it before touching behavior.
- Codebase is Java now, despite README TODOs about Kotlin migration.
- Gson is declared but current code uses `org.json`.

## API contract
- Base URL: `https://api.myquran.com/v3`
- City list: `GET /sholat/kota/semua`
- Prayer times: `GET /sholat/jadwal/{cityId}/{YYYY-M}`
- City IDs are opaque MD5 hashes; do not derive them.
- Month is dash-separated and 1-indexed.

## Runtime gotchas
- `LocationService` reverse-geocodes with `Geocoder`; null/IOException paths need handling.
- `PrayerTimeService` expects `city_name` extra, then resolves city ID via `ApiUtils.searchCityByName()`.
- Notification permission is required on Android 13+ before posting prayer notifications.

## Testing
- No unit tests currently present.
- `testImplementation` has JUnit 4.13.2; `androidTest` uses Espresso.
- No typecheck task exists; use Gradle build as verification.
