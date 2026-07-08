package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.models.WindowType
import com.example.ui.apps.*
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodels.LauncherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MacOsLauncherScreen()
            }
        }
    }
}

@Composable
fun MacOsLauncherScreen() {
    val viewModel: LauncherViewModel = viewModel()

    val activeTheme by viewModel.wallpaperTheme.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val activeWindows = viewModel.activeWindows

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Dynamic Wallpaper
        MacWallpaper(theme = activeTheme)

        // 2. Desktop Files (HD, Documents folders, etc. visible below windows)
        if (!isLocked) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 54.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DesktopIcon("Macintosh HD", Icons.Default.Storage) {
                    viewModel.executeTerminalCommand("cd ~")
                    viewModel.openWindow(WindowType.FINDER)
                }
                DesktopIcon("Documents", Icons.Default.Folder) {
                    viewModel.executeTerminalCommand("cd Documents")
                    viewModel.openWindow(WindowType.FINDER)
                }
                DesktopIcon("Downloads", Icons.Default.Folder) {
                    viewModel.executeTerminalCommand("cd Downloads")
                    viewModel.openWindow(WindowType.FINDER)
                }
            }
        }

        // 3. Floating Window Manager Layer
        if (!isLocked) {
            activeWindows.forEach { window ->
                MacWindow(
                    window = window,
                    viewModel = viewModel
                ) {
                    // Embed corresponding app Composable
                    when (window.type) {
                        WindowType.SAFARI -> SafariApp()
                        WindowType.NOTES -> NotesApp(viewModel = viewModel)
                        WindowType.TERMINAL -> TerminalApp(viewModel = viewModel)
                        WindowType.FINDER -> FinderApp(viewModel = viewModel)
                        WindowType.SETTINGS -> SettingsApp(viewModel = viewModel)
                        WindowType.ABOUT_MAC -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF1E1E1E))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AboutMacLayout()
                            }
                        }
                    }
                }
            }
        }

        // 4. Top Translucent Menu Bar
        if (!isLocked) {
            MacMenuBar(
                viewModel = viewModel,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // 5. Bottom Floating Curved Dock
        if (!isLocked) {
            MacDock(
                viewModel = viewModel,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // 6. Launchpad Full-Screen Overlay
        if (!isLocked) {
            LaunchpadOverlay(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 7. Lock Screen / Boot Startup Overlay
        LockScreenOverlay(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DesktopIcon(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.25f))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (label == "Macintosh HD") Color(0xFFB0BEC5) else Color(0xFF4FC3F7),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .shadow(2.dp)
                .background(Color.Black.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        )
    }
}
