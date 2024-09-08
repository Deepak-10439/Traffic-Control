package com.example.videoplayer.ui.Admin

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.videoplayer.ui.Driver.JSInterface
import com.example.videoplayer.ui.Driver.fetchTrafficLights
import com.example.videoplayer.ui.Driver.generateInitialOSMHtml
import com.example.videoplayer.ui.Driver.generateOSMHtml
//import com.example.videoplayer.Data.JSInterface
//import com.example.videoplayer.Data.fetchTrafficLights
//import com.example.videoplayer.Data.generateInitialOSMHtml
//import com.example.videoplayer.Data.generateOSMHtml
import com.example.videoplayer.ui.TrafficScreen
import kotlinx.coroutines.launch

// Root data class for the Overpass API response
data class OverpassApiResponse(
    val version: Double,
    val generator: String,
    val osm3s: Osm3s,
    val elements: List<Element>
)

// Data class for the osm3s object
data class Osm3s(
    val timestamp_osm_base: String,
    val copyright: String
)

// Data class for each element (traffic signal)
data class Element(
    val type: String,
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: Tags?
)

// Data class for the tags object
data class Tags(
    val highway: String,
    val traffic_signals: String? // Optional, as not all signals have this
)

enum class TrafficDirection {
    SELECT, NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
}

data class CameraDataLink(
    var link: String = "",
    var direction: TrafficDirection = TrafficDirection.SELECT
)


@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun InputScreen(navController: NavController) {
    var locationId by remember { mutableStateOf("") }
    var numCameras by remember { mutableStateOf("") }
    var cameraDataList by remember { mutableStateOf(List(6) { CameraDataLink() }) }
    var errorMessage by remember { mutableStateOf("") }
    var mapHtml by remember { mutableStateOf(generateInitialOSMHtml(450)) }
    var isLoading by remember { mutableStateOf(false) }
    var markerResponse by remember { mutableStateOf<OverpassApiResponse?>(null) }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Find Traffic Lights",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF2C3E50),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = locationId,
                onValueChange = { locationId = it },
                label = { Text("Enter Location") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3498DB),
                    unfocusedBorderColor = Color(0xFFBDC3C7),
                    focusedLabelColor = Color(0xFF3498DB),
                    unfocusedLabelColor = Color(0xFF7F8C8D),
                    cursorColor = Color(0xFF3498DB)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = ""
                        try {
                            val result = fetchTrafficLights(locationId)
                            if (result != null) {
                                markerResponse = result
                                mapHtml = generateOSMHtml(locationId, result.elements.map { Pair(it.lat, it.lon) })
                            } else {
                                errorMessage = "No traffic lights found for this location."
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                enabled = locationId.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        text = "Search",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OSM WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = WebViewClient()
                        addJavascriptInterface(JSInterface { lat, lon ->
                            latitude = lat
                            longitude = lon
                        }, "Android")
                        loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (markerResponse != null) {
            item {
               // Latitude and Longitude TextFields
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3498DB),
                        unfocusedBorderColor = Color(0xFFBDC3C7),
                        focusedLabelColor = Color(0xFF3498DB),
                        unfocusedLabelColor = Color(0xFF7F8C8D),
                        cursorColor = Color(0xFF3498DB)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3498DB),
                        unfocusedBorderColor = Color(0xFFBDC3C7),
                        focusedLabelColor = Color(0xFF3498DB),
                        unfocusedLabelColor = Color(0xFF7F8C8D),
                        cursorColor = Color(0xFF3498DB)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Display the number of camera field and submit new data field
                OutlinedTextField(
                    value = numCameras,
                    onValueChange = { value ->
                        numCameras = value
                        val num = value.toIntOrNull() ?: 0
                        when {
                            num < 3 || num > 6 -> {
                                errorMessage = "Only values from 3 to 6 are permitted"
                            }
                            else -> {
                                errorMessage = ""
                                cameraDataList = List(num) { index -> cameraDataList.getOrElse(index) { CameraDataLink() } }
                            }
                        }
                    },
                    label = { Text("Number of Cameras") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3498DB),
                        unfocusedBorderColor = Color(0xFFBDC3C7),
                        focusedLabelColor = Color(0xFF3498DB),
                        unfocusedLabelColor = Color(0xFF7F8C8D),
                        cursorColor = Color(0xFF3498DB)
                    )
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            if (numCameras.toIntOrNull() in 3..6) {
                items(numCameras.toInt()) { index ->
                    CameraInputGroup(
                        index = index,
                        cameraDatalink = cameraDataList[index],
                        onCameraDataChange = { updatedCameraData ->
                            cameraDataList = cameraDataList.toMutableList().also { it[index] = updatedCameraData }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "New data submitted successfully", Toast.LENGTH_SHORT).show()
                        locationId = ""
                        numCameras = ""
                        cameraDataList = List(6) { CameraDataLink() }
                        mapHtml = generateInitialOSMHtml(450)
                        navController.navigate(TrafficScreen.HomeScreen.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                    enabled = numCameras.toIntOrNull() in 3..6 &&
                            locationId.isNotEmpty() &&
                            cameraDataList.take(numCameras.toInt()).all {
                                it.link.isNotEmpty() && it.direction != TrafficDirection.SELECT
                            }
                ) {
                    Text(
                        text = "Submit New Data",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraInputGroup(
    index: Int,
    cameraDatalink: CameraDataLink,
    onCameraDataChange: (CameraDataLink) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Camera ${index + 1}",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2C3E50),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = cameraDatalink.link,
                onValueChange = { onCameraDataChange(cameraDatalink.copy(link = it)) },
                label = { Text("Camera Link") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3498DB),
                    unfocusedBorderColor = Color(0xFFBDC3C7),
                    focusedLabelColor = Color(0xFF3498DB),
                    unfocusedLabelColor = Color(0xFF7F8C8D),
                    cursorColor = Color(0xFF3498DB)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = cameraDatalink.direction.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Traffic Direction") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3498DB),
                        unfocusedBorderColor = Color(0xFFBDC3C7),
                        focusedLabelColor = Color(0xFF3498DB),
                        unfocusedLabelColor = Color(0xFF7F8C8D),
                        cursorColor = Color(0xFF3498DB)
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TrafficDirection.entries.forEach { direction ->
                        DropdownMenuItem(
                            text = { Text(direction.name) },
                            onClick = {
                                expanded = false
                                onCameraDataChange(cameraDatalink.copy(direction = direction))
                            }
                        )
                    }
                }
            }
        }
    }
}