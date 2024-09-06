package com.example.videoplayer.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.videoplayer.Data.DataSource.intersectionData

enum class TrafficScreen{
    LoginAdmin,
    HomeScreen,
    LoginDriver,
    InputScreen,
    EditScreen,
    IntersectionDetails,
    RoleSelection,
    TrafficCameras,
    SignUp
}

@Composable
fun TrafficApp(
    mainViewModel: MainViewModel = viewModel(),
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TrafficScreen.InputScreen.name
    ) {
        composable(route = TrafficScreen.HomeScreen.name) {
            HomeScreen(
                navController = navController,
                userName = "Admin",
                onProfileClick = {}
            )
        }
        composable(route = TrafficScreen.InputScreen.name) {
            InputScreen(navController)
        }
        composable(route = TrafficScreen.EditScreen.name) {
            EditScreen(navController)
        }
        composable(route = TrafficScreen.RoleSelection.name) {
            RoleSelectionScreen(
                navController = navController,
            )
        }
        composable(route = TrafficScreen.LoginAdmin.name) {
            LoginScreenAdmin(navController, onLoginSuccess = {
                navController.navigate(TrafficScreen.IntersectionDetails.name) {
                    popUpTo(TrafficScreen.LoginAdmin.name) { inclusive = true }
                }
            })
        }
        composable(route = TrafficScreen.LoginDriver.name) {
            LoginScreenDriver(navController, onLoginSuccess = {
                navController.navigate(TrafficScreen.IntersectionDetails.name) {
                    popUpTo(TrafficScreen.LoginDriver.name) { inclusive = true }
                }
            })
        }
        composable(route = TrafficScreen.SignUp.name) {
            SignUpScreen(navController)
        }
        composable(route = TrafficScreen.IntersectionDetails.name) {
            IntersectionDetails(
                navController = navController,
                address = intersectionData.address,
                latitude = intersectionData.latitude,
                longitude = intersectionData.longitude,
                district = intersectionData.district,
                trafficToday = intersectionData.trafficToday,
                trafficMonthly = intersectionData.trafficMonthly,
                onCardClick = { /* Define what happens on click */ }
            )
        }
        composable(route = TrafficScreen.TrafficCameras.name) {
            TrafficCamerasScreen(navController, cameras = intersectionData.cameras)
        }
    }
}