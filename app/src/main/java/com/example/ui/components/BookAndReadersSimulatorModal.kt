package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interactive Model & Simulator for "The Book and the Readers" (Ny Boky sy ny Mpamaky) 📘
 * Illustrates app distribution (Download vs Shareit APK photocopy), simultaneous reading (2 brains, 4 eyes),
 * and telemetry pings to the author's server (+1 and +1).
 */
@Composable
fun BookAndReadersSimulatorModal(
    onDismiss: () -> Unit,
    onLogTelemetry: (deviceName: String, location: String, isShareitCopy: Boolean, isBlocked: Boolean) -> Unit = { _, _, _, _ -> }
) {
    // Phone 1 (Samsung - Antananarivo - Original Download)
    var isPhone1InternetOn by remember { mutableStateOf(true) }
    var isPhone1AppOpen by remember { mutableStateOf(true) }
    var isPhone1FirewallBlocking by remember { mutableStateOf(false) }

    // Phone 2 (Tecno - Friend - Shareit Photocopy)
    var isShareitTransferred by remember { mutableStateOf(true) }
    var isTransferringShareit by remember { mutableStateOf(false) }
    var shareitTransferProgress by remember { mutableFloatStateOf(1f) }
    var isPhone2InternetOn by remember { mutableStateOf(true) }
    var isPhone2AppOpen by remember { mutableStateOf(true) }
    var isPhone2FirewallBlocking by remember { mutableStateOf(false) }

    // Simulated Server Logs
    var telemetryLogs by remember {
        mutableStateOf(
            listOf(
                "🟢 [SERVER BOOT] Author's Telemetry Service active. Awaiting active reader pings...",
                "📡 [PING +1] Phone 1: Samsung Galaxy (Antananarivo) -> 'Misokatra aho izao!' (+1 Mpamaky)",
                "📡 [PING +1] Phone 2: Tecno Camon (Namana / Shareit Copy) -> 'Misokatra koa aho!' (+1 Mpamaky fanampiny)"
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()

    // Determine whether each phone successfully counts as an active reader on the author's server
    val isPhone1Pinging = isPhone1InternetOn && isPhone1AppOpen && !isPhone1FirewallBlocking
    val isPhone2Pinging = isShareitTransferred && isPhone2InternetOn && isPhone2AppOpen && !isPhone2FirewallBlocking

    val activeReadersCount = (if (isPhone1Pinging) 1 else 0) + (if (isPhone2Pinging) 1 else 0)
    val activeBrains = activeReadersCount
    val activeEyes = activeReadersCount * 2

    // Tab state: 0 = Interactive Laboratory, 1 = Illustrated Analogy Story
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 24.dp)
                .testTag("book_and_readers_modal"),
            shape = RoundedCornerShape(24.dp),
            color = DeepSlate,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyan.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberCyan.copy(alpha = 0.2f))
                                .border(1.2.dp, CyberCyan, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Ny Boky sy ny Mpamaky 📘",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "The Book and the Readers • Fanoharana Telemetry",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hidio",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs (Simulator vs Story Analogy)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CardSlate,
                    contentColor = CyberCyan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Simulator Miasa (Live)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Fanoharana Feno (Story)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content switching
                if (selectedTab == 0) {
                    // TAB 0: LIVE INTERACTIVE LAB & AUTHOR SERVER
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Author's Central Cloud Server Gauge Card
                        item {
                            AuthorServerLiveGaugeCard(
                                activeReaders = activeReadersCount,
                                activeBrains = activeBrains,
                                activeEyes = activeEyes,
                                isPhone1Pinging = isPhone1Pinging,
                                isPhone2Pinging = isPhone2Pinging
                            )
                        }

                        // 2. Dual Phone Simulation Cards (Samsung vs Tecno)
                        item {
                            Text(
                                text = "Fitaovana Roa Mandray Anjara (Dual Device Playground):",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Phone 1 Card: Samsung (Antananarivo) - Original Download
                        item {
                            PhoneDeviceSimulatorCard(
                                deviceTitle = "Finday 1: Samsung (Ianao)",
                                deviceSubtitle = "Nividy boky (Nisintona ny App tao Antananarivo)",
                                deviceModel = "Samsung Galaxy S23",
                                isOriginalBuyer = true,
                                isInternetOn = isPhone1InternetOn,
                                onToggleInternet = {
                                    isPhone1InternetOn = !isPhone1InternetOn
                                    val nowPinging = isPhone1InternetOn && isPhone1AppOpen && !isPhone1FirewallBlocking
                                    val log = if (nowPinging) {
                                        "📡 [PING +1] Samsung (Antananarivo) mifandray indray -> +1 Mpamaky!"
                                    } else {
                                        "🔌 [OFFLINE] Samsung (Antananarivo) tapaka fifandraisana na mihidy."
                                    }
                                    telemetryLogs = (listOf(log) + telemetryLogs).take(25)
                                    onLogTelemetry("Samsung Galaxy", "Antananarivo", false, isPhone1FirewallBlocking)
                                },
                                isAppOpen = isPhone1AppOpen,
                                onToggleAppOpen = {
                                    isPhone1AppOpen = !isPhone1AppOpen
                                    val nowPinging = isPhone1InternetOn && isPhone1AppOpen && !isPhone1FirewallBlocking
                                    val log = if (nowPinging) {
                                        "📖 [OPENED] Samsung: Nisokatra ny fampiharana -> +1 Mpamaky!"
                                    } else {
                                        "📕 [CLOSED] Samsung: Nihidy ny fampiharana."
                                    }
                                    telemetryLogs = (listOf(log) + telemetryLogs).take(25)
                                    onLogTelemetry("Samsung Galaxy", "Antananarivo", false, isPhone1FirewallBlocking)
                                },
                                isFirewallBlocked = isPhone1FirewallBlocking,
                                onToggleFirewall = {
                                    isPhone1FirewallBlocking = !isPhone1FirewallBlocking
                                    val log = if (isPhone1FirewallBlocking) {
                                        "🛡 [FIREWALL BLOCK] Clonage nanakana ny telemetry amin'ny Samsung! Tsy azon'ny mpanoratra."
                                    } else {
                                        "🟢 [FIREWALL PASS] Clonage mamela ny fifandraisana amin'ny serviera."
                                    }
                                    telemetryLogs = (listOf(log) + telemetryLogs).take(25)
                                    onLogTelemetry("Samsung Galaxy", "Antananarivo", false, isPhone1FirewallBlocking)
                                },
                                isPingingServer = isPhone1Pinging
                            )
                        }

                        // Shareit Transfer Bridge Action
                        item {
                            ShareitBridgeControl(
                                isTransferred = isShareitTransferred,
                                isTransferring = isTransferringShareit,
                                progress = shareitTransferProgress,
                                onSimulateTransfer = {
                                    coroutineScope.launch {
                                        isTransferringShareit = true
                                        shareitTransferProgress = 0f
                                        for (i in 1..10) {
                                            delay(120)
                                            shareitTransferProgress = i / 10f
                                        }
                                        isTransferringShareit = false
                                        isShareitTransferred = true
                                        telemetryLogs = (listOf("📲 [SHAREIT SUCCESS] Voazara soa aman-tsara tamin'ny finday Tecno ny APK (Photocopy)!") + telemetryLogs).take(25)
                                    }
                                },
                                onRemoveTransfer = {
                                    isShareitTransferred = false
                                    telemetryLogs = (listOf("🗑 [UNINSTALL] Nofafana tao amin'ny Tecno ny APK dika mitovy.") + telemetryLogs).take(25)
                                }
                            )
                        }

                        // Phone 2 Card: Tecno (Friend) - Shareit Photocopy
                        item {
                            PhoneDeviceSimulatorCard(
                                deviceTitle = "Finday 2: Tecno (Ny Namanao)",
                                deviceSubtitle = "Nanao photocopy (Nalefa tamin'ny Shareit)",
                                deviceModel = "Tecno Camon 20 Pro",
                                isOriginalBuyer = false,
                                isTransferred = isShareitTransferred,
                                isInternetOn = isPhone2InternetOn,
                                onToggleInternet = {
                                    isPhone2InternetOn = !isPhone2InternetOn
                                    val nowPinging = isShareitTransferred && isPhone2InternetOn && isPhone2AppOpen && !isPhone2FirewallBlocking
                                    val log = if (nowPinging) {
                                        "📡 [PING +1 MORE] Tecno (Shareit copy) mifandray -> +1 Mpamaky fanampiny!"
                                    } else {
                                        "🔌 [OFFLINE] Tecno tsy manana internet na tapaka."
                                    }
                                    telemetryLogs = (listOf(log) + telemetryLogs).take(25)
                                    onLogTelemetry("Tecno Camon", "Namana (Shareit)", true, isPhone2FirewallBlocking)
                                },
                                isAppOpen = isPhone2AppOpen,
                                onToggleAppOpen = {
                                    isPhone2AppOpen = !isPhone2AppOpen
                                    val nowPinging = isShareitTransferred && isPhone2InternetOn && isPhone2AppOpen && !isPhone2FirewallBlocking
                                    val log = if (nowPinging) {
                                        "📖 [OPENED] Tecno: Nisokatra ny kopia Shareit -> +1 Mpamaky fanampiny!"
                                    } else {
                                        "📕 [CLOSED] Tecno: Nihidy ny fampiharana."
                                    }
                                    telemetryLogs = (listOf(log) + telemetryLogs).take(25)
                                    onLogTelemetry("Tecno Camon", "Namana (Shareit)", true, isPhone2FirewallBlocking)
                                },
                                isFirewallBlocked = isPhone2FirewallBlocking,
                                onToggleFirewall = {
                                    isPhone2FirewallBlocking = !isPhone2FirewallBlocking
                                    val log = if (isPhone2FirewallBlocking) {
                                        "🛡 [FIREWALL BLOCK] Clonage nanakana ny telemetry amin'ny Tecno! Tsy azon'ny mpanoratra."
                                    } else {
                                        "🟢 [FIREWALL PASS] Clonage mamela ny telemetry handalo."
                                    }
                                    telemetryLogs = (listOf(log) + telemetryLogs).take(25)
                                    onLogTelemetry("Tecno Camon", "Namana (Shareit)", true, isPhone2FirewallBlocking)
                                },
                                isPingingServer = isPhone2Pinging
                            )
                        }

                        // 3. Raw Server Telemetry Terminal Logs
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(AccentGreen)
                                            )
                                            Text(
                                                text = "Server Telemetry Stream (Hafatra Voaray)",
                                                color = CyberCyan,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Text(
                                            text = "Port 443 HTTPS",
                                            color = TextSecondary,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        telemetryLogs.take(5).forEach { logLine ->
                                            Text(
                                                text = logLine,
                                                color = when {
                                                    logLine.contains("PING") -> CyanBright
                                                    logLine.contains("FIREWALL") -> AccentAmber
                                                    logLine.contains("OFFLINE") -> TextSecondary
                                                    else -> Color.White.copy(alpha = 0.85f)
                                                },
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAB 1: THE FULL ANALOGY STORY ("The Book and the Readers")
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            StorySectionCard(
                                stepNumber = "1",
                                title = "Nividy ny Boky Ianao (Nisintona ny App)",
                                englishTitle = "1. You bought the book (You downloaded the app)",
                                icon = Icons.Default.ShoppingCart,
                                color = OceanBlue,
                                description = "Nividy ilay boky novokarin'ny mpanoratra ianao tamin'ny fomba ofisialy (tahaka ny fampidinana ny fampiharana tao amin'ny Play Store). Ny findainao dia Samsung eto Antananarivo."
                            )
                        }

                        item {
                            StorySectionCard(
                                stepNumber = "2",
                                title = "Nindrana sy Nanao Photocopy ny Namana (Shareit)",
                                englishTitle = "2. Your friend borrowed the book and made a photocopy",
                                icon = Icons.Default.Share,
                                color = CyberCyan,
                                description = "Nindrana ilay boky ny namanao ary nanao dika mitovy (photocopy) ny pejy rehetra. Eo amin'ny tontolon'ny finday, izany indrindra no miseho rehefa mamindra ny APK amin'ny alalan'ny Shareit na Bluetooth ho amin'ny finday Tecno!"
                            )
                        }

                        item {
                            StorySectionCard(
                                stepNumber = "3",
                                title = "Famakiana Miaraka: Ati-doha 2 sy Maso 4!",
                                englishTitle = "3. Two brains and four eyes reading at the same time",
                                icon = Icons.Default.Visibility,
                                color = AccentAmber,
                                description = "Rehefa samy nody tany an-tranony ianareo roa dia samy namaky ny tantara tamin'ny fotoana mitovy. Raha anontaniana ny mpanoratra hoe: 'Firy no olona mamaky ny tantaranao izao?', ny valiny marina dia ROA (2) fa tsy iray!\n\nNa dia photocopy fotsiny aza ilay teny an-tanan'ny namanao, dia mbola misy ati-doha roa (2) sy maso efatra (4) mandany fotoana mamaky azy!"
                            )
                        }

                        item {
                            StorySectionCard(
                                stepNumber = "4",
                                title = "Inona no mitranga rehefa misy Internet? (Telemetry)",
                                englishTitle = "4. What happens on both phones when connected to internet?",
                                icon = Icons.Default.CloudSync,
                                color = AccentGreen,
                                description = "Rehefa samy manokatra internet sy manokatra ny app ianareo roa:\n\n1. Ny finday voalohany (Samsung): Mandefa hafatra any amin'ny servieran'ny mpanoratra manao hoe:\n« Finday Samsung eto Antananarivo aho, ary misokatra izao. »\n-> Manisa ny rafitra: +1 mpamaky!\n\n2. Ny finday faharoa (Tecno): Na dia tamin'ny Shareit aza no nahazoana azy, rehefa misy internet dia mandefa ny hafatry ny tenany koa hoe:\n« Finday Tecno akaikin'ny namako aho, ary misokatra koa aho izao. »\n-> Manisa ny rafitra: +1 fanampiny!\n\nTotalin'ny mpamaky voasoratra any amin'ny mpanoratra = ROA (2)!"
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, AccentGreen.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = AccentGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Nahoana no Tena Ilaina ny Clonage & Firewall?",
                                            color = Color.White,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Miaraka amin'ny Clonage, afaka sakanao (Firewall) ireo hafatra telemetry ireo mba tsy hahalalan'ny serviera ny toerana sy ny findainao, na dia mampiasa ny fampiharana aza ianao!",
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("book_analogy_close_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = DeepSlate
                    )
                ) {
                    Text(
                        text = "Mazava Tsara! Miverina amin'ny Dashboard",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Author's Cloud Server Live Gauge
 */
@Composable
private fun AuthorServerLiveGaugeCard(
    activeReaders: Int,
    activeBrains: Int,
    activeEyes: Int,
    isPhone1Pinging: Boolean,
    isPhone2Pinging: Boolean
) {
    val serverBadgeColor = when (activeReaders) {
        2 -> AccentGreen
        1 -> CyberCyan
        else -> AccentAmber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("author_server_gauge_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.95f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, serverBadgeColor.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Title & Server Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = serverBadgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Servieran'ny Mpanoratra (Author's Cloud)",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fanisana mpamaky mivantana amin'ny fotoana tena izy",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(serverBadgeColor.copy(alpha = 0.18f))
                        .border(1.dp, serverBadgeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (activeReaders > 0) "● LIVE ONLINE" else "○ TSISY MPAMAKY",
                        color = serverBadgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Big Live Counter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Firy no olona mamaky izao?",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$activeReaders",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (activeReaders > 1) "Mpamaky (ROA)" else if (activeReaders == 1) "Mpamaky (IRAY)" else "Mpamaky (TSISY)",
                            color = serverBadgeColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                // Brains and Eyes indicators
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Brains",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$activeBrains Ati-doha",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Eyes",
                            tint = AccentAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$activeEyes Maso",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Active breakdown badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Samsung Ping Status
                ReaderSourceBadge(
                    label = "Samsung: +1",
                    sub = if (isPhone1Pinging) "Miasa (Original)" else "Tsy manisa",
                    isActive = isPhone1Pinging,
                    modifier = Modifier.weight(1f)
                )
                // Tecno Ping Status
                ReaderSourceBadge(
                    label = "Tecno: +1",
                    sub = if (isPhone2Pinging) "Miasa (Shareit)" else "Tsy manisa",
                    isActive = isPhone2Pinging,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ReaderSourceBadge(
    label: String,
    sub: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isActive) AccentGreen else LightSlate
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Column {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sub,
                    color = if (isActive) AccentGreen else TextSecondary,
                    fontSize = 9.sp
                )
            }
        }
    }
}

/**
 * Individual Phone Device Control in the Simulator
 */
@Composable
private fun PhoneDeviceSimulatorCard(
    deviceTitle: String,
    deviceSubtitle: String,
    deviceModel: String,
    isOriginalBuyer: Boolean,
    isTransferred: Boolean = true,
    isInternetOn: Boolean,
    onToggleInternet: () -> Unit,
    isAppOpen: Boolean,
    onToggleAppOpen: () -> Unit,
    isFirewallBlocked: Boolean,
    onToggleFirewall: () -> Unit,
    isPingingServer: Boolean
) {
    val borderColor = if (isPingingServer) AccentGreen else CyberCyan.copy(alpha = 0.3f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.85f)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isOriginalBuyer) OceanBlue.copy(alpha = 0.25f) else CyberPurple.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = if (isOriginalBuyer) OceanBlue else CyberPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = deviceTitle,
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = deviceSubtitle,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Telemetry Ping Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isPingingServer) AccentGreen.copy(alpha = 0.15f) else AccentRed.copy(alpha = 0.15f))
                        .border(1.dp, if (isPingingServer) AccentGreen else AccentRed, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isPingingServer) "📡 Mandefa PING (+1)" else "🔇 Tsy Misy Ping",
                        color = if (isPingingServer) AccentGreen else AccentRed,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Message Sent by Phone to Server
            if (isPingingServer) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isOriginalBuyer) {
                            "💬 « Finday Samsung eto Antananarivo aho, ary misokatra izao. »"
                        } else {
                            "💬 « Finday Tecno akaikin'ny namako aho, ary misokatra koa. »"
                        },
                        color = CyanBright,
                        fontSize = 10.5.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Internet Toggle
                SimulatorToggleButton(
                    label = if (isInternetOn) "Internet: ON" else "Internet: TSY MISY",
                    icon = if (isInternetOn) Icons.Default.Wifi else Icons.Default.WifiOff,
                    isActive = isInternetOn,
                    activeColor = CyberCyan,
                    onClick = onToggleInternet,
                    modifier = Modifier.weight(1f)
                )

                // 2. App Open / Closed Toggle
                SimulatorToggleButton(
                    label = if (isAppOpen) "App: MISOKATRA" else "App: MIHIDY",
                    icon = if (isAppOpen) Icons.Default.MenuBook else Icons.Default.Close,
                    isActive = isAppOpen,
                    activeColor = AccentGreen,
                    onClick = onToggleAppOpen,
                    modifier = Modifier.weight(1f)
                )

                // 3. Firewall Shield Toggle
                SimulatorToggleButton(
                    label = if (isFirewallBlocked) "Firewall: MANAKANA" else "Firewall: MAMELA",
                    icon = Icons.Default.Shield,
                    isActive = isFirewallBlocked,
                    activeColor = AccentAmber,
                    onClick = onToggleFirewall,
                    modifier = Modifier.weight(1.1f)
                )
            }
        }
    }
}

@Composable
private fun SimulatorToggleButton(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
            .border(
                1.dp,
                if (isActive) activeColor else Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = if (isActive) Color.White else TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * Shareit Transfer Bridge Simulation Control
 */
@Composable
private fun ShareitBridgeControl(
    isTransferred: Boolean,
    isTransferring: Boolean,
    progress: Float,
    onSimulateTransfer: () -> Unit,
    onRemoveTransfer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DeepViolet.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberPurple.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(CyberPurple.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Shareit",
                        tint = NeonViolet,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = "Fizarana Photocopy tamin'ny Shareit 📲",
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isTransferring) "Eo am-pamindrana... ${(progress * 100).toInt()}%" else if (isTransferred) "Voapetaka amin'ny Tecno ny APK" else "Tsy mbola nalefa tamin'ny namana",
                        color = if (isTransferred) AccentGreen else TextSecondary,
                        fontSize = 9.5.sp
                    )
                    if (isTransferring) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(4.dp)
                                .padding(top = 2.dp),
                            color = NeonViolet,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            Button(
                onClick = if (isTransferred) onRemoveTransfer else onSimulateTransfer,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTransferred) AccentAmber.copy(alpha = 0.2f) else NeonViolet,
                    contentColor = if (isTransferred) AccentAmber else Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(
                    text = if (isTransferred) "Fafao ny Kopia" else "Alefaso Shareit",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Story Section Card for Illustrated Analogy
 */
@Composable
private fun StorySectionCard(
    stepNumber: String,
    title: String,
    englishTitle: String,
    icon: ImageVector,
    color: Color,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, color.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.18f))
                        .border(1.dp, color, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = englishTitle,
                        color = color,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                thickness = 1.dp
            )

            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )
        }
    }
}
