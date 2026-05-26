package com.example.data.repository

import android.content.Context
import com.example.data.dao.DownloadDao
import com.example.data.dao.WatchHistoryDao
import com.example.data.entity.MovieDownloadEntity
import com.example.data.entity.WatchHistoryEntity
import com.example.data.model.Movie
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class MovieRepository(
    private val downloadDao: DownloadDao,
    private val watchHistoryDao: WatchHistoryDao,
    private val context: Context
) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val activeDownloadJobs = ConcurrentHashMap<String, Job>()

    fun getDownloadsForUser(userEmail: String): Flow<List<MovieDownloadEntity>> =
        downloadDao.getAllDownloads(userEmail)

    fun getWatchHistoryForUser(userEmail: String): Flow<List<WatchHistoryEntity>> =
        watchHistoryDao.getWatchHistoryFlow(userEmail)

    suspend fun getDownloadById(movieId: String, userEmail: String): MovieDownloadEntity? =
        downloadDao.getDownloadById(movieId, userEmail)

    suspend fun getHistoryById(movieId: String, userEmail: String): WatchHistoryEntity? =
        watchHistoryDao.getHistoryById(movieId, userEmail)

    suspend fun insertOrUpdateHistory(
        movieId: String,
        userEmail: String,
        title: String,
        category: String,
        posterUrl: String,
        positionMs: Long,
        durationMs: Long
    ) {
        val history = WatchHistoryEntity(
            movieId = movieId,
            userEmail = userEmail,
            title = title,
            category = category,
            posterUrl = posterUrl,
            lastPositionMs = positionMs,
            durationMs = durationMs
        )
        watchHistoryDao.insertOrUpdateHistory(history)
    }

    suspend fun deleteHistory(movieId: String, userEmail: String) {
        watchHistoryDao.deleteHistory(movieId, userEmail)
    }

    suspend fun deleteDownload(movieId: String, userEmail: String) {
        val uniqueWorkName = "download_${userEmail}_$movieId"
        try {
            val workManager = androidx.work.WorkManager.getInstance(context)
            workManager.cancelUniqueWork(uniqueWorkName)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        downloadDao.deleteDownload(movieId, userEmail)

        // Physical cleanup of local file
        val destinationFile = File(context.filesDir, "downloads/${movieId}.mp4")
        if (destinationFile.exists()) {
            destinationFile.delete()
        }
    }

    fun pauseDownload(movieId: String, userEmail: String) {
        val uniqueWorkName = "download_${userEmail}_$movieId"
        try {
            val workManager = androidx.work.WorkManager.getInstance(context)
            workManager.cancelUniqueWork(uniqueWorkName)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Instantly mark as PAUSED within database
        CoroutineScope(Dispatchers.IO).launch {
            val existing = downloadDao.getDownloadById(movieId, userEmail)
            if (existing != null && existing.status != "COMPLETED") {
                downloadDao.insertOrUpdateDownload(
                    existing.copy(status = "PAUSED")
                )
            }
        }
    }

    fun startOrResumeDownload(scope: CoroutineScope, movie: Movie, userEmail: String) {
        val uniqueWorkName = "download_${userEmail}_${movie.id}"
        
        // Initial insert as PENDING inside ROOM
        scope.launch(Dispatchers.IO) {
            val existing = downloadDao.getDownloadById(movie.id, userEmail)
            if (existing != null && existing.status == "COMPLETED") {
                return@launch
            }

            val downloadsDir = File(context.filesDir, "downloads")
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val destinationFile = File(downloadsDir, "${movie.id}.mp4")
            val existingLength = if (destinationFile.exists()) destinationFile.length() else 0L

            val initialFileSizeMb = existing?.fileSizeMb ?: 15.0
            val initialDownload = MovieDownloadEntity(
                movieId = movie.id,
                userEmail = userEmail,
                title = movie.title,
                category = movie.category,
                posterUrl = movie.posterUrl,
                language = movie.language,
                videoUrl = movie.videoUrl,
                progress = existing?.progress ?: 0,
                status = "PENDING",
                fileSizeMb = initialFileSizeMb,
                downloadedBytes = existingLength,
                totalBytes = existing?.totalBytes ?: (initialFileSizeMb * 1024L * 1024L).toLong()
            )
            downloadDao.insertOrUpdateDownload(initialDownload)

            // Trigger the WorkManager Worker
            try {
                val data = androidx.work.workDataOf(
                    com.example.data.worker.DownloadWorker.KEY_MOVIE_ID to movie.id,
                    com.example.data.worker.DownloadWorker.KEY_USER_EMAIL to userEmail,
                    com.example.data.worker.DownloadWorker.KEY_TITLE to movie.title,
                    com.example.data.worker.DownloadWorker.KEY_POSTER_URL to movie.posterUrl,
                    com.example.data.worker.DownloadWorker.KEY_CATEGORY to movie.category,
                    com.example.data.worker.DownloadWorker.KEY_LANGUAGE to movie.language,
                    com.example.data.worker.DownloadWorker.KEY_VIDEO_URL to movie.videoUrl
                )

                val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.worker.DownloadWorker>()
                    .setInputData(data)
                    .addTag("download_tag_${movie.id}")
                    .build()

                val workManager = androidx.work.WorkManager.getInstance(context)
                workManager.enqueueUniqueWork(
                    uniqueWorkName,
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRealDownloadUrlForMovie(movie: Movie): String {
        return when (movie.id) {
            "bolly_1" -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            "bolly_2" -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
            "holly_1" -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
            "holly_2" -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
            "south_1" -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
            "south_2" -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            else -> "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        }
    }
}
