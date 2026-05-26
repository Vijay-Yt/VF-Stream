package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserAvatar(
    index: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 1.5.dp
) {
    val avatars = listOf(
        Pair("🍿", Brush.linearGradient(colors = listOf(Color(0xFFFF5F6D), Color(0xFFFFC371)))),
        Pair("👑", Brush.linearGradient(colors = listOf(Color(0xFFF12711), Color(0xFFF5AF19)))),
        Pair("👾", Brush.linearGradient(colors = listOf(Color(0xFF654ea3), Color(0xFFeaafc8)))),
        Pair("🎬", Brush.linearGradient(colors = listOf(Color(0xFF11998e), Color(0xFF38ef7d)))),
        Pair("🦸", Brush.linearGradient(colors = listOf(Color(0xFF00c6ff), Color(0xFF0072ff)))),
        Pair("🎭", Brush.linearGradient(colors = listOf(Color(0xFFFC466B), Color(0xFF3F5EFB))))
    )
    val avatar = avatars.getOrElse(index) { avatars[0] }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(avatar.second)
            .border(borderWidth, Color.White.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatar.first,
            fontSize = (size.value * 0.55f).sp
        )
    }
}

// Helper to get static label names for avatar index
fun getAvatarName(index: Int): String {
    return when (index) {
        0 -> "Movie Master"
        1 -> "VIP Streamer"
        2 -> "Tech Cinephile"
        3 -> "Action Hero"
        4 -> "Superstar"
        5 -> "Creative Critic"
        else -> "Cinephile"
    }
}
