package com.example.videoplayer.Data

import com.example.videoplayer.ui.Admin.CameraData

object DataSource {
    private val cameras = listOf(
    CameraData("1", "Main Street", "https://drive.google.com/uc?export=download&id=1c18IBwjwp844Fh4K9ryxdTSK47zaXzH5", 5, 8, 3, "Red"),
    CameraData("2", "Park Avenue", "https://drive.google.com/uc?export=download&id=1QdVpssejmfykEyzhRpuXZ0os-8_6ycOX", 7, 8, 6, "Green")
    // Add more cameras as needed
    )

    val intersectionData = IntersectionData(
        address = "123 Main St, City",
        latitude = "123.456",
        longitude = "789.012",
        district = "New Delhi",
        trafficToday = "10",
        trafficMonthly = "20",
        cameras = cameras
    )
}