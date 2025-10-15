# Pokedex

A modern Android application showcasing Pokemon data using Jetpack Compose and Clean Architecture principles.

## Architecture

This project follows Clean Architecture with a modular approach:

- **app**: Presentation layer containing UI components, ViewModels, and navigation
- **domain**: Business logic layer with entities, use cases, and repository interfaces
- **data**: Data layer implementing repositories, local database, and remote API integration

## Project Structure

```
├── app/                 # Presentation layer
│   ├── src/main/java/com/ikiugu/pokedex/
│   │   ├── presentation/    # UI components, ViewModels
│   │   └── di/             # Dependency injection setup
├── domain/             # Business logic layer
│   └── src/main/java/com/ikiugu/pokedex/domain/
│       ├── entity/         # Domain models
│       ├── repository/     # Repository interfaces
│       └── usecase/        # Business logic use cases
└── data/               # Data layer
    └── src/main/java/com/ikiugu/pokedex/data/
        ├── local/          # Room database
        ├── remote/         # API services
        ├── repository/     # Repository implementations
        └── di/             # Data layer DI modules
```

## Technology Stack

- **UI**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **Database**: Room
- **Networking**: Retrofit + OkHttp
- **Async Operations**: Kotlin Coroutines + Flow
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **Paging**: AndroidX Paging 3
- **Testing**: JUnit, Mockito, Coroutines Test

## Features

- Browse Pokemon list with pagination
- Search Pokemon by name
- View detailed Pokemon information
- Offline-first approach with local caching
- Material Design 3 UI

## Build Performance

The project is optimized for fast builds with modular architecture and performance-focused Gradle configuration including:

- Configuration caching
- Build caching
- Parallel execution
- Incremental compilation
- R8 optimization

## Requirements

- Android Studio Arctic Fox or later
- JDK 17
- Android SDK 24+

## API

This application consumes the [Pokemon API](https://pokeapi.co/) for fetching Pokemon data.

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run the application

## Development

### Building
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean build
./gradlew clean
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :domain:test
./gradlew :data:test
./gradlew :app:test
```

## Testing

Each module contains its own unit tests:
- Domain tests: Use case validation and business logic
- Data tests: Repository implementation and data mapping
- App tests: ViewModel behavior and UI logic

## Implementation Highlights

- **Modular Architecture**: Clean separation between presentation, domain, and data layers
- **Dependency Injection**: Hilt for managing dependencies across modules
- **Offline-First**: Room database with network data synchronization
- **Reactive UI**: Jetpack Compose with ViewModel state management
- **Error Handling**: Comprehensive error handling with Result wrapper
- **Performance**: Optimized Gradle configuration for fast builds

---

*Note: A single-module version of this application with Clean Architecture is available on the [feature/non-modular branch](https://github.com/ikiugu/pokedex/tree/feature/non-modular).*
