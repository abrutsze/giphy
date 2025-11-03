package com.android.giphy.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.android.giphy.presentation.detail.mvi.GiphyDetailIntent
import com.android.models.UiGif
import com.android.screens.Screens
import com.android.ui.components.ErrorScreen
import com.android.ui.components.toolbar.Toolbar
import com.android.ui.config.SystemBarConfig
import com.android.ui.preview.ProjectPreviewTheme
import com.android.ui.theme.AppColors
import com.android.ui.theme.AppTypography
import org.koin.androidx.compose.koinViewModel

@Composable
fun GiphyDetailRoute(
    gifId: String,
    onNavigate: (Screens) -> Unit,
    viewModel: GiphyDetailViewModel = koinViewModel()
) {
    val state = viewModel.viewState

    SystemBarConfig(
        statusBarColor = AppColors.white,
        isFullScreen = false,
        isIconBarVisible = true,
        isEdgeToEdgeEnabled = false,
        isDarkIcons = true
    )

    LaunchedEffect(gifId) {
        viewModel.onIntent(GiphyDetailIntent.LoadGif(gifId))
    }

    Scaffold(
        topBar = {
            Toolbar(
                title = "GIF Details",
                onBackClick = { onNavigate(Screens.Back()) },
                backgroundColor = AppColors.white,
                titleColor = AppColors.grey900Text
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.primaryBlue100)
                    }
                }

                state.error != null -> {
                    ErrorScreen(
                        message = state.error,
                        onRetry = { viewModel.onIntent(GiphyDetailIntent.Retry) },
                        retryButtonText = "Retry"
                    )
                }

                state.gif != null -> {
                    GiphyDetailContent(gif = state.gif)
                }
            }
        }
    }
}

@Composable
private fun GiphyDetailContent(gif: UiGif) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main GIF display
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            SubcomposeAsyncImage(
                model = gif.originalUrl,
                contentDescription = gif.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .background(AppColors.grey100),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.primaryBlue100,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .background(AppColors.grey100),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading GIF",
                            style = AppTypography.bodyMedium,
                            color = AppColors.grey800
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = gif.title,
            style = AppTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = AppColors.grey900Text,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User info
        if (gif.username.isNotBlank() && gif.username != "Unknown") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (gif.userAvatar.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = gif.userAvatar,
                        contentDescription = "User avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AppColors.grey100),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(AppColors.grey100, CircleShape)
                            )
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AppColors.primaryBlue100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gif.username.firstOrNull()?.uppercase() ?: "?",
                            style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column {
                    Text(
                        text = gif.username,
                        style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AppColors.grey900Text
                    )
                    Text(
                        text = "Creator",
                        style = AppTypography.bodySmall,
                        color = AppColors.grey800
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Rating
        DetailRow(label = "Rating", value = gif.rating)

        Spacer(modifier = Modifier.height(8.dp))

        // Dimensions
        DetailRow(label = "Size", value = "${gif.width} × ${gif.height} px")

        Spacer(modifier = Modifier.height(8.dp))

        // ID
        DetailRow(label = "ID", value = gif.id)
    }
}

@Composable
internal fun GiphyDetailScreen(
    gif: UiGif,
    onNavigate: (Screens) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "GIF Details",
                onBackClick = { onNavigate(Screens.Back()) },
                backgroundColor = AppColors.white,
                titleColor = AppColors.grey900Text
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main GIF display
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                SubcomposeAsyncImage(
                    model = gif.originalUrl,
                    contentDescription = gif.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .background(AppColors.grey100),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = AppColors.primaryBlue100,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .background(AppColors.grey100),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error loading GIF",
                                style = AppTypography.bodyMedium,
                                color = AppColors.grey800
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = gif.title,
                style = AppTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = AppColors.grey900Text,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User info
            if (gif.username.isNotBlank() && gif.username != "Unknown") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (gif.userAvatar.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = gif.userAvatar,
                            contentDescription = "User avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AppColors.grey100),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(AppColors.grey100, CircleShape)
                                )
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AppColors.primaryBlue100, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = gif.username.firstOrNull()?.uppercase() ?: "?",
                                style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    Column {
                        Text(
                            text = gif.username,
                            style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AppColors.grey900Text
                        )
                        Text(
                            text = "Creator",
                            style = AppTypography.bodySmall,
                            color = AppColors.grey800
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Rating
            DetailRow(label = "Rating", value = gif.rating)

            Spacer(modifier = Modifier.height(8.dp))

            // Dimensions
            DetailRow(label = "Size", value = "${gif.width} × ${gif.height} px")

            Spacer(modifier = Modifier.height(8.dp))

            // ID
            DetailRow(label = "ID", value = gif.id)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.grey50, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = AppColors.grey800
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppColors.grey900Text
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GiphyDetailScreenPreview() {
    ProjectPreviewTheme {
        GiphyDetailScreen(
            gif = UiGif(
                id = "123456",
                title = "Funny Cat Dancing",
                thumbnailUrl = "",
                originalUrl = "",
                previewUrl = "",
                rating = "G",
                username = "catlovers",
                userAvatar = "",
                width = 480,
                height = 360
            ),
            onNavigate = {}
        )
    }
}