package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.entity.MovieDownloadEntity
import com.example.data.entity.WatchHistoryEntity
import com.example.data.model.Movie
import com.example.ui.components.UserAvatar
import com.example.ui.components.getAvatarName
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NetflixRed
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToDetails: (Movie) -> Unit,
    onAdminClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val displayName by viewModel.displayName.collectAsStateWithLifecycle()
    val username by viewModel.currentUserName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val membershipType by viewModel.membershipType.collectAsStateWithLifecycle()
    val avatarIndex by viewModel.avatarIndex.collectAsStateWithLifecycle()
    
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val downloads by viewModel.activeDownloads.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }
    
    // Settings sections state trackers
    var activeSection by remember { mutableStateOf<String?>(null) }

    var adminTaps by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("profile_screen_container")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            // 1. BRANDED TITLE BAR
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Vijay Film Studio",
                        color = NetflixRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "My Profile & Settings",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // 2. AVATAR & BASIC DETAILS
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        UserAvatar(
                            index = avatarIndex,
                            size = 100.dp,
                            borderWidth = 3.dp,
                            modifier = Modifier.clickable { showEditDialog = true }
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NetflixRed)
                                .border(2.dp, DarkBackground, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(20.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile Info",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = displayName.ifEmpty { "VF Cinephile" },
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@$username • $userEmail",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // 3. MEMBERSHIP & BILLING BILLBOARD
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (membershipType == "Premium VIP") {
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF232526), Color(0xFF414345))
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(DarkSurface, Color(0xFF221111))
                                )
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (membershipType == "Premium VIP") Color(0xFFF5AF19).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (membershipType == "Premium VIP") Icons.Default.WorkspacePremium else Icons.Default.CardMembership,
                                    contentDescription = "Membership",
                                    tint = if (membershipType == "Premium VIP") Color(0xFFF5AF19) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (membershipType == "Premium VIP") "Premium VIP Access" else "Free Account Pass",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            if (membershipType == "Premium VIP") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF5AF19).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "ACTIVE",
                                        color = Color(0xFFF5AF19),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (membershipType == "Premium VIP") {
                                "Thank you for supporting Vijay Film Studio! You have unlimited HLS streaming and offline video downloads unlocked in 4K Ultra HDR."
                            } else {
                                "Premium releases of Vijay Film Studio are locked under VIP. Upgrade now to liftoff all streaming and download boundaries."
                            },
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        if (membershipType != "Premium VIP") {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    viewModel.upgradeToPremium()
                                    Toast.makeText(context, "VIP Pass Subscribed!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Star, contentDescription = "Upgrade")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upgrade to VIP (₹149/mo)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // 4. WATCH HISTORY MINI-ROW
            if (watchHistory.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                    ) {
                        Text(
                            text = "Recently Watched",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(watchHistory) { history ->
                                val correspondingMovie = viewModel.allMovies.value.find { it.id == history.movieId }
                                Box(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurface)
                                        .clickable {
                                            if (correspondingMovie != null) {
                                                onNavigateToDetails(correspondingMovie)
                                            }
                                        }
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = history.posterUrl,
                                            contentDescription = history.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                        )
                                        Text(
                                            text = history.title,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. COMPLETED DOWNLOADS QUICK LIST
            if (downloads.any { it.status == "COMPLETED" }) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "My Saved Downloads",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(downloads.filter { it.status == "COMPLETED" }) { dl ->
                                val correspondingMovie = viewModel.allMovies.value.find { it.id == dl.movieId }
                                Box(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurface)
                                        .clickable {
                                            if (correspondingMovie != null) {
                                                onNavigateToDetails(correspondingMovie)
                                            }
                                        }
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = dl.posterUrl,
                                            contentDescription = dl.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                        )
                                        Text(
                                            text = dl.title,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. DETAILED SETTINGS SECTION (COLLAPSIBLE PANELS)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Vijay Film Studio Settings",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Card Settings Groups
                    CollapsibleSettingsCard(
                        title = "About Vijay Film Studio",
                        icon = Icons.Default.MovieFilter,
                        expanded = activeSection == "about",
                        onToggle = { activeSection = if (activeSection == "about") null else "about" }
                    ) {
                        Text(
                            text = "Vijay Film Studio is a legendary film studio and streaming service dedicated to curated Indian & international blockbusters. Under leadership of visionary OTT design frameworks, we provide lossless HLS mobile video streams and full offline downloads with real-time playback resume databases.",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }

                    CollapsibleSettingsCard(
                        title = "Privacy Policy",
                        icon = Icons.Default.Security,
                        expanded = activeSection == "privacy",
                        onToggle = { activeSection = if (activeSection == "privacy") null else "privacy" }
                    ) {
                        Text(
                            text = "At VF Stream, your security bounds are treated with pure dedication. All user profiles, private password buffers, and playback database tracks of watch history are stored entirely locally within Android's sandboxed hardware memory storage. We transmit zero analytical profiles to foreign cloud services.",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }

                    CollapsibleSettingsCard(
                        title = "Terms & Conditions",
                        icon = Icons.Default.Gavel,
                        expanded = activeSection == "terms",
                        onToggle = { activeSection = if (activeSection == "terms") null else "terms" }
                    ) {
                        Text(
                            text = "By accessing titles playing on VF Stream, you agree to strictly personal non-commercial screening. Playback encryption and downloaded video keys are tied to your device ID, and redistributions of physical stream packets are strictly illegal.",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }

                    CollapsibleSettingsCard(
                        title = "Contact VIP Support",
                        icon = Icons.Default.Headphones,
                        expanded = activeSection == "contact",
                        onToggle = { activeSection = if (activeSection == "contact") null else "contact" }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Email: VijayOfficialYouTube@gmail.com", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Tollfree Support Line: +91 99999-VFSTREAM", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Availability: 24/7 dedicated cinema support engineers.", color = Color.LightGray, fontSize = 12.sp)
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val emailIntent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                            data = android.net.Uri.parse("mailto:VijayOfficialYouTube@gmail.com")
                                            putExtra(android.content.Intent.EXTRA_SUBJECT, "VF Stream VIP Support Request")
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Hello Cinema Support Team,\n\nAccount Email: $userEmail\nPlan Status: $membershipType\n\nI need assistance with:")
                                        }
                                        try {
                                            context.startActivity(android.content.Intent.createChooser(emailIntent, "Send Email Support"))
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "No email app found.", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = "Email VIP Team", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Email", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                Button(
                                    onClick = {
                                        val dialIntent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                            data = android.net.Uri.parse("tel:+919999983787")
                                        }
                                        try {
                                            context.startActivity(dialIntent)
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "Dialer unavailable.", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call VIP Team", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call Helpline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Static App Version Card (Hidden door to secure Admin Panel)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(10.dp))
                            .clickable {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastTapTime < 1000) {
                                    adminTaps++
                                } else {
                                    adminTaps = 1
                                }
                                lastTapTime = currentTime
                                if (adminTaps >= 5) {
                                    adminTaps = 0
                                    // Check admin authentication secure requirements
                                    if (userEmail == "bijaydas8588@gmail.com") {
                                        Toast.makeText(context, "Access Granted. Opening Admin Dashboard...", Toast.LENGTH_SHORT).show()
                                        onAdminClick()
                                    } else {
                                        Toast.makeText(context, "Unauthorized Access", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = "Version", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("App Version", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("v2.5.0 Premium Ultra", color = NetflixRed, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // 7. SLEEK NETFLIX-RED LOGOUT ACTUATOR
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NetflixRed.copy(alpha = 0.12f),
                            contentColor = NetflixRed
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.2.dp,
                            brush = SolidColor(NetflixRed.copy(alpha = 0.4f))
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("profile_logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign Out off VF Stream",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Signed in as $userEmail",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }

    // 8. RICH EDIT PROFILE DIALOG
    if (showEditDialog) {
        var tempDisplayName by remember { mutableStateOf(displayName) }
        var tempUsername by remember { mutableStateOf(username) }
        var selectedAvatarIndex by remember { mutableStateOf(avatarIndex) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    "Customize Your Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pick Avatar Selection Subsection
                    Text(
                        "Select Custom Avatar Icon",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Grid of 6 Avatars
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (row in 0 until 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (col in 0 until 3) {
                                    val idx = row * 3 + col
                                    val isSelected = selectedAvatarIndex == idx
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) NetflixRed.copy(alpha = 0.15f) else Color.Transparent)
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) NetflixRed else Color.White.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedAvatarIndex = idx }
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            UserAvatar(index = idx, size = 44.dp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = getAvatarName(idx),
                                                color = if (isSelected) Color.White else Color.Gray,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                    // TextField display name
                    OutlinedTextField(
                        value = tempDisplayName,
                        onValueChange = { tempDisplayName = it },
                        label = { Text("Display Nickname", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NetflixRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedLabelColor = NetflixRed,
                            cursorColor = NetflixRed,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // TextField username alias
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = { tempUsername = it.trim() },
                        label = { Text("System Username", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NetflixRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedLabelColor = NetflixRed,
                            cursorColor = NetflixRed,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempDisplayName.isBlank() || tempUsername.isBlank()) {
                            Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateProfile(tempDisplayName, tempUsername, selectedAvatarIndex)
                            showEditDialog = false
                            Toast.makeText(context, "Profile details saved!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NetflixRed)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
        )
    }
}

@Composable
fun CollapsibleSettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(10.dp))
            .clickable { onToggle() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = title, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(bottom = 10.dp))
                    content()
                }
            }
        }
    }
}
