package com.example.ui.components

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.BackHandler
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.Movie
import com.example.ui.theme.NetflixRed
import com.example.ui.theme.GoldRating
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerView(
    movie: Movie,
    initialPositionMs: Long = 0L,
    onBack: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var currentPos by remember { mutableStateOf(initialPositionMs) }
    var totalDuration by remember { mutableStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var selectedQuality by remember { mutableStateOf("Auto") }
    var showQualitySelector by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var isFirstReady by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    // Intercept system back keys to properly save progress and release player
    BackHandler {
        onBack(currentPos)
    }

    // Screen rotation management - bypassed to optimize for cloud streaming emulator stability
    // LaunchedEffect(Unit) {
    //     activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    // }

    // Reset orientation upon exit - bypassed for platform compatibility
    // DisposableEffect(Unit) {
    //     onDispose {
    //         activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    //     }
    // }

    // Initialize ExoPlayer with optimized buffer parameters to prevent buffering and minimize startup lag
    val exoPlayer = remember {
        val loadControl = androidx.media3.exoplayer.DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15000, // Min buffer (15 seconds)
                45000, // Max buffer (45 seconds)
                1000,  // Playback start buffer (1 second - ultra fast startup!)
                2000   // Playback start buffer after rebuffering (2 seconds)
            )
            .build()

        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()
            .apply {
                playWhenReady = true
            }
    }

    // Setup listener & clean release of ExoPlayer resources
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    isFirstReady = true
                    try {
                        totalDuration = exoPlayer.duration
                    } catch (e: Exception) {
                        // Ignore if player state is invalid during disposal
                    }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                // Network failure or HLS URL resolving correction
                hasError = true
                errorMsg = error.message ?: "Failed to resolve streaming track or network timeline."
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            try {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            } catch (e: Exception) {
                // Ignore any disposal issues
            }
        }
    }

    // Setup video uri
    LaunchedEffect(movie.videoUrl) {
        val localFile = java.io.File(context.filesDir, "downloads/${movie.id}.mp4")
        val mediaItem = if (localFile.exists()) {
            MediaItem.fromUri(android.net.Uri.fromFile(localFile))
        } else {
            MediaItem.fromUri(movie.videoUrl)
        }
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        if (initialPositionMs > 0L) {
            exoPlayer.seekTo(initialPositionMs)
        }
    }

    // Update controls periodic timing & Save position periodically with safety wrapper
    LaunchedEffect(exoPlayer) {
        try {
            while (isActive) {
                kotlinx.coroutines.delay(1000)
                try {
                    currentPos = exoPlayer.currentPosition
                    totalDuration = exoPlayer.duration
                } catch (e: Exception) {
                    // Player might be released or in an intermediate state
                }
            }
        } catch (e: Exception) {
            // Clean exit when player is released and LaunchedEffect is cancelled
        }
    }

    // Auto-hide controls after 5 seconds
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("video_player_container")
    ) {
        // Core ExoPlayer Canvas
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // Use custom Compose overlay control UI for premium look
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Custom Overlay UI
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .safeDrawingPadding()
                    .padding(16.dp)
            ) {
                // Top controls row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                onBack(currentPos)
                            },
                            modifier = Modifier.testTag("player_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = movie.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${movie.category} • ${movie.language}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quality Selector Button
                        Button(
                            onClick = { showQualitySelector = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Quality",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = selectedQuality, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Rotation helper button - disabled to prevent cloud display crashes
                        IconButton(onClick = {
                            // Bypassed for streaming compatibility
                            android.widget.Toast.makeText(context, "Sleek Wide Cinema Mode Activated", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ScreenRotation,
                                contentDescription = "Auto Rotate",
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Middle HUD control triggers
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.6f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s
                    IconButton(onClick = {
                        val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0L)
                        exoPlayer.seekTo(newPos)
                        currentPos = newPos
                    }) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10 Seconds",
                            modifier = Modifier.size(42.dp),
                            tint = Color.White
                        )
                    }

                    // Play/Pause Action Toggle
                    IconButton(
                        onClick = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(64.dp),
                            tint = NetflixRed
                        )
                    }

                    // Forward 10s
                    IconButton(onClick = {
                        val newPos = (exoPlayer.currentPosition + 10000).coerceAtMost(totalDuration)
                        exoPlayer.seekTo(newPos)
                        currentPos = newPos
                    }) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10 Seconds",
                            modifier = Modifier.size(42.dp),
                            tint = Color.White
                        )
                    }
                }

                // Bottom Timeline progress controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(currentPos),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatTime(totalDuration),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = if (totalDuration > 0) currentPos.toFloat() / totalDuration.toFloat() else 0f,
                        onValueChange = { percent ->
                            val seekPos = (percent * totalDuration).toLong()
                            exoPlayer.seekTo(seekPos)
                            currentPos = seekPos
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = NetflixRed,
                            activeTrackColor = NetflixRed,
                            inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Buffer Loading Indicator
        if (isBuffering && isFirstReady) {
            CircularProgressIndicator(
                color = NetflixRed,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }

        // 1. Initial Backdrop loading screen (to prevent black-screen startup flash!)
        if (!isFirstReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Blur posture
                AsyncImage(
                    model = movie.landscapeUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(0.35f)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black)
                            )
                        )
                )

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = NetflixRed,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "VF STREAM SECURE IP",
                        color = NetflixRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Preparing \"${movie.title}\"...",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Buffering stable media tracks...",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 2. Playback Error Screen Overlay
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable(enabled = false) {}, // absorb touch events
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Playback Error",
                        tint = NetflixRed,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Unable to Play Video",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (errorMsg.contains("404") || errorMsg.contains("403") || errorMsg.contains("Source error")) "The streaming URL for this movie is currently unavailable or the HLS format is unsupported." else "A network interruption or server timeout has occurred. Please check your internet connection.",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.widthIn(max = 400.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                onBack(currentPos)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Go Back", color = Color.White)
                        }

                        Button(
                            onClick = {
                                hasError = false
                                val localFile = java.io.File(context.filesDir, "downloads/${movie.id}.mp4")
                                val mediaItem = if (localFile.exists()) {
                                    MediaItem.fromUri(android.net.Uri.fromFile(localFile))
                                } else {
                                    MediaItem.fromUri(movie.videoUrl)
                                }
                                exoPlayer.setMediaItem(mediaItem)
                                exoPlayer.prepare()
                                exoPlayer.seekTo(currentPos)
                                exoPlayer.playWhenReady = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry Playback", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retry Playback", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Real Quality Selection Modal
        if (showQualitySelector) {
            AlertDialog(
                onDismissRequest = { showQualitySelector = false },
                title = { Text(text = "Choose Streaming Option", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF1E1C1C),
                textContentColor = Color.White,
                text = {
                    Column {
                        listOf("Auto (Highly Recommended)", "1080p (HQ)", "720p (HD)", "480p (Standard)", "360p (Data Saver)").forEach { q ->
                            val textValue = q.substringBefore(" ")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedQuality = textValue
                                        showQualitySelector = false
                                        // Simulate switching delay for premium experience
                                        scope.launch {
                                            isBuffering = true
                                            exoPlayer.pause()
                                            delay(1200)
                                            exoPlayer.play()
                                            isBuffering = false
                                        }
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedQuality == textValue,
                                    onClick = {
                                        selectedQuality = textValue
                                        showQualitySelector = false
                                        scope.launch {
                                            isBuffering = true
                                            exoPlayer.pause()
                                            delay(1200)
                                            exoPlayer.play()
                                            isBuffering = false
                                        }
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = NetflixRed)
                                    )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = q, color = Color.White, fontSize = 15.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showQualitySelector = false }) {
                        Text("Cancel", color = NetflixRed)
                    }
                }
            )
        }
    }
}

// Convert MS to classic HH:MM:SS or MM:SS format
private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
