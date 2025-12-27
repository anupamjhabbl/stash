# Stash

_A Kotlin Multiplatform application for saving and organizing your items, built with Compose Multiplatform._

Stash is a simple, modern application designed to help you keep track of your things. Whether it's a collection of books, movies, articles, links, goals or personal notes, Stash provides a clean interface to organize them into categories. It's built from the ground up using Kotlin Multiplatform, allowing it to run on Android, iOS, and Desktop (JVM) from a single shared codebase.

## ✨ Features

*   **Cross-Platform:** Runs natively on Android, iOS, and Desktop (JVM).
*   **Shared UI:** A single, modern user interface built entirely with Compose Multiplatform.
*   **Shared Business Logic:** ViewModels, UseCases, and Repositories are all written once in `commonMain`.
*   **Categorization:** Organize your stashed items into custom categories.
*   **Asynchronous Data Sync:** A background scheduler for the desktop app keeps data up-to-date.

## 🛠️ Tech Stack & Architecture

This project follows a modern, scalable architecture with a focus on code sharing.

*   **Kotlin Multiplatform:** For sharing code between platforms.
*   **Compose Multiplatform:** For building the UI for Android, iOS, and Desktop from a single codebase.
*   **MVVM Architecture:** Using platform-agnostic ViewModels to separate UI from business logic.
*   **Coroutines:** For managing asynchronous operations and background tasks.
*   **Koin:** For dependency injection across all platforms.
*   **Domain-Driven Layers:** The logic is separated into `presentation`, `domain` (UseCases), and `data` (Repositories) layers within the `commonMain` source set.

## 📂 Project Structure

The project is organized into a multi-module structure, with the core logic residing in the `composeApp` module.

```
.
├── composeApp
│   ├── src
│   │   ├── commonMain  // Shared code (ViewModels, UseCases, Composables, etc.)
│   │   ├── androidMain // Android-specific code and resources
│   │   ├── iosMain     // iOS-specific code and entry point
│   │   └── jvmMain     // Desktop app entry point and sync scheduler
│   └── build.gradle.kts
└── build.gradle.kts
```

*   `commonMain`: Contains all the shared logic, including ViewModels, domain models, use cases, repositories, and the Compose UI.
*   `androidMain`: The Android application entry point, platform-specific dependencies, and `AndroidManifest.xml`.
*   `iosMain`: The iOS application entry point (`MainViewController.kt`) and any iOS-specific code.
*   `jvmMain`: The desktop application entry point (`main.kt`) and any desktop-specific implementations, like the `JvmSyncScheduler`.

## 🚀 Getting Started

### Prerequisites

*   **JDK 17** or higher.
*   **Android Studio** (latest version recommended).
*   **Xcode** (for running the iOS app).

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone <your-repo-url>
    ```
2.  Open the project in Android Studio.
3.  Let Gradle sync and download dependencies.

#### ▶️ Android

Select the `composeApp` run configuration in Android Studio and choose an Android emulator or a connected device. Click the "Run" button.

#### ▶️ iOS

Select an iOS Simulator target from the run configuration dropdown in Android Studio and click "Run". Alternatively, you can open the generated `iosApp.xcworkspace` in Xcode and run the project from there.

#### ▶️ Desktop

To run the desktop application, execute the following Gradle task in the terminal:

```bash
./gradlew :composeApp:run
```

## 🤝 Contributing

Contributions are welcome! If you find a bug or have a feature request, please open an issue. If you want to contribute code, please feel free to submit a pull request.

## 📄 License

This project is licensed under the MIT License. You can create a `LICENSE` file and add the license text there.
