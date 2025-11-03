package com.android.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponse(
    val results: List<MovieResponse>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)

@Serializable
data class MovieResponse(
    val id: Int?,
    @SerialName("original_title")
    val originalTitle: String?,
    val overview: String?,
    @SerialName("poster_path")
    val posterPath: String?,
    val title: String?,
    @SerialName("vote_average")
    val voteAverage: Double?
)
