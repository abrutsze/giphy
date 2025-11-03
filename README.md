# GiphyCompose

Android application for browsing GIF images from Giphy API, demonstrating modern Android development approaches.

---

## 📱 About the Project

**GiphyCompose** is an application for viewing and searching GIF animations via Giphy API.

### Core Features:

- **Browse Trending GIFs** — feed of current popular GIF images
- **Search by Keywords** — GIF search with debounce effect (500 ms)
- **Detailed View** — detailed GIF information (rating, dimensions, author)
- **Infinite Scroll** — pagination for loading large amounts of content
- **Optimized GIF Loading** — using Coil with custom decoder configuration

---

## 🏗️ Project Architecture

### Clean Architecture

The project is built on **Clean Architecture** principles with clear layer separation:

#### **Data Layer**
- **Repository Interface**: `feature/giphy/src/main/kotlin/com/android/giphy/data/GiphyRepository.kt`
- **Repository Implementation**: `feature/giphy/src/main/kotlin/com/android/giphy/data/GiphyRepositoryImpl.kt`
- **API Interface**: `core/network/api/src/main/kotlin/com/android/network/api/GiphyApi.kt`
- Network requests handling via Ktor Client
- Error handling using `Result<T>`

#### **Domain Layer**
- **Use Cases**:
  - `SearchGifsUseCase.kt` — GIF search
  - `GetTrendingGifsUseCase.kt` — trending GIFs retrieval
  - `GetGifByIdUseCase.kt` — specific GIF retrieval by ID
- **Mappers**:
  - `GiphyDomainMapper.kt` — data model to UI model transformation
- Pure business logic without Android Framework dependencies

#### **Presentation Layer**
- **MVI Pattern** — Model-View-Intent for UI state management
- **ViewModels** — business logic and screen state handling
- **Screens** — Composable functions for UI rendering

---

### MVI (Model-View-Intent) Pattern

The project uses a custom **MVI pattern** implementation for state management:

#### Base Infrastructure (`common/mvi`):

- **MviBaseViewModel** — base ViewModel for handling State, Actions, Intents, Effects
- **MviState** — marker interface for states
- **MviAction** — internal actions that trigger state changes
- **MviIntent** — user intents (e.g., clicks, text input)
- **MviEffect** — one-time side effects (navigation, toast messages)
- **Reducer** — pure function for reducing actions to new states

#### MVI Advantages:

- ✅ Unidirectional Data Flow
- ✅ Immutable State
- ✅ Predictable UI behavior
- ✅ Easy testing (pure functions)
- ✅ Centralized state management

---

### Modular Architecture

The project is divided into **multiple independent modules** with clear responsibility boundaries:

#### **Core Modules** (`core/`)
Infrastructure modules with API and Implementation separation:

- **`core:network:api`** — network interfaces
- **`core:network:impl`** — Ktor Client implementation
- **`core:dispatchers:api`** — dispatcher provider interface
- **`core:dispatchers:impl`** — coroutine dispatchers implementation
- **`core:datastore:api`** — DataStore interface
- **`core:datastore:impl`** — DataStore implementation
- **`core:ui`** — reusable UI components and theme
- **`core:resources`** — shared resources

#### **Common Modules** (`common/`)
Shared components for the entire application:

- **`common:mvi`** — base classes and interfaces for MVI pattern
- **`common:response`** — API response models
- **`common:ui-models`** — UI data models
- **`common:utils`** — utility functions

#### **Feature Modules** (`feature/`)
Feature modules with full layer implementation:

- **`feature:giphy`** — Giphy functionality:
  - `data/` — repositories
  - `domain/` — use cases, mappers
  - `presentation/` — ViewModels, Screens

#### **Other Modules**

- **`navigation`** — screen navigation
- **`screens`** — screen definitions for type-safe navigation
- **`build-logic`** — custom Gradle Convention Plugins

#### Modularity Advantages:

- ✅ Clear separation of concerns
- ✅ Independent modules with explicit dependencies
- ✅ API/Implementation separation for flexibility
- ✅ Faster build times through parallel compilation
- ✅ Component reusability

---

## 🔧 Technology Stack

### UI Layer

#### **Jetpack Compose** (100%)
- **Compose BOM**: 2025.03.00
- **Material3**: 1.3.1
- Fully declarative UI without XML
- Compose Preview for rapid development
- Main components:
  - `LazyVerticalGrid` for GIF grid
  - `Scaffold`, `TopAppBar`, `Card`
  - Custom components: `SearchBar`, `Toolbar`, `ErrorScreen`

#### **Kotlin Coroutines** (1.10.1)
- All asynchronous operations via suspend functions
- `viewModelScope` for ViewModel coroutines
- `withContext` for dispatcher switching
- **DispatchersProvider** pattern for testability:

#### **Kotlin Flow**
- `MutableSharedFlow` for debouncing search queries (500 ms)
- `Channel` for one-time effects in MVI
- Flow operators:
  - `debounce(500)` — delay before search
  - `distinctUntilChanged()` — ignore duplicates
  - `filter` — empty query filtering

### Dependency Injection

#### **Koin** (3.4.3)
- **Koin Compose**: 3.4.2 — Compose integration
- **Koin KSP**: 1.2.2 — code generation with annotations

**Used Annotations:**
- `@Module` + `@ComponentScan` — automatic dependency scanning
- `@Single` — singleton dependencies
- `@KoinViewModel` — ViewModels with injection
- `koinViewModel()` — ViewModel retrieval in Composable

**DI Modules:**
- `AppModule` — application dependencies
- `DataModule` — network dependencies (Ktor)
- `DispatchersModule` — coroutine dispatchers
- `DataStoreModule` — DataStore
- `GiphyModule` — Giphy feature dependencies

---

### Navigation

#### **Jetpack Navigation Compose** (2.8.9)
- **Type-Safe Navigation** using `@Serializable`
- Screen definitions via sealed classes

### Network Layer

#### **Ktor Client** (3.1.0)
- **Ktor Client OkHttp** — HTTP client based on OkHttp
- **Content Negotiation** — automatic JSON serialization/deserialization
- **Logging** — request and response logging
- **Error Handling** — centralized error handling

#### **Kotlinx Serialization** (1.8.0)
- `@Serializable` data classes
- `@SerialName` for JSON field mapping
- Type-safe JSON handling

---

### Image Loading

#### **Coil** (2.7.0)
- **coil-compose** — Jetpack Compose integration
- **coil-gif** — GIF animation support

### Testing

#### **Unit Testing**
- **JUnit 4** (4.13.2)
- **MockK** (1.13.4) — mocking for Kotlin
- **Kotlinx Coroutines Test** (1.10.1) — coroutines testing

#### **UI Testing**
- **Compose UI Test**
- **Espresso** (3.6.1)

## 🛠️ Technical Requirements

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Kotlin**: 2.1.0
- **Android Gradle Plugin**: 8.9.1
- **Java Version**: 17

Demo video
[Download demo video](media/demo.mp4)