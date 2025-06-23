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
import com.example.kantingo.ui.pages.HistoryScreen

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
                            // SignInScreen handles its own navigation based on success/failure.
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
                            // SignUpScreen handles its own navigation after sign up.
                            SignUpScreen(
                                onSignUpSuccess = {
                                    navController.popBackStack() // Go back to sign in.
                                },
                                onNavigateToSignIn = {
                                    navController.popBackStack() // Go back to sign in.
                                }
                            )
                        }

                        composable(route = AppRoutes.OTP_SCREEN) {
                            // OtpVerificationScreen navigates to the main menu upon success.
                            OtpVerificationScreen(
                                onVerificationSuccess = {
                                    navController.navigate(AppRoutes.MENU_SCREEN) {
                                        // Clear the back stack up to and including the sign-in screen
                                        // so that the user cannot go back to auth screens after login.
                                        popUpTo(AppRoutes.SIGN_IN_SCREEN) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable(route = AppRoutes.MENU_SCREEN) {
                            // Pass the navController to MenuScreen so it can handle
                            // bottom navigation bar clicks.
                            MenuScreen(navController = navController)
                        }

                        // Define the composable for the History screen.
                        composable(route = AppRoutes.HISTORY_SCREEN) {
                            // Pass the navController to HistoryScreen for bottom navigation.
                            HistoryScreen(navController = navController)
                        }

                        // Define the composable for the Profile screen.
                        composable(route = AppRoutes.PROFILE_SCREEN) {
                            // Pass the navController to ProfileScreen for bottom navigation.
                            ProfileScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
