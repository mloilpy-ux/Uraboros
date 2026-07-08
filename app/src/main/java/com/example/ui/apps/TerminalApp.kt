package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.LineType
import com.example.viewmodels.LauncherViewModel

@Composable
fun TerminalApp(viewModel: LauncherViewModel, modifier: Modifier = Modifier) {
    val history by viewModel.terminalHistory.collectAsState()
    var inputCommand by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto scroll to bottom when history changes
    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(10.dp)
    ) {
        // Scrollable logs area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(history) { line ->
                val color = when (line.type) {
                    LineType.INPUT -> Color(0xFF81D4FA)
                    LineType.SUCCESS -> Color(0xFF66BB6A)
                    LineType.ERROR -> Color(0xFFEF5350)
                    LineType.OUTPUT -> Color.White.copy(alpha = 0.9f)
                }

                Text(
                    text = line.text,
                    color = color,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Input Prompt Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "visitor@macbook-pro ~ % ",
                color = Color(0xFF9F7AEA),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            BasicTextField(
                value = inputCommand,
                onValueChange = { inputCommand = it },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                ),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.executeTerminalCommand(inputCommand)
                        inputCommand = ""
                    }
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Emulated Return key for mobile convenience
            Text(
                text = "Return",
                color = Color(0xFF7C3AED),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .clickable {
                        viewModel.executeTerminalCommand(inputCommand)
                        inputCommand = ""
                    }
                    .padding(horizontal = 6.dp)
            )
        }
    }
}
