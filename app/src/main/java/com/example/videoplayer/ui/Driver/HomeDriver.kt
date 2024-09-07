package com.example.videoplayer.ui.Driver

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.videoplayer.ui.Admin.JSInterface
import com.example.videoplayer.ui.Admin.generateInitialOSMHtml
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.videoplayer.R

@SuppressLint("SetJavaScriptEnabled", "MissingPermission")
@Composable
fun HomeDriver(navController: NavController, locationProvider: FusedLocationProviderClient) {
    val mapHtml by remember { mutableStateOf(generateInitialOSMHtml(1000)) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var isLocationLoaded by remember { mutableStateOf(false) }

    // Location icon URL
    val iconUrl = "https://firebasestorage.googleapis.com/v0/b/gren-usar.appspot.com/o/marker-icon-2x.png?alt=media&token=43d7e0cc-b6eb-47ab-b951-f44968e15174" // Replace with your actual icon URL

    // Fetch the user's current location
    LaunchedEffect(Unit) {
        locationProvider.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                isLocationLoaded = true // Trigger UI update when location is loaded
            }
        }
    }

    var startLocation by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    addJavascriptInterface(JSInterface { lat, lon ->
                        // Update state with the coordinates received from JavaScript if needed
                    }, "Android")
                    loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                if (isLocationLoaded) {
                    // Pass current latitude and longitude to the map when the location is ready
                    webView.evaluateJavascript(
                        """
                        (function() {
                            var map = L.map('map').setView([$latitude, $longitude], 12);
                            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                maxZoom: 18,
                                attribution: '© OpenStreetMap'
                            }).addTo(map);
                            var marker = L.marker([$latitude, $longitude], {
                                icon: L.icon({
                                    iconUrl: '${iconUrl}',
                                    iconSize: [38, 38], // Adjust the size as per your needs
                                })
                            }).addTo(map).bindPopup('You are here').openPopup();
                        })();
                        """.trimIndent(),
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Input card at the top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "Calculate Your Route",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF3498DB),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    AnimatedLocationField(
                        value = startLocation,
                        onValueChange = { startLocation = it },
                        label = "Start Location",
                        icon = Icons.Default.LocationOn
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedLocationField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = "Destination",
                        icon = Icons.Default.Place
                    )
                }
            }

            // Spacer to push the button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Calculate Route button at the bottom
            Button(
                onClick = { /* Handle route calculation */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Calculate Route", color = Color.White)
            }
        }

        // Current location icon
        IconButton(
            onClick = { /* Handle current location click */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 88.dp) // Adjust padding as needed
                .size(48.dp)
                .shadow(elevation = 4.dp, shape = CircleShape)
                .background(Color.White, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_maps_current_location_icon_2),
                contentDescription = "Current Location",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun AnimatedLocationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    var isFocused by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        targetValue = if (isFocused) Color(0xFF3498DB) else Color(0xFFBDC3C7),
        label = "color"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = animatedColor,
            unfocusedBorderColor = animatedColor,
            focusedLabelColor = animatedColor,
            unfocusedLabelColor = animatedColor,
            cursorColor = Color(0xFF3498DB)
        ),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = animatedColor
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}
