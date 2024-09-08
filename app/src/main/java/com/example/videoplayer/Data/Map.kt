package com.example.videoplayer.Data

import android.webkit.JavascriptInterface
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.videoplayer.ui.Admin.OverpassApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.net.URLEncoder

//fun generateInitialOSMHtml(height: Int): String {
//    return """
//        <!DOCTYPE html>
//        <html>
//        <head>
//            <meta charset="utf-8" />
//            <meta name="viewport" content="width=device-width, initial-scale=1.0">
//            <title>OSM Map</title>
//            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
//            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
//            <script src="https://unpkg.com/leaflet-polyline/1.0.0/leaflet.polyline.js"></script>
//            <style>
//                #map { height: ${height}px; width: 100%; }
//            </style>
//        </head>
//        <body>
//            <div id="map"></div>
//            <script>
//                var map = L.map('map').setView([28.6139, 77.2090], 12); // Default to Delhi
//                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
//                    maxZoom: 18,
//                    attribution: '© OpenStreetMap'
//                }).addTo(map);
//            </script>
//        </body>
//        </html>
//    """.trimIndent()
//}
//
//fun generateOSMHtml(location: String, markers: List<Pair<Double, Double>>): String {
//    val markersScript = markers.joinToString("\n") { (lat, lon) ->
//        """
//        L.marker([$lat, $lon], {icon: customIcon}).addTo(map)
//            .bindPopup("Traffic Signal at: [$lat, $lon]")
//            .on('click', function(e) {
//                Android.setCoordinates($lat.toString(), $lon.toString());
//            });
//        """.trimIndent()
//    }
//
//    return """
//        <!DOCTYPE html>
//        <html>
//        <head>
//            <meta charset="utf-8" />
//            <meta name="viewport" content="width=device-width, initial-scale=1.0">
//            <title>OSM Map - $location</title>
//            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
//            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
//            <style>
//                #map { height: 400px; width: 100%; }
//            </style>
//        </head>
//        <body>
//            <div id="map"></div>
//            <script>
//                var map = L.map('map').setView([${markers.firstOrNull()?.first ?: 28.6139}, ${markers.firstOrNull()?.second ?: 77.2090}], 14);
//                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
//                    maxZoom: 18,
//                    attribution: '© OpenStreetMap'
//                }).addTo(map);
//
//                // Custom icon
//                var customIcon = L.icon({
//                    iconUrl: 'https://firebasestorage.googleapis.com/v0/b/gren-usar.appspot.com/o/marker-icon-2x.png?alt=media&token=43d7e0cc-b6eb-47ab-b951-f44968e15174',
//                    iconSize: [25, 41], // size of the icon
//                    iconAnchor: [12, 41], // point of the icon which will correspond to marker's location
//                    popupAnchor: [1, -34], // point from which the popup should open relative to the iconAnchor
//                    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
//                    shadowSize: [41, 41] // size of the shadow
//                });
//
//                $markersScript
//            </script>
//        </body>
//        </html>
//    """.trimIndent()
//}
//@androidx.annotation.OptIn(UnstableApi::class)
//suspend fun fetchTrafficLights(location: String): OverpassApiResponse? {
//    val coordinates = getCoordinates(location) ?: return null
//
//    val (latitude, longitude) = coordinates
//
//    // Construct the Overpass API query
//    val overpassQuery = """
//        [out:json][timeout:25];
//        (
//          node["highway"="traffic_signals"](around:3000, $latitude, $longitude);
//        );
//        out body;
//        >;
//        out skel qt;
//    """.trimIndent()
//
//    val client = OkHttpClient()
//    val url = "https://overpass-api.de/api/interpreter"
//
//    return withContext(Dispatchers.IO) {
//        try {
//            val requestBody = RequestBody.create("application/x-www-form-urlencoded".toMediaTypeOrNull(), "data=$overpassQuery")
//            val request = Request.Builder().url(url).post(requestBody).build()
//
//            val response = client.newCall(request).execute()
//            val jsonData = response.body?.string() ?: return@withContext null
//
//            // Print JSON response to Logcat
//            Log.d("TrafficLightsResponse", jsonData)
//
//            // Parse JSON data
//            Gson().fromJson(jsonData, OverpassApiResponse::class.java)
//        } catch (e: IOException) {
//            Log.e("TrafficLightsError", e.message ?: "Error fetching traffic lights")
//            null
//        }
//    }
//}
//suspend fun getCoordinates(address: String): Pair<Double, Double>? {
//    val client = OkHttpClient()
//    val url = "https://nominatim.openstreetmap.org/search?q=${URLEncoder.encode(address, "UTF-8")}&format=json&limit=1"
//
//    return withContext(Dispatchers.IO) {
//        try {
//            val request = Request.Builder().url(url).get().build()
//            val response = client.newCall(request).execute()
//            val json = response.body?.string() ?: return@withContext null
//            val results = Gson().fromJson(json, Array<NominatimResponse>::class.java)
//
//            if (results.isNotEmpty()) {
//                Pair(results[0].lat.toDouble(), results[0].lon.toDouble())
//            } else {
//                null
//            }
//        } catch (e: IOException) {
//            null
//        }
//    }
//}
//
//data class NominatimResponse(val lat: String, val lon: String)
//
//class JSInterface(private val callback: (String, String) -> Unit) {
//    @JavascriptInterface
//    fun setCoordinates(lat: String, lon: String) {
//        callback(lat, lon)
//    }
//}