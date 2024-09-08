package com.example.videoplayer.ui.Driver

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.OptIn
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.videoplayer.R
import com.example.videoplayer.ui.Admin.OverpassApiResponse
import com.example.videoplayer.ui.Admin.TopAppBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.lang.reflect.Type
import java.io.IOException
import java.net.URLEncoder

@OptIn(UnstableApi::class)
@SuppressLint("SetJavaScriptEnabled", "MissingPermission", "JavascriptInterface",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@Composable
fun HomeDriver(navController: NavController, locationProvider: FusedLocationProviderClient) {
    val mapHtml by remember { mutableStateOf(generateInitialOSMHtml(1000)) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var isLocationLoaded by remember { mutableStateOf(false) }

    // Location icon URL
    val iconUrl = "https://firebasestorage.googleapis.com/v0/b/gren-usar.appspot.com/o/marker-icon-2x.png?alt=media&token=43d7e0cc-b6eb-47ab-b951-f44968e15174" // Replace with your actual icon URL

    //Navigation URL
    val navigationUrl = "https://osrm-service-196751621509.asia-south1.run.app/route/v1/driving/lat1,long1;lat2,long2"
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

    var routeGeometry by remember { mutableStateOf<String?>(null) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Function to calculate route
    fun calculateRoute() {
        CoroutineScope(Dispatchers.IO).launch {
            val startCoordinates = getCoordinates(startLocation)
            val destinationCoordinates = getCoordinates(destination)

            Log.d("RouteCalculation", "Start Coordinates: $startCoordinates")
            Log.d("RouteCalculation", "Destination Coordinates: $destinationCoordinates")

            if (startCoordinates != null && destinationCoordinates != null) {
                val (startLat, startLon) = startCoordinates
                val (destLat, destLon) = destinationCoordinates
                val navigationUrl = "https://osrm-service-196751621509.asia-south1.run.app/route/v1/driving/$startLon,$startLat;$destLon,$destLat?steps=true&geometries=polyline"

                try {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(navigationUrl).build()
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    Log.d("NavigationResponse", responseBody ?: "No response")

                    if (responseBody != null) {
                        val gson = provideGson()
                        val routeResponse = gson.fromJson(responseBody, ResponseData::class.java)
                        routeGeometry = routeResponse.routes.firstOrNull()?.geometry.toString()
                        
                        // Update the map with the new route
                        withContext(Dispatchers.Main) {
                            webViewInstance?.evaluateJavascript(
                                """
                                (function() {
                                    window.drawRoute('$routeGeometry');
                                })();
                                """.trimIndent(),
                                null
                            )
                        }
                    }
                } catch (e: IOException) {
                    Log.e("NavigationError", e.message ?: "Error fetching navigation data")
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(navController = navController)}
    ){
        Box(modifier = Modifier.fillMaxSize().padding(top = 70.dp)) {
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
                        webViewInstance = this
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
                                     iconSize: [38, 38],
                                 })
                             }).addTo(map).bindPopup('You are here').openPopup();
                             
                             window.map = map; // Make map accessible globally
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
                    .padding(start = 16.dp, end = 16.dp, top = 23.dp, bottom = 16.dp),
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
                    onClick = { calculateRoute() },
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

data class ResponseData(
    val code: String,
    val routes: List<Route>,
    val waypoints: List<Waypoint>
)

data class Route(
    val geometry: Any, // Change to Any to handle both String and Geometry
    val legs: List<Leg>,
    val weight_name: String,
    val weight: Double,
    val duration: Double,
    val distance: Double
)

data class Geometry(
    val coordinates: List<List<Double>>?,
    val type: String?
)

data class Leg(
    val steps: List<Step>,
    val summary: String,
    val weight: Double,
    val duration: Double,
    val distance: Double
)

data class Step(
    val geometry: Any, // Change to Any to handle both String and Geometry
    val maneuver: Maneuver,
    val mode: String,
    val driving_side: String,
    val name: String,
    val intersections: List<Intersection>,
    val weight: Double,
    val duration: Double,
    val distance: Double
)

data class Maneuver(
    val bearing_after: Int,
    val bearing_before: Int,
    val location: List<Double>,
    val modifier: String?,
    val type: String
)

data class Intersection(
    val out: Int?,
    val `in`: Int?,
    val entry: List<Boolean>,
    val bearings: List<Int>,
    val location: List<Double>,
    val classes: List<String>?
)

data class Waypoint(
    val hint: String,
    val distance: Double,
    val name: String,
    val location: List<Double>
)

class RouteAdapter : JsonDeserializer<Route> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Route {
        val jsonObject = json.asJsonObject

        val geometryElement = jsonObject.get("geometry")
        val geometry: Any = if (geometryElement.isJsonObject) {
            context.deserialize<Geometry>(geometryElement, Geometry::class.java)
        } else {
            geometryElement.asString
        }

        val legs = context.deserialize<List<Leg>>(jsonObject.get("legs"), object : TypeToken<List<Leg>>() {}.type)
        val weightName = jsonObject.get("weight_name").asString
        val weight = jsonObject.get("weight").asDouble
        val duration = jsonObject.get("duration").asDouble
        val distance = jsonObject.get("distance").asDouble

        return Route(geometry, legs, weightName, weight, duration, distance)
    }
}

class StepAdapter : JsonDeserializer<Step> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Step {
        val jsonObject = json.asJsonObject

        val geometryElement = jsonObject.get("geometry")
        val geometry: Any = if (geometryElement.isJsonObject) {
            context.deserialize<Geometry>(geometryElement, Geometry::class.java)
        } else {
            geometryElement.asString
        }

        val maneuver = context.deserialize<Maneuver>(jsonObject.get("maneuver"), Maneuver::class.java)
        val mode = jsonObject.get("mode").asString
        val drivingSide = jsonObject.get("driving_side").asString
        val name = jsonObject.get("name").asString
        val intersections = context.deserialize<List<Intersection>>(jsonObject.get("intersections"), object : TypeToken<List<Intersection>>() {}.type)
        val weight = jsonObject.get("weight").asDouble
        val duration = jsonObject.get("duration").asDouble
        val distance = jsonObject.get("distance").asDouble

        return Step(geometry, maneuver, mode, drivingSide, name, intersections, weight, duration, distance)
    }
}

fun provideGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(Route::class.java, RouteAdapter())
        .registerTypeAdapter(Step::class.java, StepAdapter())
        .create()
}
fun generateInitialOSMHtml(height: Int): String {
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
                #map { height: ${height}px; width: 100%; }
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

                // Polyline decoding function
                function decodePolyline(str, precision) {
                    var index = 0,
                        lat = 0,
                        lng = 0,
                        coordinates = [],
                        shift = 0,
                        result = 0,
                        byte = null,
                        latitude_change,
                        longitude_change,
                        factor = Math.pow(10, precision || 5);

                    while (index < str.length) {
                        byte = null;
                        shift = 0;
                        result = 0;

                        do {
                            byte = str.charCodeAt(index++) - 63;
                            result |= (byte & 0x1f) << shift;
                            shift += 5;
                        } while (byte >= 0x20);

                        latitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));
                        shift = result = 0;

                        do {
                            byte = str.charCodeAt(index++) - 63;
                            result |= (byte & 0x1f) << shift;
                            shift += 5;
                        } while (byte >= 0x20);

                        longitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));
                        lat += latitude_change;
                        lng += longitude_change;
                        coordinates.push([lat / factor, lng / factor]);
                    }

                    return coordinates;
                }

                window.drawRoute = function(encodedPolyline) {
                    if (window.routeLayer) {
                        map.removeLayer(window.routeLayer);
                    }
                    var decodedCoords = decodePolyline(encodedPolyline);
                    window.routeLayer = L.polyline(decodedCoords, {color: 'blue', weight: 5}).addTo(map);
                    map.fitBounds(window.routeLayer.getBounds());
                };
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