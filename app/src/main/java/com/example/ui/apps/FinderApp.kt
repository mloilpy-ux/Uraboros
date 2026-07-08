package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.FinderFile
import com.example.viewmodels.LauncherViewModel

@Composable
fun FinderApp(viewModel: LauncherViewModel, modifier: Modifier = Modifier) {
    val currentPath by viewModel.currentPath.collectAsState()
    val allFiles = viewModel.simulatedFiles

    val currentFiles = remember(currentPath) {
        allFiles[currentPath] ?: emptyList()
    }

    var selectedFile by remember { mutableStateOf<FinderFile?>(null) }
    var viewingFileContent by remember { mutableStateOf<FinderFile?>(null) }

    Row(modifier = modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
        // --- FINDER LEFT SIDEBAR ---
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(Color(0xFF141414))
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = "Favorites",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )

            SidebarItem("Desktop", Icons.Default.Computer, currentPath == "/User/visitor/Desktop") {
                viewModel.executeTerminalCommand("cd Desktop")
            }
            SidebarItem("Documents", Icons.Default.Description, currentPath == "/User/visitor/Documents") {
                viewModel.executeTerminalCommand("cd Documents")
            }
            SidebarItem("Downloads", Icons.Default.Download, currentPath == "/User/visitor/Downloads") {
                viewModel.executeTerminalCommand("cd Downloads")
            }
            SidebarItem("Home", Icons.Default.Home, currentPath == "/User/visitor") {
                viewModel.executeTerminalCommand("cd ~")
            }
        }

        // --- FINDER MAIN FILE GRID ---
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // Path breadcrumbs header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(Color(0xFF141414))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back/Forward arrows
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = if (currentPath != "/User/visitor") Color.White else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(enabled = currentPath != "/User/visitor") {
                            viewModel.executeTerminalCommand("cd cd ..") // Trigger viewmodel cd parent
                            val parts = currentPath.split("/")
                            if (parts.size > 2) {
                                val parent = parts.dropLast(1).joinToString("/")
                                viewModel.executeTerminalCommand("cd $parent")
                            }
                        }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = currentPath,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Grid items container
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (currentFiles.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("This folder is empty.", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentFiles) { file ->
                            val isSelected = selectedFile?.name == file.name
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFF7C3AED).copy(alpha = 0.3f) else Color.Transparent)
                                    .clickable {
                                        if (isSelected) {
                                            // Double tap action: Navigate or Open file
                                            if (file.isDirectory) {
                                                val target = if (currentPath == "/") "/${file.name}" else "$currentPath/${file.name}"
                                                viewModel.executeTerminalCommand("cd $target")
                                                selectedFile = null
                                            } else {
                                                viewingFileContent = file
                                            }
                                        } else {
                                            selectedFile = file
                                        }
                                    }
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Dynamic icons matching macOS
                                val iconColor = if (file.isDirectory) Color(0xFF4FC3F7) else Color.White
                                Icon(
                                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                                    contentDescription = file.name,
                                    tint = iconColor,
                                    modifier = Modifier.size(34.dp)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = file.name,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // File Reader modal layer overlay
                if (viewingFileContent != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f))
                            .clickable { viewingFileContent = null } // tap to dismiss
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2B2B2B))
                                .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = viewingFileContent?.name ?: "Quick Look",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Default.Close,
                                    "Close",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewingFileContent = null }
                                )
                            }

                            Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                            Text(
                                text = viewingFileContent?.content ?: "",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarItem(
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
            .padding(horizontal = 10.dp, vertical = 6.dp),
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
