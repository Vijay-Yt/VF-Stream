package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Movie
import com.example.ui.components.VideoPlayerView
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NetflixRed
import com.example.ui.viewmodel.MainViewModel

import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mandatory Edge-to-Edge display
        
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val isLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
                var showSplash by remember { mutableStateOf(true) }
                
                LaunchedEffect(Unit) {
                    delay(2500) // Cinematic brand intro
                    showSplash = false
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    if (showSplash) {
                        SplashScreen()
                    } else if (!isLoggedIn) {
                        AuthScreen(
                            viewModel = viewModel,
                            onAuthSuccess = {
                                // Handled internally by MainViewModel state flow updates
                            }
                        )
                    } else {
                        MainAppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

enum class SearchTab {
    HOME, SEARCH, DOWNLOADS, PROFILE
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainAppContent(viewModel: MainViewModel) {
    var activeTab by remember { mutableStateOf(SearchTab.HOME) }
    var detailMovie by remember { mutableStateOf<Movie?>(null) }
    var playbackMovie by remember { mutableStateOf<Movie?>(null) }
    var showAdminPanel by remember { mutableStateOf(false) }
    val isOnline by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()
    
    // Resume position coordinator helper
    var currentSeekResumeMs by remember { mutableStateOf(0L) }
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Core View Switcher
        Scaffold(
            bottomBar = {
                // Hide bottom navigation completely when streaming full screen
                if (playbackMovie == null) {
                    NavigationBar(
                        containerColor = DarkSurface.copy(alpha = 0.95f),
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .testTag("app_bottom_navigation")
                            .windowInsetsPadding(WindowInsets.navigationBars) // Safe gestures padding
                    ) {
                        NavigationBarItem(
                            selected = activeTab == SearchTab.HOME,
                            onClick = { activeTab = SearchTab.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = NetflixRed
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == SearchTab.SEARCH,
                            onClick = { activeTab = SearchTab.SEARCH },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = NetflixRed
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == SearchTab.DOWNLOADS,
                            onClick = { activeTab = SearchTab.DOWNLOADS },
                            icon = { Icon(Icons.Default.ArrowDownward, contentDescription = "Downloads") },
                            label = { Text("Downloads", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = NetflixRed
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == SearchTab.PROFILE,
                            onClick = { activeTab = SearchTab.PROFILE },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = NetflixRed
                            )
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (!isOnline) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE50914).copy(alpha = 0.95f))
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Offline Mode",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Offline Mode Active • Streaming Offline Downloads",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Mount Screen matching tab click
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        modifier = Modifier.weight(1f),
                        label = "tab_transitions"
                    ) { state ->
                    when (state) {
                        SearchTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToDetails = { movie -> detailMovie = movie },
                            onNavigateToPlayer = { movie, resumePositionMs ->
                                // Get watch history position if opening from continue watching
                                currentSeekResumeMs = resumePositionMs
                                playbackMovie = movie
                            }
                        )
                        SearchTab.SEARCH -> SearchScreen(
                            viewModel = viewModel,
                            onNavigateToDetails = { movie -> detailMovie = movie }
                        )
                        SearchTab.DOWNLOADS -> DownloadsScreen(
                            viewModel = viewModel,
                            onNavigateToPlayer = { movie ->
                                currentSeekResumeMs = 0L
                                playbackMovie = movie
                            }
                        )
                        SearchTab.PROFILE -> ProfileScreen(
                            viewModel = viewModel,
                            onNavigateToDetails = { movie -> detailMovie = movie },
                            onAdminClick = { showAdminPanel = true }
                        )
                    }
                }
                }
            }
        }

        // Overlay: Movie Details Screen
        detailMovie?.let { movie ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
            ) {
                MovieDetailsScreen(
                    viewModel = viewModel,
                    movie = movie,
                    onBack = { detailMovie = null },
                    onStreamClick = { selectedMovie ->
                        // Query previous playback position if any from database
                        val existingHistory = watchHistory.find { it.movieId == selectedMovie.id }
                        currentSeekResumeMs = existingHistory?.lastPositionMs ?: 0L
                        playbackMovie = selectedMovie
                    }
                )
            }
        }
        
        // Overlay: Admin Dashboard
        if (showAdminPanel) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
            ) {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onBack = { showAdminPanel = false }
                )
            }
        }

        // Overlay: Immersive Fullscreen ExoPlayer Layout Stream
        playbackMovie?.let { movie ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                VideoPlayerView(
                    movie = movie,
                    initialPositionMs = currentSeekResumeMs,
                    onBack = { finalSavedPosSettle ->
                        // Save play state persistently so it triggers the Continue Watching slot on Home
                        viewModel.updateMovieWatchPlayback(
                            movie = movie,
                            positionMs = finalSavedPosSettle,
                            durationMs = if (movie.category == "Web Series") 10L * 60L * 1000L else 2L * 60L * 60L * 1000L // mock duration coordinate
                        )
                        playbackMovie = null
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
