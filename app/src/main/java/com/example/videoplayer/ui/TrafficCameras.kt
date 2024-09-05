package com.example.videoplayer.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import kotlinx.coroutines.delay

data class CameraData(
    val id: String,
    val name: String,
    val videoUrl: String,
    val redLightDuration: Int,
    val greenLightDuration: Int,
    val yellowLightDuration: Int,
    val initialLight: String
)

@Composable
fun TrafficCamerasScreen(navController: NavController,cameras: List<CameraData>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Traffic Camera Feeds",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cameras) { camera ->
                CameraCard(camera)
            }
        }
    }
}

@Composable
fun CameraCard(camera: CameraData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = camera.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            VideoPlayer(camera.videoUrl)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrafficLight(
                    redLightDuration = camera.redLightDuration,
                    greenLightDuration = camera.greenLightDuration,
                    yellowLightDuration = camera.yellowLightDuration,
                    initialLight = camera.initialLight
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val player = remember { SimpleExoPlayer.Builder(context).build() }

    DisposableEffect(player) {
        player.setMediaItem(MediaItem.fromUri(videoUrl))
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL

        onDispose {
            player.release()
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                this.player = player
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun TrafficLight(
    redLightDuration: Int,
    greenLightDuration: Int,
    yellowLightDuration: Int,
    initialLight: String
) {
    var currentColor by remember { mutableStateOf(getColorForLight(initialLight)) }
    var countdown by remember { mutableStateOf(getDurationForLight(initialLight, redLightDuration, greenLightDuration, yellowLightDuration)) }

    LaunchedEffect(Unit) {
        while (true) {
            currentColor = Color.Red
            countdown = redLightDuration
            repeat(redLightDuration) {
                delay(1000L)
                countdown--
            }

            currentColor = Color.Green
            countdown = greenLightDuration
            repeat(greenLightDuration) {
                delay(1000L)
                countdown--
            }

            currentColor = Color.Yellow
            countdown = yellowLightDuration
            repeat(yellowLightDuration) {
                delay(1000L)
                countdown--
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Circle(color = if (currentColor == Color.Red) Color.Red else Color.Gray.copy(alpha = 0.3f))
            Circle(color = if (currentColor == Color.Yellow) Color.Yellow else Color.Gray.copy(alpha = 0.3f))
            Circle(color = if (currentColor == Color.Green) Color.Green else Color.Gray.copy(alpha = 0.3f))
        }

        Text(
            text = countdown.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = currentColor
        )
    }
}

@Composable
fun Circle(color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(color, CircleShape)
    )
}

// Helper functions remain the same
fun getColorForLight(light: String): Color {
    return when (light) {
        "Red" -> Color.Red
        "Green" -> Color.Green
        "Yellow" -> Color.Yellow
        else -> Color.Red
    }
}

fun getDurationForLight(light: String, redDuration: Int, greenDuration: Int, yellowDuration: Int): Int {
    return when (light) {
        "Red" -> redDuration
        "Green" -> greenDuration
        "Yellow" -> yellowDuration
        else -> redDuration
    }
}