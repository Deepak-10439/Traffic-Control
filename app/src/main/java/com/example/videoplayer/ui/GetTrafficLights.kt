package com.example.videoplayer.ui

//import io.ktor.application.*
//import io.ktor.http.*
//import io.ktor.request.*
//import io.ktor.response.*
//import io.ktor.routing.*
//import io.ktor.server.engine.embeddedServer
//import io.ktor.server.netty.Netty
//import io.ktor.html.*
//import kotlinx.html.*
//import io.ktor.features.*
//import kotlinx.coroutines.*
//import io.ktor.jackson.jackson
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import com.fasterxml.jackson.databind.*
//import org.json.JSONObject
//import kotlinx.serialization.*
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class TrafficSignal(val lat: Double, val lon: Double)
//
//val client = HttpClient(CIO)
//
//fun main() {
//    embeddedServer(Netty, port = 8080) {
//        install(ContentNegotiation) {
//            jackson {
//                enable(SerializationFeature.INDENT_OUTPUT)
//            }
//        }
//
//        routing {
//            get("/") {
//                call.respondHtml(HttpStatusCode.OK) {
//                    head {
//                        title { +"Traffic Light Finder" }
//                    }
//                    body {
//                        h1 { +"Find Traffic Signals Nearby" }
//                        form(action = "/", method = FormMethod.post) {
//                            label {
//                                +"Location: "
//                                textInput(name = "location")
//                            }
//                            br()
//                            submitInput { value = "Find Traffic Signals" }
//                        }
//                    }
//                }
//            }
//
//            post("/") {
//                val location = call.receiveParameters()["location"] ?: ""
//                val coordinates = getCoordinates(location)
//
//                if (coordinates != null) {
//                    val (latitude, longitude) = coordinates
//                    val trafficSignals = getTrafficSignals(latitude, longitude)
//
//                    call.respondHtml(HttpStatusCode.OK) {
//                        head {
//                            title { +"Traffic Light Finder" }
//                        }
//                        body {
//                            h1 { +"Traffic Signals near $location" }
//
//                            if (trafficSignals.isNotEmpty()) {
//                                ul {
//                                    trafficSignals.forEach { signal ->
//                                        li {
//                                            +"Signal at Lat: ${signal.lat}, Lon: ${signal.lon}"
//                                        }
//                                    }
//                                }
//                            } else {
//                                p { +"No traffic signals found." }
//                            }
//
//                            a(href = "/") {
//                                +"Search again"
//                            }
//                        }
//                    }
//                } else {
//                    call.respondHtml(HttpStatusCode.OK) {
//                        body {
//                            h1 { +"Location not found." }
//                            a(href = "/") {
//                                +"Search again"
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }.start(wait = true)
//}
//
//// Function to get coordinates using Nominatim
//suspend fun getCoordinates(address: String): Pair<Double, Double>? {
//    val geolocator = "https://nominatim.openstreetmap.org/search?format=json&q=$address"
//    val response: String = client.get(geolocator)
//
//    val jsonArray = JSONArray(response)
//    return if (jsonArray.length() > 0) {
//        val location = jsonArray.getJSONObject(0)
//        Pair(location.getDouble("lat"), location.getDouble("lon"))
//    } else {
//        null
//    }
//}
//
//// Function to get traffic signals using Overpass API
//suspend fun getTrafficSignals(lat: Double, lon: Double): List<TrafficSignal> {
//    val overpassQuery = """
//        [out:json][timeout:25];
//        (
//          node["highway"="traffic_signals"]
//          (around:3000, $lat, $lon);
//        );
//        out body;
//        >;
//        out skel qt;
//    """
//
//    val response: String = client.get("http://overpass-api.de/api/interpreter") {
//        parameter("data", overpassQuery)
//    }
//
//    val jsonObject = JSONObject(response)
//    val elements = jsonObject.getJSONArray("elements")
//    val trafficSignals = mutableListOf<TrafficSignal>()
//
//    for (i in 0 until elements.length()) {
//        val element = elements.getJSONObject(i)
//        val signal = TrafficSignal(element.getDouble("lat"), element.getDouble("lon"))
//        trafficSignals.add(signal)
//    }
//
//    return trafficSignals
//}
