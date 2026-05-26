package com.example.data.dao

import androidx.room.*
import com.example.data.entity.MovieDownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM movie_downloads WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    fun getAllDownloads(userEmail: String): Flow<List<MovieDownloadEntity>>

    @Query("SELECT * FROM movie_downloads")
    fun getAllMovieDownloadsFlow(): Flow<List<MovieDownloadEntity>>

    @Query("SELECT * FROM movie_downloads WHERE movieId = :movieId AND userEmail = :userEmail LIMIT 1")
    suspend fun getDownloadById(movieId: String, userEmail: String): MovieDownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDownload(download: MovieDownloadEntity)

    @Query("DELETE FROM movie_downloads WHERE movieId = :movieId AND userEmail = :userEmail")
    suspend fun deleteDownload(movieId: String, userEmail: String)

    @Query("UPDATE movie_downloads SET progress = :progress, status = :status, downloadedBytes = :downloaded WHERE movieId = :movieId AND userEmail = :userEmail")
    suspend fun updateDownloadProgress(movieId: String, userEmail: String, progress: Int, status: String, downloaded: Long)
}
