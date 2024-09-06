package com.example.videoplayer.ui

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.net.URLEncoder

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavController) {
    var locationId by remember { mutableStateOf("") }
    var numCameras by remember { mutableStateOf("") }
    var cameraDataList by remember { mutableStateOf(List(6) { CameraDataLink() }) }
    var errorMessage by remember { mutableStateOf("") }
    var mapHtml by remember { mutableStateOf(generateInitialOSMHtml()) }
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
                    .height(400.dp)
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
                        mapHtml = generateInitialOSMHtml()
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
                    TrafficDirection.values().forEach { direction ->
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

fun generateInitialOSMHtml(): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>OSM Map</title>
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                #map { height: 400px; width: 100%; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([28.6139, 77.2090], 12); // Default to Delhi
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                    attribution: '© OpenStreetMap'
                }).addTo(map);
            </script>
        </body>
        </html>
    """.trimIndent()
}

fun generateOSMHtml(location: String, markers: List<Pair<Double, Double>>): String {
    val markersScript = markers.joinToString("\n") { (lat, lon) ->
        """
        L.marker([$lat, $lon], {icon: customIcon}).addTo(map)
            .bindPopup("Traffic Signal at: [$lat, $lon]")
            .on('click', function(e) {
                Android.setCoordinates($lat.toString(), $lon.toString());
            });
        """.trimIndent()
    }

    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>OSM Map - $location</title>
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                #map { height: 400px; width: 100%; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([${markers.firstOrNull()?.first ?: 28.6139}, ${markers.firstOrNull()?.second ?: 77.2090}], 14);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                // Custom icon
                var customIcon = L.icon({
                    iconUrl: 'https://firebasestorage.googleapis.com/v0/b/gren-usar.appspot.com/o/marker-icon-2x.png?alt=media&token=43d7e0cc-b6eb-47ab-b951-f44968e15174',
                    iconSize: [25, 41], // size of the icon
                    iconAnchor: [12, 41], // point of the icon which will correspond to marker's location
                    popupAnchor: [1, -34], // point from which the popup should open relative to the iconAnchor
                    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
                    shadowSize: [41, 41] // size of the shadow
                });

                $markersScript
            </script>
        </body>
        </html>
    """.trimIndent()
}
@androidx.annotation.OptIn(UnstableApi::class)
suspend fun fetchTrafficLights(location: String): OverpassApiResponse? {
    val coordinates = getCoordinates(location) ?: return null

    val (latitude, longitude) = coordinates

    // Construct the Overpass API query
    val overpassQuery = """
        [out:json][timeout:25];
        (
          node["highway"="traffic_signals"](around:3000, $latitude, $longitude);
        );
        out body;
        >;
        out skel qt;
    """.trimIndent()

    val client = OkHttpClient()
    val url = "https://overpass-api.de/api/interpreter"

    return withContext(Dispatchers.IO) {
        try {
            val requestBody = RequestBody.create("application/x-www-form-urlencoded".toMediaTypeOrNull(), "data=$overpassQuery")
            val request = Request.Builder().url(url).post(requestBody).build()

            val response = client.newCall(request).execute()
            val jsonData = response.body?.string() ?: return@withContext null

            // Print JSON response to Logcat
            Log.d("TrafficLightsResponse", jsonData)

            // Parse JSON data
            Gson().fromJson(jsonData, OverpassApiResponse::class.java)
        } catch (e: IOException) {
            Log.e("TrafficLightsError", e.message ?: "Error fetching traffic lights")
            null
        }
    }
}
suspend fun getCoordinates(address: String): Pair<Double, Double>? {
    val client = OkHttpClient()
    val url = "https://nominatim.openstreetmap.org/search?q=${URLEncoder.encode(address, "UTF-8")}&format=json&limit=1"

    return withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: return@withContext null
            val results = Gson().fromJson(json, Array<NominatimResponse>::class.java)

            if (results.isNotEmpty()) {
                Pair(results[0].lat.toDouble(), results[0].lon.toDouble())
            } else {
                null
            }
        } catch (e: IOException) {
            null
        }
    }
}

data class NominatimResponse(val lat: String, val lon: String)

class JSInterface(private val callback: (String, String) -> Unit) {
    @JavascriptInterface
    fun setCoordinates(lat: String, lon: String) {
        callback(lat, lon)
    }
}