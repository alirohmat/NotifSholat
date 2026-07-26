# Adzan App - TODO Analysis

## Prioritas Tinggi (Critical Fixes)

1. [x] **Fix LocationService memory leak**
   - Hapus location updates di `onDestroy()`
   - Stop service dengan benar setelah tidak dibutuhkan lagi

2. [x] **Fix MainActivity.onDestroy() crash**
   - Bungkus `unregisterReceiver()` dengan try-catch atau `LocalBroadcastManager`

3. [x] **Fix city name mismatch**
   - LocationService kirim `getLocality()` tapi PrayerTimeService cari kota pakai `lokasi` seperti "Bukittinggi, Sumatra Barat"
   - Harus match substring kolom `lokasi` bukan nama kota murni

4. [x] **Fix syntax error PrayerTimeService.java:105**
   - Lengkapi semicolon pada PendingIntent line atau rapikan

5. [x] **Foreground service untuk PrayerTimeService**
   - Android O+ butuh foreground service agar tidak dimatikan sistem

---

## Prioritas Menengah (Refactor)

6. **Database/ SharedPref**
   - Simpan cityId dan kota user biar tidak search tiap buka app

7. **Extract constants**
   - Pindah semua hardcoded string ke `strings.xml` / `constants`

8. **Error boundary**
   - Tambahkan try-catch di service dan broadcast
   - Handle null pointer geocoder

9. **Replace manual Thread dengan Coroutine / WorkManager**

10. **Unit test skeleton**
    - Setup JUnit 4 + Robolectric

---

## Catatan Bug Minor Ditunda

- [x] `MainActivity` masih pakai `getResources().getColor(...)`; idealnya pindah ke `ContextCompat.getColor(...)` untuk kompatibilitas API lebih rapi.
- [x] `LocationService` masih bergantung pada `Geocoder` dan fallback lokasi manual; edge case lokasi kosong belum dibungkus lebih ketat.
- `PrayerTimeService` masih pakai `Thread` mentah; bisa diganti `WorkManager` kalau butuh eksekusi lebih tahan proses mati.
- `ApiUtils.searchCityByName()` masih pakai substring match; hasil bisa false positive untuk nama kota mirip.

## Prioritas Rendah (Polish)

11. **UI state separation**
    - Gunakan ViewModel (sudah ada lifecycle deps)

12. **Clean Architecture**
    - Domain / Data / Presentation layer

13. **Logging framework**
    - Ganti `Log.e/d` dengan Timber / custom wrapper

14. **CI/CD**
    - GitHub Actions yang sudah ada → tambah lint + coverage

15. **ProGuard full rules**
    - Saat ini relax; matikan map dan debug info di release
