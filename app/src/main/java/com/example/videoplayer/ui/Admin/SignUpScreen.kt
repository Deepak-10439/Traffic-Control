package com.example.videoplayer.ui.Admin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.videoplayer.R
import com.example.videoplayer.ui.TrafficScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun SignUpScreen(navController: NavController) {
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFF0F0F0))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.emblem_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Create Account",
                color = Color(0xFF2C3E50),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sign up to get started",
                color = Color(0xFF7F8C8D),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                label = { Text("First Name") },
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
                value = lastName.value,
                onValueChange = { lastName.value = it },
                label = { Text("Last Name") },
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
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email/Mobile Number") },
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
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3498DB),
                    unfocusedBorderColor = Color(0xFFBDC3C7),
                    focusedLabelColor = Color(0xFF3498DB),
                    unfocusedLabelColor = Color(0xFF7F8C8D),
                    cursorColor = Color(0xFF3498DB)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { CreateUserAccount(firstName.value, lastName.value, email.value, password.value, navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))
            ) {
                Text(
                    text = "Create Account",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = Color(0xFF7F8C8D),
                    fontSize = 16.sp
                )
                TextButton(
                    onClick = { navController.navigate(TrafficScreen.RoleSelection.name) }
                ) {
                    Text(
                        text = "Log In",
                        color = Color(0xFF3498DB),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
//
//@Composable
//fun CustomTextField(
//    value: String,
//    onValueChange: (String) -> Unit,
//    hint: String,
//    isPassword: Boolean = false
//) {
//    Box(
//        modifier = Modifier
//            .widthIn(250.dp, 311.dp)
//            .height(48.dp)
//            .background(Color.Transparent, shape = AppShapes.small)
//            .padding(0.dp)
//    ) {
//        BasicTextField(
//            value = value,
//            onValueChange = onValueChange,
//            modifier = Modifier.fillMaxSize(),
//            textStyle = TextStyle(color = Color.White),
//            decorationBox = { innerTextField ->
//                Box(
//                    contentAlignment = Alignment.CenterStart,
//                    modifier = Modifier
//                        .background(Color.Transparent, shape = AppShapes.small)
//                        .border(2.dp, Color.White, shape = AppShapes.large)
//                        .padding(start = 20.dp)
//                        .fillMaxSize()
//                ) {
//                    if (value.isEmpty()) {
//                        Text(text = hint, color = Color.White.copy(alpha = 1.0f))
//                    }
//                    innerTextField()
//                }
//            },
//            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
//        )
//    }
//}

fun CreateUserAccount(firstName: String, lastName: String, email: String, password: String, navController: NavController) {
    if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Toast.makeText(navController.context, "Account Created Successfully", Toast.LENGTH_LONG).show()
                navController.navigate(TrafficScreen.RoleSelection.name) // Navigate to login screen
            }
            .addOnFailureListener { exception ->
                Toast.makeText(navController.context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(navController.context, "All fields must be filled", Toast.LENGTH_SHORT).show()
    }
}