package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class StoreApkItem(
    val id: String,
    val name: String,
    val sourceStore: String,
    val category: String,
    val sizeMb: Int,
    val iconKey: String,
    val icon: ImageVector,
    val brandColor: Color,
    val rating: String,
    val description: String
)

/**
 * SIMULATED APP STORE & BATCH APP MANAGER:
 * 1. Uses AI to fetch and install APKs from trusted sources (Google Play, APKPure, Uptodown, F-Droid).
 * 2. Ensures sandboxed execution in isolated containers.
 * 3. Supports changing/replacing apps across ALL devices simultaneously ("Azo atao ihany koa ny manova anireo App mipetaka aminy appareil rehetra").
 * 4. Shows live Internet connection status.
 */
@Composable
fun SimulatedAppStoreModal(
    activePhoneId: Int,
    allProfiles: List<PhoneProfileEntity>,
    isGlobalInternetConnected: Boolean,
    onDismiss: () -> Unit,
    onInstallApk: (appName: String, sourceStore: String, iconKey: String, targetPhoneId: Int?, sizeMb: Int) -> Unit,
    onBatchUpdateAllDevices: (selectedKeys: List<String>, replaceExisting: Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = App Store APKs, 1 = Manova Apps Rehetra
    var searchQuery by remember { mutableStateOf("") }
    var selectedStoreSource by remember { mutableStateOf("DAHOLO") }
    var targetDeviceSelection by remember { mutableStateOf<Int?>(activePhoneId) } // null = All devices
    var isAiFetchingApk by remember { mutableStateOf(false) }
    var aiFetchStatusText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Curated catalog from trusted sources
    val defaultStoreCatalog = remember {
        listOf(
            StoreApkItem("chatgpt", "ChatGPT", "Google Play", "IA & Bot", 48, "chatgpt", Icons.Default.SmartToy, Color(0xFF10A37F), "4.9 ★", "IA mpanampy manan-tsaina amin'ny fikarohana sy asa."),
            StoreApkItem("vidmat", "Vidmat HD", "APKPure", "Horonan-tsary", 24, "vidmat", Icons.Default.Download, Color(0xFFFF5722), "4.8 ★", "Fampidinana horonan-tsary sy mozika haingana."),
            StoreApkItem("tiktok", "TikTok", "Google Play", "Media & Fialamboly", 92, "tiktok", Icons.Default.Movie, Color(0xFFFE2C55), "4.7 ★", "Feeds horonan-tsary fohy sy fialamboly maneran-tany."),
            StoreApkItem("whatsapp", "WhatsApp Messenger", "Google Play", "Messagerie", 38, "whatsapp", Icons.Default.Chat, AccentGreen, "4.8 ★", "Fifandraisana voaaro amin'ny E2E encryption."),
            StoreApkItem("telegram", "Telegram", "F-Droid", "Messagerie", 44, "telegram", Icons.Default.Send, Color(0xFF2AABEE), "4.9 ★", "Hafatra haingana sy fantsona fampahalalana."),
            StoreApkItem("capcut", "CapCut Video Editor", "APKPure", "Horonan-tsary", 85, "vidmat", Icons.Default.VideoLibrary, Color(0xFF00E5FF), "4.7 ★", "Fanitsiana horonan-tsary avo lenta amin'ny finday."),
            StoreApkItem("canva", "Canva Graphic Design", "Google Play", "Famolavolana", 35, "browser", Icons.Default.Palette, Color(0xFF00C4CC), "4.8 ★", "Famolavolana sary, afisy, sy famelabelarana."),
            StoreApkItem("snaptube", "Snaptube VIP", "Uptodown", "Media", 26, "vidmat", Icons.Default.FileDownload, Color(0xFFFFB300), "4.6 ★", "Fampidinana horonan-tsary avy amin'ny tranonkala."),
            StoreApkItem("tradingview", "TradingView", "Google Play", "Bourse & Vola", 42, "browser", Icons.Default.TrendingUp, Color(0xFF2962FF), "4.9 ★", "Famakafakana tsenam-bola sy crypto amin'ny fotoana tena izy."),
            StoreApkItem("duolingo", "Duolingo", "Google Play", "Fianarana", 56, "browser", Icons.Default.School, Color(0xFF58CC02), "4.8 ★", "Fianarana teny vahiny amin'ny fomba lalao."),
            StoreApkItem("spotify", "Spotify Music", "Google Play", "Mozika", 38, "browser", Icons.Default.MusicNote, Color(0xFF1DB954), "4.8 ★", "Henoy ireo hira sy podcast an-tapitrisany.")
        )
    }

    // State for Tab 2: Batch App Manager across ALL devices
    val allAvailableAppKeys = remember {
        listOf(
            Triple("vidmat", "Vidmat (Fampidinana Media)", Color(0xFFFF5722)),
            Triple("youtube", "YouTube (Horonan-tsary)", AccentRed),
            Triple("whatsapp", "WhatsApp (Messagerie)", AccentGreen),
            Triple("tiktok", "TikTok (Feeds Horonan-tsary)", Color(0xFFFE2C55)),
            Triple("chatgpt", "ChatGPT (IA Mpanampy)", Color(0xFF10A37F)),
            Triple("facebook", "Facebook (Tambazotra)", Color(0xFF1877F2)),
            Triple("telegram", "Telegram (Fifandraisana)", Color(0xFF2AABEE)),
            Triple("browser", "Chrome • Navigateur", CyberCyan),
            Triple("downloads", "Téléchargements & Fichiers", AccentAmber)
        )
    }
    val batchSelectedKeys = remember { mutableStateListOf("vidmat", "youtube", "whatsapp", "browser") }
    var replaceExistingAppsOnDevices by remember { mutableStateOf(false) }

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
                // 1. HEADER WITH INTERNET STATUS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCyan.copy(alpha = 0.2f))
                                .border(1.dp, CyberCyan, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shop, contentDescription = null, tint = CyanBright, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Virtual App Store (IA)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            // Internet status badge
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (isGlobalInternetConnected) AccentGreen else AccentRed)
                                )
                                Text(
                                    text = if (isGlobalInternetConnected) "Internet : Mifandray (En ligne)" else "Internet : Tsy mifandray",
                                    color = if (isGlobalInternetConnected) AccentGreen else AccentRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Akatona", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2. TABS: [ 🏬 App Store APKs ]  [ 🔄 Manova Apps Rehetra ]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) CyberCyan else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.GetApp, contentDescription = null, tint = if (selectedTab == 0) Color.Black else CyanBright, modifier = Modifier.size(16.dp))
                            Text("🏬 App Store (APKs)", color = if (selectedTab == 0) Color.Black else Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) NeonViolet else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = if (selectedTab == 1) Color.White else NeonViolet, modifier = Modifier.size(16.dp))
                            Text("🔄 Manova Apps Rehetra", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedTab == 0) {
                    // ==========================================
                    // TAB 0: VIRTUAL APP STORE (AI APK FETCHER)
                    // ==========================================
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // AI Sandboxed Execution Guarantee Banner
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DeepSlate)
                                    .border(1.dp, CyberCyan.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "Ny IA no misintona APK avy amin'ny loharano voamarina (Google Play, APKPure, Uptodown) ary mametraka azy ao anaty Sandbox voaaro tsy misy leakage.",
                                        color = CyanBright,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        // Search Bar
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Mikaroka app na mametraka rohy APK...", fontSize = 11.sp, color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp)) },
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            )
                        }

                        // Store Source Filter Chips
                        item {
                            val sources = listOf("DAHOLO", "Google Play", "APKPure", "Uptodown", "F-Droid")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(sources) { src ->
                                    val isSel = selectedStoreSource == src
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { selectedStoreSource = src },
                                        label = { Text(src, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                            selectedLabelColor = CyanBright
                                        )
                                    )
                                }
                            }
                        }

                        // Target Device Selector
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Fitaovana hapetrahana azy :", color = TextSecondary, fontSize = 11.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        FilterChip(
                                            selected = targetDeviceSelection == activePhoneId,
                                            onClick = { targetDeviceSelection = activePhoneId },
                                            label = { Text("Finday #$activePhoneId", fontSize = 10.sp) }
                                        )
                                        FilterChip(
                                            selected = targetDeviceSelection == null,
                                            onClick = { targetDeviceSelection = null },
                                            label = { Text("Rehetra (${allProfiles.size})", fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                        }

                        // Dynamic AI search result if query is not in catalog
                        val filteredCatalog = defaultStoreCatalog.filter {
                            (selectedStoreSource == "DAHOLO" || it.sourceStore.equals(selectedStoreSource, ignoreCase = true)) &&
                            (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true))
                        }

                        if (filteredCatalog.isEmpty() && searchQuery.isNotBlank()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = DeepSlate),
                                    border = BorderStroke(1.dp, NeonViolet)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(24.dp))
                                            Column {
                                                Text(searchQuery.trim(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("Loharano: Google Play / APKPure via IA", color = NeonViolet, fontSize = 10.5.sp)
                                            }
                                        }

                                        Text(
                                            text = "Tsy ao anatin'ny lisitra mivantana ity app ity, saingy ny IA dia afaka mikaroka sy misintona ny APK ho azy avy amin'ny Google Play na APKPure.",
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        )

                                        Button(
                                            onClick = {
                                                isAiFetchingApk = true
                                                aiFetchStatusText = "IA mikaroka sy misintona APK ho an'i '${searchQuery.trim()}'..."
                                                scope.launch {
                                                    delay(1600)
                                                    onInstallApk(searchQuery.trim(), "APKPure", "browser", targetDeviceSelection, 45)
                                                    isAiFetchingApk = false
                                                    searchQuery = ""
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().height(42.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet, contentColor = Color.White)
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (isAiFetchingApk) aiFetchStatusText else "✨ Sintomy & Apetaho amin'ny IA", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // App Store Catalog Items
                        items(filteredCatalog) { apk ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.85f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(apk.brandColor.copy(alpha = 0.2f))
                                            .border(1.dp, apk.brandColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(apk.icon, contentDescription = apk.name, tint = apk.brandColor, modifier = Modifier.size(24.dp))
                                    }

                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(apk.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(apk.rating, color = AccentAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color.White.copy(alpha = 0.08f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(apk.sourceStore, color = CyanBright, fontSize = 8.5.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                            Text("• ${apk.sizeMb} Mo • Sandbox Safe", color = TextSecondary, fontSize = 9.sp)
                                        }
                                        Text(apk.description, color = TextSecondary, fontSize = 9.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }

                                    Button(
                                        onClick = {
                                            onInstallApk(apk.name, apk.sourceStore, apk.iconKey, targetDeviceSelection, apk.sizeMb)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(if (targetDeviceSelection == null) "Rehetra" else "Apetaho", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // ==========================================
                    // TAB 1: MANOVA NY APPS AMIN'NY FITAOVANA REHETRA
                    // "Azo atao ihany koa ny manova anireo App mipetaka aminy appareil rehetra App"
                    // ==========================================
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
                                border = BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Layers, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(20.dp))
                                        Text("Fandrindrana Apps amin'ny Fitaovana Rehetra", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Text(
                                        text = "Safidio ireo fampiharana tianao hapetraka na hovaina amin'ny fitaovana ${allProfiles.size} rehetra indray miaraka amin'ny tsindry iray.",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )

                                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

                                    // Replace toggle
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Soloy tanteraka (Remplacer)", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            Text("Fafao ireo apps teo aloha ka ireto vaovao ihany no avela", color = TextSecondary, fontSize = 9.5.sp)
                                        }
                                        Switch(
                                            checked = replaceExistingAppsOnDevices,
                                            onCheckedChange = { replaceExistingAppsOnDevices = it }
                                        )
                                    }
                                }
                            }
                        }

                        // App checkboxes
                        items(allAvailableAppKeys) { appTriple ->
                            val key = appTriple.first
                            val label = appTriple.second
                            val color = appTriple.third
                            val isSelected = batchSelectedKeys.contains(key)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) batchSelectedKeys.remove(key) else batchSelectedKeys.add(key)
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) color.copy(alpha = 0.15f) else CardSlate
                                ),
                                border = BorderStroke(1.dp, if (isSelected) color else Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Box(
                                            modifier = Modifier.size(10.dp).clip(CircleShape).background(color)
                                        )
                                        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            if (isSelected) batchSelectedKeys.remove(key) else batchSelectedKeys.add(key)
                                        }
                                    )
                                }
                            }
                        }

                        // One-click apply button
                        item {
                            Button(
                                onClick = {
                                    if (batchSelectedKeys.isNotEmpty()) {
                                        onBatchUpdateAllDevices(batchSelectedKeys.toList(), replaceExistingAppsOnDevices)
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet, contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ampiharo amin'ny Fitaovana Rehetra (${allProfiles.size} Apareils)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
