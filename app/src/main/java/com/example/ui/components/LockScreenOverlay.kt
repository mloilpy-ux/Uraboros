package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodels.LauncherViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LockScreenOverlay(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val isLocked by viewModel.isLocked.collectAsState()
    val activeTheme by viewModel.wallpaperTheme.collectAsState()

    var timeStr by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val date = Date()
            timeStr = SimpleDateFormat("h:mm", Locale.getDefault()).format(date)
            dateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(date)
            delay(1000)
        }
    }

    AnimatedVisibility(
        visible = isLocked,
        enter = fadeIn(),
        exit = fadeOut() + scaleOut(targetScale = 1.15f) // realistic zoom reveal animation
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable { /* Block bottom clicks */ }
        ) {
            // Background is the active wallpaper, but blurred
            MacWallpaper(theme = activeTheme)

            // Blur overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Time Display
                Text(
                    text = timeStr,
                    color = Color.White,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-1).sp
                )

                // Date Display
                Text(
                    text = dateStr,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // User profile avatar (styled like macOS)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFE040FB), Color(0xFF00E5FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Account Name
                Text(
                    text = "visitor@macbook-pro",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Input bar
                Row(
                    modifier = Modifier
                        .width(180.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                        cursorBrush = SolidColor(Color.White),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                viewModel.unlock()
                                passwordInput = ""
                            }
                        ),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box {
                                if (passwordInput.isEmpty()) {
                                    Text("Enter Password", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                                }
                                innerTextField()
                            }
                        }
                    )

                    // Arrow submit icon
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Login",
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .clickable {
                                viewModel.unlock()
                                passwordInput = ""
                            }
                    )
                }

                Spacer(modifier = Modifier.weight(1.2f))

                // Bottom actions (Sleep, Restart, Shut Down)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    LockScreenActionButton("Sleep", Icons.Default.PowerSettingsNew) {
                        viewModel.unlock() // waking up is fast login
                    }
                    LockScreenActionButton("Restart", Icons.Default.Refresh) {
                        passwordInput = ""
                    }
                }
            }
        }
    }
}

@Composable
fun LockScreenActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .border(0.5.dp, Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 9.sp
        )
    }
}
