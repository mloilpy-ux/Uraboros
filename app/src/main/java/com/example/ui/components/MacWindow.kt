package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.models.WindowItem
import com.example.viewmodels.LauncherViewModel

@Composable
fun MacWindow(
    window: WindowItem,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    if (window.isMinimized) return

    val density = LocalDensity.current

    // Base layout modifiers
    val sizeModifier = if (window.isMaximized) {
        Modifier
            .fillMaxSize()
            .padding(top = 30.dp, bottom = 76.dp) // Leave menu bar and dock visible
    } else {
        Modifier
            .offset(x = window.x, y = window.y)
            .size(width = window.width, height = window.height)
    }

    Box(
        modifier = sizeModifier
            .zIndex(window.zIndex)
            .shadow(24.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A0A0A).copy(alpha = 0.88f)) // Sophisticated Dark base
            .border(0.5.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
            .pointerInput(window.type) {
                // Focus window when tapped anywhere
                detectTapGestures(
                    onPress = { viewModel.focusWindow(window.type) }
                )
            }
            .then(modifier)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- TITLE BAR (HEADER) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(Color(0xFF141414)) // Darker matching title bar
                    .pointerInput(window.type) {
                        // Focus and drag support
                        detectDragGestures(
                            onDragStart = { viewModel.focusWindow(window.type) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val dx = with(density) { dragAmount.x.toDp() }
                                val dy = with(density) { dragAmount.y.toDp() }
                                viewModel.dragWindow(window.type, dx, dy)
                            }
                        )
                    }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // macOS traffic lights
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close (Red)
                    TrafficLightButton(Color(0xFFFF5F56)) {
                        viewModel.closeWindow(window.type)
                    }
                    // Minimize (Yellow)
                    TrafficLightButton(Color(0xFFFFBD2E)) {
                        viewModel.minimizeWindow(window.type)
                    }
                    // Maximize (Green)
                    TrafficLightButton(Color(0xFF27C93F)) {
                        viewModel.toggleMaximize(window.type)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title
                Text(
                    text = window.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Balanced spacing spacer mirroring traffic lights width
                Spacer(modifier = Modifier.width(52.dp))
            }

            // --- WINDOW CONTENT CONTAINER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0F0F0F)) // Sophisticated Charcoal base
            ) {
                content()
            }
        }

        // --- RESIZE GRIIPPER (Bottom Right corner, only if not maximized) ---
        if (!window.isMaximized) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .pointerInput(window.type) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val dw = with(density) { dragAmount.x.toDp() }
                            val dh = with(density) { dragAmount.y.toDp() }
                            viewModel.resizeWindow(window.type, dw, dh)
                        }
                    }
            )
        }
    }
}

@Composable
fun TrafficLightButton(color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
            .border(0.5.dp, color.copy(alpha = 0.3f), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() }
                )
            }
    )
}
