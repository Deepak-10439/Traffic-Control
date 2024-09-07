package com.example.videoplayer.Data

import com.example.videoplayer.ui.Admin.CameraData

data class IntersectionData(
    val address: String,
    val latitude: String,
    val longitude: String,
    val district: String,
    val trafficToday: String,
    val trafficMonthly: String,
    val cameras: List<CameraData>
)

data class CameraData(
    val id: String,
    val location: String,
    val imageUrl: String,
    val vehiclesToday: Int,
    val vehiclesMonthly: Int,
    val incidentsToday: Int,
    val status: String
)
