package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.WindowType
import com.example.viewmodels.LauncherViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MacMenuBar(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val activeApp = viewModel.focusedWindowType.collectAsState().value
    val activeAppName = remember(activeApp) {
        when (activeApp) {
            WindowType.SAFARI -> "Safari"
            WindowType.NOTES -> "Notes"
            WindowType.TERMINAL -> "Terminal"
            WindowType.FINDER -> "Finder"
            WindowType.SETTINGS -> "Settings"
            WindowType.ABOUT_MAC -> "About"
            null -> "Finder"
        }
    }

    var timeText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val calendar = Calendar.getInstance()
            timeText = SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
            dateText = SimpleDateFormat("EEE MMM d", Locale.getDefault()).format(calendar.time)
            delay(1000)
        }
    }

    val isAppleMenuOpen by viewModel.isAppleMenuOpen.collectAsState()
    val isControlCenterOpen by viewModel.isControlCenterOpen.collectAsState()
    val isCalendarOpen by viewModel.isCalendarOpen.collectAsState()

    val isWifiOn by viewModel.isWifiEnabled.collectAsState()
    val isBtOn by viewModel.isBluetoothEnabled.collectAsState()
    val isDndOn by viewModel.isDoNotDisturb.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp) // standard macOS thin menu bar height
            .background(Color.Black.copy(alpha = 0.20f)) // bg-black/20 backdrop
            .drawBehind {
                val strokeWidth = 0.5.dp.toPx()
                drawLine(
                    color = Color.White.copy(alpha = 0.05f), // border-white/5 style
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Left side: Apple, Active App, Standard menus
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Apple logo character 
            Text(
                text = "",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        viewModel.isAppleMenuOpen.value = !viewModel.isAppleMenuOpen.value
                        viewModel.isControlCenterOpen.value = false
                        viewModel.isCalendarOpen.value = false
                    }
                    .padding(horizontal = 6.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Active App Menu
            Text(
                text = activeAppName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Dynamic menus (File, Edit, etc.)
            val menus = listOf("File", "Edit", "View", "Go", "Window")
            menus.forEach { menu ->
                Text(
                    text = menu,
                    color = Color.White.copy(alpha = 0.70f), // opacity-70 style
                    fontSize = 11.sp, // text-[11px] style
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* Expand options */ }
                        .padding(horizontal = 8.dp)
                )
            }
        }

        // Right side: Status icons & clock
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wi-Fi icon
            Icon(
                imageVector = if (isWifiOn) Icons.Default.Wifi else Icons.Default.WifiOff,
                contentDescription = "Wi-Fi",
                tint = if (isWifiOn) Color.White else Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(16.dp)
                    .clickable { viewModel.isWifiEnabled.value = !isWifiOn }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Battery Icon (Simulated full)
            Icon(
                imageVector = Icons.Default.BatteryChargingFull,
                contentDescription = "Battery",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Control Center button
            Icon(
                imageVector = Icons.Default.Settings, // Mocking CC slider icon with gear/settings
                contentDescription = "Control Center",
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        viewModel.isControlCenterOpen.value = !viewModel.isControlCenterOpen.value
                        viewModel.isAppleMenuOpen.value = false
                        viewModel.isCalendarOpen.value = false
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Time & Date
            Row(
                modifier = Modifier
                    .clickable {
                        viewModel.isCalendarOpen.value = !viewModel.isCalendarOpen.value
                        viewModel.isAppleMenuOpen.value = false
                        viewModel.isControlCenterOpen.value = false
                    }
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$dateText   $timeText",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // --- Dropdowns & Overlays ---

        // Apple Dropdown Menu
        if (isAppleMenuOpen) {
            Box(
                modifier = Modifier
                    .absoluteOffset(x = 6.dp, y = 32.dp)
                    .width(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1E1E).copy(alpha = 0.92f))
                    .padding(vertical = 6.dp)
            ) {
                Column {
                    DropdownMenuItemText("About This Mac") {
                        viewModel.isAppleMenuOpen.value = false
                        viewModel.openWindow(WindowType.ABOUT_MAC)
                    }
                    DropdownMenuItemText("System Settings...") {
                        viewModel.isAppleMenuOpen.value = false
                        viewModel.openWindow(WindowType.SETTINGS)
                    }
                    DividerText()
                    DropdownMenuItemText("Lock Screen") {
                        viewModel.isAppleMenuOpen.value = false
                        viewModel.lock()
                    }
                    DropdownMenuItemText("Shut Down...") {
                        // Just lock as a shutdown feel
                        viewModel.isAppleMenuOpen.value = false
                        viewModel.lock()
                    }
                }
            }
        }

        // Control Center Panel Overlay
        if (isControlCenterOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absoluteOffset(x = (-12).dp, y = 35.dp)
                    .width(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E).copy(alpha = 0.94f))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Top Row: Connectivity grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Wifi + Bluetooth Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ControlCenterTile(
                                icon = Icons.Default.Wifi,
                                title = "Wi-Fi",
                                subtitle = if (isWifiOn) "Home_Wifi_5G" else "Off",
                                active = isWifiOn,
                                onClick = { viewModel.isWifiEnabled.value = !isWifiOn }
                            )
                            ControlCenterTile(
                                icon = Icons.Default.Bluetooth,
                                title = "Bluetooth",
                                subtitle = if (isBtOn) "On" else "Off",
                                active = isBtOn,
                                onClick = { viewModel.isBluetoothEnabled.value = !isBtOn }
                            )
                        }

                        // Do Not Disturb + AirDrop Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ControlCenterTile(
                                icon = Icons.Default.DoNotDisturbOn,
                                title = "DND",
                                subtitle = if (isDndOn) "On" else "Off",
                                active = isDndOn,
                                onClick = { viewModel.isDoNotDisturb.value = !isDndOn }
                            )
                            ControlCenterTile(
                                icon = Icons.Default.Airplay,
                                title = "AirDrop",
                                subtitle = "Contacts Only",
                                active = true,
                                onClick = {}
                            )
                        }
                    }

                    // Brightness slider
                    val brightnessValue by viewModel.brightness.collectAsState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LightMode, contentDescription = "Brightness", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Display Brightness", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = brightnessValue,
                            onValueChange = { viewModel.brightness.value = it },
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                    }

                    // Volume slider
                    val volumeValue by viewModel.volume.collectAsState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sound Volume", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = volumeValue,
                            onValueChange = { viewModel.volume.value = it },
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                    }

                    // Music Control Card (Now Playing)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = Color(0xFFFF4081),
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFF4081).copy(alpha = 0.2f))
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MacBook Pro Air", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Apple Music Ambient", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        Row {
                            Icon(Icons.Default.SkipPrevious, "Prev", tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Calendar / Notification Widget
        if (isCalendarOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absoluteOffset(x = (-12).dp, y = 35.dp)
                    .width(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E).copy(alpha = 0.94f))
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Render a mini 7x5 calendar grid
                    val calendar = Calendar.getInstance()
                    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    val startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

                    val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")

                    // Row of weekdays
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        weekdays.forEach { day ->
                            Text(day, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    var dayCounter = 1
                    for (row in 0..5) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            for (col in 0..6) {
                                val cellIndex = row * 7 + col
                                if (cellIndex < startDayOfWeek || dayCounter > daysInMonth) {
                                    Spacer(modifier = Modifier.width(28.dp))
                                } else {
                                    val isToday = dayCounter == currentDay
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isToday) Color(0xFF007AFF) else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayCounter.toString(),
                                            color = if (isToday) Color.White else Color.White.copy(alpha = 0.9f),
                                            fontSize = 11.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                    dayCounter++
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        if (dayCounter > daysInMonth) break
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No upcoming events today.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownMenuItemText(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    )
}

@Composable
fun DividerText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.1f))
    )
}

@Composable
fun ControlCenterTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (active) Color.White else Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (active) Color(0xFF007AFF) else Color.White.copy(alpha = 0.1f))
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 8.sp)
        }
    }
}
