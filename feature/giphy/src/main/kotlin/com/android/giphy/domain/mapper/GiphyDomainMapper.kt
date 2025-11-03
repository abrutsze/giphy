package com.android.giphy.domain.mapper

import com.android.models.UiGif
import com.android.response.GifData

fun GifData.toUiGif(): UiGif {
    val thumbnail = images.fixedWidthSmall?.url ?: images.fixedHeightSmall?.url ?: images.downsized?.url ?: ""
    val original = images.original?.url ?: ""
    val preview = images.previewGif?.url ?: images.fixedWidth?.url ?: original

    return UiGif(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Untitled",
        thumbnailUrl = thumbnail,
        originalUrl = original,
        previewUrl = preview,
        rating = rating?.uppercase() ?: "NOT RATED",
        username = user?.displayName ?: user?.username ?: "Unknown",
        userAvatar = user?.avatarUrl ?: "",
        width = images.original?.width?.toIntOrNull() ?: 0,
        height = images.original?.height?.toIntOrNull() ?: 0
    )
}

fun List<GifData>.toUiGifs(): List<UiGif> = map { it.toUiGif() }