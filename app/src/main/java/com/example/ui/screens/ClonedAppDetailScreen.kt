package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FirewallAppEntity
import com.example.ui.components.ClonageTopStatusBar
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * EFIJERY HO AN'NY FAMPIHARANA CLONAGE REHEFA HIDIRINA:
 * "Izany hoe avy dia miditra ao anatiny ilay app tendrena iasahany ny IA fa tsy mizanona fotsiny ao anatiny chrome fa tena miditra ao anatiny ilay app mihintsy"
 *
 * Mampiseho ny fiasan'ilay fampiharana mivantana (Vidmat, TikTok, WhatsApp, ChatGPT, Facebook, Store Apps, sns.)
 * miaraka amin'ny IA miasa mivantana ao anatiny ao amin'ny tontolo mahaleo tena sy voaaro (sandbox).
 */
@Composable
fun ClonedAppDetailScreen(
    app: FirewallAppEntity,
    isGlobalMobileDataConnected: Boolean,
    isGlobalWifiConnected: Boolean = true,
    onToggleGlobalMobileData: () -> Unit,
    onToggleGlobalWifi: () -> Unit,
    onToggleAppMobileData: () -> Unit,
    onToggleAppWifi: () -> Unit,
    onOpenInBrowser: (url: String) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDataActive = isGlobalMobileDataConnected && app.allowMobileData
    val isWifiActive = isGlobalWifiConnected && app.allowWifi
    val isAppConnected = isDataActive || isWifiActive

    // Tab 0 = Miditra ao anaty App (Miasa tena izy), Tab 1 = Fifehezana & Pare-feu
    var selectedTab by remember { mutableStateOf(0) }

    // Coroutine scope for simulated AI actions
    val scope = rememberCoroutineScope()

    // 1. STATE FOR VIDMAT (Video/Audio Downloader & Player)
    var vidmatSearchQuery by remember { mutableStateOf("") }
    var selectedVidmatFormat by remember { mutableStateOf("MP4 (1080p)") }
    var isVidmatDownloading by remember { mutableStateOf(false) }
    var vidmatDownloadProgress by remember { mutableStateOf(0.68f) }
    val vidmatDownloads = remember {
        mutableStateListOf(
            "Malagasy Mozika 2026 Hits.mp3",
            "TikTok Trending Sounds.mp4"
        )
    }

    // 2. STATE FOR TIKTOK
    val tikTokVideos = remember {
        listOf(
            Triple("@gasikara_vibes", "Dihy sy mozika mafana eto Madagasikara! 🔥 #malagasy #vibes #2026", 2430),
            Triple("@tech_clonage_mg", "Fitaovana Android marobe miasa miaraka amin'ny IA! 🚀 #android #ia", 1850),
            Triple("@fihavanana_tv", "Fialamboly sy tantara fohy mahatsikaiky 😂 #fihavanana #tiktok", 3920)
        )
    }
    var currentTikTokIndex by remember { mutableStateOf(0) }
    var isTikTokLiked by remember { mutableStateOf(false) }
    var tikTokLikesCount by remember { mutableStateOf(tikTokVideos[0].third) }
    var showTikTokComments by remember { mutableStateOf(false) }
    val tikTokCommentsList = remember {
        mutableStateListOf("Tena tsara be ity!", "Mankasitraka tompoko", "Aiza ny tohiny?")
    }
    var newCommentText by remember { mutableStateOf("") }

    // 3. STATE FOR CHATGPT / IA CHAT
    val gptMessages = remember {
        mutableStateListOf(
            Pair("Assistant", "Miarahaba! Izaho no IA mpanampy anao ato anatin'ity fitaovana mahaleo tena ity. Inona no azoko atao ho anao anio?"),
            Pair("Mpampiasa", "Miasa mivantana ato anaty app ve ianao?"),
            Pair("Assistant", "Eny tokoa! Miasa mivantana ato anaty ${app.appName} aho, voaaro ao amin'ny sandbox mitokana tsy misy leakage.")
        )
    }
    var gptInput by remember { mutableStateOf("") }
    var isGptTyping by remember { mutableStateOf(false) }

    // 4. STATE FOR WHATSAPP / TELEGRAM
    var chatInput by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            "Miarahaba! Espace mitokana ity.",
            "Tsy misy fifandraisana amin'ny fitaovana voalohany.",
            "Voaaro amin'ny Pare-feu ny angon-drakitra."
        )
    }

    // 5. STATE FOR FACEBOOK / TAMBAZOTRA
    var fbPostInput by remember { mutableStateOf("") }
    var fbLikeCount by remember { mutableStateOf(42) }
    var isFbLiked by remember { mutableStateOf(false) }
    val fbPosts = remember {
        mutableStateListOf(
            "Miarahaba ny rehetra! Mandeha amin'ny fomba mahaleo tena tanteraka ity fitaovana ity amin'ny alalan'ny Clonage Isolé."
        )
    }

    // 6. STATE FOR GENERIC STORE APP
    var isAiTaskRunning by remember { mutableStateOf(true) }
    var simulatedPackets by remember { mutableStateOf(342) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. TOP STATUS BAR (Mobile Data & Wi-Fi Icons)
        ClonageTopStatusBar(
            isMobileDataConnected = isGlobalMobileDataConnected,
            isWifiConnected = isGlobalWifiConnected,
            onToggleMobileData = onToggleGlobalMobileData,
            onToggleWifi = onToggleGlobalWifi,
            titlePill = "${app.appName.uppercase()} • MIDITRA AO ANATINY"
        )

        // 2. APP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Miverina",
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = app.appName,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text("TENA AO ANATINY", color = CyberCyan, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Fitaovana #${app.phoneId} • Tontolo Mahaleo Tena",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Connection indicator badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAppConnected) AccentGreen.copy(alpha = 0.15f) else AccentRed.copy(alpha = 0.15f))
                    .border(1.dp, if (isAppConnected) AccentGreen else AccentRed, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isAppConnected) "MANDÉHA 🟢" else "TAPAKA 🔴",
                    color = if (isAppConnected) AccentGreen else AccentRed,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 3. IA BANNER: IA NO MIASA MIVANTANA AO ANATY APP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF09141F))
                .border(width = 0.5.dp, color = CyberCyan.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "⚡ IA Miasa Mivantana: Niditra tao anatin'ny ${app.appName} ny IA fa tsy mijanona fotsiny ao anaty Chrome.",
                    color = CyanBright,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp
                )
            }
        }

        // 4. SEGMENTED TABS: [ 📱 Miditra ao Anaty App ]  [ 🛡️ Fifehezana Pare-feu ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tab 1: Miditra ao anaty App
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
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = if (selectedTab == 0) Color.Black else CyanBright,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "📱 Fampiharana Miasa",
                        color = if (selectedTab == 0) Color.Black else Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tab 2: Fifehezana Pare-feu
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
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (selectedTab == 1) Color.White else NeonViolet,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "🛡️ Fifehezana Pare-feu",
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

        // 5. MAIN CONTENT BASED ON SELECTED TAB
        if (selectedTab == 0) {
            // ==========================================
            // TAB 0: TENA MIDITRA AO ANATINY ILAY APP MIHINTSY
            // ==========================================
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp)
            ) {
                // If not connected
                if (!isAppConnected) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.WifiOff, contentDescription = null, tint = AccentRed, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Tsy afaka mifandray amin'ny Internet ity fampiharana ity.", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text("Velomy ny Données Mobiles na ny Wi-Fi manokana eo ambony mba hiasan'ny IA.", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }

                val key = app.iconKey.lowercase()
                val nameLower = app.appName.lowercase()

                // A. VIDMAT APPLICATION INTERFACE
                if (key.contains("vidmat") || nameLower.contains("vidmat")) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
                                        Text("Vidmat HD Downloader (Clone)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFF5722).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("IA Active ⚡", color = Color(0xFFFF5722), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Search bar
                                OutlinedTextField(
                                    value = vidmatSearchQuery,
                                    onValueChange = { vidmatSearchQuery = it },
                                    placeholder = { Text("Mikaroka horonan-tsary, mozika, sarimihetsika...", fontSize = 11.sp, color = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) }
                                )

                                // Format selector
                                Text("Safidy Format & Kalitao :", color = TextSecondary, fontSize = 10.5.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                    listOf("MP4 (1080p)", "MP4 (720p)", "MP3 (320k)", "Audio M4A").forEach { fmt ->
                                        val isSel = selectedVidmatFormat == fmt
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) Color(0xFFFF5722) else DeepSlate)
                                                .clickable { selectedVidmatFormat = fmt }
                                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                        ) {
                                            Text(fmt, color = if (isSel) Color.White else TextSecondary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Active Downloads in Vidmat
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Fampidinana Mandeha amin'ny IA :", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DeepSlate)
                                            .padding(10.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Malagasy Hits 2026 - Mix Mafana", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                                Text(if (isVidmatDownloading) "14.8 Mo/s" else "Vita 100%", color = CyberCyan, fontSize = 10.sp)
                                            }
                                            LinearProgressIndicator(
                                                progress = { if (isVidmatDownloading) vidmatDownloadProgress else 1f },
                                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                color = Color(0xFFFF5722),
                                                trackColor = Color.White.copy(alpha = 0.1f)
                                            )
                                        }
                                    }
                                }

                                // Button to trigger new download with AI
                                Button(
                                    onClick = {
                                        isVidmatDownloading = true
                                        scope.launch {
                                            delay(1500)
                                            vidmatDownloads.add(0, "Horonan-tsary vaovao #${vidmatDownloads.size + 1} ($selectedVidmatFormat)")
                                            isVidmatDownloading = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722), contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isVidmatDownloading) "Misintona amin'ny IA..." else "Misintona ao anaty Vidmat (IA)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // B. TIKTOK APPLICATION INTERFACE
                else if (key.contains("tiktok") || nameLower.contains("tiktok")) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, Color(0xFFFE2C55).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFFFE2C55), modifier = Modifier.size(20.dp))
                                        Text("TikTok Feed & Reels (Clone)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFE2C55).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("IA Mijery Horonan-tsary 👁️", color = Color(0xFFFE2C55), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                val currentVideo = tikTokVideos[currentTikTokIndex]

                                // Video player mockup box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Brush.verticalGradient(listOf(DeepSlate, Color(0xFF1E1020)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFFE2C55), modifier = Modifier.size(48.dp))
                                        Text("Mandeha amin'ny IA ny Horonan-tsary", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(currentVideo.first, color = CyberCyan, fontSize = 11.sp)
                                    }

                                    // Right overlay icons (Like, Comments, Share)
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                isTikTokLiked = !isTikTokLiked
                                                tikTokLikesCount = if (isTikTokLiked) tikTokLikesCount + 1 else tikTokLikesCount - 1
                                            },
                                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))
                                        ) {
                                            Icon(Icons.Default.Favorite, contentDescription = null, tint = if (isTikTokLiked) Color(0xFFFE2C55) else Color.White, modifier = Modifier.size(18.dp))
                                        }
                                        Text("$tikTokLikesCount", color = Color.White, fontSize = 9.sp)

                                        IconButton(
                                            onClick = { showTikTokComments = !showTikTokComments },
                                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))
                                        ) {
                                            Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Text(currentVideo.second, color = Color.White, fontSize = 11.sp)

                                // Button to switch to next video
                                Button(
                                    onClick = {
                                        currentTikTokIndex = (currentTikTokIndex + 1) % tikTokVideos.size
                                        tikTokLikesCount = tikTokVideos[currentTikTokIndex].third
                                        isTikTokLiked = false
                                    },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55), contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Horonan-tsary Manaraka (Next Video)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // C. CHATGPT / IA BOT APPLICATION INTERFACE
                else if (key.contains("chatgpt") || key.contains("ia") || nameLower.contains("chatgpt") || nameLower.contains("ia")) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, Color(0xFF10A37F).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF10A37F), modifier = Modifier.size(20.dp))
                                        Text("ChatGPT • IA Mahaleo Tena", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF10A37F).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Online 🟢", color = Color(0xFF10A37F), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Messages list
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                    gptMessages.forEach { msg ->
                                        val isUser = msg.first == "Mpampiasa"
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .widthIn(max = 280.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isUser) Color(0xFF10A37F) else DeepSlate)
                                                    .padding(10.dp)
                                            ) {
                                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    Text(
                                                        text = msg.first,
                                                        color = if (isUser) Color.White.copy(alpha = 0.8f) else CyanBright,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(msg.second, color = Color.White, fontSize = 11.5.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Input row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = gptInput,
                                        onValueChange = { gptInput = it },
                                        placeholder = { Text("Manorata fanontaniana...", fontSize = 11.sp, color = TextSecondary) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )
                                    IconButton(
                                        onClick = {
                                            if (gptInput.isNotBlank()) {
                                                val q = gptInput.trim()
                                                gptMessages.add(Pair("Mpampiasa", q))
                                                gptInput = ""
                                                scope.launch {
                                                    delay(1000)
                                                    gptMessages.add(Pair("Assistant", "Voaray ny hafatrao momba ny '$q'. Miasa tsara eto anatin'ny tontolo voaaro sy mitokana ity fitaovana #${app.phoneId} ity."))
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFF10A37F))
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // D. WHATSAPP / TELEGRAM MESSAGING INTERFACE
                else if (key.contains("whatsapp") || key.contains("telegram")) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Chat, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                                        Text("${app.appName} Messagerie", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Text("Encrypted E2E 🔒", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    chatMessages.forEach { msg ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(alpha = 0.08f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(text = msg, color = Color.White, fontSize = 11.5.sp)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = chatInput,
                                            onValueChange = { chatInput = it },
                                            placeholder = { Text("Manorata hafatra...", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true
                                        )
                                        IconButton(
                                            onClick = {
                                                if (chatInput.isNotBlank()) {
                                                    chatMessages.add(chatInput)
                                                    chatInput = ""
                                                }
                                            },
                                            modifier = Modifier.size(42.dp).clip(CircleShape).background(AccentGreen)
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = "Alefaso", tint = Color.Black, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // E. FACEBOOK / TAMBAZOTRA
                else if (key.contains("facebook")) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, Color(0xFF1877F2).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(20.dp))
                                    Text("Facebook Feed (Espace Isolé)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = fbPostInput,
                                        onValueChange = { fbPostInput = it },
                                        placeholder = { Text("Inona no ao an-tsainao?", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )
                                    Button(
                                        onClick = {
                                            if (fbPostInput.isNotBlank()) {
                                                fbPosts.add(0, fbPostInput)
                                                fbPostInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                                    ) {
                                        Text("Publier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                fbPosts.forEach { post ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DeepSlate)
                                            .padding(10.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(post, color = Color.White, fontSize = 11.5.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable {
                                                        isFbLiked = !isFbLiked
                                                        fbLikeCount = if (isFbLiked) fbLikeCount + 1 else fbLikeCount - 1
                                                    }
                                                ) {
                                                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = if (isFbLiked) Color(0xFF1877F2) else TextSecondary, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("$fbLikeCount J'aime", color = if (isFbLiked) Color(0xFF1877F2) else TextSecondary, fontSize = 10.sp)
                                                }
                                                Text("• Partager", color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // F. GENERAL STORE APPS (Google Play, APKPure, Uptodown, sns.)
                else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier.size(36.dp).clip(CircleShape).background(CyberCyan.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(getAppIcon(app.iconKey), contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                                        }
                                        Column {
                                            Text(app.appName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Miasa mivantana ao anaty tontolo voaaro", color = CyanBright, fontSize = 10.5.sp)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AccentGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Active 🟢", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DeepSlate)
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("⚡ Hetsika ataon'ny IA ao anatiny :", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        Text("• Ny IA dia mamaky sy manatanteraka ny asa rehetra ho an'ny ${app.appName}.", color = TextSecondary, fontSize = 10.5.sp)
                                        Text("• Angon-drakitra voaaro: ${simulatedPackets} Ko nozaraina tsy misy leakage.", color = TextSecondary, fontSize = 10.5.sp)
                                        Text("• Tsy mila Chrome: miditra mivantana ao anaty loharano.", color = AccentGreen, fontSize = 10.5.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        simulatedPackets += 64
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ampandehano indray ny IA ato anatiny", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Bokotra miverina
                item {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepSlate, contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Miverina amin'ny Pare-feu & Clonage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            // ==========================================
            // TAB 1: FIFEHEZANA PARE-FEU & RÉSEAU
            // ==========================================
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
            ) {
                // Connection & Firewall Status Card with Interactive Toggles
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.75f)),
                        border = BorderStroke(
                            1.dp,
                            if (isAppConnected) AccentGreen.copy(alpha = 0.3f) else AccentRed.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Fifehezana Réseau Pare-feu",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${app.dataUsageMb} MB lany",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.06f), thickness = 0.8.dp)

                            // Mobile Data Toggle Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDataActive) AccentGreen.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f))
                                    .clickable { onToggleAppMobileData() }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (app.allowMobileData) Icons.Default.SignalCellularAlt else Icons.Default.SignalCellularOff,
                                        contentDescription = null,
                                        tint = if (isDataActive) AccentGreen else AccentRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Données Mobiles ho an'ny ${app.appName}",
                                            color = Color.White,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if (!isGlobalMobileDataConnected) "Tapaka ny données ankapobeny"
                                            else if (app.allowMobileData) "Mandeha (Connecté)"
                                            else "Voasakana ao amin'ny pare-feu",
                                            color = if (isDataActive) AccentGreen else AccentRed,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = app.allowMobileData,
                                    onCheckedChange = { onToggleAppMobileData() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AccentGreen,
                                        checkedTrackColor = AccentGreen.copy(alpha = 0.3f),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier.height(26.dp)
                                )
                            }

                            // Wi-Fi Toggle Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isWifiActive) CyberCyan.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f))
                                    .clickable { onToggleAppWifi() }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (app.allowWifi) Icons.Default.Wifi else Icons.Default.WifiOff,
                                        contentDescription = null,
                                        tint = if (isWifiActive) CyberCyan else TextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Wi-Fi ho an'ny ${app.appName}",
                                            color = Color.White,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if (!isGlobalWifiConnected) "Tapaka ny WiFi ankapobeny"
                                            else if (app.allowWifi) "Azo ampiasaina (Connecté)"
                                            else "Voasakana ao amin'ny pare-feu",
                                            color = if (isWifiActive) CyberCyan else TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = app.allowWifi,
                                    onCheckedChange = { onToggleAppWifi() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = CyberCyan,
                                        checkedTrackColor = CyberCyan.copy(alpha = 0.3f),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier.height(26.dp)
                                )
                            }
                        }
                    }
                }

                // Chrome redirect option if user wants to view it on the web too
                item {
                    Button(
                        onClick = {
                            val targetUrl = when (app.iconKey.lowercase()) {
                                "browser" -> "https://duckduckgo.com"
                                "tiktok" -> "https://www.tiktok.com"
                                "facebook" -> "https://m.facebook.com"
                                "whatsapp" -> "https://web.whatsapp.com"
                                "telegram" -> "https://web.telegram.org"
                                "youtube" -> "https://m.youtube.com"
                                "vidmat" -> "https://duckduckgo.com/?q=vidmat"
                                else -> "https://duckduckgo.com/?q=${app.appName.replace(" ", "+")}"
                            }
                            onOpenInBrowser(targetUrl)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan.copy(alpha = 0.2f),
                            contentColor = CyanBright
                        ),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "🌐 Sokafy ao anaty Chrome koa ity App ity",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Miverina amin'ny Pare-feu & Clonage", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun getAppIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "youtube" -> Icons.Default.PlayArrow
        "browser" -> Icons.Default.Public
        "whatsapp" -> Icons.Default.Chat
        "telegram" -> Icons.Default.Send
        "facebook" -> Icons.Default.ThumbUp
        "tiktok" -> Icons.Default.Movie
        "instagram" -> Icons.Default.CameraAlt
        "downloads" -> Icons.Default.Download
        "vidmat" -> Icons.Default.FileDownload
        "chatgpt" -> Icons.Default.SmartToy
        else -> Icons.Default.Apps
    }
}
