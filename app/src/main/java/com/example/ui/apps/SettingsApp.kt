package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodels.LauncherViewModel

@Composable
fun SettingsApp(viewModel: LauncherViewModel, modifier: Modifier = Modifier) {
    var activeCategory by remember { mutableStateOf("Wallpaper") }

    val wifiOn by viewModel.isWifiEnabled.collectAsState()
    val btOn by viewModel.isBluetoothEnabled.collectAsState()
    val dndOn by viewModel.isDoNotDisturb.collectAsState()
    val activeTheme by viewModel.wallpaperTheme.collectAsState()

    Row(modifier = modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
        // --- SETTINGS SIDEBAR ---
        Column(
            modifier = Modifier
                .width(110.dp)
                .fillMaxHeight()
                .background(Color(0xFF141414))
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = "System Settings",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )

            SettingsSidebarItem("Wallpaper", Icons.Default.Palette, activeCategory == "Wallpaper") {
                activeCategory = "Wallpaper"
            }
            SettingsSidebarItem("Wifi & BT", Icons.Default.Wifi, activeCategory == "Wifi & BT") {
                activeCategory = "Wifi & BT"
            }
            SettingsSidebarItem("About Mac", Icons.Default.Info, activeCategory == "About Mac") {
                activeCategory = "About Mac"
            }
        }

        // --- SETTINGS CONTENT AREA ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(14.dp)
        ) {
            Text(
                text = activeCategory,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(bottom = 14.dp))

            when (activeCategory) {
                "Wallpaper" -> {
                    Text("Select Desktop Background Theme:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val themesList = listOf("Sophisticated", "Sonoma", "Sequoia", "Ventura", "Aurora", "Graphite")
                        themesList.forEach { theme ->
                            val isSelected = theme == activeTheme
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFF007AFF).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.04f))
                                    .border(if (isSelected) 1.dp else 0.dp, Color(0xFF007AFF), RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setWallpaperTheme(theme) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini background preview
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(getThemeBrush(theme))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (theme == "Sophisticated") "Sophisticated Dark" else "macOS $theme",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                "Wifi & BT" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Wifi, "Wifi", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Wi-Fi network connection", color = Color.White, fontSize = 11.sp)
                                    Text("Status: ${if (wifiOn) "Connected (Home_Wifi)" else "Disconnected"}", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                                }
                            }
                            Switch(
                                checked = wifiOn,
                                onCheckedChange = { viewModel.isWifiEnabled.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bluetooth, "BT", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Bluetooth services", color = Color.White, fontSize = 11.sp)
                                    Text("Status: ${if (btOn) "Discoverable (AirPods)" else "Off"}", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                                }
                            }
                            Switch(
                                checked = btOn,
                                onCheckedChange = { viewModel.isBluetoothEnabled.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DoNotDisturbOn, "DND", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Do Not Disturb Mode", color = Color.White, fontSize = 11.sp)
                                    Text("Silences notifications", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                                }
                            }
                            Switch(
                                checked = dndOn,
                                onCheckedChange = { viewModel.isDoNotDisturb.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                            )
                        }
                    }
                }

                "About Mac" -> {
                    AboutMacLayout()
                }
            }
        }
    }
}

@Composable
fun SettingsSidebarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (active) Color.White.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) Color(0xFF007AFF) else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (active) Color.White else Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AboutMacLayout() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Styled Laptop Display
        Icon(
            Icons.Default.Laptop,
            "MacBook Pro",
            tint = Color(0xFFE0E0E0),
            modifier = Modifier.size(54.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("MacBook Pro 14\"", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text("Chip Apple M3 Max (Emulated)", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)

        Spacer(modifier = Modifier.height(14.dp))

        Divider(color = Color.White.copy(alpha = 0.1f))

        Spacer(modifier = Modifier.height(8.dp))

        SpecRow("Memory", "16 GB Unified Memory")
        SpecRow("Graphics", "Apple GPU 40-Core")
        SpecRow("Serial Number", "C02F28HPMD6P")
        SpecRow("macOS Version", "Sequoia 15.1 Beta")
    }
}

@Composable
fun SpecRow(label: String, valText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
        Text(valText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

fun getThemeBrush(theme: String): Brush {
    return when (theme) {
        "Sophisticated" -> Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFFDB2777)))
        "Sonoma" -> Brush.verticalGradient(listOf(Color(0xFFFF5E00), Color(0xFF9E0059), Color(0xFF1D3557)))
        "Sequoia" -> Brush.verticalGradient(listOf(Color(0xFFE63946), Color(0xFFFFB703), Color(0xFF3B1354)))
        "Ventura" -> Brush.verticalGradient(listOf(Color(0xFFFF9F1C), Color(0xFFF28482), Color(0xFF84A59D)))
        "Aurora" -> Brush.verticalGradient(listOf(Color(0xFF7209B7), Color(0xFF4CC9F0), Color(0xFF03071E)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1E2022), Color(0xFF0D0D0D)))
    }
}
