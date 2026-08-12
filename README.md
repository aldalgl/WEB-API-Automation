# API Automation Test - Siswa Management (api.rizqifauzan.com)

Project API automation test menggunakan **Java 21 + TestNG + REST Assured + Gradle (Groovy DSL) + Allure Report**.

---

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

## 🚀 STEP BY STEP 

### Step 1 — Install Prasyarat
1. **JDK 21** → download dari [Adoptium](https://adoptium.net/) (pilih Temurin 21 LTS). Setelah install, cek di terminal:
   ```
   java -version
   ```
   Harus muncul versi 21.
2. **IntelliJ IDEA** (Community edition juga cukup) → [download di sini](https://www.jetbrains.com/idea/download/).
3. Pastikan koneksi internet aktif (Gradle perlu download dependency dari Maven Central saat pertama kali build).

### Step 2 — Eksplorasi API terlebih dahulu
Sebelum menulis test, harus tahu persis bentuk request & response API-nya:
1. Buka `https://api.rizqifauzan.com/api-docs` di browser.
2. Klik tab **"Examples"** untuk setiap endpoint → lihat contoh request body & response body-nya.
3. Atau klik tab **"Postman"** → import collection-nya ke Postman, lalu coba manual: register, login, create siswa, dsb. Perhatikan:
   - Field apa saja yang wajib diisi saat register & create siswa
   - Struktur response sukses (apakah ada wrapper `data`, `success`, dll)
   - Struktur response error (field `message`/`errors`)

### Step 3 — Jalankan Test Pertama Kali
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

### Step 4 — Generate & Lihat Allure Report
Setelah `gradle test` selesai (baik pass maupun fail, report tetap bisa dilihat):
```
./gradlew allureReport
./gradlew allureServe
```
- `allureReport` → generate file report statis ke `build/allure-report`.
- `allureServe` → langsung buka report di browser

Report akan menampilkan: jumlah test pass/fail, durasi, detail request/response tiap step (karena kita pakai `AllureRestAssured` filter), severity, dan grouping berdasarkan `@Epic`/`@Feature`/`@Story` yang sudah ditulis di kode.

### Step 5 — Baca Hasilnya
- Kalau semua hijau → 6 test case berhasil sesuai ekspektasi.
<img width="1710" height="1029" alt="Screenshot 2026-08-12 at 15 31 29" src="https://github.com/user-attachments/assets/7a54bc26-4064-4ab4-bf7b-b3f7e47a37c8" />


---

## Author
Alda Giot Marito Lumban Gaol
