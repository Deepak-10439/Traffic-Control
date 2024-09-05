package com.example.videoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.videoplayer.ui.CameraData
import com.example.videoplayer.ui.IntersectionDetails
import com.example.videoplayer.ui.TrafficApp
import com.example.videoplayer.ui.TrafficCamerasScreen
import com.example.videoplayer.ui.VideoPlayerComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerComposeTheme {
                TrafficApp()
            }
        }
    }
}
