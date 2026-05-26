package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.BackHandler
import coil.compose.AsyncImage
import com.example.data.entity.MovieDownloadEntity
import com.example.data.model.Movie
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldRating
import com.example.ui.theme.NetflixRed
import com.example.ui.theme.DownloadGreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MovieDetailsScreen(
    viewModel: MainViewModel,
    movie: Movie,
    onBack: () -> Unit,
    onStreamClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val downloads by viewModel.activeDownloads.collectAsStateWithLifecycle()
    val membershipType by viewModel.membershipType.collectAsStateWithLifecycle()
    var showSubscribeDialog by remember { mutableStateOf(false) }

    val isDownloaded = remember(downloads, movie) {
        downloads.find { it.movieId == movie.id }
    }

    // Intercept system back presses to close movie details cleanly
    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("movie_details_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 60.dp)
        ) {
            // BACKDROP HERO IMAGE PORTRAIT/LANDSCAPE WITH OVERLAY GRADIENT
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = movie.landscapeUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("details_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Smooth Gradient Overlay to cinema background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f),
                                    DarkBackground.copy(alpha = 0.9f),
                                    DarkBackground
                                )
                            )
                        )
                )
            }

            // MAIN METADATA PANEL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("movie_details_title")
                    )

                    // Small floating badge for premium
                    if (movie.isPremium) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFF12711), Color(0xFFF5AF19))
                                    )
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "VIP PREMIUM",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = GoldRating,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${movie.rating}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(text = "•", color = Color.Gray)
                    Text(text = "${movie.releaseYear}", color = Color.White, fontSize = 14.sp)
                    Text(text = "•", color = Color.Gray)
                    Text(text = movie.language, color = Color.White, fontSize = 14.sp)
                    Text(text = "•", color = Color.Gray)
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = movie.category, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Premium subscription alert banner if not VIP
                if (movie.isPremium && membershipType != "Premium VIP") {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NetflixRed.copy(alpha = 0.15f))
                            .border(1.dp, NetflixRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "VIP Locked",
                                tint = NetflixRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Premium Content Locked",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "This movie requires an active VF Stream VIP Access plan to watch or download.",
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons (Stream & Download)
                Button(
                    onClick = {
                        if (movie.isPremium && membershipType != "Premium VIP") {
                            showSubscribeDialog = true
                        } else {
                            onStreamClick(movie)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NetflixRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("details_stream_button")
                ) {
                    Icon(
                        imageVector = if (movie.isPremium && membershipType != "Premium VIP") Icons.Default.LockOpen else Icons.Default.PlayArrow,
                        contentDescription = "Stream Now"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (movie.isPremium && membershipType != "Premium VIP") "Unlock VIP with Premium" else "Stream Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Real Resumable Download Action Trigger
                    Button(
                        onClick = {
                            if (movie.isPremium && membershipType != "Premium VIP") {
                                showSubscribeDialog = true
                            } else {
                                when (isDownloaded?.status) {
                                    null -> viewModel.startMovieDownload(movie)
                                    "DOWNLOADING", "PENDING" -> viewModel.pauseMovieDownload(movie.id)
                                    "PAUSED" -> viewModel.startMovieDownload(movie)
                                    "COMPLETED" -> {
                                        android.widget.Toast.makeText(context, "Ready Offline! You can play this movie without internet.", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (isDownloaded?.status) {
                                "COMPLETED" -> DownloadGreen
                                "DOWNLOADING" -> NetflixRed.copy(alpha = 0.8f)
                                "PAUSED" -> Color.Gray
                                "PENDING" -> Color.DarkGray
                                else -> DarkSurface
                            },
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .border(
                                width = 1.dp,
                                color = if (isDownloaded?.status == "COMPLETED") Color.Transparent else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("details_download_button")
                    ) {
                        Icon(
                            imageVector = when (isDownloaded?.status) {
                                "COMPLETED" -> Icons.Default.Check
                                "DOWNLOADING" -> Icons.Default.Downloading
                                "PENDING" -> Icons.Default.HourglassEmpty
                                "PAUSED" -> Icons.Default.PlayArrow
                                else -> Icons.Default.ArrowDownward
                            },
                            contentDescription = "Download"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (isDownloaded?.status) {
                                "COMPLETED" -> "Downloaded"
                                "DOWNLOADING" -> "${isDownloaded?.progress}%"
                                "PENDING" -> "Queued..."
                                "PAUSED" -> "Resume"
                                else -> "Download"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share trigger using native Android Share Sheet
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "VF Stream Movie Recommendation")
                                putExtra(Intent.EXTRA_TEXT, "Hey! Check out this movie '${movie.title}' playing on VF Stream (Vijay Film Studio)! Category: ${movie.category}, Rating: ${movie.rating}. Let's stream it together!")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Movie Recommendation"))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurface,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("details_share_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Share", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description Title
                Text(
                    text = "Description",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Description text body
                Text(
                    text = movie.description,
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))

                // More Technical Details grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Duration", color = Color.Gray, fontSize = 12.sp)
                        Text(text = movie.duration, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                    }
                    Column {
                        Text(text = "Release Year", color = Color.Gray, fontSize = 12.sp)
                        Text(text = "${movie.releaseYear}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                    }
                    Column {
                        Text(text = "Source", color = Color.Gray, fontSize = 12.sp)
                        Text(text = "VF HLS Server", color = NetflixRed, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }

    // High fidelity custom Subscribe popup system
    if (showSubscribeDialog) {
        AlertDialog(
            onDismissRequest = { showSubscribeDialog = false },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFF12711), Color(0xFFF5AF19))
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("VF VIP ACCESS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Unlock Premium Streaming",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "The masterpiece \"${movie.title}\" is reserved exclusively for VIP members. Subscribe now to instantly lift all locks across VF Stream!",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    // High-quality bullet points
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = "VIP Option", tint = Color(0xFFF5AF19), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Full ad-free 4K Ultra HDR Streaming", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = "VIP Option", tint = Color(0xFFF5AF19), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Fast direct HLS high-speed offline access", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = "VIP Option", tint = Color(0xFFF5AF19), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Access unlimited downloads & resume playbacks", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "VIP Pass costs just ₹149/month. Cancel at any hour.",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.upgradeToPremium()
                        showSubscribeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NetflixRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = "SUBSCRIBE NOW (₹149/MO)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSubscribeDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Maybe Later", color = Color.Gray, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
        )
    }
}
