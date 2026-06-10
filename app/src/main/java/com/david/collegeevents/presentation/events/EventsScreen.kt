package com.david.collegeevents.presentation.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.david.collegeevents.domain.model.EventSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onEventClick: (String) -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val categories = listOf("All", "Technical", "Cultural", "Sports", "Robotics")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top Toolbar


        // Greeting Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = "Hey, ${state.currentUserName}!",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827)
            )
            Text(
                text = "Ready for some campus action today?",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        // Horizontal Category Filter Scrollbar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                val isSelected = state.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.fetchEvents(category) },
                    label = { Text(category, fontWeight = FontWeight.Medium) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFA7F3D0), // Custom Light Cyan/Teal shade from screenshot
                        selectedLabelColor = Color(0xFF047857),
                        containerColor = Color(0xFFE5E7EB),
                        labelColor = Color.DarkGray
                    ),
                    border = null
                )
            }
        }

        // Main List Rendering with Content Loading Guard
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF1A237E)
                )
            } else if (state.error != null) {
                Text(
                    state.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp),
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            } else if (state.eventsList.isEmpty()) {
                // EDGE CASE REQ: If empty state show "No events" layout text natively
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No events",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                    Text(
                        text = "There are no events registered in this block.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.eventsList) { event ->
                        EventFeedItem(event = event, onEventClick = onEventClick)
                    }
                }
            }
        }
    }
}

@Composable
fun EventFeedItem(
    event: EventSummary,
    onEventClick: (String) -> Unit
) {
    val isTrending = event.registrationBadge == "TRENDING"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable { onEventClick(event.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)) {
                // Fallback stock image mapping based on index category for testing representation
                val sampleImage = when (event.clubName.contains("Music")) {
                    true -> "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800"
                    false -> "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800"
                }

                AsyncImage(
                    model = event.bannerUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Custom Badges positioning mapping based on backend payload tags inside design matching
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(
                            color = if (isTrending) Color(0xFF1A237E) else Color(0xFFA7F3D0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = event.registrationBadge,
                        color = if (isTrending) Color.White else Color(0xFF047857),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        event.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = Color(0xFF1A237E)
                    )
                }

                Text(
                    event.clubName,
                    color = Color(0xFF0D9488),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFF3F4F6))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${event.date} • ${event.time}", color = Color.Gray, fontSize = 13.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(event.venue, color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}