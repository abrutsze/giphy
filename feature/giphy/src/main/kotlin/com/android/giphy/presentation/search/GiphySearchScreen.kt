package com.android.giphy.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.giphy.presentation.search.mvi.GiphySearchEffect
import com.android.giphy.presentation.search.mvi.GiphySearchIntent
import com.android.giphy.presentation.search.mvi.GiphySearchState
import com.android.models.UiGif
import com.android.resources.R
import com.android.screens.GiphyScreens
import com.android.screens.Screens
import com.android.ui.components.ErrorScreen
import com.android.ui.components.GifItem
import com.android.ui.components.input.SearchBar
import com.android.ui.components.toolbar.Toolbar
import com.android.ui.config.SystemBarConfig
import com.android.ui.preview.ProjectPreviewTheme
import com.android.ui.theme.AppColors
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@Composable
fun GiphySearchRoute(
    onNavigate: (Screens) -> Unit,
    viewModel: GiphySearchViewModel = koinViewModel(),
) {
    val state = viewModel.viewState
    val scope = rememberCoroutineScope()

    SystemBarConfig(
        statusBarColor = AppColors.white,
        isFullScreen = false,
        isIconBarVisible = true,
        isEdgeToEdgeEnabled = false,
        isDarkIcons = true
    )

    LaunchedEffect(Unit) {
        viewModel.effects.onEach { effect ->
            when (effect) {
                is GiphySearchEffect.NavigateToDetail -> {
                    // Navigate to detail screen with GIF ID
                    onNavigate(GiphyScreens.DetailScreen(effect.gifId))
                }
            }
        }.launchIn(scope)
    }

    GiphySearchScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigate = onNavigate
    )
}

@Composable
internal fun GiphySearchScreen(
    state: GiphySearchState,
    onIntent: (GiphySearchIntent) -> Unit,
    onNavigate: (Screens) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columns = if (isLandscape) 4 else 2

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(AppColors.white)
            ) {
                Toolbar(
                    title = stringResource(R.string.app_name),
                    backgroundColor = AppColors.white,
                    titleColor = AppColors.grey900Text
                )
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { query ->
                        onIntent(GiphySearchIntent.OnSearchQueryChanged(query))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading && state.gifs.isEmpty() -> {
                    LoadingScreen()
                }

                !state.error.isNullOrEmpty() && state.gifs.isEmpty() -> {
                    ErrorScreenWithRetry(
                        message = state.error,
                        onRetry = { onIntent(GiphySearchIntent.OnRetry) }
                    )
                }

                state.gifs.isEmpty() && !state.isLoading -> {
                    EmptySearchScreen()
                }

                else -> {
                    GifGrid(
                        gifs = state.gifs,
                        columns = columns,
                        isLoadingMore = state.isLoadingMore,
                        hasMorePages = state.hasMorePages,
                        onGifClick = { gifId ->
                            onIntent(GiphySearchIntent.OnGifClicked(gifId))
                        },
                        onLoadMore = {
                            onIntent(GiphySearchIntent.OnLoadMore)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GifGrid(
    gifs: List<UiGif>,
    columns: Int,
    isLoadingMore: Boolean,
    hasMorePages: Boolean,
    onGifClick: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyGridState()

    // Detect when user reaches the end for pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = layoutInfo.totalItemsCount

            // Only trigger if we have items and user is near the end
            totalItems > 0 &&
                    lastVisibleItem != null &&
                    lastVisibleItem.index >= totalItems - 6 &&
                    !isLoadingMore &&
                    hasMorePages
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = listState,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = gifs.size,
            key = { index -> "${gifs[index].id}_$index" }
        ) { index ->
            val gif = gifs[index]
            GifItem(
                thumbnailUrl = gif.thumbnailUrl,
                title = gif.title,
                onClick = { onGifClick(gif.id) }
            )
        }

        // Loading more indicator
        if (isLoadingMore) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(columns) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.primaryBlue100)
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AppColors.primaryBlue100)
    }
}

@Composable
private fun ErrorScreenWithRetry(
    message: String,
    onRetry: () -> Unit,
) {
    ErrorScreen(
        message = message,
        onRetry = onRetry,
        retryButtonText = "Retry"
    )
}

@Composable
private fun EmptySearchScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No GIFs found",
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.grey800
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GiphySearchScreenPreview() {
    ProjectPreviewTheme {
        GiphySearchScreen(
            state = GiphySearchState(
                gifs = listOf(
                    UiGif(
                        id = "1",
                        title = "Funny Cat",
                        thumbnailUrl = "",
                        originalUrl = "",
                        previewUrl = "",
                        rating = "G",
                        username = "user1",
                        userAvatar = "",
                        width = 200,
                        height = 200
                    )
                )
            ),
            onIntent = {},
            onNavigate = {}
        )
    }
}