package com.android.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiphyResponse(
    @SerialName("data")
    val data: List<GifData> = emptyList(),
    @SerialName("pagination")
    val pagination: Pagination? = null,
    @SerialName("meta")
    val meta: Meta? = null
)

@Serializable
data class SingleGifResponse(
    @SerialName("data")
    val data: GifData,
    @SerialName("meta")
    val meta: Meta? = null
)

@Serializable
data class GifData(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String? = null,
    @SerialName("images")
    val images: Images,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("import_datetime")
    val importDatetime: String? = null,
    @SerialName("trending_datetime")
    val trendingDatetime: String? = null,
    @SerialName("user")
    val user: User? = null
)

@Serializable
data class Images(
    @SerialName("original")
    val original: ImageData? = null,
    @SerialName("fixed_height")
    val fixedHeight: ImageData? = null,
    @SerialName("fixed_width")
    val fixedWidth: ImageData? = null,
    @SerialName("fixed_height_small")
    val fixedHeightSmall: ImageData? = null,
    @SerialName("fixed_width_small")
    val fixedWidthSmall: ImageData? = null,
    @SerialName("downsized")
    val downsized: ImageData? = null,
    @SerialName("preview_gif")
    val previewGif: ImageData? = null
)

@Serializable
data class ImageData(
    @SerialName("url")
    val url: String,
    @SerialName("width")
    val width: String? = null,
    @SerialName("height")
    val height: String? = null,
    @SerialName("size")
    val size: String? = null
)

@Serializable
data class User(
    @SerialName("username")
    val username: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

@Serializable
data class Pagination(
    @SerialName("total_count")
    val totalCount: Int = 0,
    @SerialName("count")
    val count: Int = 0,
    @SerialName("offset")
    val offset: Int = 5
)

@Serializable
data class Meta(
    @SerialName("status")
    val status: Int = 0,
    @SerialName("msg")
    val msg: String? = null
)