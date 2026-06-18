package com.david.collegeevents.presentation.createEvent

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateEventScreen(
    editEventId: String?,
    onBack: () -> Unit,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    var eventTitle by remember { mutableStateOf("") }
    var selectedClub by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var venueAddress by remember { mutableStateOf("") }
    var registrationFee by remember { mutableStateOf("") }
    var aboutDescription by remember { mutableStateOf("") }

    var dropdownExpanded by remember { mutableStateOf(false) }
    val clubsList = listOf(
        "TECH INNOVATORS SOCIETY",
        "Music & Arts Club",
        "Design Enthusiasts Guild",
        "Athletics Council"
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.uploadBannerImage(it) } }

    LaunchedEffect(editEventId) {
        if (editEventId != null) {
            viewModel.populateFieldsForEdit(editEventId)
        }
    }

    LaunchedEffect(state.prefillTitle) {
        state.prefillTitle?.let { eventTitle = it }
        state.prefillClub?.let { selectedClub = it }
        state.prefillDate?.let { eventDate = it }
        state.prefillTime?.let { eventTime = it }
        state.prefillVenue?.let { venueAddress = it }
        state.prefillFee?.let { registrationFee = it }
        state.prefillDescription?.let { aboutDescription = it }
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CreateEventViewModel.CreateEventUiEvent.ShowToast ->
                    Toast.makeText(context, event.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(state.executionSuccess) {
        if (state.executionSuccess) {
            Toast.makeText(
                context,
                if (editEventId == null) "Event Published Successfully!" else "Event Saved!",
                Toast.LENGTH_LONG
            ).show()
            onBack()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetErrors()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── HEADER ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = if (editEventId == null) "Create Event" else "Edit Event",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        // ── FORM CONTENT ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Banner Upload
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (state.uploadedBannerUrl != null) {
                    AsyncImage(
                        model = state.uploadedBannerUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (state.isUploadingBanner) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Upload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Upload Event Banner",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                        Text(
                            "Recommended size: 1200x630px",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            FormLabel("Event Title")
            OutlinedTextField(
                value = eventTitle,
                onValueChange = { eventTitle = it },
                placeholder = {
                    Text(
                        "e.g., Annual Tech Symposium 2024",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            FormLabel("Organizing Committee / Club")
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedClub,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            "Select a committee",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            null,
                            Modifier.clickable { dropdownExpanded = true }
                        )
                    }
                )
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    clubsList.forEach { club ->
                        DropdownMenuItem(
                            text = { Text(club) },
                            onClick = { selectedClub = club; dropdownExpanded = false }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel("Date")
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = { eventDate = it },
                        placeholder = {
                            Text(
                                "mm/dd/yyyy",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, null) }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel("Time")
                    OutlinedTextField(
                        value = eventTime,
                        onValueChange = { eventTime = it },
                        placeholder = {
                            Text(
                                "--:-- --",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { Icon(Icons.Default.AccessTime, null) }
                    )
                }
            }

            FormLabel("Venue")
            OutlinedTextField(
                value = venueAddress,
                onValueChange = { venueAddress = it },
                placeholder = {
                    Text(
                        "Search campus locations...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            FormLabel("Registration Fee")
            OutlinedTextField(
                value = registrationFee,
                onValueChange = { registrationFee = it },
                placeholder = {
                    Text(
                        "$ 0.00",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            FormLabel("About Event")
            OutlinedTextField(
                value = aboutDescription,
                onValueChange = { aboutDescription = it },
                placeholder = {
                    Text(
                        "Share more details about what participants can expect...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val cat = if (selectedClub.contains("TECH") || selectedClub.contains("Design"))
                        "Technical" else "Cultural"
                    viewModel.saveOrUpdateForm(
                        editEventId, eventTitle, selectedClub, eventDate,
                        eventTime, venueAddress, registrationFee, aboutDescription, cat
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isPublishingEvent
            ) {
                if (state.isPublishingEvent) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        if (editEventId == null) "Create Event" else "Save Changes 💾",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 4.dp)
    )
}