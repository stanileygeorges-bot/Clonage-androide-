package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    phoneId: Int,
    initialUrl: String = "https://duckduckgo.com",
    appName: String? = null,
    allProfiles: List<PhoneProfileEntity> = emptyList(),
    onSelectPhone: (Int) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    var currentUrl by remember(initialUrl) { mutableStateOf(initialUrl) }
    var inputUrl by remember(initialUrl) { mutableStateOf(initialUrl) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isAdBlockActive by remember { mutableStateOf(true) }

    val activeProfile = allProfiles.find { it.phoneId == phoneId }
    val deviceDisplayName = activeProfile?.name ?: "Appareil #$phoneId"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Multi-device Switcher Header Bar
        Surface(
            color = DeepSlate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                // Top device bar: Switch which mobile device is using Chrome
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(AccentGreen)
                        )
                        Text(
                            text = "📱 $deviceDisplayName ao anaty Chrome",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Sandbox Isolation Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🛡️ Sandbox Mitokana",
                            color = CyanBright,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // If multiple devices exist, allow instant device switching in Chrome
                if (allProfiles.size > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allProfiles.size) { index ->
                            val profile = allProfiles[index]
                            val isCurrent = profile.phoneId == phoneId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isCurrent) CyberCyan.copy(alpha = 0.25f)
                                        else Color.White.copy(alpha = 0.06f)
                                    )
                                    .clickable { onSelectPhone(profile.phoneId) }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "📱 #${profile.phoneId} ${profile.name.take(12)}",
                                    color = if (isCurrent) CyanBright else Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // App Bar & URL Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSlate)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Miverina",
                    tint = Color.White
                )
            }

            // URL input text field
            OutlinedTextField(
                value = inputUrl,
                onValueChange = { inputUrl = it },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = if (phoneId == 1) Icons.Default.Lock else Icons.Default.Shield,
                        contentDescription = "Security",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            )

            // Go / Search button
            IconButton(
                onClick = {
                    val formatted = when {
                        inputUrl.startsWith("http://") || inputUrl.startsWith("https://") -> inputUrl
                        inputUrl.contains(".") && !inputUrl.contains(" ") -> "https://$inputUrl"
                        else -> "https://duckduckgo.com/?q=${inputUrl.replace(" ", "+")}"
                    }
                    currentUrl = formatted
                    inputUrl = formatted
                    webViewInstance?.loadUrl(formatted)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Mandehana",
                    tint = accentColor
                )
            }

            // Refresh
            IconButton(
                onClick = { webViewInstance?.reload() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Avereno",
                    tint = Color.White
                )
            }
        }

        // AI Navigator Status Banner (Highlighting: Tsy mila maka APK!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131D2E))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonViolet,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (appName != null) "🤖 IA: Mampiasa an'i '$appName' ao anaty Chrome (Tsy mila APK)"
                    else "🤖 IA Navigator: Mampiasa ny fampiharana ao anaty Chrome sandbox",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.5.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // AdBlock Toggle
            TextButton(
                onClick = { isAdBlockActive = !isAdBlockActive },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                modifier = Modifier.height(24.dp)
            ) {
                Text(
                    text = if (isAdBlockActive) "🛡️ AdBlock ON" else "AdBlock OFF",
                    color = if (isAdBlockActive) AccentGreen else TextSecondary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Quick Bookmarks Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSlate.copy(alpha = 0.8f))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BookmarkChip("DuckDuckGo", accentColor) {
                val url = "https://duckduckgo.com"
                inputUrl = url
                currentUrl = url
                webViewInstance?.loadUrl(url)
            }
            BookmarkChip("Wikipedia", accentColor) {
                val url = "https://mg.wikipedia.org"
                inputUrl = url
                currentUrl = url
                webViewInstance?.loadUrl(url)
            }
            BookmarkChip("Google", accentColor) {
                val url = "https://www.google.com"
                inputUrl = url
                currentUrl = url
                webViewInstance?.loadUrl(url)
            }
        }

        // Linear Progress bar when loading
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = accentColor,
                trackColor = Color.Transparent
            )
        }

        // Real Interactive Android WebView
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                isLoading = true
                                if (url != null) inputUrl = url
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }
                        }
                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BookmarkChip(
    title: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp
        )
    }
}
