
# API Muslim Documentation

This documentation provides a comprehensive overview of the API Muslim, which serves various needs for Muslims in Indonesia. The API provides data for prayer schedules, Qibla direction, Hijri calendar conversion, and other useful tools.

## 📬 Contact

For suggestions, ideas, discussions, and communication, you can reach out through:

-   **Telegram:** [@ApiMuslim](https://t.me/apimuslim)
-   **Email:** banghasan@gmail.com

## Tags

### Sholat

The Salat endpoint covers important details such as the scope of available cities, accurate daily prayer schedules, and other relevant technical information. Please use this data as needed.

> Data source from the [Kemenag Bimas Islam website](https://bimasislam.kemenag.go.id/web/jadwalshalat). The city and regency columns have been adjusted according to the source.

### Hadis

An encyclopedia of Hadith with navigation by ID, random access, and exploration with pagination.

### Kalender

This endpoint is for converting between the Common Era (CE) and Hijri calendars.

**CE** stands for *Common Era*, another name for the Gregorian calendar. This term is a modern and religiously neutral alternative to **Anno Domini** (AD), used mainly in academic and scientific contexts.

Another name is **Syamsiah**, or *Solar Year*. This name refers to its calculation based on the sun, as opposed to the Hijri Calendar, which is based on the movement of the moon (Komariah).

Selectable methods include:

-   `standar` (default)
-   `islamic-umalqura`
-   `islamic-civil`
-   `islamic-tabular`

The default calculation is based on the **Global Moon Sighting** method.

### Asmaul Husna

The Asmaul Husna endpoint provides access to the 99 names of Allah, complete with their meanings and brief explanations.

### Doa

The Doa endpoint provides a collection of selected prayers sourced from the Quran and Hadith.

### Tahlil

The Tahlil endpoint provides a guide and text for the Tahlil ceremony.

### Wirid

The Wirid endpoint provides a collection of wirid that can be recited after prayers.

### Ayat Kursi

The Ayat Kursi endpoint provides the text and translation of Ayat Kursi.

### Juz Amma

The Juz Amma endpoint provides a collection of short surahs from the 30th Juz of the Quran.

### Miscellaneous

This endpoint is for various needs such as:

-   **Niat Sholat:** The intention for prayers.
-   **Bacaan Sholat:** The readings for prayers.
-   **Surah:** A list of surahs in the Quran.
-   **Ayat:** The verses of the Quran.

## Paths

### GET /v3/sholat/kota/semua

**Description:** Get a list of all cities.

**Response:**

-   `200`: A list of all cities.
    -   `application/json`:
        -   `id`: City ID
        -   `nama`: City name

### GET /v3/sholat/kota/cari/{nama}

**Description:** Search for a city by name.

**Parameters:**

-   `nama`: City name

**Response:**

-   `200`: A list of cities that match the search.
    -   `application/json`:
        -   `id`: City ID
        -   `nama`: City name

### GET /v3/sholat/jadwal/{id}/{tahun}/{bulan}/{tanggal}

**Description:** Get the prayer schedule for a specific city and date.

**Parameters:**

-   `id`: City ID
-   `tahun`: Year
-   `bulan`: Month
-   `tanggal`: Date

**Response:**

-   `200`: Prayer schedule for the specified city and date.
    -   `application/json`:
        -   `subuh`: Fajr prayer time
        -   `dhuha`: Dhuha prayer time
        -   `dzuhur`: Dhuhr prayer time
        -   `ashar`: Asr prayer time
        -   `maghrib`: Maghrib prayer time
        -   `isya`: Isha prayer time

### GET /v3/kalender/{tahun}/{bulan}/{tanggal}

**Description:** Convert a Gregorian date to a Hijri date.

**Parameters:**

-   `tahun`: Year
-   `bulan`: Month
-   `tanggal`: Date

**Response:**

-   `200`: Hijri date.
    -   `application/json`:
        -   `tahun`: Hijri year
        -   `bulan`: Hijri month
        -   `tanggal`: Hijri date

### GET /v3/hadis/{id}

**Description:** Get a hadith by ID.

**Parameters:**

-   `id`: Hadith ID

**Response:**

-   `200`: Hadith.
    -   `application/json`:
        -   `id`: Hadith ID
        -   `nama`: Hadith name
        -   `arab`: Hadith in Arabic
        -   `terjemah`: Hadith translation

### GET /v3/hadis/acak

**Description:** Get a random hadith.

**Response:**

-   `200`: Random hadith.
    -   `application/json`:
        -   `id`: Hadith ID
        -   `nama`: Hadith name
        -   `arab`: Hadith in Arabic
        -   `terjemah`: Hadith translation

### GET /v3/asmaul-husna

**Description:** Get the Asmaul Husna.

**Response:**

-   `200`: Asmaul Husna.
    -   `application/json`:
        -   `nama`: Name of Allah
        -   `terjemah`: Translation of the name
        -   `penjelasan`: Explanation of the name

### GET /v3/doa

**Description:** Get a list of prayers.

**Response:**

-   `200`: A list of prayers.
    -   `application/json`:
        -   `nama`: Prayer name
        -   `arab`: Prayer in Arabic
        -   `terjemah`: Prayer translation

### GET /v3/tahlil

**Description:** Get the Tahlil guide.

**Response:**

-   `200`: Tahlil guide.
    -   `application/json`:
        -   `nama`: Tahlil section name
        -   `arab`: Tahlil in Arabic
        -   `terjemah`: Tahlil translation

### GET /v3/wirid

**Description:** Get a collection of wirid.

**Response:**

-   `200`: A collection of wirid.
    -   `application/json`:
        -   `nama`: Wirid name
        -   `arab`: Wirid in Arabic
        -   `terjemah`: Wirid translation

### GET /v3/ayat-kursi

**Description:** Get the Ayat Kursi.

**Response:**

-   `200`: Ayat Kursi.
    -   `application/json`:
        -   `nama`: Ayat Kursi
        -   `arab`: Ayat Kursi in Arabic
        -   `terjemah`: Ayat Kursi translation

### GET /v3/juz-amma

**Description:** Get the Juz Amma.

**Response:**

-   `200`: Juz Amma.
    -   `application/json`:
        -   `nama`: Surah name
        -   `arab`: Surah in Arabic
        -   `terjemah`: Surah translation

### GET /v3/niat-sholat

**Description:** Get the intention for prayers.

**Response:**

-   `200`: The intention for prayers.
    -   `application/json`:
        -   `nama`: Prayer name
        -   `arab`: Intention in Arabic
        -   `terjemah`: Intention translation

### GET /v3/bacaan-sholat

**Description:** Get the readings for prayers.

**Response:**

-   `200`: The readings for prayers.
    -   `application/json`:
        -   `nama`: Reading name
        -   `arab`: Reading in Arabic
        -   `terjemah`: Reading translation

### GET /v3/surah

**Description:** Get a list of surahs in the Quran.

**Response:**

-   `200`: A list of surahs.
    -   `application/json`:
        -   `id`: Surah ID
        -   `nama`: Surah name
        -   `arab`: Surah in Arabic
        -   `terjemah`: Surah translation

### GET /v3/ayat

**Description:** Get the verses of the Quran.

**Response:**

-   `200`: The verses of the Quran.
    -   `application/json`:
        -   `id`: Verse ID
        -   `surah`: Surah name
        -   `ayat`: Verse number
        -   `arab`: Verse in Arabic
        -   `terjemah`: Verse translation
