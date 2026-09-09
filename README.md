# Minimalist Launcher 📱✨

A modern, distraction-free Android launcher built natively with **Jetpack Compose** and **Kotlin Coroutines**. Engineered for peak efficiency, solid 120 FPS rendering, multi-user/work profile support, and **digital wellbeing features** designed to foster intentional device usage.

---

## 🚀 Key Features

- **Zero-Distraction Experience**: Minimalist home screen with subtle typography, dynamic solar-based theming (light/dark/OLED black/warm eye comfort), and smart widget integration.
- **Multi-User & Work Profile Support**: Full enterprise support utilizing Android's `LauncherApps` and `UserManager` APIs. Supports secondary users, Samsung Secure Folder, Xiaomi Dual Apps, and brand-specific implementations.
- **Reactive App Detection**: Real-time package monitoring via `LauncherApps.Callback` (`onPackageAdded`, `onPackageRemoved`, `onPackageChanged`) with zero-leak lifecycle cleanup.
- **Safe Launch Engine (Intent Dispatcher)**: 3-level fallback launch strategy injecting mandatory system flags (`FLAG_ACTIVITY_NEW_TASK` and `FLAG_ACTIVITY_RESET_TASK_IF_NEEDED`) with typed `LaunchResult`.
- **Debounced Search (120 FPS UI)**: Reactive search bar featuring `150ms` debounce, `distinctUntilChanged()`, and asynchronous filtering on `Dispatchers.Default`.
- **Alphabetical App Grouping**: App drawer organized alphabetically (A, B, C...) with visual headers and right-aligned category labels in muted typography for effortless navigation.
- **Async Icon Pipeline**: In-memory `LruCache` and background drawable-to-bitmap decoding (`Dispatchers.IO`) preventing any frame drop during fast scrolling.
- **Customizable Minimalist Aesthetics**: Curated theme variants (OLED Puro, Grafito, Gris), typography selectors (Sans, Mono, Serif), dynamic spectrum slider, custom icon shapes (Squircle, Pebble, Cut Corner, etc.), and minimalist rendering styles (Monogram, Tinted Monochrome).
- **🧘 Digital Wellbeing Hub**: Dedicated **WellbeingScreen** with:
  - Screen time summary and unlock counter
  - Per-app consumption breakdown
  - **Strict Focus**: Block distracting apps during focus sessions
  - **Mindful Pause**: Interactive guided breathing exercises before app launches
  - **Auto Grayscale**: Automated desaturation to reduce engagement
  - **Intentional Delay**: Customizable 1-5s mindful pause with visual countdown before opening blocked apps

---

## 🏛️ Architecture

```
app/src/main/java/com/example/
├── data/
│   ├── model/
│   │   └── AppItem.kt                     # Core multi-user domain data class
│   └── repository/
│       ├── AppRepository.kt               # Repository interface & StateFlow contract
│       └── AppRepositoryImpl.kt            # LauncherApps & UserManager implementation
├── domain/
│   └── launcher/
│       ├── LaunchAppUseCase.kt            # 3-level fallback safe dispatch engine
│       └── LaunchResult.kt                # Typed launch result (Success, NotFound, Denied)
├── ui/
│   ├── presentation/
│   │   ├── LauncherScreen.kt              # Edge-to-edge debounced 120 FPS search UI
│   │   ├── WellbeingScreen.kt             # Screen time analytics & mindful pause tools
│   │   ├── AsyncAppIcon.kt                # Async background icon decoding & LruCache
│   │   └── HomeScreen.kt                  # 5-tab navigation (HOME, WIDGETS, WELLBEING, CUSTOMIZE, SETTINGS)
│   ├── home/                              # Home widgets, clock, and alphabetically grouped app drawer
│   ├── theme/                             # Solar calculator, dynamic themes, typography variants
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
- **Persistence**: SharedPreferences (theme variants, typography, settings)
- **Unit Testing**: JUnit 4 & Robolectric (SDK 36)

---

## 🎨 Customization Options

### Theme Variants
- **OLED Puro**: Pure blacks for maximum contrast on OLED displays
- **Grafito**: Sophisticated grayscale variant
- **Gris**: Neutral gray palette

### Typography Selectors
- **Sans**: Modern, clean sans-serif
- **Mono**: Monospace for technical aesthetic
- **Serif**: Classic serif elegance

### Wellbeing Controls
- **Intentional Delay**: 1-5 second mindful pause before app launch (slider & selector)
- **Strict Focus**: Toggle blocking for specific apps during focus sessions
- **Mindful Pause**: Breathing exercise guidance before opening flagged apps

---

## 📄 License

This project is licensed under the Apache License 2.0.
