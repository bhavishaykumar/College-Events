package com.david.collegeevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.david.collegeevents.presentation.auth.LoginScreen
import com.david.collegeevents.presentation.auth.RegisterScreen
import com.david.collegeevents.ui.theme.CollegeEventsTheme
import com.david.collegeevents.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeEventsTheme {
                val startDestination = runBlocking {
                    val token = tokenManager.tokenFlow.first()
                    if (!token.isNullOrBlank()) "home" else "login"
                }

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startDestination) {
                    composable("login") {
                        LoginScreen(navController = navController, onLoginSuccess = {
                            // Navigate to home screen clearing the backstack
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        })
                    }
                    composable("register") {
                        RegisterScreen(navController = navController, onRegisterSuccess = {
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        })
                    }
                    composable("home") {
                        // Hum agle step me banayenge tab tak ke liye placeholders!
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                "Home Screen Boilerplate Success",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }


            }
        }
    }
}
