package com.example.models

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowType {
    SAFARI,
    NOTES,
    TERMINAL,
    FINDER,
    SETTINGS,
    ABOUT_MAC
}

data class WindowItem(
    val type: WindowType,
    val title: String,
    val x: Dp = 40.dp,
    val y: Dp = 100.dp,
    val width: Dp = 320.dp,
    val height: Dp = 420.dp,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val zIndex: Float = 1f
)

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable?,
    val launchIntent: Intent?
)

data class TerminalLine(
    val text: String,
    val type: LineType = LineType.OUTPUT
)

enum class LineType {
    INPUT,
    OUTPUT,
    ERROR,
    SUCCESS
}

data class FinderFile(
    val name: String,
    val isDirectory: Boolean,
    val content: String = "",
    val iconRes: String = "folder" // "folder", "text", "safari"
)
