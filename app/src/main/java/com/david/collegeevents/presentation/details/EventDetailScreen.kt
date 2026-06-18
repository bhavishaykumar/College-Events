package com.david.collegeevents.presentation.details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun EventDetailScreen(
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current
    val scrollState = rememberLazyListState()

    // Dynamic Alpha Mapping Calculations based on scroll index bounds
    val bannerHeight = 280.dp
    val bannerHeightPx = 840f

    val dynamicAlpha by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 0f
            else {
                val offset = scrollState.firstVisibleItemScrollOffset.toFloat()
                val calculated = 1f - (offset / bannerHeightPx)
                calculated.coerceIn(0f, 1f)
            }
        }
    }

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (state.event != null) {
        val event = state.event

        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {

            // Animated Header Banner Layer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bannerHeight)
                    .graphicsLayer {
                        alpha = dynamicAlpha
                        translationY =
                            -scrollState.firstVisibleItemScrollOffset.toFloat() * 0.4f // Parallax tracking dampener
                    }
            ) {
                AsyncImage(
                    model = event.bannerUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Bottom ambient light gradient overlay mapping matching Stitch UI
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 400f
                            )
                        )
                )
            }

            // Foreground Content Layout
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = bannerHeight - 24.dp, bottom = 100.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(24.dp)
                    ) {
                        // Club identity segment
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Adjust,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = event.clubName,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Organizing Committee",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = event.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Metadata schedule card mapping
                        DetailMetaCard(
                            icon = Icons.Default.CalendarToday,
                            title = event.date,
                            sub = event.time,
                            badgeColor = MaterialTheme.colorScheme.primaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailMetaCard(
                            icon = Icons.Default.LocationOn,
                            title = event.venue,
                            sub = "University Central Campus",
                            badgeColor = MaterialTheme.colorScheme.secondaryContainer
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "About Event",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🔴 DYNAMIC COMPONENT BOUND: Show confirmation workflow if data payload reports user registration active
                        if (event.isUserRegistered) {
                            RegistrationStatusCard(
                                seatNo = event.seatAvailability,
                                onDeregister = { viewModel.executeAction(register = false) },
                                isProcessing = state.actionLoading
                            )
                        }
                    }
                }
            }

            // Floating Custom Translucent Navigation Bar overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingRoundNavButton(icon = Icons.Default.ArrowBack, onClick = onBack)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FloatingRoundNavButton(icon = Icons.Default.Share, onClick = {})
                    FloatingRoundNavButton(icon = Icons.Default.FavoriteBorder, onClick = {})
                }
            }

            // Fixed Bottom Action CTA Dock layout
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Registration Fee",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Text(
                            text = event.registrationFee,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = { viewModel.executeAction(register = true) },
                        modifier = Modifier
                            .width(180.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (event.isUserRegistered) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !event.isUserRegistered && !state.actionLoading
                    ) {
                        if (state.actionLoading && !event.isUserRegistered) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = if (event.isUserRegistered) "Registered" else "Register Now",
                                fontWeight = FontWeight.Bold,
                                color = if (event.isUserRegistered) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailMetaCard(icon: ImageVector, title: String, sub: String, badgeColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(badgeColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(text = sub, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RegistrationStatusCard(seatNo: String, onDeregister: () -> Unit, isProcessing: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), // Soft success tint mapping
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "You're Registered!",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = seatNo,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onDeregister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.errorContainer
                ),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Deregister from Event",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Refunds available until 48h before start.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun FloatingRoundNavButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}