// file: app/src/main/java/com/example/kantingo/OtpVerificationScreen.kt

package com.example.kantingo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    onVerificationSuccess: () -> Unit
) {
    val otpLength = 4
    var otpValues by remember { mutableStateOf(List(otpLength) { "" }) }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
             Image(
                 painter = painterResource(id = R.drawable.logo),
                 contentDescription = "KantinGo Logo",
                 modifier = Modifier.size(36.dp)
             )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "KantinGo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006970)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Enter the 4-digit code", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Please input the verification code sent to your phone number 23480******90",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        TextButton(onClick = { /* TODO: Change number */ }) {
            Text("Change number?", color = Color(0xFF006970))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            otpValues.forEachIndexed { index, value ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                            val newOtpValues = otpValues.toMutableList()
                            newOtpValues[index] = newValue
                            otpValues = newOtpValues

                            if (newValue.isNotEmpty() && index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(60.dp)
                        .focusRequester(focusRequesters[index]),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { /* TODO: Resend */ }) {
            Text("Resend code", color = Color(0xFF006970))
        }

        Spacer(modifier = Modifier.height(24.dp))

        val isOtpFull = otpValues.all { it.isNotEmpty() }

        Button(
            onClick = {
                onVerificationSuccess()
            },
            enabled = isOtpFull,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006970))
        ) {
            Text("Verify")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "By signing up, you agree to snap Terms of Service and Privacy Policy.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequesters[0].requestFocus()
    }
}