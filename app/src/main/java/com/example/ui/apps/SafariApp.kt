package com.example.ui.apps

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SafariApp(modifier: Modifier = Modifier) {
    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var currentUrl by remember { mutableStateOf("https://www.google.com") }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
        // --- BROWSER CONTROL BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFF141414))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Buttons
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = if (canGoBack) Color.White else Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(enabled = canGoBack) {
                        webViewInstance?.goBack()
                    }
            )

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Forward",
                tint = if (canGoForward) Color.White else Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(enabled = canGoForward) {
                        webViewInstance?.goForward()
                    }
            )

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reload",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        webViewInstance?.reload()
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // URL Address Bar
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F0F0F))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // BasicTextField for high customizability
                BasicTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (urlInput.isEmpty()) {
                                  Text("Search or enter website name", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
                            }
                            innerTextField()
                        }
                    }
                )

                // Quick load triggers when we click return / press something,
                // or we can add a small "Go" text
                Text(
                    text = "Go",
                    color = Color(0xFF7C3AED),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clickable {
                            var formatted = urlInput.trim()
                            if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
                                formatted = "https://$formatted"
                            }
                            currentUrl = formatted
                            urlInput = formatted
                        }
                        .padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFF7C3AED),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // --- WEBVIEW CONTAINER ---
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                isLoading = true
                                url?.let {
                                    urlInput = it
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                canGoBack = view?.canGoBack() ?: false
                                canGoForward = view?.canGoForward() ?: false
                            }
                        }
                        webChromeClient = WebChromeClient()
                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    if (webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                    webViewInstance = webView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
