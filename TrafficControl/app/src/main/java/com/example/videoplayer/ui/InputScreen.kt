package com.example.videoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

enum class TrafficDirection {
    NONE, NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
}

data class CameraDataLink(
    var link: String = "",
    var direction: TrafficDirection = TrafficDirection.NONE
)

@Composable
fun InputScreen(navController: NavController) {
    var locationId by remember { mutableStateOf("") }
    var numCameras by remember { mutableStateOf("") }
    var cameraDataList by remember { mutableStateOf(List(6) { CameraDataLink() }) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) { 
            item {
                Text(
                    text = "Input New Intersection Data",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF2C3E50),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFD))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = numCameras,
                            onValueChange = { value ->
                                numCameras = value
                                val num = value.toIntOrNull() ?: 0
                                when {
                                    num < 3 || num > 6-> {
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

                        Spacer(modifier = Modifier.height(16.dp))

                        if (numCameras.toIntOrNull() in 3..6) {
                            OutlinedTextField(
                                value = locationId,
                                onValueChange = { locationId = it },
                                label = { Text("Location Name") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF3498DB),
                                    unfocusedBorderColor = Color(0xFFBDC3C7),
                                    focusedLabelColor = Color(0xFF3498DB),
                                    unfocusedLabelColor = Color(0xFF7F8C8D),
                                    cursorColor = Color(0xFF3498DB)
                                )
                            )
                        }
                    }
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
            }
            item {
                Button(
                    onClick = {
                        // Show success Toast message
                        Toast.makeText(context, "New data submitted successfully", Toast.LENGTH_SHORT).show()
                        // Reset form
                        locationId = ""
                        numCameras = ""
                        cameraDataList = List(6) { CameraDataLink() }
                        // Navigate to view dashboard
                        navController.navigate(TrafficScreen.HomeScreen.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                    enabled = numCameras.toIntOrNull() in 3..6 &&
                            locationId.isNotEmpty() &&
                            cameraDataList.take(numCameras.toInt()).all { 
                                it.link.isNotEmpty() && it.direction != TrafficDirection.NONE 
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (cameraDatalink.direction == TrafficDirection.NONE) "Select" else cameraDatalink.direction.name,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(text = "Traffic Direction") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                    TrafficDirection.entries.filter { it != TrafficDirection.NONE }.forEach { direction ->
                        DropdownMenuItem(
                            text = { Text(direction.name) },
                            onClick = {
                                onCameraDataChange(cameraDatalink.copy(direction = direction))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
