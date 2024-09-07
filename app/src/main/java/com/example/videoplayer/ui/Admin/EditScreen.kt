package com.example.videoplayer.ui.Admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.videoplayer.ui.TrafficScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavController) {
    var locationId by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }

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
                    text = "Edit Intersection Data",
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
                            value = locationId,
                            onValueChange = { locationId = it },
                            label = { Text("Location ID") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3498DB),
                                unfocusedBorderColor = Color(0xFFBDC3C7),
                                focusedLabelColor = Color(0xFF3498DB),
                                unfocusedLabelColor = Color(0xFF7F8C8D),
                                cursorColor = Color(0xFF3498DB)
                            )
                        )

                        OutlinedTextField(
                            value = longitude,
                            onValueChange = { longitude = it },
                            label = { Text("Longitude") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3498DB),
                                unfocusedBorderColor = Color(0xFFBDC3C7),
                                focusedLabelColor = Color(0xFF3498DB),
                                unfocusedLabelColor = Color(0xFF7F8C8D),
                                cursorColor = Color(0xFF3498DB)
                            )
                        )

                        OutlinedTextField(
                            value = latitude,
                            onValueChange = { latitude = it },
                            label = { Text("Latitude") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Button(
                    onClick = {
                        // Show success Toast message
                        Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show()
                        // Reset form after submission
                        locationId = ""
                        longitude = ""
                        latitude = ""
                        // Navigate to the view page after submission
                        navController.navigate(TrafficScreen.HomeScreen.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                    enabled = locationId.isNotEmpty() && longitude.isNotEmpty() && latitude.isNotEmpty()
                ) {
                    Text(
                        text = "Update Data",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
