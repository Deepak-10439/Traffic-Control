package com.example.videoplayer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.videoplayer.R
import com.google.android.libraries.intelligence.acceleration.Analytics

data class DashboardItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userName: String,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = { HomeTopAppBar() },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        HomeContent(paddingValues, navController, userName)
    }
}

@Composable
fun WelcomeHeader(userName: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Text(
            text = "Welcome back,",
            color = Color(0xFF34495E),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = userName,
            color = Color(0xFF2C3E50),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DashboardCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color(0xFF2C3E50)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFECF0F1), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF3498DB),
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF7F8C8D)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.emblem_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Text(
                    "Traffic Management",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0xFF3498DB),
            titleContentColor = Color.White
        ),
        modifier = Modifier.height(64.dp)
    )
}

@Composable
fun HomeContent(paddingValues: PaddingValues, navController: NavController, userName: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { WelcomeHeader(userName) }

        val dashboardItems = listOf(
            DashboardItem(Icons.Default.List, "View Traffic Data", "Access real-time traffic information and analytics") { navController.navigate(TrafficScreen.IntersectionDetails.name) },
            DashboardItem(Icons.Default.Edit, "Input Traffic Data", "Update and manage traffic data entries") { navController.navigate(TrafficScreen.InputScreen.name) },
            DashboardItem(Icons.Default.Settings, "Edit Traffic Settings", "Configure traffic management parameters") { navController.navigate(TrafficScreen.EditScreen.name) },
        )

        items(dashboardItems) { item ->
            DashboardCard(
                icon = item.icon,
                title = item.title,
                description = item.description,
                onClick = item.onClick
            )
        }
    }
}