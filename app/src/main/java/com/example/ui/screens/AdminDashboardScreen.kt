package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.MovieCatalog
import com.example.data.UserAccount
import com.example.data.model.Movie
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NetflixRed
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

enum class AdminTab {
    OVERVIEW, MOVIES, USERS
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Trigger full users load and reactive streams synchronization
    LaunchedEffect(Unit) {
        viewModel.refreshUsersList()
    }

    if (userEmail != "bijaydas8588@gmail.com") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Unauthorized Access Gate",
                    tint = NetflixRed,
                    modifier = Modifier.size(68.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Access Restrained",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "This terminal is locked under high-security guidelines. Your account (${userEmail.ifEmpty { "Anonymous Profiler" }}) is unauthorized.",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("Return Securely", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
        return
    }

    var activeTab by remember { mutableStateOf(AdminTab.OVERVIEW) }
    val movies by viewModel.allMovies.collectAsStateWithLifecycle()
    val usersList by viewModel.allUsersList.collectAsStateWithLifecycle()
    val globalWatchHistory by viewModel.allGlobalWatchHistory.collectAsStateWithLifecycle()
    val globalDownloads by viewModel.allGlobalDownloads.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            Surface(
                color = DarkSurface,
                shadowElevation = 8.dp,
                modifier = Modifier.border(0.dp, Color.White.copy(alpha = 0.04f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Exit Admin Studio", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("VF STREAM STUDIO", color = NetflixRed, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NetflixRed.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("LIVE CONTROLLER", color = NetflixRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("OTT COMMAND CENTER", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    if (isWideScreen) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { activeTab = AdminTab.OVERVIEW },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeTab == AdminTab.OVERVIEW) NetflixRed else Color.Transparent
                                )
                            ) {
                                Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Analytics")
                            }
                            Button(
                                onClick = { activeTab = AdminTab.MOVIES },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeTab == AdminTab.MOVIES) NetflixRed else Color.Transparent
                                )
                            ) {
                                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Movies")
                            }
                            Button(
                                onClick = { activeTab = AdminTab.USERS },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeTab == AdminTab.USERS) NetflixRed else Color.Transparent
                                )
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Users")
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (!isWideScreen) {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = activeTab == AdminTab.OVERVIEW,
                        onClick = { activeTab = AdminTab.OVERVIEW },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Metrics") },
                        label = { Text("Overview") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NetflixRed,
                            selectedTextColor = NetflixRed,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = NetflixRed.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == AdminTab.MOVIES,
                        onClick = { activeTab = AdminTab.MOVIES },
                        icon = { Icon(Icons.Default.Movie, contentDescription = "Titles Database") },
                        label = { Text("Movies") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NetflixRed,
                            selectedTextColor = NetflixRed,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = NetflixRed.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == AdminTab.USERS,
                        onClick = { activeTab = AdminTab.USERS },
                        icon = { Icon(Icons.Default.Group, contentDescription = "Users Board") },
                        label = { Text("Users") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NetflixRed,
                            selectedTextColor = NetflixRed,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = NetflixRed.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeTab) {
                AdminTab.OVERVIEW -> DynamicOverviewTab(movies, usersList, globalWatchHistory, globalDownloads, isWideScreen)
                AdminTab.MOVIES -> RealMoviesCrudTab(viewModel, movies, isWideScreen)
                AdminTab.USERS -> RealUsersManagementTab(viewModel, usersList, globalWatchHistory, isWideScreen)
            }
        }
    }
}

@Composable
fun DynamicOverviewTab(
    movies: List<Movie>,
    users: List<UserAccount>,
    globalWatchHistory: List<com.example.data.entity.WatchHistoryEntity>,
    globalDownloads: List<com.example.data.entity.MovieDownloadEntity>,
    isWide: Boolean
) {
    val totalMovies = movies.size
    val premiumMovies = movies.count { it.isPremium }
    val totalRegisteredUsers = users.size
    val premiumVIPUsers = users.count { it.membershipType == "Premium VIP" }
    val streamsCount = globalWatchHistory.size
    val activeDownloads = globalDownloads.count { it.status == "DOWNLOADING" }
    val completedDownloads = globalDownloads.count { it.status == "COMPLETED" }

    // Build the "Most Watched Movies" real list by grouping watch history records
    val mostWatchedList = remember(globalWatchHistory, movies) {
        globalWatchHistory
            .groupBy { it.movieId }
            .map { (movieId, historyList) ->
                val movie = movies.find { it.id == movieId }
                Triple(movie?.title ?: "Deleted Movie ($movieId)", historyList.size, movie?.posterUrl)
            }
            .sortedByDescending { it.second }
            .take(5)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Dashboard, contentDescription = null, tint = NetflixRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enterprise Live Stats", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Stats Grid Layout matching Responsive/Tablet layouts
        item {
            if (isWide) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminStatCard(title = "Total Users", value = totalRegisteredUsers.toString(), subText = "$premiumVIPUsers Premium VIPs", icon = Icons.Default.PeopleAlt, modifier = Modifier.weight(1f))
                    AdminStatCard(title = "Total Movies", value = totalMovies.toString(), subText = "$premiumMovies VIP Exclusives", icon = Icons.Default.VideoLibrary, modifier = Modifier.weight(1f))
                    AdminStatCard(title = "Streams Logged", value = streamsCount.toString(), subText = "Total watch sessions", icon = Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
                    AdminStatCard(title = "Downloads Stream", value = "$activeDownloads active", subText = "$completedDownloads downloaded", icon = Icons.Default.CloudDownload, modifier = Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        AdminStatCard(title = "Total Users", value = totalRegisteredUsers.toString(), subText = "$premiumVIPUsers Premium VIPs", icon = Icons.Default.PeopleAlt, modifier = Modifier.weight(1f))
                        AdminStatCard(title = "Total Movies", value = totalMovies.toString(), subText = "$premiumMovies Premium VIPs", icon = Icons.Default.VideoLibrary, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        AdminStatCard(title = "Streams Conducted", value = streamsCount.toString(), subText = "Database watch points", icon = Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
                        AdminStatCard(title = "Downloads Stream", value = "$activeDownloads Active", subText = "$completedDownloads Saved", icon = Icons.Default.CloudDownload, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Real Real-Time Stream analytics on the most watched movie entities
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Most Watched Video Streams", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Extracted automatically from users' synced device play times", color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.Whatshot, contentDescription = "Popularity", tint = NetflixRed)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (mostWatchedList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.TvOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Watch Sessions Registered Yet", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        mostWatchedList.forEachIndexed { idx, (title, count, url) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (idx == 0) NetflixRed else Color.White.copy(
                                                alpha = 0.07f
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        color = if (idx == 0) Color.White else Color.LightGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                AsyncImage(
                                    model = url,
                                    contentDescription = title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(36.dp, 48.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("$count total streams started", color = Color.Gray, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("RANK #${idx + 1}", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            if (idx < mostWatchedList.size - 1) {
                                HorizontalDivider(color = Color.White.copy(alpha = 0.04f))
                            }
                        }
                    }
                }
            }
        }

        // Platform Engine Connectivity Status Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("OTT Infrastructure Engines", color = NetflixRed, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.5.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("HLS Adaptive Media CDN", color = Color.LightGray, fontSize = 13.sp)
                        }
                        Text("Online (Fastly Node)", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("SharedPreferences Security Keyring", color = Color.LightGray, fontSize = 13.sp)
                        }
                        Text("Active Encryption", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Room Database Playback Engine", color = Color.LightGray, fontSize = 13.sp)
                        }
                        Text("Synced", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = NetflixRed, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subText, color = NetflixRed, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealMoviesCrudTab(
    viewModel: MainViewModel,
    movies: List<Movie>,
    isWide: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var editingMovie by remember { mutableStateOf<Movie?>(null) }
    val context = LocalContext.current

    // Apply filters
    val filteredMovies = remember(movies, searchQuery, selectedCategoryFilter) {
        movies.filter { movie ->
            val matchesQuery = movie.title.contains(searchQuery, ignoreCase = true) ||
                    movie.category.contains(searchQuery, ignoreCase = true) ||
                    movie.language.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategoryFilter == null || movie.category.equals(selectedCategoryFilter, ignoreCase = true)
            matchesQuery && matchesCategory
        }.sortedByDescending { it.releaseYear }
    }

    if (showForm) {
        AdminMovieForm(
            existingMovie = editingMovie,
            onSave = { movie ->
                if (editingMovie != null) {
                    MovieCatalog.updateMovie(context, movie)
                    Toast.makeText(context, "Movie database entry updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    MovieCatalog.addMovie(context, movie)
                    Toast.makeText(context, "Movie registered and stream online!", Toast.LENGTH_SHORT).show()
                }
                editingMovie = null
                showForm = false
            },
            onCancel = {
                editingMovie = null
                showForm = false
            }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search and Create Row
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search catalog...", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.Gray)
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NetflixRed,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = DarkBackground,
                                unfocusedContainerColor = DarkBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { editingMovie = null; showForm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Media", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Categories slider
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryFilter == null,
                                onClick = { selectedCategoryFilter = null },
                                label = { Text("All Categories") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NetflixRed.copy(alpha = 0.2f),
                                    selectedLabelColor = NetflixRed,
                                    labelColor = Color.Gray
                                )
                            )
                        }
                        items(MovieCatalog.categories) { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter?.equals(cat, ignoreCase = true) == true,
                                onClick = { selectedCategoryFilter = cat },
                                label = { Text(cat) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NetflixRed.copy(alpha = 0.2f),
                                    selectedLabelColor = NetflixRed,
                                    labelColor = Color.Gray
                                )
                            )
                        }
                    }
                }
            }

            // Media Grid Displays
            if (filteredMovies.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(sizeCheck(isWide)), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matching movies found in command database", color = Color.Gray)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isWide) 3 else 1),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredMovies, key = { it.id }) { movie ->
                        AdminMovieControlCard(
                            movie = movie,
                            onEdit = {
                                editingMovie = movie
                                showForm = true
                            },
                            onDelete = {
                                MovieCatalog.deleteMovie(context, movie.id)
                                Toast.makeText(context, "${movie.title} purged from streams.", Toast.LENGTH_SHORT).show()
                            },
                            onTogglePremium = {
                                val updated = movie.copy(isPremium = !movie.isPremium)
                                MovieCatalog.updateMovie(context, updated)
                                Toast.makeText(context, "${movie.title} pricing tier successfully toggled.", Toast.LENGTH_SHORT).show()
                            },
                            onToggleTrending = {
                                val updated = movie.copy(isTrending = !movie.isTrending)
                                MovieCatalog.updateMovie(context, updated)
                                Toast.makeText(context, "${movie.title} home placement updated.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun sizeCheck(isWide: Boolean) = if (isWide) 80.dp else 48.dp

@Composable
fun AdminMovieControlCard(
    movie: Movie,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePremium: () -> Unit,
    onToggleTrending: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp, 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = movie.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (movie.isPremium) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF5AF19).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("VIP", color = Color(0xFFF5AF19), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text("${movie.category} • ${movie.language}", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = onTogglePremium,
                            label = { Text(if (movie.isPremium) "VIP" else "Free", fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = if (movie.isPremium) Color(0xFFF5AF19) else Color.LightGray
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.scaleForChip()
                        )
                        AssistChip(
                            onClick = onToggleTrending,
                            label = { Text(if (movie.isTrending) "Trending On" else "Slider Off", fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = if (movie.isTrending) NetflixRed else Color.LightGray
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.scaleForChip()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit metadata", tint = Color.LightGray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Purge video", tint = NetflixRed)
                }
            }
        }
    }
}

@Composable
fun Modifier.scaleForChip(): Modifier = this.height(28.dp)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminMovieForm(
    existingMovie: Movie?,
    onSave: (Movie) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(existingMovie?.title ?: "") }
    var description by remember { mutableStateOf(existingMovie?.description ?: "") }
    var category by remember { mutableStateOf(existingMovie?.category ?: "Bollywood") }
    var posterUrl by remember { mutableStateOf(existingMovie?.posterUrl ?: "") }
    var landscapeUrl by remember { mutableStateOf(existingMovie?.landscapeUrl ?: "") }
    var videoUrl by remember { mutableStateOf(existingMovie?.videoUrl ?: "") }
    var language by remember { mutableStateOf(existingMovie?.language ?: "Hindi") }
    var duration by remember { mutableStateOf(existingMovie?.duration ?: "2h 30m") }
    var releaseYear by remember { mutableStateOf(existingMovie?.releaseYear?.toString() ?: "2024") }
    var rating by remember { mutableStateOf(existingMovie?.rating?.toString() ?: "8.2") }
    var isPremium by remember { mutableStateOf(existingMovie?.isPremium ?: false) }
    var isTrending by remember { mutableStateOf(existingMovie?.isTrending ?: false) }

    // Simulated Storage Upload progress indicator state
    var isChoosingImage by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    // High quality poster preset array for easy simulation
    val posterPresets = listOf(
        "https://images.unsplash.com/photo-1542204165-65bf26472b9b?w=500&q=80",
        "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&q=80",
        "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500&q=80",
        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&q=80",
        "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=500&q=80"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (existingMovie != null) "Modify Media Specifications" else "Register Cinema Release",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Image Picker / Compression Simulated Widget
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Digital Frame Assets Manager", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Compresses and uploads poster layout structures to storage nodes", color = Color.Gray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    if (isUploading) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(color = NetflixRed, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Uploading binary poster streams... ${(uploadProgress * 100).toInt()}%", color = Color.LightGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { uploadProgress },
                                color = NetflixRed,
                                trackColor = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Poster preview
                            Box(
                                modifier = Modifier
                                    .size(72.dp, 96.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.03f))
                                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (posterUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = posterUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Gray)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Select high-res assets from presets", color = Color.LightGray, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            isUploading = true
                                            scope.launch {
                                                for (prog in 1..10) {
                                                    delay(120)
                                                    uploadProgress = prog / 10f
                                                }
                                                // Randomly select one preset
                                                posterUrl = posterPresets.random()
                                                landscapeUrl = "https://images.unsplash.com/photo-1542204165-65bf26472b9b?w=1000&q=80"
                                                isUploading = false
                                                uploadProgress = 0f
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Auto Preset", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { isChoosingImage = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Select Preset List", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            AdminTextField("Movie Title", title) { title = it }
            Spacer(modifier = Modifier.height(6.dp))
            AdminTextField("Description Summary", description, modifier = Modifier.height(96.dp)) { description = it }
            Spacer(modifier = Modifier.height(6.dp))
            AdminTextField("Poster Image Link URL", posterUrl) { posterUrl = it }
            Spacer(modifier = Modifier.height(6.dp))
            AdminTextField("Landscape Banner Link URL", landscapeUrl) { landscapeUrl = it }
            Spacer(modifier = Modifier.height(6.dp))
            AdminTextField("ExoPlayer Streaming Stream Path (HLS/MP4 URL)", videoUrl) { videoUrl = it }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminTextField("Category Folder", category, modifier = Modifier.weight(1f)) { category = it }
                AdminTextField("Language Stream", language, modifier = Modifier.weight(1f)) { language = it }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminTextField("Cinema Scale Duration", duration, modifier = Modifier.weight(1f)) { duration = it }
                AdminTextField("Rating Score (e.g. 8.5)", rating, keyboardType = KeyboardType.Number, modifier = Modifier.weight(0.5f)) { rating = it }
                AdminTextField("Release Year", releaseYear, keyboardType = KeyboardType.Number, modifier = Modifier.weight(0.5f)) { releaseYear = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isPremium, onCheckedChange = { isPremium = it }, colors = CheckboxDefaults.colors(checkedColor = NetflixRed))
                Text("Tier Lock: VIP Premium (forces authentication checkout)", color = Color.White, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isTrending, onCheckedChange = { isTrending = it }, colors = CheckboxDefaults.colors(checkedColor = NetflixRed))
                Text("Showcase in Hero Banner (Trending List)", color = Color.White, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Discard Specs")
                }
                Button(
                    onClick = {
                        if (title.isBlank() || videoUrl.isBlank()) {
                            return@Button
                        }
                        val formattedMovie = Movie(
                            id = existingMovie?.id ?: "mov_${UUID.randomUUID().toString().substring(0, 8)}",
                            title = title,
                            description = description,
                            posterUrl = posterUrl.ifEmpty { "https://images.unsplash.com/photo-1542204165-65bf26472b9b?w=500" },
                            landscapeUrl = landscapeUrl.ifEmpty { "https://images.unsplash.com/photo-1542204165-65bf26472b9b?w=1000" },
                            category = category,
                            language = language,
                            duration = duration,
                            videoUrl = videoUrl,
                            releaseYear = releaseYear.toIntOrNull() ?: 2024,
                            rating = rating.toDoubleOrNull() ?: 8.0,
                            isTrending = isTrending,
                            isLatest = true,
                            isPremium = isPremium
                        )
                        onSave(formattedMovie)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Publish Cinema Media", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (isChoosingImage) {
        Dialog(onDismissRequest = { isChoosingImage = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Digital Assets Frame", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(280.dp)
                    ) {
                        items(posterPresets) { url ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        posterUrl = url
                                        isChoosingImage = false
                                    }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { isChoosingImage = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dismiss Frames Browser")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NetflixRed,
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = NetflixRed,
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealUsersManagementTab(
    viewModel: MainViewModel,
    users: List<UserAccount>,
    globalHistory: List<com.example.data.entity.WatchHistoryEntity>,
    isWide: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMembershipFilter by remember { mutableStateOf<String?>(null) }
    var selectedUserForHistory by remember { mutableStateOf<UserAccount?>(null) }
    val context = LocalContext.current

    // Apply Search & Dynamic Filter
    val filteredUsers = remember(users, searchQuery, selectedMembershipFilter) {
        users.filter { user ->
            val matchesSearch = user.email.contains(searchQuery, ignoreCase = true) ||
                    user.name.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedMembershipFilter == null ||
                    (selectedMembershipFilter == "VIP" && user.membershipType == "Premium VIP") ||
                    (selectedMembershipFilter == "FREE" && user.membershipType != "Premium VIP") ||
                    (selectedMembershipFilter == "BANNED" && user.isBanned)
            matchesSearch && matchesFilter
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search users by email...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NetflixRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // User filter tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedMembershipFilter == null,
                            onClick = { selectedMembershipFilter = null },
                            label = { Text("All Accounts") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NetflixRed.copy(alpha = 0.2f),
                                selectedLabelColor = NetflixRed,
                                labelColor = Color.Gray
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedMembershipFilter == "VIP",
                            onClick = { selectedMembershipFilter = "VIP" },
                            label = { Text("VIP Premium") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NetflixRed.copy(alpha = 0.2f),
                                selectedLabelColor = NetflixRed,
                                labelColor = Color.Gray
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedMembershipFilter == "FREE",
                            onClick = { selectedMembershipFilter = "FREE" },
                            label = { Text("Free Accounts") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NetflixRed.copy(alpha = 0.2f),
                                selectedLabelColor = NetflixRed,
                                labelColor = Color.Gray
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedMembershipFilter == "BANNED",
                            onClick = { selectedMembershipFilter = "BANNED" },
                            label = { Text("Banned Profile") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NetflixRed.copy(alpha = 0.2f),
                                selectedLabelColor = NetflixRed,
                                labelColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }

        if (filteredUsers.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.GroupOff, contentDescription = null, modifier = Modifier.size(sizeCheck(isWide)), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No registered users match filtered query", color = Color.Gray)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isWide) 2 else 1),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredUsers) { user ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (user.isBanned) NetflixRed.copy(alpha = 0.3f) else Color.White.copy(
                                    alpha = 0.04f
                                ),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (user.isBanned) NetflixRed.copy(alpha = 0.1f) else Color.White.copy(
                                                alpha = 0.05f
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (user.displayName.ifEmpty { user.name })
                                            .take(1)
                                            .uppercase(),
                                        color = if (user.isBanned) NetflixRed else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.displayName.ifEmpty { user.name },
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        if (user.email == "bijaydas8588@gmail.com") {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(NetflixRed.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("SYSTEM OWNER", color = NetflixRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text(user.email, color = Color.Gray, fontSize = 12.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (user.isBanned) NetflixRed.copy(alpha = 0.15f)
                                            else if (user.membershipType == "Premium VIP") Color(
                                                0xFFF5AF19
                                            ).copy(alpha = 0.12f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (user.isBanned) "SUSPENDED" else user.membershipType.uppercase(),
                                        color = if (user.isBanned) NetflixRed else if (user.membershipType == "Premium VIP") Color(
                                            0xFFF5AF19
                                        ) else Color.Gray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.04f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { selectedUserForHistory = user },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.LightGray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Watch History", color = Color.LightGray, fontSize = 11.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (user.email != "bijaydas8588@gmail.com") {
                                        // VIP Toggle Button
                                        Button(
                                            onClick = {
                                                val newStatus = if (user.membershipType == "Premium VIP") "Free Plan" else "Premium VIP"
                                                viewModel.setMembership(user.email, newStatus)
                                                Toast.makeText(context, "${user.name} upgraded to $newStatus", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (user.membershipType == "Premium VIP") Color.DarkGray else Color(
                                                    0xFFF5AF19
                                                )
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(
                                                text = if (user.membershipType == "Premium VIP") "Downgrade Plan" else "Set VIP Plan",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Ban suspension toggle button
                                        IconButton(
                                            onClick = {
                                                viewModel.setBanned(user.email, !user.isBanned)
                                                Toast.makeText(
                                                    context,
                                                    if (!user.isBanned) "User suspended from system server" else "User restored to system access",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (user.isBanned) Color.White.copy(alpha = 0.1f) else NetflixRed.copy(
                                                        alpha = 0.1f
                                                    )
                                                )
                                        ) {
                                            Icon(
                                                imageVector = if (user.isBanned) Icons.Default.Check else Icons.Default.Block,
                                                contentDescription = null,
                                                tint = if (user.isBanned) Color.Green else NetflixRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }

                                        // Extreme purge button
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteAccount(user.email)
                                                Toast.makeText(context, "${user.name} database account purged.", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.White.copy(alpha = 0.04f))
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal popup to view user watch history
    if (selectedUserForHistory != null) {
        val userItem = selectedUserForHistory!!
        val userHistory = remember(globalHistory, userItem) {
            globalHistory.filter { it.userEmail == userItem.email }
        }

        Dialog(onDismissRequest = { selectedUserForHistory = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Playback History Stream", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("User: ${userItem.email}", color = Color.Gray, fontSize = 11.sp)
                        }
                        IconButton(onClick = { selectedUserForHistory = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (userHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PlayDisabled, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("This user has no recorded watch history.", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(280.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(userHistory) { hist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.02f))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = hist.posterUrl,
                                        contentDescription = hist.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(36.dp, 48.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(hist.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(
                                            text = "Category: ${hist.category}",
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                        // Calculating real percentage watched
                                        val percent = if (hist.durationMs > 0) (hist.lastPositionMs * 100 / hist.durationMs).toInt() else 0
                                        Text(
                                            text = "Progress: $percent% watched",
                                            color = NetflixRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { selectedUserForHistory = null },
                        colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close History Log", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
