package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*

/**
 * Data class representing an application discovered or cataloged by the AI
 */
data class DiscoveredApp(
    val id: String,
    val name: String,
    val category: String,
    val webUrl: String,
    val iconKey: String,
    val icon: ImageVector,
    val brandColor: Color,
    val aiInsight: String,
    val isVerifiedWebCompatible: Boolean = true
)

/**
 * Modal permitting users to search and add ANY application (not just YouTube/WhatsApp/Facebook).
 * If the app is not downloadable as APK, the AI discovers its Web App URL and each virtual mobile
 * device can independently enter Chrome and run it in an isolated sandbox.
 */
@Composable
fun AiAppDiscoveryModal(
    activePhoneId: Int,
    allProfiles: List<PhoneProfileEntity> = emptyList(),
    onDismiss: () -> Unit,
    onOpenInChrome: (url: String, appName: String, targetPhoneId: Int) -> Unit,
    onOpenMultiDeviceChrome: (url: String, appName: String) -> Unit = { _, _ -> },
    onAddAppToDevice: (name: String, url: String, iconKey: String, targetPhoneId: Int) -> Unit,
    onAddAppToAllDevices: (name: String, url: String, iconKey: String) -> Unit = { _, _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("DAHOLO") }
    var selectedTargetPhone by remember { mutableStateOf(activePhoneId) }
    var isTargetingAllDevices by remember { mutableStateOf(false) }

    // Pre-curated catalog of popular web apps known to the AI
    val appCatalog = remember {
        listOf(
            DiscoveredApp(
                id = "chatgpt",
                name = "ChatGPT",
                category = "IA & Fitaovana",
                webUrl = "https://chatgpt.com",
                iconKey = "browser",
                icon = Icons.Default.SmartToy,
                brandColor = Color(0xFF10A37F),
                aiInsight = "Fitaovana IA mpanampy. Miasa tsara ao anatin'ny Chrome Sandbox."
            ),
            DiscoveredApp(
                id = "tiktok",
                name = "TikTok",
                category = "Horonan-tsary",
                webUrl = "https://www.tiktok.com",
                iconKey = "tiktok",
                icon = Icons.Default.Movie,
                brandColor = Color(0xFFFE2C55),
                aiInsight = "Horonan-tsary fohy sy fialamboly. Miasa amin'ny dika Web."
            ),
            DiscoveredApp(
                id = "canva",
                name = "Canva",
                category = "IA & Fitaovana",
                webUrl = "https://www.canva.com",
                iconKey = "browser",
                icon = Icons.Default.Palette,
                brandColor = Color(0xFF00C4CC),
                aiInsight = "Famolavolana sary sy afisy. Web App feno ao anaty Chrome."
            ),
            DiscoveredApp(
                id = "tradingview",
                name = "TradingView",
                category = "Bourse & Vola",
                webUrl = "https://www.tradingview.com",
                iconKey = "browser",
                icon = Icons.Default.TrendingUp,
                brandColor = Color(0xFF2962FF),
                aiInsight = "Tabilao famakafakana vola crypto sy stock. Azo ampiasaina mivantana."
            ),
            DiscoveredApp(
                id = "spotify",
                name = "Spotify Web",
                category = "Mozika & Feo",
                webUrl = "https://open.spotify.com",
                iconKey = "browser",
                icon = Icons.Default.MusicNote,
                brandColor = Color(0xFF1DB954),
                aiInsight = "Mozika sy podcast mitokana tsy misy fanelingelenana."
            ),
            DiscoveredApp(
                id = "telegram",
                name = "Telegram Web",
                category = "Fifandraisana",
                webUrl = "https://web.telegram.org",
                iconKey = "telegram",
                icon = Icons.Default.Send,
                brandColor = Color(0xFF2AABEE),
                aiInsight = "Kaonty Telegram mitokana tanteraka amin'ny clone."
            ),
            DiscoveredApp(
                id = "aliexpress",
                name = "AliExpress",
                category = "Fiantsenana",
                webUrl = "https://m.aliexpress.com",
                iconKey = "browser",
                icon = Icons.Default.ShoppingCart,
                brandColor = Color(0xFFFF4747),
                aiInsight = "Tsena fiantsenana iraisam-pirenena. Cookies mitokana."
            ),
            DiscoveredApp(
                id = "netflix",
                name = "Netflix Web",
                category = "Horonan-tsary",
                webUrl = "https://www.netflix.com",
                iconKey = "youtube",
                icon = Icons.Default.Tv,
                brandColor = Color(0xFFE50914),
                aiInsight = "Streaming sarimihetsika sy andian-tantara."
            ),
            DiscoveredApp(
                id = "github",
                name = "GitHub Mobile",
                category = "IA & Fitaovana",
                webUrl = "https://github.com",
                iconKey = "browser",
                icon = Icons.Default.Code,
                brandColor = Color(0xFF8B5CF6),
                aiInsight = "Fitehirizana kaody sy tetikasa rindrambaiko."
            ),
            DiscoveredApp(
                id = "duolingo",
                name = "Duolingo",
                category = "Fianarana",
                webUrl = "https://www.duolingo.com",
                iconKey = "browser",
                icon = Icons.Default.School,
                brandColor = Color(0xFF58CC02),
                aiInsight = "Fianarana teny vahiny mifototra amin'ny lalao."
            ),
            DiscoveredApp(
                id = "reddit",
                name = "Reddit",
                category = "Fifandraisana",
                webUrl = "https://www.reddit.com",
                iconKey = "browser",
                icon = Icons.Default.Forum,
                brandColor = Color(0xFFFF4500),
                aiInsight = "Kominote sy adihevitra an-tserasera maneran-tany."
            ),
            DiscoveredApp(
                id = "google",
                name = "Google Search & Tools",
                category = "IA & Fitaovana",
                webUrl = "https://www.google.com",
                iconKey = "browser",
                icon = Icons.Default.Search,
                brandColor = CyberCyan,
                aiInsight = "Fikarohana sy servisy Google amin'ny sandbox Chrome."
            )
        )
    }

    // Categories
    val categories = listOf("DAHOLO", "IA & Fitaovana", "Horonan-tsary", "Fifandraisana", "Bourse & Vola", "Fiantsenana", "Mozika & Feo")

    // Filtered list
    val filteredCatalog = remember(searchQuery, selectedCategory) {
        val list = if (selectedCategory == "DAHOLO") appCatalog else appCatalog.filter { it.category == selectedCategory }
        if (searchQuery.isBlank()) {
            list
        } else {
            val matches = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.aiInsight.contains(searchQuery, ignoreCase = true)
            }
            if (matches.isNotEmpty()) {
                matches
            } else {
                // Dynamic AI Synthesis for ANY unknown app typed by the user!
                listOf(
                    DiscoveredApp(
                        id = "custom-${searchQuery.lowercase().replace(" ", "")}",
                        name = searchQuery.trim(),
                        category = "Fampiharana Vaovao",
                        webUrl = if (searchQuery.contains(".")) {
                            if (searchQuery.startsWith("http")) searchQuery else "https://$searchQuery"
                        } else {
                            "https://duckduckgo.com/?q=${searchQuery.replace(" ", "+")}"
                        },
                        iconKey = "browser",
                        icon = Icons.Default.Language,
                        brandColor = CyberCyan,
                        aiInsight = "Karohan'ny IA amin'ny Internet: '${searchQuery.trim()}'. Vonona hosokafana sy hapetaka ao anaty Chrome.",
                        isVerifiedWebCompatible = true
                    )
                )
            }
        }
    }

    // Pulse animation for AI searching status
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCyan.copy(alpha = 0.2f))
                                .border(1.dp, CyberCyan, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Fikarohana App amin'ny IA",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Apareil #$activePhoneId • Mifidiana na mikaroha izay app tianao",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Hikatona", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Input with AI indicator
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Soraty ny anaran'ny app (oh: ChatGPT, TikTok, Canva, TradingView...)",
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Fafao", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_app_search_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = LightSlate.copy(alpha = 0.6f),
                        focusedContainerColor = CardSlate,
                        unfocusedContainerColor = CardSlate,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Device Target Selector: Choose whether to launch on ALL devices or a specific phone
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepSlate)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📱 Appareil hampiasana an'ilay app ao anaty Chrome:",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isTargetingAllDevices) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonViolet.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("👥 Appareil Tsirairay Avy", color = NeonViolet, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Option 1: All Devices
                        item {
                            FilterChip(
                                selected = isTargetingAllDevices,
                                onClick = { isTargetingAllDevices = true },
                                label = { Text("🌐 Appareil Tsirairay Avy (${allProfiles.size})", fontSize = 10.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonViolet.copy(alpha = 0.3f),
                                    selectedLabelColor = NeonViolet
                                )
                            )
                        }

                        // Option 2: Individual Devices
                        items(allProfiles) { profile ->
                            val isSelected = !isTargetingAllDevices && selectedTargetPhone == profile.phoneId
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    isTargetingAllDevices = false
                                    selectedTargetPhone = profile.phoneId
                                },
                                label = { Text("📱 #${profile.phoneId} ${profile.name.take(10)}", fontSize = 10.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan.copy(alpha = 0.3f),
                                    selectedLabelColor = CyanBright
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Informational Banner: Tsy mila maka APK (Mampiasa Chrome amin'ny alalan'ny IA)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f))
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "💡 Tsy voatery maka APK (Chrome Web Sandbox)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Raha tsy azo alaina na sarotra alaina ilay fampiharana, ny appareil mobile tsirairay avy dia mampiasa azy mivantana ao anaty Chrome amin'ny alalan'ny IA.",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 13.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AI Status Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCyan.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AccentGreen.copy(alpha = pulseAlpha))
                    )
                    Text(
                        text = if (searchQuery.isBlank()) "Ny IA dia afaka mikaroka sy mampiditra app na tranonkala rehetra ao anaty Chrome."
                        else "Ny IA dia namantatra sy nampifanaraka ny: '${searchQuery.trim()}'",
                        color = CyanBright,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                selectedLabelColor = CyanBright
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = LightSlate.copy(alpha = 0.4f),
                                selectedBorderColor = CyberCyan
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of Discovered Apps
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCatalog, key = { it.id }) { app ->
                        DiscoveredAppCard(
                            app = app,
                            targetPhoneId = selectedTargetPhone,
                            isTargetingAllDevices = isTargetingAllDevices,
                            onOpenChrome = {
                                if (isTargetingAllDevices) {
                                    onOpenMultiDeviceChrome(app.webUrl, app.name)
                                } else {
                                    onOpenInChrome(app.webUrl, app.name, selectedTargetPhone)
                                }
                            },
                            onAddToDevice = {
                                if (isTargetingAllDevices) {
                                    onAddAppToAllDevices(app.name, app.webUrl, app.iconKey)
                                } else {
                                    onAddAppToDevice(app.name, app.webUrl, app.iconKey, selectedTargetPhone)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoveredAppCard(
    app: DiscoveredApp,
    targetPhoneId: Int,
    isTargetingAllDevices: Boolean,
    onOpenChrome: () -> Unit,
    onAddToDevice: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.95f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, app.brandColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Row: Icon, Name, Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(app.brandColor.copy(alpha = 0.15f))
                            .border(1.dp, app.brandColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = app.icon,
                            contentDescription = app.name,
                            tint = app.brandColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = app.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = app.category,
                            color = TextSecondary,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // Web URL badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepSlate)
                        .border(1.dp, LightSlate.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🌐 Web App",
                        color = CyanBright,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // AI Insight text
            Text(
                text = "💡 ${app.aiInsight}",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            // No APK requirement indicator + Target device tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "✨ Tsy mila maka APK • Ao anaty Chrome",
                        color = AccentGreen,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isTargetingAllDevices) "👥 Fitaovana Rehetra" else "📱 Apareil #$targetPhoneId",
                    color = CyanBright,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // URL indicator
            Text(
                text = "URL: ${app.webUrl}",
                color = CyberCyan.copy(alpha = 0.8f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(color = LightSlate.copy(alpha = 0.3f), thickness = 0.8.dp)

            // Action Buttons: [Sokafy ao anaty Chrome] & [Apetaho]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Open inside Chrome directly
                Button(
                    onClick = onOpenChrome,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTargetingAllDevices) NeonViolet.copy(alpha = 0.25f) else CyberCyan.copy(alpha = 0.2f),
                        contentColor = if (isTargetingAllDevices) NeonViolet else CyanBright
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isTargetingAllDevices) NeonViolet.copy(alpha = 0.7f) else CyberCyan.copy(alpha = 0.7f)
                    )
                ) {
                    Icon(
                        imageVector = if (isTargetingAllDevices) Icons.Default.Groups else Icons.Default.OpenInBrowser,
                        contentDescription = "Chrome",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTargetingAllDevices) "Alefaso amin'ny Rehetra" else "Sokafy ao Chrome",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Button 2: Install/Add to device
                Button(
                    onClick = onAddToDevice,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen.copy(alpha = 0.2f),
                        contentColor = AccentGreen
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Apetaho",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTargetingAllDevices) "Apetaho Rehetra" else "Apetaho",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
