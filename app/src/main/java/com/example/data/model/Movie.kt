package com.example.data.model

data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val posterUrl: String,
    val landscapeUrl: String,
    val category: String, // Bollywood, Hollywood, South Hindi Dubbed, Web Series, Anime, Punjabi Movies
    val rating: Double,
    val language: String,
    val duration: String,
    val videoUrl: String, // HLS (.m3u8) stream URL
    val releaseYear: Int,
    val isTrending: Boolean,
    val isLatest: Boolean,
    val isPremium: Boolean = false
)
