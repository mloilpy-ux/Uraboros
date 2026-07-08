package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodels.LauncherViewModel

@Composable
fun NotesApp(viewModel: LauncherViewModel, modifier: Modifier = Modifier) {
    val notes by viewModel.notes.collectAsState()
    val activeIndex by viewModel.activeNoteIndex.collectAsState()

    val activeNoteText = remember(notes, activeIndex) {
        if (activeIndex in notes.indices) notes[activeIndex] else ""
    }

    Row(modifier = modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
        // --- SIDEBAR (List of notes) ---
        Column(
            modifier = Modifier
                .width(110.dp)
                .fillMaxHeight()
                .background(Color(0xFF141414))
                .padding(vertical = 8.dp)
        ) {
            // Note Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notes", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Note",
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { viewModel.createNote() }
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { viewModel.deleteActiveNote() }
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 6.dp))

            // Notes List
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(notes) { index, noteText ->
                    val isSelected = index == activeIndex
                    val lines = noteText.split("\n")
                    val title = lines.firstOrNull() ?: "Empty Note"
                    val subtitle = lines.drop(1).firstOrNull { it.trim().isNotEmpty() } ?: "No additional text"

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectNote(index) }
                            .background(if (isSelected) Color(0xFF7C3AED).copy(alpha = 0.20f) else Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color(0xFF9F7AEA) else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // --- NOTE EDITOR CANVAS ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFFFFFDF0)) // Warm paper background matching notes app style
                .padding(14.dp)
        ) {
            if (notes.isNotEmpty() && activeIndex in notes.indices) {
                BasicTextField(
                    value = activeNoteText,
                    onValueChange = { viewModel.updateActiveNoteContent(it) },
                    textStyle = TextStyle(
                        color = Color(0xFF3E2723), // Dark brown ink
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    ),
                    cursorBrush = SolidColor(Color(0xFFE65100)),
                    modifier = Modifier.fillMaxSize(),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (activeNoteText.isEmpty()) {
                                Text("New Note Content...", color = Color.Gray.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                            innerTextField()
                        }
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Note Selected", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
