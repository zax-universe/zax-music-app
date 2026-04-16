# 🎵 Musify — Android Music Streaming App

Musify adalah aplikasi pemutar musik Android dengan fitur lengkap, menggunakan **6 API musik gratis** sebagai fallback, autentikasi lokal, playback ExoPlayer, download offline, dan arsitektur MVVM + Hilt.

---

## 📱 Fitur

| Fitur | Detail |
|---|---|
| **Auth Lokal** | Register / Login dengan SHA-256 hash, Room DB |
| **Streaming** | ExoPlayer dengan foreground service, notifikasi |
| **6 API Fallback** | Deezer → Jamendo → LastFM → FMA → SoundCloud |
| **Download Offline** | WorkManager + progress notification |
| **Library** | Buat & kelola playlist, history, downloads |
| **Search** | Debounce search, riwayat pencarian |
| **Player Lengkap** | Seekbar, shuffle, repeat, like, share |
| **Mini Player** | Sticky bottom player di semua tab |
| **Settings** | Streaming quality, cache, logout |

---

## 🏗️ Arsitektur

```
MVVM + Repository Pattern + Hilt DI
├── data/
│   ├── local/          (Room DB — entities, DAOs)
│   ├── remote/         (Retrofit APIs, DTOs)
│   ├── repository/     (MusicRepository, AuthRepository)
│   └── model/          (Track)
├── di/                 (AppModule — Hilt)
├── service/            (MusicService — ExoPlayer)
├── worker/             (DownloadWorker — WorkManager)
└── ui/
    ├── auth/           (Splash, Welcome, Login, Register)
    ├── main/           (MainActivity + BottomNav)
    ├── home/           (HomeFragment + adapters)
    ├── search/         (SearchFragment)
    ├── library/        (Library, Playlist, Detail)
    ├── player/         (PlayerActivity)
    ├── history/        (HistoryFragment)
    ├── downloads/      (DownloadsFragment)
    └── settings/       (SettingsFragment)
```

---

## 🚀 Setup di Android Studio

### Requirements
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 34
- Gradle 8.7

### Langkah-langkah

1. **Buka Android Studio** → `File > Open` → pilih folder `Musify`

2. **Sync Gradle** — klik "Sync Now" jika muncul prompt

3. **Build** — `Build > Make Project` (Ctrl+F9)

4. **Run** — pilih emulator/device → klik tombol ▶️

> ⚠️ Butuh koneksi internet saat pertama kali build untuk download dependencies.

---

## 📦 Dependencies Utama

```toml
ExoPlayer (Media3)   = "1.3.1"    # Audio playback
Room                 = "2.6.1"    # Local database
Hilt                 = "2.51"     # Dependency injection
Retrofit             = "2.11.0"   # HTTP client
Glide                = "4.16.0"   # Image loading
WorkManager          = "2.9.0"    # Background downloads
Navigation           = "2.7.7"    # Fragment navigation
Kotlin Coroutines    = "1.8.1"    # Async
```

---

## 🔑 API yang Digunakan

| API | Base URL | Auth |
|---|---|---|
| Deezer | `api.deezer.com` | Tidak perlu |
| Jamendo | `api.jamendo.com/v3.0` | Client ID gratis |
| LastFM | `ws.audioscrobbler.com/2.0` | API key gratis |
| Free Music Archive | `freemusicarchive.org/api` | Tidak perlu |
| SoundCloud | `api.soundcloud.com` | Client ID |

---

## 📁 Struktur File Penting

```
app/src/main/
├── AndroidManifest.xml
├── java/com/musify/
│   ├── MusifyApp.kt
│   ├── data/...
│   ├── di/AppModule.kt
│   ├── service/MusicService.kt
│   ├── worker/DownloadWorker.kt
│   └── ui/...
└── res/
    ├── layout/         (20+ XML layouts)
    ├── values/         (colors, strings, themes, dimens)
    ├── drawable/       (icons, shapes, gradients)
    ├── navigation/     (nav_graph, auth_nav_graph)
    └── xml/            (network_security_config, file_paths)
```

---

## 🎨 Design System

- **Primary Color**: `#1DB954` (Spotify-inspired green)
- **Background**: `#121212` (dark)
- **Surface**: `#282828`
- **Typography**: Roboto / System font
- **Icons**: Material Icons

---

## 📝 Catatan

- Preview URL dari Deezer hanya 30 detik (free tier)
- Jamendo menyediakan full track gratis (Creative Commons)
- Download memerlukan URL audio yang valid
- Semua data user tersimpan lokal (Room DB), tidak ada backend

---

Made with ❤️ — Musify v1.0.0
