# Giphy Feature Implementation

## Overview
This feature implements a Giphy GIF search and browsing functionality using the Giphy API. It includes:
- Search functionality with debouncing (500ms delay)
- Pagination support (25 items per page)
- Grid layout with orientation support (2 columns portrait, 4 columns landscape)
- Detail view for individual GIFs
- Error handling with retry functionality
- Loading states with shimmer effects
- Unit tests for repository, ViewModel, and reducer

## Technical Requirements Completed

### 1. Auto Search ✅
- Implemented debounced search with 500ms delay after user stops typing
- Uses Flow with `debounce` operator in `GiphySearchViewModel`
- Location: `feature/giphy/src/main/kotlin/com/android/giphy/presentation/search/GiphySearchViewModel.kt:48`

### 2. Pagination ✅
- Loads 25 items per page
- Automatically triggers when scrolling reaches last 6 items
- Appends new results to existing list
- Tracks current page and hasMorePages state
- Location: `feature/giphy/src/main/kotlin/com/android/giphy/presentation/search/GiphySearchScreen.kt:161`

### 3. Orientation Support ✅
- Portrait: 2 columns grid
- Landscape: 4 columns grid
- Uses `LocalConfiguration.current` to detect orientation changes
- Location: `feature/giphy/src/main/kotlin/com/android/giphy/presentation/search/GiphySearchScreen.kt:75`

### 4. Error Handling ✅
- Network error handling with Result type
- Error display with retry button
- Empty state handling
- Location: Repository errors, ViewModel error states, UI error screens

### 5. Unit Tests ✅
Implemented comprehensive tests:
- `GiphyRepositoryImplTest`: Repository layer tests (4 test cases)
- `GiphySearchViewModelTest`: ViewModel logic tests (7 test cases)
- `GiphySearchReducerTest`: State reducer tests (9 test cases)
- Location: `feature/giphy/src/test/kotlin/com/android/giphy/`

### UI Requirements

#### 1. Multiple Views ✅
- **Search Screen**: Grid view with search bar, trending/search results
- **Detail Screen**: Full GIF display with metadata (title, creator, rating, dimensions)

#### 2. Grid Display ✅
- Responsive grid using `LazyVerticalGrid`
- Grid cells with equal aspect ratio (1:1)
- Rounded corners and elevation
- Loading placeholders during image load

#### 3. Detail Activity ✅
Opens detail screen on item click with:
- Full-size GIF display
- Title
- Creator information with avatar
- Rating
- Dimensions
- GIF ID

#### 4. Loading Indicators ✅
- Initial loading: Full screen CircularProgressIndicator
- Load more: Bottom CircularProgressIndicator
- Image loading: Individual placeholder with spinner

#### 5. Error Display ✅
- Error screen with message
- Retry button
- Empty state handling

## Setup Instructions

### 1. Add Giphy API Key

You need to configure the Giphy API credentials in your `gradle.properties` or `BuildConfig`:

**Option A: Using gradle.properties**
Add to `gradle.properties`:
```properties
GIPHY_BASE_URL=api.giphy.com
GIPHY_API_KEY=your_api_key_here
GIPHY_PROTOCOL=https
```

**Option B: Using BuildConfig**
Update `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"api.giphy.com\"")
        buildConfigField("String", "API_KEY", "\"your_giphy_api_key\"")
        buildConfigField("String", "PROTOCOL", "\"https\"")
    }
}
```

### 2. Get Giphy API Key
1. Visit https://developers.giphy.com/
2. Create a free account
3. Create a new app
4. Copy your API key
5. Add it to your configuration (see step 1)

### 3. Navigate to Giphy Feature

From any screen in your app, navigate to:
```kotlin
// Navigate to Giphy search screen
onNavigate(GiphyScreens.SearchScreen)

// Or navigate to root
onNavigate(Screens.NavigateToRoot(GiphyScreens.SearchScreen))
```

Example: Update LoginScreen after successful login:
```kotlin
// In LoginViewModel or LoginScreen
onNavigate(Screens.NavigateToRoot(GiphyScreens.SearchScreen))
```

### MVI Pattern
Following your existing MVI architecture:

**State**: `GiphySearchState`
- Holds search query, GIFs list, loading states, pagination info

**Intent**: `GiphySearchIntent`
- User actions: OnSearchQueryChanged, OnLoadMore, OnRetry, OnGifClicked

**Action**: `GiphySearchAction`
- Internal actions for state updates

**Effect**: `GiphySearchEffect`
- One-time events like navigation

**Reducer**: `GiphySearchReducer`
- Pure function that transforms state based on actions

### Module Structure
```
feature/giphy/
├── data/
│   ├── GiphyRepository.kt
│   └── GiphyRepositoryImpl.kt
├── domain/
│   ├── di/GiphyModule.kt
│   ├── mapper/GiphyDomainMapper.kt
│   └── usecase/
│       ├── SearchGifsUseCase.kt
│       └── GetTrendingGifsUseCase.kt
└── presentation/
    ├── detail/GiphyDetailScreen.kt
    └── search/
        ├── GiphySearchScreen.kt
        ├── GiphySearchViewModel.kt
        └── mvi/
            ├── GiphySearchState.kt
            ├── GiphySearchIntent.kt
            ├── GiphySearchAction.kt
            ├── GiphySearchEffect.kt
            └── GiphySearchReducer.kt
```

## API Documentation
Giphy API Docs: https://developers.giphy.com/docs/api/

### Endpoints Used
- **Search**: `GET /v1/gifs/search?api_key={key}&q={query}&limit={limit}&offset={offset}`
- **Trending**: `GET /v1/gifs/trending?limit={limit}&offset={offset}&api_key={key}`

## Testing

Run tests:
```bash
./gradlew :feature:giphy:test
```

Test coverage:
- Repository: API integration, error handling, data mapping
- ViewModel: Search debouncing, pagination, state management
- Reducer: All state transformations

## Dependencies
All dependencies are handled by the existing feature convention plugin:
- Compose UI
- Ktor HTTP Client
- Koin DI
- Coil image loading
- Kotlinx Serialization
- Coroutines & Flow
- JUnit, MockK (testing)