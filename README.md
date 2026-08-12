# API Automation Test - Siswa Management (api.rizqifauzan.com)

Project API automation test menggunakan **Java 21 + TestNG + REST Assured + Gradle (Groovy DSL) + Allure Report**.

---

## ⚠️ PENTING - BACA DULU SEBELUM MULAI

Saya tidak bisa mengakses halaman dokumentasi interaktif (`/api-docs`) secara penuh karena halaman itu di-render dengan JavaScript (React), sehingga detail schema request/response persis (nama field, struktur JSON) tidak bisa saya ambil otomatis. Saya sudah cek dari halaman utama & docs bahwa endpoint yang tersedia adalah:

**Authentication**
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout` (Auth required)
- `GET /api/auth/me` (Auth required)

**Siswa Management**
- `GET /api/siswa` (Auth required)
- `GET /api/siswa/{id}` (Auth required)
- `POST /api/siswa` (Auth required)
- `PUT /api/siswa/{id}` (Auth required)
- `PATCH /api/siswa/{id}` (Auth required)
- `DELETE /api/siswa/{id}` (Auth required)

Karena field JSON pasti (misalnya apakah response dibungkus `data.token` atau langsung `token`, apakah field siswa namanya `nama`/`nama_siswa`, dll) tidak bisa saya pastikan, kode di project ini saya buat dengan **asumsi struktur response yang umum** (`{ "data": {...} }` dan error `{ "message": "..." }`). **Langkah pertama yang WAJIB kamu lakukan** adalah memverifikasi struktur asli lewat Postman/browser (ada tombol "Postman" di halaman `/api-docs`), lalu sesuaikan field di:
- `src/test/resources/testdata/*.json`
- Baris `response.jsonPath().get("...")` di `AuthTests.java` dan `SiswaTests.java` (saya sudah kasih komentar `// NOTE` di bagian yang paling mungkin perlu disesuaikan)

Ini bukan halangan besar — justru ini adalah langkah #1 yang **selalu** dilakukan QA engineer sebelum menulis automation test (disebut *API exploration/discovery*). Panduan detailnya ada di Step 2 di bawah.

---

## 📁 Struktur Project

```
api-automation-siswa/
├── build.gradle                     # Konfigurasi dependency & build (Groovy DSL)
├── settings.gradle
├── src/test/java/com/rizqifauzan/api/
│   ├── base/
│   │   └── BaseTest.java            # Setup environment testing (base URL, request spec)
│   ├── utils/
│   │   ├── ConfigReader.java        # Baca config.properties
│   │   ├── TestDataReader.java      # Baca test data dari file JSON
│   │   └── RandomDataUtils.java     # Generate email/NIS unik biar test bisa diulang
│   ├── tests/
│   │   ├── AuthTests.java           # 2 Positive Test (Register, Login)
│   │   └── SiswaTests.java          # 3 Positive Test + 1 Negative Test (CRUD Siswa)
│   └── runner/
│       └── TestRunner.java          # Class untuk menjalankan semua test secara programmatic
└── src/test/resources/
    ├── config.properties            # base.url API
    ├── testng.xml                   # Suite definition (test runner utama)
    └── testdata/
        ├── register_payload.json
        ├── siswa_payload.json
        └── siswa_invalid_payload.json
```

### Ringkasan 6 test case
| # | Test | Endpoint | Tipe | Validasi |
|---|------|----------|------|----------|
| 1 | Register user valid | `POST /api/auth/register` | Positive | Status 201 + email di response sesuai |
| 2 | Login kredensial valid | `POST /api/auth/login` | Positive | Status 200 + token tidak kosong |
| 3 | Create siswa valid | `POST /api/siswa` | Positive | Status 201 + nama di response sesuai payload |
| 4 | Get siswa by ID | `GET /api/siswa/{id}` | Positive | Status 200 + ID di response sesuai |
| 5 | Get all siswa | `GET /api/siswa` | Positive | Status 200 + `data` berupa list |
| 6 | Create siswa invalid (field `nama` kosong, `kelas` salah tipe) | `POST /api/siswa` | **Negative** | Status 400 + ada `message` error |

---

## 🚀 STEP BY STEP - Dari Nol

### Step 1 — Install Prasyarat
1. **JDK 21** → download dari [Adoptium](https://adoptium.net/) (pilih Temurin 21 LTS). Setelah install, cek di terminal:
   ```
   java -version
   ```
   Harus muncul versi 21.
2. **IntelliJ IDEA** (Community edition juga cukup) → [download di sini](https://www.jetbrains.com/idea/download/).
3. Pastikan koneksi internet aktif (Gradle perlu download dependency dari Maven Central saat pertama kali build).

### Step 2 — Eksplorasi API terlebih dahulu (WAJIB, jangan dilewati)
Sebelum menulis test, kamu harus tahu persis bentuk request & response API-nya:
1. Buka `https://api.rizqifauzan.com/api-docs` di browser.
2. Klik tab **"Examples"** untuk setiap endpoint → lihat contoh request body & response body-nya.
3. Atau klik tab **"Postman"** → import collection-nya ke Postman, lalu coba manual: register, login, create siswa, dsb. Perhatikan:
   - Field apa saja yang wajib diisi saat register & create siswa
   - Struktur response sukses (apakah ada wrapper `data`, `success`, dll)
   - Struktur response error (field `message`/`errors`)
4. Catat hasil temuanmu, lalu sesuaikan file-file berikut sesuai temuanmu:
   - `src/test/resources/testdata/register_payload.json`
   - `src/test/resources/testdata/siswa_payload.json`
   - `src/test/resources/testdata/siswa_invalid_payload.json`
   - Assertion `jsonPath().get(...)` di `AuthTests.java` & `SiswaTests.java`

### Step 3 — Buka Project di IntelliJ
1. Extract project (jika dalam bentuk zip) ke folder pilihanmu.
2. Buka IntelliJ → **File > Open** → pilih folder `api-automation-siswa` (folder yang berisi `build.gradle`).
3. IntelliJ akan otomatis mendeteksi ini sebagai Gradle project dan mulai **sync** (proses download dependency). Tunggu sampai selesai (lihat progress bar di kanan bawah / tab "Build").
4. Kalau IntelliJ menanyakan Gradle JVM, pilih JDK 21 yang sudah kamu install.

### Step 4 — Pastikan Project Structure sudah benar
1. **File > Project Structure > Project** → pastikan **SDK** = 21 dan **Language level** = 21.
2. Kalau folder `src/test/java` belum ditandai sebagai *Test Sources Root* (biasanya berwarna hijau), klik kanan folder tersebut → **Mark Directory as > Test Sources Root**.

### Step 5 — Jalankan Test Pertama Kali
Ada 2 cara:

**Cara A — Lewat Gradle (direkomendasikan, karena Allure report otomatis ter-generate dengan benar)**
1. Buka tab **Gradle** di sisi kanan IntelliJ.
2. Expand `api-automation-siswa > Tasks > verification` → double click **`test`**.
3. Atau lewat terminal di root project:
   ```
   ./gradlew test
   ```
   (Kalau belum ada `gradlew`, jalankan `gradle wrapper` dulu satu kali menggunakan Gradle yang ter-install di komputer, atau generate wrapper dari IntelliJ: klik kanan project > Add Framework Support, atau cukup jalankan `gradle test` jika Gradle sudah terinstall global.)

**Cara B — Lewat testng.xml langsung**
- Klik kanan file `src/test/resources/testng.xml` → **Run**.
- (Cara ini menjalankan test tapi tidak selalu attach Allure agent dengan sempurna, jadi untuk report tetap disarankan pakai `gradle test`.)

### Step 6 — Generate & Lihat Allure Report
Setelah `gradle test` selesai (baik pass maupun fail, report tetap bisa dilihat):
```
./gradlew allureReport
./gradlew allureServe
```
- `allureReport` → generate file report statis ke `build/allure-report`.
- `allureServe` → langsung buka report di browser (paling gampang untuk pertama kali).

Report akan menampilkan: jumlah test pass/fail, durasi, detail request/response tiap step (karena kita pakai `AllureRestAssured` filter), severity, dan grouping berdasarkan `@Epic`/`@Feature`/`@Story` yang sudah ditulis di kode.

### Step 7 — Baca Hasilnya
- Kalau semua hijau → 6 test case berhasil sesuai ekspektasi.
- Kalau ada yang merah, klik test tersebut di Allure report → lihat detail request/response di tab "Test body" untuk debug. Kemungkinan besar penyebabnya adalah struktur JSON response API yang berbeda dari asumsi saya (lihat bagian ⚠️ PENTING di atas) — cukup sesuaikan path `jsonPath()`-nya.

---

## 🔧 Penjelasan Utility Class

- **`ConfigReader`** → membaca `config.properties` (saat ini hanya `base.url`). Kalau nanti kamu punya banyak environment (dev/staging/prod), tinggal tambah properties baru di sini.
- **`TestDataReader`** → membaca file JSON di `testdata/` menjadi `Map<String, Object>`, supaya payload test tidak "hardcode" di dalam kode Java (lebih rapi & reusable).
- **`RandomDataUtils`** → generate email & NIS unik berbasis timestamp/UUID, supaya test bisa dijalankan berkali-kali tanpa bentrok data (menghindari error "email sudah terdaftar").
- **`BaseTest`** → parent class semua test, tempat setup `base URL` dan `RequestSpecification` (dengan/tanpa token auth) supaya tidak diulang-ulang di tiap test class.

## 🏃 Penjelasan Runner
- **`testng.xml`** → mendefinisikan class mana saja yang dieksekusi dalam 1 suite (ini yang dipanggil oleh task `gradle test`).
- **`TestRunner.java`** → cara alternatif menjalankan suite yang sama secara programmatic (klik kanan → Run pada method `main`), berguna kalau kamu ingin trigger test dari luar Gradle (misalnya dari script lain).

---

## 🛠️ Troubleshooting Umum
| Masalah | Solusi |
|---|---|
| `Status code harus 201... tapi dapat 404` | Cek lagi endpoint path di dokumentasi, mungkin ada prefix berbeda |
| `Status code harus 201... tapi dapat 409/422` | Kemungkinan email dianggap duplikat atau validasi field berbeda dari asumsi — cek Postman collection |
| `NullPointerException` di `jsonPath().getString(...)` | Struktur response beda dari asumsi (`data.token` dsb) — print `response.asPrettyString()` dulu untuk lihat struktur aslinya |
| Gradle sync gagal / dependency tidak ketemu | Pastikan koneksi internet aktif dan `mavenCentral()` bisa diakses (cek firewall/proxy kampus jika ada) |
| Allure report kosong | Pastikan menjalankan test lewat `gradle test` (bukan langsung run class Java biasa), lalu jalankan `gradle allureReport` |
| `Could not create task ':allureReport' ... DefaultDecoratedConvention` | Ini terjadi kalau versi plugin `io.qameta.allure` yang dipakai adalah versi lama (2.x) sementara Gradle yang jalan sudah versi baru (8.11+/9.x) yang menghapus `Convention` API lama. **Solusi**: pastikan `build.gradle` memakai `id 'io.qameta.allure' version '4.1.0'` (bukan 2.x) seperti di project ini. Kalau masih error, cek versi Gradle yang dipakai IntelliJ: **File > Settings > Build Tools > Gradle**, pastikan Gradle version ≥ 8.11 (kalau kurang, ganti ke "Use Gradle from: 'wrapper'" atau versi terbaru di distribusi lokal). |

---

Selamat mencoba! Kalau setelah cek Postman collection ada perbedaan struktur yang signifikan (misalnya field siswa ternyata bukan `nama`/`nis`/`kelas`), kirim contoh response JSON-nya dan saya bantu sesuaikan assertion-nya.
