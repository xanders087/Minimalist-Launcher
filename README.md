# Minimalist Launcher 📱✨

A modern, distraction-free Android launcher built natively with **Jetpack Compose** and **Kotlin Coroutines**. Engineered for peak efficiency, solid 120 FPS rendering, multi-user/work profile support, and dynamic aesthetic customization.

---

## 🚀 Key Features

- **Zero-Distraction Experience**: Minimalist home screen with subtle typography, dynamic solar-based theming (light/dark/OLED black/warm eye comfort), and smart widget integration.
- **Multi-User & Work Profile Support**: Full enterprise support utilizing Android's `LauncherApps` and `UserManager` APIs. Supports secondary users, Samsung Secure Folder, Xiaomi Dual Apps, and badged Work Profile apps.
- **Reactive App Detection**: Real-time package monitoring via `LauncherApps.Callback` (`onPackageAdded`, `onPackageRemoved`, `onPackageChanged`) with zero-leak lifecycle cleanup.
- **Safe Launch Engine (Intent Dispatcher)**: 3-level fallback launch strategy injecting mandatory system flags (`FLAG_ACTIVITY_NEW_TASK` and `FLAG_ACTIVITY_RESET_TASK_IF_NEEDED`) with typed `LaunchResult` handling.
- **Debounced Search (120 FPS UI)**: Reactive search bar featuring `150ms` debounce, `distinctUntilChanged()`, and asynchronous filtering on `Dispatchers.Default`.
- **Async Icon Pipeline**: In-memory `LruCache` and background drawable-to-bitmap decoding (`Dispatchers.IO`) preventing any frame drop during fast scrolling.
- **Customizable Minimalist Aesthetics**: Curated palettes, dynamic spectrum slider, custom icon shapes (Squircle, Pebble, Cut Corner, etc.) and minimalist rendering styles (Monogram, Tinted Monochrome, Outlined).

---

## 🏛️ Architecture

```
app/src/main/java/com/example/
├── data/
│   ├── model/
│   │   └── AppItem.kt                     # Core multi-user domain data class
│   └── repository/
│       ├── AppRepository.kt               # Repository interface & StateFlow contract
│       └── AppRepositoryImpl.kt           # LauncherApps & UserManager implementation
├── domain/
│   └── launcher/
│       ├── LaunchAppUseCase.kt            # 3-level fallback safe dispatch engine
│       └── LaunchResult.kt                # Typed launch result (Success, NotFound, Denied)
├── ui/
│   ├── presentation/
│   │   ├── LauncherScreen.kt              # Edge-to-edge debounced 120 FPS search UI
│   │   └── AsyncAppIcon.kt                # Async background icon decoding & LruCache
│   ├── home/                              # Home widgets, clock, and app drawer
│   ├── theme/                             # Solar calculator & dynamic themes
│   └── gestures/                          # Double-tap, swipe gestures & accessibility
├── AppInfo.kt                             # UI presentation model & adapter
├── LauncherViewModel.kt                   # Central StateFlow management & lifecycle
└── MainActivity.kt                        # Edge-to-edge Home activity
```

---

## 🛠️ Tech Stack & Requirements

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose (Material 3)
- **Asynchrony**: Kotlin Coroutines & Reactive Flow (`StateFlow`, `Channel`)
- **Image Loading**: Coil & Custom `LruCache`
- **System APIs**: `LauncherApps`, `UserManager`, Android Insets & Edge-to-Edge
- **Unit Testing**: JUnit 4 & Robolectric (SDK 36)

---

## 📄 License

This project is licensed under the Apache License 2.0.
