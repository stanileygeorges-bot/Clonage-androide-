package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiInspectionLog
import com.example.data.model.FirewallAppEntity
import com.example.data.model.PhoneProfileEntity
import com.example.ui.components.AiInspectionCard
import com.example.ui.components.BatchManagementCard
import com.example.ui.components.BookAndReadersSummaryCard
import com.example.ui.components.ClonageTopStatusBar
import com.example.ui.components.HierarchyTreeViewCard
import com.example.ui.components.NewIndependentDeviceModal
import com.example.ui.theme.*

/**
 * FIREWALL & CLONAGE INTERFACE:
 * - Eny ambony à gauche: BOKOTRA PLUS (+) izay manokatra fenêtre vaovao sy mampisafidy apps
 *   apetaka amin'ny apareil vaovao na Android mahaleo tena tanteraka (tsy mifandray amin'ny voalohany).
 * - Sary Donnée Mobile Ngezabe (MENA rehefa déconnecté, MAINTSO rehefa connecté).
 * - Rehefa tsindrina ny donnée mobile dia MIASA DAHOLO ireo fampiharana napetaka.
 * - IA no MAMAKY AZY amin'ny fotoana tena izy (AI Inspector & Telemetry).
 * - Azo atao ihany koa ny MIDITRA AO ANATINY ilay app rehefa mandeha izy.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FirewallDashboardScreen(
    isGlobalMobileDataConnected: Boolean,
    isGlobalWifiConnected: Boolean = true,
    isHostInternetAvailable: Boolean = true,
    hostNetworkType: String = "Internet Mavitrika",
    onToggleGlobalMobileData: () -> Unit,
    onToggleGlobalWifi: () -> Unit = {},
    firewallApps: List<FirewallAppEntity>,
    allProfiles: List<PhoneProfileEntity> = emptyList(),
    activePhoneId: Int = 1,
    onSelectPhone: (Int) -> Unit = {},
    isNewDeviceWindowOpen: Boolean = false,
    onOpenNewDeviceWindow: () -> Unit = {},
    onCloseNewDeviceWindow: () -> Unit = {},
    onCreateIndependentDevice: (
        name: String,
        apps: List<String>,
        count: Int,
        isInfinite: Boolean,
        isAiVideoWatchEnabled: Boolean,
        isChromeMode: Boolean,
        chromeCommandName: String,
        chromeTargetAppName: String,
        deviceModelName: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    aiLogs: List<AiInspectionLog> = emptyList(),
    isAiAnalyzing: Boolean = false,
    onTriggerAiAnalysis: () -> Unit = {},
    onToggleAppMobileData: (FirewallAppEntity) -> Unit,
    onToggleAppWifi: (FirewallAppEntity) -> Unit,
    onToggleAllAppsMobileData: ((Boolean) -> Unit)? = null,
    onToggleAllAppsWifi: ((Boolean) -> Unit)? = null,
    onAddFirewallApp: (String, String) -> Unit,
    onReplaceApp: (phoneId: Int, newAppName: String, newIconKey: String, newPackageName: String) -> Unit = { _, _, _, _ -> },
    onDeleteFirewallApp: (FirewallAppEntity) -> Unit = {},
    onDeleteDevice: (Int) -> Unit = {},
    onDeleteMultipleDevices: (Collection<Int>) -> Unit = {},
    onDisconnectAllConnections: () -> Unit = {},
    onConnectAllConnections: () -> Unit = {},
    onToggleSingleDeviceConnection: (Int) -> Unit = {},
    onToggleDeviceMobileData: (Int) -> Unit = {},
    onToggleDeviceWifi: (Int) -> Unit = {},
    onOpenRealisticDevice: (Int) -> Unit = {},
    onExportBackup: () -> Unit = {},
    isBatchRestarting: Boolean = false,
    onPauseAllClones: () -> Unit = {},
    onResumeAllClones: () -> Unit = {},
    onRestartAllClones: () -> Unit = {},
    onPauseSingleClone: (Int) -> Unit = {},
    onResumeSingleClone: (Int) -> Unit = {},
    onRestartSingleClone: (Int) -> Unit = {},
    onOpenBookAnalogy: () -> Unit = {},
    onOpenAiAppDiscovery: () -> Unit = {},
    onOpenClonedApp: (FirewallAppEntity) -> Unit,
    onBackToGate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddAppDialog by remember { mutableStateOf(false) }
    var deviceToDelete by remember { mutableStateOf<PhoneProfileEntity?>(null) }
    var appToDelete by remember { mutableStateOf<FirewallAppEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, BLOCKED

    // Multi-select and Long-press state for active clone devices:
    var isDeviceMultiSelectMode by remember { mutableStateOf(false) }
    var selectedDeviceIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var longPressedDeviceId by remember { mutableStateOf<Int?>(null) }
    var inspectedRealProfile by remember { mutableStateOf<PhoneProfileEntity?>(null) }
    var deviceForActions by remember { mutableStateOf<PhoneProfileEntity?>(null) }
    var deviceForReplaceApp by remember { mutableStateOf<PhoneProfileEntity?>(null) }

    // Current active phone profile
    val currentProfile = allProfiles.find { it.phoneId == activePhoneId }
    val isPrimaryDevice = activePhoneId == 1
    val deviceDisplayName = currentProfile?.name ?: if (isPrimaryDevice) "Finday Voalohany (A)" else "Apareil Android #$activePhoneId"

    // Sary Donnée Mobile Ngezabe lokony:
    // MENA rehefa déconnecté, MAINTSO rehefa connecté
    val mobileDataColor = if (isGlobalMobileDataConnected) AccentGreen else AccentRed
    val mobileDataStatusText = if (isGlobalMobileDataConnected) "DONNÉES MOBILES : CONNECTÉ" else "DONNÉES MOBILES : DÉCONNECTÉ"
    val mobileDataSubText = if (isGlobalMobileDataConnected) {
        "Mandeha daholo ny connexion ny appareil rehetra • IA no mamaky azy"
    } else {
        "Tapaka ny données mobiles • Tsindrio ilay sary ngezabe mba hampandehanana ny rehetra"
    }

    // Sary Wi-Fi Ngezabe lokony ("de otrany izay ihany koa ny WiFi"):
    // MENA rehefa déconnecté, MAINTSO rehefa connecté
    val wifiColor = if (isGlobalWifiConnected) AccentGreen else AccentRed
    val wifiStatusText = if (isGlobalWifiConnected) "WI-FI : CONNECTÉ" else "WI-FI : DÉSACTIVÉ"
    val wifiSubText = if (isGlobalWifiConnected) {
        "Mandeha daholo ny Wi-Fi amin'ny fitaovana rehetra"
    } else {
        "Tapaka ny Wi-Fi • Tsindrio ilay sary ngezabe mba hampandehanana ny Wi-Fi rehetra"
    }

    // Filter apps: only show apps for this phone profile (or generic apps if none)
    val phoneSpecificApps = remember(firewallApps, activePhoneId) {
        val specific = firewallApps.filter { it.phoneId == activePhoneId }
        if (specific.isNotEmpty()) specific else firewallApps.filter { it.phoneId == 0 || it.phoneId == 1 }
    }

    val filteredApps = remember(phoneSpecificApps, searchQuery, selectedFilter, isGlobalMobileDataConnected) {
        phoneSpecificApps.filter { app ->
            val matchesQuery = searchQuery.isBlank() ||
                    app.appName.contains(searchQuery, ignoreCase = true)
            val isDataActive = isGlobalMobileDataConnected && app.allowMobileData
            val matchesFilter = when (selectedFilter) {
                "ACTIVE" -> isDataActive || app.allowWifi
                "BLOCKED" -> !isDataActive && !app.allowWifi
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. TOP STATUS BAR:
        // Misy bokotra PLUS (+) eny ambony à gauche, ora, ary sary Donnée Mobile sy Wi-Fi
        ClonageTopStatusBar(
            isMobileDataConnected = isGlobalMobileDataConnected,
            isWifiConnected = isGlobalWifiConnected,
            onToggleMobileData = onToggleGlobalMobileData,
            onToggleWifi = onToggleGlobalWifi,
            activePhoneId = activePhoneId,
            titlePill = if (isPrimaryDevice) "FINDAY 1" else "ANDROID #$activePhoneId ISOLÉ",
            onNewDeviceClick = onOpenNewDeviceWindow
        )

        // 2. APP BAR PARE-FEU CLONAGE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bokotra miverina
            IconButton(onClick = onBackToGate) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Miverina",
                    tint = Color.White
                )
            }

            // BOKOTRA PLUS (+) ENY AMBONY À GAUCHE:
            // "Ary eny ambony à gauche eny kosa misy otrany plus de rehefa tsindrina ilay plus de mamoaka fenêtre vaovao le izy
            //  ary mampisafidy app izay apetaka amin'ilay apareil vaovao na Android izy izay mahaleo tena"
            Button(
                onClick = onOpenNewDeviceWindow,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan.copy(alpha = 0.22f),
                    contentColor = CyberCyan
                ),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("top_left_plus_new_device_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Hamorona Apareil Vaovao (+)",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+ Apareil",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Clonage",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isPrimaryDevice) "Fitaovana Voalohany" else "Apareil Mahaleo Tena • Tsy mifandray",
                    color = if (isPrimaryDevice) CyberCyan else AccentGreen,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Bokotra hampidirana fampiharana ao anatin'ity apareil ity
            IconButton(
                onClick = { showAddAppDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Hampiditra app clonage",
                    tint = Color.White
                )
            }

            // Bokotra fanondranana Backup (Export state)
            IconButton(
                onClick = onExportBackup,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = "Haondrana ny Backup",
                    tint = CyberCyan
                )
            }

            // Bokotra "The Book & Readers" Analogy Simulator
            IconButton(
                onClick = onOpenBookAnalogy,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(OceanBlue.copy(alpha = 0.22f))
                    .testTag("top_book_analogy_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Ny Boky sy ny Mpamaky (The Book & Readers)",
                    tint = CyanBright
                )
            }

            // Bokotra Fikarohana App amin'ny IA (Any App + Chrome Sandbox)
            IconButton(
                onClick = onOpenAiAppDiscovery,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NeonViolet.copy(alpha = 0.22f))
                    .testTag("top_ai_app_discovery_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Fikarohana App amin'ny IA",
                    tint = NeonViolet
                )
            }

            // Raha apareil clone faharoa no voafidy dia azo fafana izy
            if (!isPrimaryDevice && currentProfile != null) {
                IconButton(
                    onClick = { deviceToDelete = currentProfile },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AccentRed.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hamafa ity Apareil ity",
                        tint = AccentRed
                    )
                }
            }
        }

        // 3. HORIZONTAL DEVICE SWITCHER ROW (Finday 1, Apareil 2 Mahaleo Tena, + Vaovao)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            allProfiles.forEach { profile ->
                val isSelected = profile.phoneId == activePhoneId
                val isPrimary = profile.phoneId == 1
                val devApps = firewallApps.filter { it.phoneId == profile.phoneId || (profile.phoneId == 1 && it.phoneId == 0) }
                val primaryApp = devApps.firstOrNull()
                val appIcon = getClonageAppIcon(primaryApp?.iconKey ?: "")
                val appColor = getClonageAppColor(primaryApp?.iconKey ?: "")

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectPhone(profile.phoneId) },
                    label = {
                        Text(
                            text = if (isPrimary) "Finday A (Voalohany)" else profile.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isPrimary) Icons.Default.PhoneAndroid else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isSelected) (if (isPrimary) CyberCyan else AccentGreen) else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(13.dp)
                        )
                    },
                    trailingIcon = {
                        // Sary famantarana ny app eo amin'ny sisiny
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(appColor.copy(alpha = 0.22f))
                                .border(1.dp, appColor.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = appIcon,
                                contentDescription = primaryApp?.appName ?: "App",
                                tint = appColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (isPrimary) CyberCyan.copy(alpha = 0.2f) else AccentGreen.copy(alpha = 0.2f),
                        selectedLabelColor = Color.White,
                        containerColor = CardSlate.copy(alpha = 0.6f),
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.White.copy(alpha = 0.1f),
                        selectedBorderColor = if (isPrimary) CyberCyan else AccentGreen,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.2.dp
                    )
                )
            }

            // Quick '+' chip to open new device window
            AssistChip(
                onClick = onOpenNewDeviceWindow,
                label = { Text("+ Apareil Vaovao", fontSize = 11.sp, color = CyberCyan) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(13.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(containerColor = CyberCyan.copy(alpha = 0.12f)),
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberCyan.copy(alpha = 0.4f))
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // 0. TOETRY NY INTERNET AMIN'NY FOTOANA TENA IZY (Live Host Internet & Sandbox Status)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHostInternetAvailable) Color(0xFF0C1F17) else Color(0xFF281014)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (isHostInternetAvailable) AccentGreen.copy(alpha = 0.55f) else AccentRed.copy(alpha = 0.55f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isHostInternetAvailable) AccentGreen.copy(alpha = 0.2f) else AccentRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isHostInternetAvailable) Icons.Default.Language else Icons.Default.WifiOff,
                                    contentDescription = null,
                                    tint = if (isHostInternetAvailable) AccentGreen else AccentRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isHostInternetAvailable) "🌐 Internet Finday : MIFANDRAY" else "⚠️ Internet Finday : TSY MISY",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (isHostInternetAvailable) AccentGreen else AccentRed)
                                    )
                                }
                                Text(
                                    text = if (isHostInternetAvailable)
                                        "Tambazotra tena izy : $hostNetworkType • Vonona ny Chrome & IA"
                                    else
                                        "Ampifandraiso amin'ny Wi-Fi na Données Mobiles ny finday",
                                    color = if (isHostInternetAvailable) AccentGreen else AccentRed.copy(alpha = 0.85f),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Badge toetry ny Pare-feu
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isGlobalMobileDataConnected) AccentGreen.copy(alpha = 0.18f) else AccentRed.copy(alpha = 0.18f))
                                .border(1.dp, if (isGlobalMobileDataConnected) AccentGreen.copy(alpha = 0.5f) else AccentRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isGlobalMobileDataConnected) "DATA: 🟢 ON" else "DATA: 🔴 OFF",
                                color = if (isGlobalMobileDataConnected) AccentGreen else AccentRed,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // 4. INDEPENDENCE NOTICE BANNER (rehefa ao amin'ny apareil mahaleo tena)
            if (!isPrimaryDevice) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Apareil Mahaleo Tena Tanteraka (Isolation 100%)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ity apareil ity dia tsy mifandray amin'ny voalohany. Ny kaonty sy ny fitahirizana dia mitokana.",
                                    color = TextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4b. FITANTANANA IREO APAREIL VIRTOALY MIPETAKA (ACTIVE VIRTUAL DEVICES SECTION)
            // - "Dia rehefa voafidy ilay ilay App de tokony iseo eo aminy sisiny eo ny sarininy mba afatarana azy"
            // - "de azo atao ihany koa ny my retirer anireo appareil miseo ireo rehefa tsindrina elabe ilay appareil de misy soratra hoe retirer ny eo de rehefa tsindrina ilay retirer de miala ilay appareil no tsindrina"
            // - "de azo atao ihany koa ny my sélectionner azy rehetra rehefa tsindrina elabe ny ray aminio appareil io de misy otrany zavatra boribory ny eny ambony aminy aminy sisiny à droite de rehefa tsindrina iny de vo sélectionner daholo ny appareil rehetra eo de azo esorina daholo"
            // - "de azo atao tsara ihany koa ny manazanona ny connexion rehetra rehefa ao anatiny ao sy ny tsy rairay"
            item {
                val nonPrimaryProfiles = allProfiles.filter { it.phoneId > 1 }
                val isAllSelected = nonPrimaryProfiles.isNotEmpty() && nonPrimaryProfiles.all { selectedDeviceIds.contains(it.phoneId) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_devices_cluster_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Header with Title and "Zavatra boribory eny ambony amin'ny sisiny à droite"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CyberCyan.copy(alpha = 0.15f))
                                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Devices,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Ireo Apareil Virtoaly (${allProfiles.size})",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Tsindrio ela be mba hanesorana ('Retirer')",
                                        color = TextSecondary,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }

                            // ZAVATRA BORIBORY ENY AMBONY AMIN'NY SISINY À DROITE:
                            // "de misy otrany zavatra boribory ny eny ambony aminy aminy sisiny à droite de rehefa tsindrina iny de vo sélectionner daholo ny appareil rehetra eo de azo esorina daholo"
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isDeviceMultiSelectMode) {
                                    Text(
                                        text = if (isAllSelected) "Daholo" else "Safidio Daholo",
                                        color = CyberCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isAllSelected) CyberCyan else Color.White.copy(alpha = 0.1f))
                                        .border(2.dp, if (isAllSelected) CyberCyan else Color.White.copy(alpha = 0.4f), CircleShape)
                                        .clickable {
                                            if (isAllSelected) {
                                                selectedDeviceIds = emptySet()
                                                isDeviceMultiSelectMode = false
                                            } else {
                                                selectedDeviceIds = nonPrimaryProfiles.map { it.phoneId }.toSet()
                                                isDeviceMultiSelectMode = true
                                            }
                                        }
                                        .testTag("circle_select_all_devices_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isAllSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Voafidy daholo",
                                            tint = Color.Black,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, CyberCyan, CircleShape)
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Multi-Select Batch Delete Banner (rehefa voatsindry ny boribory na voafidy)
                        if (selectedDeviceIds.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentRed.copy(alpha = 0.2f))
                                    .border(1.2.dp, AccentRed.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "Voafidy: ${selectedDeviceIds.size} fitaovana",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            onDeleteMultipleDevices(selectedDeviceIds)
                                            selectedDeviceIds = emptySet()
                                            isDeviceMultiSelectMode = false
                                            longPressedDeviceId = null
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Retirer Daholo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(
                                        onClick = {
                                            selectedDeviceIds = emptySet()
                                            isDeviceMultiSelectMode = false
                                            longPressedDeviceId = null
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Aoka ihany", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        // 3. MANAJANONA NY CONNEXION REHETRA:
                        // "de azo atao tsara ihany koa ny manazanona ny connexion rehetra rehefa ao anatiny ao sy ny tsy rairay"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = onDisconnectAllConnections,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(9.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentRed.copy(alpha = 0.2f),
                                    contentColor = AccentRed
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.SignalCellularOff, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("🔴 Hajanona Ny Rehetra", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }

                            Button(
                                onClick = onConnectAllConnections,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(9.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentGreen.copy(alpha = 0.2f),
                                    contentColor = AccentGreen
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.SignalCellularAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("🟢 Mampifandray Daholo", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }

                        // 4. LISITRY NY APAREIL TSIRAIRAY (MIARAKA AMIN'NY SARY EO AMIN'NY SISINY SY BOKOTRA RETIRER)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            allProfiles.forEach { profile ->
                                val isPrimary = profile.phoneId == 1
                                val isDeviceSelected = selectedDeviceIds.contains(profile.phoneId)
                                val isLongPressed = longPressedDeviceId == profile.phoneId
                                val isDeviceActive = profile.phoneId == activePhoneId
                                val devApps = firewallApps.filter { it.phoneId == profile.phoneId || (profile.phoneId == 1 && it.phoneId == 0) }
                                val primaryApp = devApps.firstOrNull()
                                val appIcon = getClonageAppIcon(primaryApp?.iconKey ?: "")
                                val appColor = getClonageAppColor(primaryApp?.iconKey ?: "")
                                val isConnected = profile.mobileDataConnected || (isPrimary && isGlobalMobileDataConnected)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                if (isDeviceMultiSelectMode && !isPrimary) {
                                                    selectedDeviceIds = if (isDeviceSelected) selectedDeviceIds - profile.phoneId else selectedDeviceIds + profile.phoneId
                                                } else {
                                                    onSelectPhone(profile.phoneId)
                                                }
                                            },
                                            onLongClick = {
                                                longPressedDeviceId = profile.phoneId
                                                deviceForActions = profile
                                                if (!isPrimary) {
                                                    selectedDeviceIds = selectedDeviceIds + profile.phoneId
                                                }
                                            }
                                        ),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isDeviceSelected) AccentRed.copy(alpha = 0.12f) else if (isDeviceActive) DeepSlate else CardSlate.copy(alpha = 0.6f)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isDeviceActive || isDeviceSelected) 1.5.dp else 1.dp,
                                        if (isDeviceSelected) AccentRed else if (isDeviceActive) CyberCyan else Color.White.copy(alpha = 0.08f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // ANKAVIA: Selection checkbox (if multi-select) + Device info
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (isDeviceMultiSelectMode && !isPrimary) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isDeviceSelected) AccentRed else Color.Transparent)
                                                        .border(1.5.dp, if (isDeviceSelected) AccentRed else Color.White.copy(alpha = 0.4f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isDeviceSelected) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(30.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isPrimary) CyberCyan.copy(alpha = 0.15f) else AccentGreen.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPrimary) Icons.Default.PhoneAndroid else Icons.Default.Shield,
                                                        contentDescription = null,
                                                        tint = if (isPrimary) CyberCyan else AccentGreen,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Text(
                                                        text = if (isPrimary) "Finday A (Voalohany)" else profile.name,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    if (isDeviceActive) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(CyberCyan.copy(alpha = 0.2f))
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        ) {
                                                            Text("ACTIVE", color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(CyberCyan.copy(alpha = 0.12f))
                                                            .border(0.5.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                            .clickable { inspectedRealProfile = profile }
                                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                            Text(
                                                                text = "📱 ${profile.modelName}",
                                                                color = CyanBright,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Icon(
                                                                imageVector = Icons.Default.Info,
                                                                contentDescription = "Spécifications Réelles",
                                                                tint = CyanBright,
                                                                modifier = Modifier.size(10.dp)
                                                            )
                                                        }
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color.White.copy(alpha = 0.05f))
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "12GB • Isolé",
                                                            color = TextSecondary,
                                                            fontSize = 8.5.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // AN-KAVANANA (EO AMIN'NY SISINY):
                                        // 1. Sary famantarana ny App voafidy ("mba hahafantarana azy")
                                        // 2. Bokotra Retirer (raha tsindrina ela be)
                                        // 3. Fampifandraisana ny tsirairay (Connecté/Déconnecté)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // SARY EO AMIN'NY SISINY:
                                            // Azo tsindrina mivantana na tsindrina ela be mba hanoloana ny app na hisafidianana safidy
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(appColor.copy(alpha = 0.16f))
                                                    .border(1.dp, appColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                    .combinedClickable(
                                                        onClick = {
                                                            deviceForReplaceApp = profile
                                                        },
                                                        onLongClick = {
                                                            longPressedDeviceId = profile.phoneId
                                                            deviceForActions = profile
                                                        }
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = appIcon,
                                                        contentDescription = primaryApp?.appName ?: "App",
                                                        tint = appColor,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = primaryApp?.appName?.take(9) ?: "App",
                                                        color = Color.White,
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            // BOKOTRA REHEFA TSINDRINA ELA BE: "suprimer sy remplacer ary singer"
                                            // - "raha hoatra ka suprimer izy de ny appareil mihintsy no suprimer"
                                            // - "raha remplacer izy de ny application fotsiny no ovaina"
                                            // - "ary singer"
                                            if (isLongPressed || isDeviceSelected || longPressedDeviceId == profile.phoneId) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    // 1. SUPRIMER: Ny appareil mihintsy no suprimer
                                                    if (!isPrimary) {
                                                        Button(
                                                            onClick = {
                                                                onDeleteDevice(profile.phoneId)
                                                                longPressedDeviceId = null
                                                                selectedDeviceIds = selectedDeviceIds - profile.phoneId
                                                            },
                                                            shape = RoundedCornerShape(7.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White),
                                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                            modifier = Modifier
                                                                .height(28.dp)
                                                                .testTag("device_suprimer_button_${profile.phoneId}")
                                                        ) {
                                                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(2.dp))
                                                            Text("Suprimer", fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold)
                                                        }
                                                    }

                                                    // 2. REMPLACER: Ny application fotsiny no ovaina
                                                    Button(
                                                        onClick = {
                                                            deviceForReplaceApp = profile
                                                            longPressedDeviceId = null
                                                        },
                                                        shape = RoundedCornerShape(7.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = BackgroundDark),
                                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                        modifier = Modifier
                                                            .height(28.dp)
                                                            .testTag("device_remplacer_button_${profile.phoneId}")
                                                    ) {
                                                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Text("Remplacer", fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold)
                                                    }


                                                }
                                            }

                                            // FAMPANDREHESANA SY NY TSIRAIRAY (Données Mobiles & Wi-Fi Manokana):
                                            // "fa azo atao tsara anefa manazanona ny afa aminy alalany donne mobile manokana eo aminy de otrany izay ihany koa ny WiFi"
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                // 1. Donnée Mobile Manokana
                                                val isDevMobileDataOn = profile.mobileDataConnected || (isPrimary && isGlobalMobileDataConnected)
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(7.dp))
                                                        .background(if (isDevMobileDataOn) AccentGreen.copy(alpha = 0.16f) else AccentRed.copy(alpha = 0.16f))
                                                        .border(1.dp, if (isDevMobileDataOn) AccentGreen.copy(alpha = 0.6f) else AccentRed.copy(alpha = 0.6f), RoundedCornerShape(7.dp))
                                                        .clickable {
                                                            if (isPrimary) {
                                                                onToggleGlobalMobileData()
                                                            } else {
                                                                onToggleDeviceMobileData(profile.phoneId)
                                                            }
                                                        }
                                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                                        .testTag("device_mobile_data_toggle_${profile.phoneId}"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                        Icon(
                                                            imageVector = if (isDevMobileDataOn) Icons.Default.SignalCellularAlt else Icons.Default.SignalCellularOff,
                                                            contentDescription = "Données Mobiles Manokana",
                                                            tint = if (isDevMobileDataOn) AccentGreen else AccentRed,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Text(
                                                            text = if (isDevMobileDataOn) "Data: 🟢" else "Data: 🔴",
                                                            color = if (isDevMobileDataOn) AccentGreen else AccentRed,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }

                                                // 2. Wi-Fi Manokana
                                                val isDevWifiOn = profile.wifiConnected || (isPrimary && isGlobalWifiConnected)
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(7.dp))
                                                        .background(if (isDevWifiOn) CyberCyan.copy(alpha = 0.16f) else AccentRed.copy(alpha = 0.16f))
                                                        .border(1.dp, if (isDevWifiOn) CyberCyan.copy(alpha = 0.6f) else AccentRed.copy(alpha = 0.6f), RoundedCornerShape(7.dp))
                                                        .clickable {
                                                            if (isPrimary) {
                                                                onToggleGlobalWifi()
                                                            } else {
                                                                onToggleDeviceWifi(profile.phoneId)
                                                            }
                                                        }
                                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                                        .testTag("device_wifi_toggle_${profile.phoneId}"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                        Icon(
                                                            imageVector = if (isDevWifiOn) Icons.Default.Wifi else Icons.Default.WifiOff,
                                                            contentDescription = "Wi-Fi Manokana",
                                                            tint = if (isDevWifiOn) CyberCyan else AccentRed,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Text(
                                                            text = if (isDevWifiOn) "Wi-Fi: 🟢" else "Wi-Fi: 🔴",
                                                            color = if (isDevWifiOn) CyberCyan else AccentRed,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. ENY AMIN'NY FARANY AMBONY: SARY DONNÉE MOBILE NGEZABE SY SARY WI-FI NGEZABE
            // "Rehefa tsindrina ilay sary donne mobile ngezabe io de avy dia mandeha daholo ny connexion ny appareil rehetra
            // fa azo atao tsara anefa manazanona ny afa aminy alalany donne mobile manokana eo aminy de otrany izay ihany koa ny WiFi"
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // --- 1. SARY DONNÉE MOBILE NGEZABE ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.85f)),
                        border = androidx.compose.foundation.BorderStroke(2.dp, mobileDataColor.copy(alpha = 0.65f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = mobileDataColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "DONNÉES MOBILES ($deviceDisplayName)",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // SARY DONNÉE MOBILE NGEZABE (Tsindrina mba hiova mena/maintso ary handeha daholo ny connexion)
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                                    .background(mobileDataColor.copy(alpha = 0.18f))
                                    .border(3.dp, mobileDataColor, CircleShape)
                                    .clickable { onToggleGlobalMobileData() }
                                    .testTag("big_mobile_data_icon"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isGlobalMobileDataConnected) Icons.Default.SignalCellularAlt else Icons.Default.SignalCellularOff,
                                    contentDescription = "Donnée Mobile Ngezabe",
                                    tint = mobileDataColor,
                                    modifier = Modifier.size(52.dp)
                                )
                            }

                            // Soratra milaza ny toetrany (Connecté na Déconnecté)
                            Text(
                                text = mobileDataStatusText,
                                color = mobileDataColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )

                            Text(
                                text = mobileDataSubText,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isGlobalMobileDataConnected) AccentGreen.copy(alpha = 0.15f) else AccentRed.copy(alpha = 0.15f))
                                    .border(1.dp, if (isGlobalMobileDataConnected) AccentGreen.copy(alpha = 0.4f) else AccentRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isGlobalMobileDataConnected)
                                        "● MANDEHA DAHOLO NY CONNEXION NY APAREIL REHETRA"
                                    else
                                        "● TSINDRIO MBA HANDEHA DAHOLO NY CONNEXION",
                                    color = if (isGlobalMobileDataConnected) AccentGreen else AccentRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // --- 2. SARY WI-FI NGEZABE ("de otrany izay ihany koa ny WiFi") ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.85f)),
                        border = androidx.compose.foundation.BorderStroke(2.dp, wifiColor.copy(alpha = 0.65f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = wifiColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "WI-FI GLOBAL ($deviceDisplayName)",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // SARY WI-FI NGEZABE (Tsindrina mba hiova mena/maintso ary handeha daholo ny Wi-Fi)
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                                    .background(wifiColor.copy(alpha = 0.18f))
                                    .border(3.dp, wifiColor, CircleShape)
                                    .clickable { onToggleGlobalWifi() }
                                    .testTag("big_wifi_icon"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isGlobalWifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                                    contentDescription = "Wi-Fi Ngezabe",
                                    tint = wifiColor,
                                    modifier = Modifier.size(52.dp)
                                )
                            }

                            // Soratra milaza ny toetrany (Connecté na Déconnecté)
                            Text(
                                text = wifiStatusText,
                                color = wifiColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )

                            Text(
                                text = wifiSubText,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isGlobalWifiConnected) AccentGreen.copy(alpha = 0.15f) else AccentRed.copy(alpha = 0.15f))
                                    .border(1.dp, if (isGlobalWifiConnected) AccentGreen.copy(alpha = 0.4f) else AccentRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isGlobalWifiConnected)
                                        "● MANDEHA DAHOLO NY WI-FI AMIN'NY APAREIL REHETRA"
                                    else
                                        "● TSINDRIO MBA HANDEHA DAHOLO NY WI-FI",
                                    color = if (isGlobalWifiConnected) AccentGreen else AccentRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // 5b. "THE BOOK AND THE READERS" ANALOGY & TELEMETRY SIMULATOR
            item {
                BookAndReadersSummaryCard(
                    onOpenSimulator = onOpenBookAnalogy
                )
            }

            // 5c. BATCH MANAGEMENT CONTROLS (Pause, Resume, Restart all active Android clones simultaneously)
            item {
                BatchManagementCard(
                    profiles = allProfiles,
                    isBatchRestarting = isBatchRestarting,
                    onPauseAll = onPauseAllClones,
                    onResumeAll = onResumeAllClones,
                    onRestartAll = onRestartAllClones,
                    onPauseSingle = onPauseSingleClone,
                    onResumeSingle = onResumeSingleClone,
                    onRestartSingle = onRestartSingleClone
                )
            }

            // 5d. NESTED EXPANSION & TREE VIEW (Groupes -> Appareils -> Applications avec modes Vidéo/Pub/Rien)
            item {
                HierarchyTreeViewCard(
                    profiles = allProfiles
                )
            }

            // 5e. VIRTUAL DEVICE RESOURCE MONITOR
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.8f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Virtual Device Resource Monitor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        allProfiles.forEach { profile ->
                            val isCpuSpiking = profile.cpuLoad > 90
                            val isRamSpiking = profile.ramLoad > 90

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(if (isCpuSpiking || isRamSpiking) AccentRed.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(profile.name.take(15), color = if (isCpuSpiking || isRamSpiking) AccentRed else TextSecondary, fontSize = 12.sp, fontWeight = if (isCpuSpiking || isRamSpiking) FontWeight.Bold else FontWeight.Normal)
                                    if (isCpuSpiking || isRamSpiking) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Warning, contentDescription = "Spike Warning", tint = AccentRed, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("CPU", fontSize = 9.sp, color = TextSecondary)
                                        Text("${profile.cpuLoad}%", fontSize = 11.sp, color = if (isCpuSpiking) AccentRed else Color.White)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("RAM", fontSize = 9.sp, color = TextSecondary)
                                        Text("${profile.ramLoad}%", fontSize = 11.sp, color = if (isRamSpiking) AccentRed else Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. IA NO MAMAKY AZY (AI INSPECTOR & TELEMETRY)
            // "ary IA no mamaky azy fa azo atao ihany koa ny miditra ao anatiny ilay app rehefa mandeha izy"
            item {
                AiInspectionCard(
                    isDataActive = isGlobalMobileDataConnected,
                    aiLogs = aiLogs,
                    isAiAnalyzing = isAiAnalyzing,
                    onTriggerAiAnalysis = onTriggerAiAnalysis
                )
            }

            // 7. LOHATENY: IREO FAMPIHARANA CLONAGE FOTSINY
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Fampiharana Clonage Napetaka",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${filteredApps.size} fampiharana • Mahaleo tena tanteraka",
                            color = CyberCyan,
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedFilter == "ALL",
                            onClick = { selectedFilter = "ALL" },
                            label = { Text("Daholo", fontSize = 10.5.sp) }
                        )
                        FilterChip(
                            selected = selectedFilter == "ACTIVE",
                            onClick = { selectedFilter = "ACTIVE" },
                            label = { Text("Miasa", fontSize = 10.5.sp) }
                        )
                    }
                }
            }

            // 8. LIST OF CLONED APPS WITH RUNNING STATUS & "MIDITRA AO ANATINY" BUTTON
            // "ary avy dia miasa daholo rehefa tsindrina ilay donne mobile misy anilay app napetaka ary IA no mamaky azy
            //  fa azo atao ihany koa ny miditra ao anatiny ilay app rehefa mandeha izy"
            if (filteredApps.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Mbola tsy misy fampiharana napetaka eto",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tsindrio ny '+ Hampiditra Application' na ny bokotra '+' eny ambony à gauche",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredApps, key = { it.id }) { app ->
                    ClonageAppItemRow(
                        app = app,
                        isGlobalMobileDataConnected = isGlobalMobileDataConnected,
                        isGlobalWifiConnected = isGlobalWifiConnected,
                        onToggleMobileData = { onToggleAppMobileData(app) },
                        onToggleWifi = { onToggleAppWifi(app) },
                        onEnterApp = { onOpenClonedApp(app) },
                        onDeleteApp = { appToDelete = app }
                    )
                }
            }

            // 9. BOKOTRA HAMPIDIRANA APPLICATION CLONAGE VAOVAO
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showAddAppDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Hampiditra app",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ Hampiditra Application Clonage ao amin'ity apareil ity",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onOpenAiAppDiscovery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonViolet.copy(alpha = 0.2f),
                            contentColor = NeonViolet
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonViolet.copy(alpha = 0.7f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = NeonViolet
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✨ Mikaroka App amin'ny IA & Sokafy ao Chrome",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // 10. FENÊTRE VAOVAO: APAREIL ANDROID VAOVAO MAHALEO TENA (rehefa tsindrina ilay + eny ambony à gauche)
    if (isNewDeviceWindowOpen) {
        NewIndependentDeviceModal(
            onDismiss = onCloseNewDeviceWindow,
            onPlace = { name, apps, count, isInfinite, isAiVideoWatchEnabled, isChrome, cmdName, targetApp, modelName ->
                onCreateIndependentDevice(name, apps, count, isInfinite, isAiVideoWatchEnabled, isChrome, cmdName, targetApp, modelName)
            },
            nextDeviceNumber = (allProfiles.maxOfOrNull { it.phoneId } ?: 1) + 1
        )
    }

    // DIALOG: MOMBA NY FITAOVANA TENA IZY (Spécifications Réelles an'ilay fitaovana nofitinina)
    inspectedRealProfile?.let { profile ->
        val isDevMobileDataOn = profile.mobileDataConnected || (profile.phoneId == 1 && isGlobalMobileDataConnected)
        val isDevWifiOn = profile.wifiConnected || (profile.phoneId == 1 && isGlobalWifiConnected)
        val devApps = firewallApps.filter { it.phoneId == profile.phoneId || (profile.phoneId == 1 && it.phoneId == 0) }

        androidx.compose.ui.window.Dialog(onDismissRequest = { inspectedRealProfile = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = DeepSlate,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyan)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = CyanBright, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(profile.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(profile.modelName, color = CyanBright, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        IconButton(onClick = { inspectedRealProfile = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Akatona", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 Ity fitaovana ity dia tena fitaovana tena izy (hardware réel), nofitinina fotsiny ny endriny eto mba hanamora ny fiasany sy ny fitantanana azy marobe miaraka.",
                            color = TextSecondary,
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardSlate)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Modely Hardware :", color = TextSecondary, fontSize = 11.sp)
                            Text(profile.modelName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rafitra (OS) :", color = TextSecondary, fontSize = 11.sp)
                            Text("Android 14 (Patch Sécurité 2026)", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Processeur :", color = TextSecondary, fontSize = 11.sp)
                            Text("Octa-core 3.3 GHz (64-bit)", color = Color.White, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("RAM sy Fitahirizana :", color = TextSecondary, fontSize = 11.sp)
                            Text("12 Go RAM • ${profile.storageTotalMb / 1000} Go Stockage", color = Color.White, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("IMEI Fitaovana :", color = TextSecondary, fontSize = 11.sp)
                            Text("86549204819400${profile.phoneId}", color = CyanBright, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("MAC Wi-Fi Sandbox :", color = TextSecondary, fontSize = 11.sp)
                            Text("54:E4:3A:89:01:${String.format("%02X", profile.phoneId)}", color = Color.White, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kaonty Google Mitokana :", color = TextSecondary, fontSize = 11.sp)
                            Text(profile.googleAccount, color = Color.White, fontSize = 10.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    // Toggles réseau
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (profile.phoneId == 1) onToggleGlobalMobileData() else onToggleDeviceMobileData(profile.phoneId)
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDevMobileDataOn) AccentGreen.copy(alpha = 0.2f) else AccentRed.copy(alpha = 0.2f),
                                contentColor = if (isDevMobileDataOn) AccentGreen else AccentRed
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDevMobileDataOn) AccentGreen else AccentRed)
                        ) {
                            Text(if (isDevMobileDataOn) "Data: 🟢 Mandeha" else "Data: 🔴 Tapaka", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (profile.phoneId == 1) onToggleGlobalWifi() else onToggleDeviceWifi(profile.phoneId)
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDevWifiOn) CyberCyan.copy(alpha = 0.2f) else AccentRed.copy(alpha = 0.2f),
                                contentColor = if (isDevWifiOn) CyberCyan else AccentRed
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDevWifiOn) CyberCyan else AccentRed)
                        ) {
                            Text(if (isDevWifiOn) "Wi-Fi: 🟢 Mandeha" else "Wi-Fi: 🔴 Tapaka", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            onSelectPhone(profile.phoneId)
                            inspectedRealProfile = null
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("FIDIO HO FITAOVANA MIASA (Actif)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 11. DIALOG HAMPIDIRANA FAMPIHARANA VAOVAO ETO
    if (showAddAppDialog) {
        var newAppName by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("youtube") }

        val appTemplates = listOf(
            Triple("YouTube (Clone)", "youtube", Color(0xFFFF0000)),
            Triple("WhatsApp (Clone)", "whatsapp", AccentGreen),
            Triple("Facebook (Clone)", "facebook", Color(0xFF1877F2)),
            Triple("Telegram (Clone)", "telegram", Color(0xFF2AABEE)),
            Triple("TikTok (Clone)", "tiktok", Color(0xFFEC4899)),
            Triple("Chrome (Clone)", "browser", CyberCyan)
        )

        AlertDialog(
            onDismissRequest = { showAddAppDialog = false },
            title = {
                Text(
                    text = "Hampiditra Application Clonage",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Safidio na ampidiro ny anaran'ny fampiharana clonage tianao hapetaka eto:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "Fampiharana matetika ampiasaina:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        appTemplates.take(3).forEach { (name, iconKey, _) ->
                            FilterChip(
                                selected = newAppName == name,
                                onClick = {
                                    newAppName = name
                                    selectedType = iconKey
                                },
                                label = { Text(name.substringBefore(" "), fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newAppName,
                        onValueChange = { newAppName = it },
                        label = { Text("Anaran'ny Application") },
                        placeholder = { Text("oh: YouTube Clone 2, WhatsApp Pro") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAppName.isNotBlank()) {
                            onAddFirewallApp(newAppName, selectedType)
                            showAddAppDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                ) {
                    Text("Ampidiro")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAppDialog = false }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }

    // 12. DIALOG HANAISORANA / HAMAFA FANAMPIHARANA (Retirer App)
    if (appToDelete != null) {
        val app = appToDelete!!
        AlertDialog(
            onDismissRequest = { appToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = AccentRed)
                    Text("Hanaisotra ity Fampiharana ity?", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Tena hofafana sy hesorina (retirer) amin'ity Apareil ity ve ny fampiharana \"${app.appName}\"?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteFirewallApp(app)
                        appToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White)
                ) {
                    Text("Eny, Esory")
                }
            },
            dismissButton = {
                TextButton(onClick = { appToDelete = null }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }

    // 13. DIALOG HAMAFA APAREIL ANDROID CLONE (Delete Device Instance)
    if (deviceToDelete != null) {
        val dev = deviceToDelete!!
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = AccentRed)
                    Text("Hamafa ny ${dev.name}?", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Tena hofafana tanteraka ve ity Apareil Android mahaleo tena ity sy ireo apps ary tahiry rehetra ao aminy?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDevice(dev.phoneId)
                        deviceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White)
                ) {
                    Text("Eny, Fafao")
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }

    // 14. DIALOG SAFIDY REHEFA TSINDRINA ELA BE: "suprimer sy remplacer ary singer"
    // User request: "raha tsindrina ela be ilay izy de misy soratra hoe suprimer sy remplacer ary singer
    // raha hoatra ka suprimer izy de ny appareil mihintsy no suprimer raha remplacer izy de ny application fotsiny no ovaina"
    if (deviceForActions != null) {
        val dev = deviceForActions!!
        val isDevPrimary = dev.phoneId == 1
        val devApps = firewallApps.filter { it.phoneId == dev.phoneId || (isDevPrimary && it.phoneId == 0) }
        val devApp = devApps.firstOrNull()

        AlertDialog(
            onDismissRequest = { deviceForActions = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(
                            text = "Fitantanana: ${dev.name}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "📱 ${dev.modelName} • App: ${devApp?.appName ?: "Fampiharana"}",
                            color = CyanBright,
                            fontSize = 10.5.sp
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Safidio ny hetsika tianao hatao amin'ity fitaovana ity:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 1. SAFIDY SUPRIMER
                    Button(
                        onClick = {
                            val targetId = dev.phoneId
                            deviceForActions = null
                            if (!isDevPrimary) {
                                onDeleteDevice(targetId)
                            } else {
                                deviceToDelete = dev
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.9f))
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Suprimer (Ny Appareil)", fontWeight = FontWeight.Bold)
                    }

                    // 2. SAFIDY REMPLACER
                    Button(
                        onClick = {
                            val target = dev
                            deviceForActions = null
                            deviceForReplaceApp = target
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.9f), contentColor = BackgroundDark)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remplacer (App)", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { deviceForActions = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aoka ihany (Fermer)")
                }
            },
            containerColor = CardSlate
        )
    }

    // 15. DIALOG REMPLACER APPLICATION (Ny Application fotsiny no ovaina!)
    if (deviceForReplaceApp != null) {
        val dev = deviceForReplaceApp!!
        val isDevPrimary = dev.phoneId == 1
        val devApps = firewallApps.filter { it.phoneId == dev.phoneId || (isDevPrimary && it.phoneId == 0) }
        val currentApp = devApps.firstOrNull()

        var customAppName by remember { mutableStateOf("") }
        var isCustomSelected by remember { mutableStateOf(false) }

        val replacementCatalog = listOf(
            Triple("YouTube", "youtube", "com.google.android.youtube"),
            Triple("Chrome", "browser", "com.android.chrome"),
            Triple("WhatsApp", "whatsapp", "com.whatsapp"),
            Triple("Telegram", "telegram", "org.telegram.messenger"),
            Triple("Facebook", "facebook", "com.facebook.katana"),
            Triple("TikTok", "tiktok", "com.zhiliaoapp.musically"),
            Triple("Instagram", "instagram", "com.instagram.android"),
            Triple("Twitter / X", "twitter", "com.twitter.android"),
            Triple("Messenger", "messenger", "com.facebook.orca"),
            Triple("Netflix", "netflix", "com.netflix.mediaclient"),
            Triple("Spotify", "spotify", "com.spotify.music"),
            Triple("VidMat", "vidmat", "com.nemo.vidmate")
        )

        AlertDialog(
            onDismissRequest = { deviceForReplaceApp = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = CyberCyan)
                    Column {
                        Text("Remplacer l'Application", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("📱 ${dev.name} • Ny app ihany no soloina", color = CyanBright, fontSize = 11.sp)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "App mipetaka ankehitriny : \"${currentApp?.appName ?: "Fampiharana"}\"",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Safidio ny fampiharana vaovao hisolo toerana azy eo amin'ity appareil ity:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    // Grid-like chips of popular applications
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val chunked = replacementCatalog.chunked(3)
                        chunked.forEach { rowApps ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowApps.forEach { (name, iconKey, pkg) ->
                                    val chipColor = getClonageAppColor(iconKey)
                                    val isSelected = !isCustomSelected && customAppName == name
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) chipColor.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f))
                                            .border(1.dp, if (isSelected) chipColor else Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                isCustomSelected = false
                                                customAppName = name
                                                onReplaceApp(dev.phoneId, name, iconKey, pkg)
                                                deviceForReplaceApp = null
                                            }
                                            .padding(vertical = 8.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = getClonageAppIcon(iconKey),
                                                contentDescription = name,
                                                tint = chipColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = name,
                                                color = Color.White,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Safidy hampiditra Application manokana (Custom App name)
                    Text(
                        text = "Na ampidiro anarana app hafa manokana:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = customAppName,
                        onValueChange = {
                            customAppName = it
                            isCustomSelected = true
                        },
                        label = { Text("Anaran'ny App Vaovao") },
                        placeholder = { Text("oh: TikTok Lite, Canva, CapCut") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customAppName.isNotBlank()) {
                            val iconKey = when {
                                customAppName.contains("you", true) -> "youtube"
                                customAppName.contains("chrom", true) -> "browser"
                                customAppName.contains("what", true) -> "whatsapp"
                                customAppName.contains("tele", true) -> "telegram"
                                customAppName.contains("face", true) -> "facebook"
                                customAppName.contains("tik", true) -> "tiktok"
                                customAppName.contains("insta", true) -> "instagram"
                                else -> "custom"
                            }
                            val pkg = "com.app.${customAppName.lowercase().replace(" ", "")}"
                            onReplaceApp(dev.phoneId, customAppName, iconKey, pkg)
                            deviceForReplaceApp = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = BackgroundDark)
                ) {
                    Text("Hanolo (Remplacer)")
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceForReplaceApp = null }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }


}

/**
 * LISITRY NY APPLICATION CLONAGE TSIRAIRAY:
 * - "ary avy dia miasa daholo rehefa tsindrina ilay donne mobile misy anilay app napetaka ary IA no mamaky azy
 *    fa azo atao ihany koa ny miditra ao anatiny ilay app rehefa mandeha izy"
 * - Sisiny à droite: Sary Donnée Mobile sy WiFi
 * - Bokotra "MIDITRA AO ANATINY" mba hidirana ao amin'ilay app rehefa mandeha izy
 */
@Composable
private fun ClonageAppItemRow(
    app: FirewallAppEntity,
    isGlobalMobileDataConnected: Boolean,
    isGlobalWifiConnected: Boolean = true,
    onToggleMobileData: () -> Unit,
    onToggleWifi: () -> Unit,
    onEnterApp: () -> Unit,
    onDeleteApp: () -> Unit = {}
) {
    // Is this app currently able to use mobile data?
    val isDataActive = isGlobalMobileDataConnected && app.allowMobileData
    val dataIconColor = if (isDataActive) AccentGreen else AccentRed

    // Is this app currently able to use Wi-Fi?
    val isWifiActive = isGlobalWifiConnected && app.allowWifi
    val wifiIconColor = if (isWifiActive) CyberCyan else TextSecondary

    val isRunning = isDataActive || isWifiActive

    // Pulsing alpha for active running state
    val infiniteTransition = rememberInfiniteTransition(label = "app_running_pulse")
    val runningPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "running_pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEnterApp() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRunning) CardSlate.copy(alpha = 0.9f) else CardSlate.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (isRunning) AccentGreen.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Ankavia: Sary & Anaran'ilay Application Clonage
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(getClonageAppColor(app.iconKey).copy(alpha = 0.2f))
                            .border(1.dp, getClonageAppColor(app.iconKey).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getClonageAppIcon(app.iconKey),
                            contentDescription = app.appName,
                            tint = getClonageAppColor(app.iconKey),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = app.appName,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "Clone",
                                    color = CyberCyan,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Status soratra: MIASA / MANDÉHA na TAPAKA NY DATA
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isRunning) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen.copy(alpha = runningPulse))
                                )
                                Text(
                                    text = "MANDÉHA (Miasa) • IA no mamaky azy",
                                    color = AccentGreen,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "Tapaka ny données • Tsindrio mba hampandehanana",
                                    color = TextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }

                // FARANY AMIN'NY SISINY À DROITE: SARY DONNÉE MOBILE SY WIFI
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sary Donnée Mobile (sisiny havanana)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(dataIconColor.copy(alpha = 0.18f))
                            .border(1.2.dp, dataIconColor.copy(alpha = 0.6f), CircleShape)
                            .clickable { onToggleMobileData() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (app.allowMobileData) Icons.Default.SignalCellularAlt else Icons.Default.SignalCellularOff,
                            contentDescription = "Donnée Mobile ho an'ny ${app.appName}",
                            tint = dataIconColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Sary WiFi (sisiny havanana)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(wifiIconColor.copy(alpha = 0.18f))
                            .border(
                                1.2.dp,
                                if (isWifiActive) CyberCyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f),
                                CircleShape
                            )
                            .clickable { onToggleWifi() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (app.allowWifi) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = "WiFi ho an'ny ${app.appName}",
                            tint = wifiIconColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // BOKOTRA: "MIDITRA AO ANATINY" SY "ESORY / RETIRER"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isRunning) "⚡ Mandeha amin'ny tambazotra voaaro" else "○ Miandry famelomana données",
                    color = if (isRunning) CyberCyan else TextSecondary,
                    fontSize = 10.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Bokotra manala/manaisotra ity app ity (Retirer App)
                    IconButton(
                        onClick = onDeleteApp,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentRed.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Esory ity app ity",
                            tint = AccentRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Button(
                        onClick = onEnterApp,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) CyberCyan else Color.White.copy(alpha = 0.1f),
                            contentColor = if (isRunning) BackgroundDark else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Miditra ao anatiny",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getClonageAppIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "youtube" -> Icons.Default.PlayCircle
        "browser", "chrome" -> Icons.Default.Public
        "whatsapp" -> Icons.Default.Chat
        "telegram" -> Icons.Default.Send
        "facebook" -> Icons.Default.People
        "tiktok" -> Icons.Default.Movie
        "instagram" -> Icons.Default.CameraAlt
        "downloads" -> Icons.Default.Folder
        "vidmat" -> Icons.Default.FileDownload
        else -> Icons.Default.Apps
    }
}

private fun getClonageAppColor(key: String): Color {
    return when (key.lowercase()) {
        "youtube" -> Color(0xFFFF0000)
        "browser", "chrome" -> CyberCyan
        "whatsapp" -> AccentGreen
        "telegram" -> Color(0xFF2AABEE)
        "facebook" -> Color(0xFF1877F2)
        "tiktok" -> Color(0xFFEC4899)
        "instagram" -> Color(0xFFF59E0B)
        "downloads" -> AccentAmber
        "vidmat" -> Color(0xFFFF5722)
        else -> NeonViolet
    }
}
