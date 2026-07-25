# Adzan - Aplikasi Notifikasi Waktu Sholat Android

Aplikasi Android untuk notifikasi waktu sholat menggunakan data dari API myquran.com.

## To-Do List

### Prioritas Tinggi
- [x] Panggil `startLocationService()` di `MainActivity` — service lokasi belum dijalankan
- [x] Tangani izin `POST_NOTIFICATIONS` (Android 13+) di `PrayerTimeReceiver` — ada TODO di kode
- [x] Perbaiki `findNearestCity` di `ApiUtils` — saat ini hanya balik kota pertama dari list API
- [x] Tambah penanganan error saat GPS/network tidak tersedia di `LocationService`

### Penyempurnaan
- [ ] Optimasi interval location update (saat ini 10 detik — boros baterai)
- [ ] Jadikan notifikasi lebih informatif (waktu, lokasi, nama kota)
- [ ] Validasi response API — handle jika kota tidak ditemukan atau data kosong
- [ ] Tambahkan `WorkManager` sebagai fallback untuk background task

### Fitur Tambahan
- [ ] Settings screen (pilih kota manual, enable/disable notifikasi per waktu sholat)
- [ ] Hitung waktu sholat offline (tanpa API) menggunakan algoritma hisab
- [ ] Widget homescreen untuk jadwal hari ini
- [ ] Qibla direction compass
- [ ] Dukungan multi-bahasa (Indonesia, Inggris, Arab)
- [ ] Tema gelap / terang

### Teknis & Deployment
- [ ] Migrasi dari Java ke Kotlin (file saat ini masih `.java`)
- [ ] Tambah unit test untuk logika perhitungan waktu
- [ ] Konfigurasi CI/CD untuk otomatis rilis ke Play Store
- [ ] ProGuard rules untuk minify release APK
