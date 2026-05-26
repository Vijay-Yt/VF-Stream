package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history", primaryKeys = ["movieId", "userEmail"])
data class WatchHistoryEntity(
    val movieId: String,
    val userEmail: String,
    val title: String,
    val category: String,
    val posterUrl: String,
    val lastPositionMs: Long,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)
