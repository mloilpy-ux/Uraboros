package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun MacWallpaper(theme: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        when (theme) {
            "Sophisticated" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Deep #0a0a0a background
                    drawRect(color = Color(0xFF0A0A0A))

                    // Glowing Indigo/Violet orb top-left: rgba(79,70,229,0.35)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4F46E5).copy(alpha = 0.35f),
                                Color(0xFF7C3AED).copy(alpha = 0.20f),
                                Color.Transparent
                            ),
                            center = Offset(width * -0.1f, height * -0.1f),
                            radius = width * 1.3f
                        ),
                        center = Offset(width * -0.1f, height * -0.1f),
                        radius = width * 1.3f
                    )

                    // Glowing Pink/Crimson orb bottom-right: rgba(219,39,119,0.2)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFDB2777).copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = Offset(width * 1.1f, height * 1.1f),
                            radius = width * 1.1f
                        ),
                        center = Offset(width * 1.1f, height * 1.1f),
                        radius = width * 1.1f
                    )
                }
            }
            "Sonoma" -> {
                // Vibrant macOS Sonoma style: Deep orange, magenta, and deep blue waves
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Deep blue background
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF0F1A30), Color(0xFF1D3557)),
                            start = Offset(0f, 0f),
                            end = Offset(width, height)
                        )
                    )

                    // Magenta wave blob
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF9E0059).copy(alpha = 0.8f), Color.Transparent),
                            center = Offset(width * 0.8f, height * 0.3f),
                            radius = width * 0.7f
                        ),
                        center = Offset(width * 0.8f, height * 0.3f),
                        radius = width * 0.7f
                    )

                    // Vibrant orange wave blob
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF5E00).copy(alpha = 0.75f), Color.Transparent),
                            center = Offset(width * 0.2f, height * 0.7f),
                            radius = width * 0.8f
                        ),
                        center = Offset(width * 0.2f, height * 0.7f),
                        radius = width * 0.8f
                    )

                    // Golden highlight wave
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFB703).copy(alpha = 0.6f), Color.Transparent),
                            center = Offset(width * 0.6f, height * 0.8f),
                            radius = width * 0.6f
                        ),
                        center = Offset(width * 0.6f, height * 0.8f),
                        radius = width * 0.6f
                    )
                }
            }
            "Sequoia" -> {
                // macOS Sequoia style: warm gold, dark aubergine, and rich red-crimson
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Dark Aubergine background
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1A0B2E), Color(0xFF3B1354)),
                            start = Offset(0f, 0f),
                            end = Offset(width, height)
                        )
                    )

                    // Crimson highlight blob
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE63946).copy(alpha = 0.7f), Color.Transparent),
                            center = Offset(width * 0.3f, height * 0.2f),
                            radius = width * 0.8f
                        ),
                        center = Offset(width * 0.3f, height * 0.2f),
                        radius = width * 0.8f
                    )

                    // Deep gold warm layer
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFB703).copy(alpha = 0.65f), Color.Transparent),
                            center = Offset(width * 0.8f, height * 0.6f),
                            radius = width * 0.7f
                        ),
                        center = Offset(width * 0.8f, height * 0.6f),
                        radius = width * 0.7f
                    )

                    // Bright Yellow Sequoia accent
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFE066).copy(alpha = 0.5f), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.9f),
                            radius = width * 0.5f
                        ),
                        center = Offset(width * 0.5f, height * 0.9f),
                        radius = width * 0.5f
                    )
                }
            }
            "Ventura" -> {
                // macOS Ventura style: bright abstract amber and peach curves
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Peach/Orange bg
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFF28482), Color(0xFFF6BD60)),
                            start = Offset(0f, 0f),
                            end = Offset(width, height)
                        )
                    )

                    // Sunny Amber blob
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF9F1C).copy(alpha = 0.8f), Color.Transparent),
                            center = Offset(width * 0.2f, height * 0.3f),
                            radius = width * 0.8f
                        ),
                        center = Offset(width * 0.2f, height * 0.3f),
                        radius = width * 0.8f
                    )

                    // Deep Peach accents
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE5989B).copy(alpha = 0.7f), Color.Transparent),
                            center = Offset(width * 0.7f, height * 0.8f),
                            radius = width * 0.7f
                        ),
                        center = Offset(width * 0.7f, height * 0.8f),
                        radius = width * 0.7f
                    )

                    // Soft Turquoise transition
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF84A59D).copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(width * 0.9f, height * 0.1f),
                            radius = width * 0.5f
                        ),
                        center = Offset(width * 0.9f, height * 0.1f),
                        radius = width * 0.5f
                    )
                }
            }
            "Aurora" -> {
                // Electric glowing Aurora: deep violet, emerald green and flowing cyan
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Midnight Blue background
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF03071E), Color(0xFF130022)),
                            start = Offset(0f, 0f),
                            end = Offset(width, height)
                        )
                    )

                    // Electric Violet neon blob
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF7209B7).copy(alpha = 0.75f), Color.Transparent),
                            center = Offset(width * 0.2f, height * 0.4f),
                            radius = width * 0.8f
                        ),
                        center = Offset(width * 0.2f, height * 0.4f),
                        radius = width * 0.8f
                    )

                    // Bright Neon Emerald Green
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF4CC9F0).copy(alpha = 0.65f), Color.Transparent),
                            center = Offset(width * 0.8f, height * 0.8f),
                            radius = width * 0.7f
                        ),
                        center = Offset(width * 0.8f, height * 0.8f),
                        radius = width * 0.7f
                    )

                    // Glowing Cyan pulse
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF4895EF).copy(alpha = 0.5f), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.2f),
                            radius = width * 0.6f
                        ),
                        center = Offset(width * 0.5f, height * 0.2f),
                        radius = width * 0.6f
                    )
                }
            }
            else -> {
                // Graphite: Sleek, high-contrast professional dark slate
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0D0D0D), Color(0xFF1E2022), Color(0xFF0A0A0A))
                            )
                        )
                    )
            }
        }
    }
}
