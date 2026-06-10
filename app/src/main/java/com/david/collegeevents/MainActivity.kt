package com.david.collegeevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.david.collegeevents.presentation.auth.LoginScreen
import com.david.collegeevents.presentation.auth.RegisterScreen
import com.david.collegeevents.presentation.createEvent.CreateEventScreen
import com.david.collegeevents.presentation.details.EventDetailScreen
import com.david.collegeevents.presentation.events.EventsScreen
import com.david.collegeevents.presentation.profile.ProfileScreen
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

    @OptIn(ExperimentalMaterial3Api::class)
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
                val currentRoute = navController
                    .currentBackStackEntryFlow
                    .collectAsState(initial = navController.currentBackStackEntry)
                    .value?.destination?.route

                // Bottom bar sirf in routes pe dikhegi
                val showBottomBar = currentRoute in listOf("home", "profile")
                val showTopBar = currentRoute == "home"  // sirf home pe

                var selectedTab by remember { mutableStateOf("events") }

                val context = LocalContext.current
                val tokenManager = remember { TokenManager(context) }
                val userRole by tokenManager.userRoleFlow.collectAsState(initial = "STUDENT")
                val showFabButton = userRole == "ADMIN" || userRole == "TEACHER"

                Scaffold(
                    floatingActionButton = {
                        if (showFabButton && showTopBar) {
                            FloatingActionButton(
                                onClick = { navController.navigate("create_event_route") },
                                containerColor = Color(0xFF1A237E),
                                contentColor = Color.White,
                                shape = CircleShape
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Publish Event")
                            }
                        }
                    },
                    topBar = {
                        if (showTopBar) {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.School,
                                            contentDescription = null,
                                            tint = Color(0xFF1A237E),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "College Event",
                                            color = Color(0xFF1A237E),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { }) {
                                        Icon(
                                            Icons.Default.NotificationsNone,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(containerColor = Color.White) {
                                NavigationBarItem(
                                    selected = selectedTab == "events",
                                    onClick = {
                                        selectedTab = "events"
                                        navController.navigate("home") {
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Events") },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(
                                            0xFFA7F3D0
                                        )
                                    )
                                )
                                NavigationBarItem(
                                    selected = selectedTab == "profile",
                                    onClick = {
                                        selectedTab = "profile"
                                        navController.navigate("profile") {
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.PersonOutline,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Profile") },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(
                                            0xFFA7F3D0
                                        )
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)  // ✅ innerPadding NavHost ko milega
                    ) {
                        composable("login") {
                            LoginScreen(navController = navController) {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                        composable("register") {
                            RegisterScreen(navController = navController) {
                                navController.navigate("home")
                            }
                        }
                        composable("home") {
                            EventsScreen(onEventClick = { eventId ->
                                navController.navigate("event_details/$eventId")
                            })
                        }
                        composable("profile") {
                            ProfileScreen(
                                onEventClick = { eventId ->
                                    navController.navigate("event_details/$eventId")
                                },
                                onLogoutDone = {
                                    // 🔴 EDGE CASE ROBUST HANDLER: Poora backstack clear karke root login par send kiya
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "event_details/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) {
                            EventDetailScreen(onBack = { navController.popBackStack() })
                        }

                        composable("create_event_route") {
                            CreateEventScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}