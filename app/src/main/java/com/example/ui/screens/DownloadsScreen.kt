package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.entity.MovieDownloadEntity
import com.example.data.model.Movie
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NetflixRed
import com.example.ui.theme.DownloadGreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    viewModel: MainViewModel,
    onNavigateToPlayer: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val downloads by viewModel.activeDownloads.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Physical storage system queries with fallback protections
    val filesDir = context.filesDir
    val totalSpace = filesDir.totalSpace
    val freeSpace = filesDir.freeSpace
    val totalSpaceGb = if (totalSpace > 0L) totalSpace.toDouble() / (1024.0 * 1024.0 * 1024.0) else 16.0
    val freeSpaceGb = if (freeSpace > 0L) freeSpace.toDouble() / (1024.0 * 1024.0 * 1024.0) else 8.0
    val appDownloadsMb = downloads.sumOf { it.downloadedBytes }.toDouble() / (1024.0 * 1024.0)
    val appDownloadsGb = appDownloadsMb / 1024.0
    val systemAndOtherGb = (totalSpaceGb - freeSpaceGb - appDownloadsGb).coerceAtLeast(0.0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("downloads_screen_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Offline Downloads",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Manage downloaded movies that you can stream without an internet connection.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 0. PREMIUM STORAGE FOOTPRINT INFO CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FolderOff,
                                contentDescription = "Storage",
                                tint = NetflixRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Device Storage",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f GB Free", freeSpaceGb),
                            color = DownloadGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multi-segmented bar: App Downloads (Red), Other/System (Gray), Free (Dark)
                    val appProgressRaw = if (totalSpaceGb > 0.0) (appDownloadsGb / totalSpaceGb).toFloat() else 0f
                    val systemProgressRaw = if (totalSpaceGb > 0.0) (systemAndOtherGb / totalSpaceGb).toFloat() else 0f
                    val appProgress = if (appProgressRaw.isNaN() || appProgressRaw.isInfinite()) 0f else appProgressRaw.coerceIn(0f, 1f)
                    val systemProgress = if (systemProgressRaw.isNaN() || systemProgressRaw.isInfinite()) 0f else systemProgressRaw.coerceIn(0f, 1f)
                    val freeProgress = (1f - appProgress - systemProgress).coerceIn(0f, 1f)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            // VF Stream downloads segment (Red)
                            if (appProgress > 0.001f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(appProgress)
                                        .background(NetflixRed)
                                )
                            }
                            // Other apps/System segment (Gray)
                            if (systemProgress > 0.001f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(systemProgress)
                                        .background(Color.Gray.copy(alpha = 0.6f))
                                )
                            }
                            // Empty segment (Free space)
                            if (freeProgress > 0.001f) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(freeProgress)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text legends
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NetflixRed))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = String.format(java.util.Locale.US, "VF Stream: %.1f MB", appDownloadsMb),
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.6f)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "System & Other",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            if (downloads.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "No downloads",
                            tint = Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Downloads Found",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap on any movie detail page to download it.",
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(downloads, key = { it.movieId }) { entry ->
                        val correspondingMovie = viewModel.allMovies.value.find { it.id == entry.movieId }
                        DownloadItemCard(
                            entry = entry,
                            onPlayClick = {
                                if (correspondingMovie != null) {
                                    onNavigateToPlayer(correspondingMovie)
                                }
                            },
                            onResumeClick = {
                                if (correspondingMovie != null) {
                                    viewModel.startMovieDownload(correspondingMovie)
                                }
                            },
                            onPauseClick = {
                                viewModel.pauseMovieDownload(entry.movieId)
                            },
                            onDeleteClick = {
                                viewModel.deleteDownloadedMovie(entry.movieId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    entry: MovieDownloadEntity,
    onPlayClick: () -> Unit,
    onResumeClick: () -> Unit,
    onPauseClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movie Poster Thumbnail
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 110.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = entry.posterUrl,
                    contentDescription = entry.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Progress stats
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${entry.category} • ${entry.language}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                )

                // Download Progress States
                when (entry.status) {
                    "COMPLETED" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DownloadGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed",
                                        tint = DownloadGreen,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "OFFLINE READY",
                                        color = DownloadGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = String.format(java.util.Locale.US, "%.1f MB", entry.fileSizeMb),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    "PAUSED" -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Paused",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${entry.progress}%",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            LinearProgressIndicator(
                                progress = entry.progress / 100f,
                                color = Color.Gray,
                                trackColor = Color.Gray.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                            )
                        }
                    }
                    "ERROR" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = NetflixRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Failed (Tap retry)",
                                color = NetflixRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    else -> { // PENDING or DOWNLOADING
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (entry.status == "PENDING") "Preparing..." else "Downloading...",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${entry.progress}%",
                                    color = NetflixRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            LinearProgressIndicator(
                                progress = entry.progress / 100f,
                                color = NetflixRed,
                                trackColor = Color.Gray.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action Triggers based on state
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (entry.status == "COMPLETED") {
                    IconButton(
                        onClick = onPlayClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = NetflixRed,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("play_download_${entry.movieId}").size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play offline",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    // Pause/Resume toggles
                    val isPausedOrError = entry.status == "PAUSED" || entry.status == "ERROR"
                    IconButton(
                        onClick = {
                            if (isPausedOrError) {
                                onResumeClick()
                            } else {
                                onPauseClick()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isPausedOrError) DownloadGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f),
                            contentColor = if (isPausedOrError) DownloadGreen else Color.White
                        ),
                        modifier = Modifier.testTag("pause_resume_${entry.movieId}").size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPausedOrError) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPausedOrError) "Resume" else "Pause",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.08f),
                        contentColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("delete_download_${entry.movieId}").size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete download",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
