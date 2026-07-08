package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.WindowType
import com.example.viewmodels.LauncherViewModel

@Composable
fun MacDock(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val activeWindows = viewModel.activeWindows
    val isLaunchpadOpen by viewModel.isLaunchpadOpen.collectAsState()

    // Map showing which app types are open
    val openStatusMap = remember(activeWindows.size) {
        WindowType.values().associateWith { type ->
            activeWindows.any { it.type == type && !it.isMinimized }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Floating glass bar
        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.15f)) // bg-white/15
                .border(0.5.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(22.dp)) // border-white/20
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Finder Icon
            DockIcon(
                icon = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF64B5F6), Color(0xFF1E88E5))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, "Finder", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                },
                isOpen = openStatusMap[WindowType.FINDER] == true,
                onClick = { viewModel.openWindow(WindowType.FINDER) }
            )

            // 2. Launchpad Icon
            DockIcon(
                icon = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Apps, "Launchpad", tint = Color(0xFF333333), modifier = Modifier.size(24.dp))
                    }
                },
                isOpen = isLaunchpadOpen,
                onClick = { viewModel.isLaunchpadOpen.value = !isLaunchpadOpen }
            )

            // 3. Safari Icon
            DockIcon(
                icon = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Explore, "Safari", tint = Color.White, modifier = Modifier.size(25.dp))
                    }
                },
                isOpen = openStatusMap[WindowType.SAFARI] == true,
                onClick = { viewModel.openWindow(WindowType.SAFARI) }
            )

            // 4. Notes Icon
            DockIcon(
                icon = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFF176), Color(0xFFFBC02D))
                                )
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFE65100)))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF9E9E9E)))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF9E9E9E)))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF9E9E9E)))
                        }
                        Icon(
                            Icons.Default.EditNote,
                            "Notes",
                            tint = Color(0xFF3E2723),
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                },
                isOpen = openStatusMap[WindowType.NOTES] == true,
                onClick = { viewModel.openWindow(WindowType.NOTES) }
            )

            // 5. Terminal Icon
            DockIcon(
                icon = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1C1C1C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(">_", color = Color(0xFF00FF66), fontSize = 16.sp, modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                },
                isOpen = openStatusMap[WindowType.TERMINAL] == true,
                onClick = { viewModel.openWindow(WindowType.TERMINAL) }
            )

            // Separator line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            // 6. System Settings Icon
            DockIcon(
                icon = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFB0BEC5), Color(0xFF607D8B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, "Settings", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                },
                isOpen = openStatusMap[WindowType.SETTINGS] == true,
                onClick = { viewModel.openWindow(WindowType.SETTINGS) }
            )
        }
    }
}

@Composable
fun DockIcon(
    icon: @Composable () -> Unit,
    isOpen: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bouncing/Shrinking animation when pressed
    val scaleFactor by animateFloatAsState(
        targetValue = if (isPressed) 0.82f else 1.0f,
        label = "DockIconScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(46.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .scale(scaleFactor)
                .clip(RoundedCornerShape(11.dp))
                .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(11.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        ) {
            icon()
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Open state indicator dot
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isOpen) Color.White else Color.Transparent)
        )
    }
}
