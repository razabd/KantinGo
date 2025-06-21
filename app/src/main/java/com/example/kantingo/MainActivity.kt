// file: app/src/main/java/com/example/kantingo/MainActivity.kt

package com.example.kantingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kantingo.navigation.AppRoutes
import com.example.kantingo.ui.theme.KantinGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KantinGoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.SIGN_IN_SCREEN
                    ) {
                        composable(route = AppRoutes.SIGN_IN_SCREEN) {
                            SignInScreen(
                                onSignInSuccess = {
                                    navController.navigate(AppRoutes.OTP_SCREEN)
                                },
                                onNavigateToSignUp = {
                                    navController.navigate(AppRoutes.SIGN_UP_SCREEN)
                                }
                            )
                        }

                        composable(route = AppRoutes.SIGN_UP_SCREEN) {
                            SignUpScreen(
                                onSignUpSuccess = {
                                    navController.popBackStack()
                                },
                                onNavigateToSignIn = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(route = AppRoutes.OTP_SCREEN) {
                            OtpVerificationScreen(
                                onVerificationSuccess = {
                                    navController.navigate(AppRoutes.MENU_SCREEN) {
                                        popUpTo(AppRoutes.SIGN_IN_SCREEN) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable(route = AppRoutes.MENU_SCREEN) {
                            MenuScreen()
                        }
                    }
                }
            }
        }
    }
}