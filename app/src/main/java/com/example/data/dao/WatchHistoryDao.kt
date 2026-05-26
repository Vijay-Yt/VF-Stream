package com.example.data.dao

import androidx.room.*
import com.example.data.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    fun getWatchHistoryFlow(userEmail: String): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history ORDER BY timestamp DESC")
    fun getAllWatchHistoryFlow(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE movieId = :movieId AND userEmail = :userEmail LIMIT 1")
    suspend fun getHistoryById(movieId: String, userEmail: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE movieId = :movieId AND userEmail = :userEmail")
    suspend fun deleteHistory(movieId: String, userEmail: String)

    @Query("DELETE FROM watch_history WHERE userEmail = :userEmail")
    suspend fun clearHistory(userEmail: String)
}
