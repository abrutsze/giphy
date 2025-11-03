package com.android.models

data class UiMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val voteAverage: Double?,
) {
    val rating = voteAverage?.toString() ?: "0.0"
}
