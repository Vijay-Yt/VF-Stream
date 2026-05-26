package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_downloads", primaryKeys = ["movieId", "userEmail"])
data class MovieDownloadEntity(
    val movieId: String,
    val userEmail: String,
    val title: String,
    val category: String,
    val posterUrl: String,
    val language: String,
    val videoUrl: String,
    val progress: Int, // 0 to 100
    val status: String, // PENDING, DOWNLOADING, COMPLETED, PAUSED
    val fileSizeMb: Double,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val timestamp: Long = System.currentTimeMillis()
)
