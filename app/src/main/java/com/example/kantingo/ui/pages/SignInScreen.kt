// file: app/src/main/java/com/example/kantingo/SignInScreen.kt

package com.example.kantingo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    val isFormValid = email.contains("@") && email.contains(".") && password.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "KantinGo Logo",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "KantinGo",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006970)
            )
        }

        Text("Welcome", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Please input your details",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = !email.contains("@") || !email.contains(".")
            },
            label = { Text("Your email address") },
            isError = emailError,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        if (emailError && email.isNotBlank()) {
            Text("Invalid email format", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { /* TODO: Forgot Password */ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text("Forgot Password?")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onSignInSuccess()
            },
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF006970),
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Get Started", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Need an account?")
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onNavigateToSignUp, contentPadding = PaddingValues(0.dp)) {
                Text(
                    "Sign up",
                    color = Color(0xFF006970),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}