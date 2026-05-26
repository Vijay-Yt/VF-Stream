package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NetflixRed

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    // Elegant pulsing and scaling animations
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    var startAnimation by remember { mutableStateOf(false) }
    val entryAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutQuad),
        label = "entry_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic radial ambient red glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NetflixRed.copy(alpha = 0.25f * glowAlpha),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .align(Alignment.Center)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale)
                .alpha(entryAlpha)
        ) {
            // Elegant Canvas-drawn Brand Mark representing VF
            Canvas(
                modifier = Modifier
                    .size(120.dp)
                    .padding(16.dp)
            ) {
                val w = size.width
                val h = size.height

                // Custom VF Monogram Vector Path drawing
                val redBrush = Brush.linearGradient(
                    colors = listOf(NetflixRed, Color(0xFF8B0000))
                )

                // Draw V (left wing down to center bottom, then back up slightly)
                val pathV = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w * 0.35f, h * 0.95f)
                    lineTo(w * 0.45f, h * 0.95f)
                    lineTo(w * 0.65f, 0f)
                    lineTo(w * 0.5f, 0f)
                    lineTo(w * 0.4f, h * 0.7f)
                    lineTo(w * 0.15f, 0f)
                    close()
                }

                // Draw F (combining with the right wing)
                val pathF = Path().apply {
                    moveTo(w * 0.6f, 0f)
                    lineTo(w * 0.6f, h * 0.95f)
                    lineTo(w * 0.72f, h * 0.95f)
                    lineTo(w * 0.72f, h * 0.55f)
                    lineTo(w * 0.95f, h * 0.55f)
                    lineTo(w * 0.95f, h * 0.42f)
                    lineTo(w * 0.72f, h * 0.42f)
                    lineTo(w * 0.72f, h * 0.18f)
                    lineTo(w * 1.0f, h * 0.18f)
                    lineTo(w * 1.0f, 0f)
                    close()
                }

                drawPath(path = pathV, brush = redBrush)
                drawPath(path = pathF, brush = redBrush)

                // Central high-contrast glowing play button matching the new launcher icon
                val pathPlay = Path().apply {
                    moveTo(w * 0.42f, h * 0.41f)
                    lineTo(w * 0.56f, h * 0.50f)
                    lineTo(w * 0.42f, h * 0.59f)
                    close()
                }
                drawPath(path = pathPlay, color = Color.White)

                // Optional accent lines forming a futuristic border circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    radius = w * 0.75f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand name
            Text(
                text = "VF STREAM",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            // Brand Parent
            Text(
                text = "VIJAY FILM STUDIO",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NetflixRed,
                letterSpacing = 2.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Elegant bottom credits & spinner
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = NetflixRed,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CINEMATIC ENTERTAINMENT REIMAGINED",
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
        }
    }
}
