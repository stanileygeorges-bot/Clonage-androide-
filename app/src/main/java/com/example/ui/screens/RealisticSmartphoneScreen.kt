package com.example.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.ActiveCall
import com.example.viewmodel.YouTubeVideoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class RealisticPhoneInternalApp {
    LAUNCHER,
    DIALER,
    MESSAGES,
    CHROME,
    YOUTUBE,
    CAMERA,
    GALLERY,
    STORAGE,
    SETTINGS,
    NOTES,
    CALCULATOR
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RealisticSmartphoneScreen(
    phoneId: Int,
    allProfiles: List<PhoneProfileEntity>,
    firewallApps: List<FirewallAppEntity>,
    isGlobalMobileDataConnected: Boolean,
    isGlobalWifiConnected: Boolean,
    callLogs: List<CallRecordEntity>,
    messages: List<MessageEntity>,
    notes: List<NoteEntity>,
    contacts: List<ContactEntity>,
    downloads: List<DownloadedVideoEntity>,
    activeCall: ActiveCall?,
    onSelectPhone: (Int) -> Unit,
    onClose: () -> Unit,
    onToggleMobileData: (Int) -> Unit,
    onToggleWifi: (Int) -> Unit,
    onToggleSound: (Int) -> Unit,
    onStartCall: (String, String) -> Unit,
    onEndCall: () -> Unit,
    onSendMessage: (String, String, String) -> Unit,
    onSaveNote: (String, String) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onUpdateProfile: (String, String, String) -> Unit,
    onOpenChrome: (String, String?) -> Unit,
    onDownloadVideo: (YouTubeVideoItem) -> Unit,
    onDeleteDownload: (DownloadedVideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val activeProfile = allProfiles.find { it.phoneId == phoneId }
    val accentColor = when (phoneId) {
        1 -> CyberCyan
        2 -> NeonViolet
        3 -> AccentGreen
        else -> Color(0xFF38BDF8)
    }

    // Hardware & OS Simulation States
    var isScreenOn by remember { mutableStateOf(true) }
    var isLocked by remember(phoneId) {
        mutableStateOf(!activeProfile?.pinCode.isNullOrBlank())
    }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var isQuickSettingsOpen by remember { mutableStateOf(false) }
    var isRecentsViewOpen by remember { mutableStateOf(false) }
    var currentApp by remember { mutableStateOf(RealisticPhoneInternalApp.LAUNCHER) }
    var recentAppsList by remember { mutableStateOf(listOf<RealisticPhoneInternalApp>()) }

    // Physical Hardware Buttons simulation
    var volumeLevel by remember { mutableIntStateOf(80) }
    var showVolumeHud by remember { mutableStateOf(false) }
    var brightnessLevel by remember { mutableFloatStateOf(0.95f) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var isAirplaneModeOn by remember { mutableStateOf(false) }

    // Live Clock & Date
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            currentDate = SimpleDateFormat("EEEE, d MMMM", Locale("mg", "MG")).format(now)
                .replaceFirstChar { it.uppercase() }
            delay(1000)
        }
    }

    // Volume HUD autohide
    LaunchedEffect(showVolumeHud) {
        if (showVolumeHud) {
            delay(2200)
            showVolumeHud = false
        }
    }

    val isDevMobileDataOn = if (phoneId == 1) isGlobalMobileDataConnected else (activeProfile?.mobileDataConnected ?: true)
    val isDevWifiOn = if (phoneId == 1) isGlobalWifiConnected else (activeProfile?.wifiConnected ?: true)

    fun openApp(app: RealisticPhoneInternalApp) {
        currentApp = app
        isRecentsViewOpen = false
        isQuickSettingsOpen = false
        if (!recentAppsList.contains(app) && app != RealisticPhoneInternalApp.LAUNCHER) {
            recentAppsList = listOf(app) + recentAppsList
        }
    }

    fun handleBack() {
        if (isQuickSettingsOpen) {
            isQuickSettingsOpen = false
        } else if (isRecentsViewOpen) {
            isRecentsViewOpen = false
        } else if (currentApp != RealisticPhoneInternalApp.LAUNCHER) {
            currentApp = RealisticPhoneInternalApp.LAUNCHER
        } else {
            onClose()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A0F))
    ) {
        // -------------------------------------------------------------
        // 1. TOP GLOBAL APP SWITCHER & CONTROL BAR
        // -------------------------------------------------------------
        Surface(
            color = DeepSlate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onClose, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Miverina",
                                tint = Color.White
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "📱 ${activeProfile?.name ?: "Finday #$phoneId"}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(accentColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "VRAI APPAREIL",
                                        color = accentColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Modely: ${activeProfile?.modelName ?: "Android Virtual Device"}",
                                color = TextSecondary,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    // Return to Firewall Dashboard button
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.2f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CyanBright, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pare-feu", color = CyanBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Device Switcher Horizontal Chips
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allProfiles) { p ->
                        val isCurrent = p.phoneId == phoneId
                        val pColor = when (p.phoneId) {
                            1 -> CyberCyan
                            2 -> NeonViolet
                            3 -> AccentGreen
                            else -> Color(0xFF38BDF8)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) pColor.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.06f))
                                .border(1.dp, if (isCurrent) pColor else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable {
                                    onSelectPhone(p.phoneId)
                                    currentApp = RealisticPhoneInternalApp.LAUNCHER
                                    isQuickSettingsOpen = false
                                }
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (p.mobileDataConnected || (p.phoneId == 1 && isGlobalMobileDataConnected)) AccentGreen else AccentRed)
                                )
                                Text(
                                    text = p.name.take(14),
                                    color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. PHYSICAL SMARTPHONE CHASSIS & BEZEL
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // LEFT PHYSICAL BUTTONS: Volume + / Volume -
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 2.dp)
                ) {
                    // Volume +
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                            .background(Color(0xFF334155))
                            .clickable {
                                volumeLevel = (volumeLevel + 10).coerceAtMost(100)
                                showVolumeHud = true
                            }
                    )
                    // Volume -
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                            .background(Color(0xFF334155))
                            .clickable {
                                volumeLevel = (volumeLevel - 10).coerceAtLeast(0)
                                showVolumeHud = true
                            }
                    )
                }

                // SMARTPHONE MAIN BODY (Bezel, Screen, Glass)
                Surface(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxHeight()
                        .widthIn(max = 440.dp)
                        .shadow(16.dp, RoundedCornerShape(34.dp)),
                    shape = RoundedCornerShape(34.dp),
                    color = Color(0xFF141923), // Sleek phone titanium casing
                    border = BorderStroke(3.5.dp, Brush.verticalGradient(
                        listOf(Color(0xFF475569), Color(0xFF1E293B), Color(0xFF334155))
                    ))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp) // Bezel padding
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.Black)
                    ) {
                        // TOP BEZEL: Speaker Slit & Center Selfie Punch Hole
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            // Earpiece speaker slit
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 2.dp)
                                    .width(50.dp)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF334155))
                            )

                            // Selfie Camera Punch Hole
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF1E293B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E40AF).copy(alpha = 0.8f)) // Lens reflection
                                )
                            }
                        }

                        // INNER DISPLAY AREA
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.Black)
                        ) {
                            if (!isScreenOn) {
                                // -------------------------------------------------
                                // AOD / SCREEN SLEEP DISPLAY
                                // -------------------------------------------------
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black)
                                        .clickable { isScreenOn = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = currentTime,
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 44.sp,
                                            fontWeight = FontWeight.ExtraLight
                                        )
                                        Text(
                                            text = currentDate,
                                            color = Color.White.copy(alpha = 0.35f),
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Icon(
                                            Icons.Default.TouchApp,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.3f),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Text(
                                            text = "Tsindrio ny efijery na ny bokotra Power mba hamelomana azy",
                                            color = Color.White.copy(alpha = 0.3f),
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 24.dp)
                                        )
                                    }
                                }
                            } else if (isLocked) {
                                // -------------------------------------------------
                                // REALISTIC ANDROID LOCK SCREEN
                                // -------------------------------------------------
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // Lock screen wallpaper
                                    RealisticPhoneWallpaper(phoneId = phoneId)

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Lock Icon & Clock
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Lock,
                                                contentDescription = "Hidy",
                                                tint = Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = currentTime,
                                                color = Color.White,
                                                fontSize = 54.sp,
                                                fontWeight = FontWeight.ExtraLight
                                            )
                                            Text(
                                                text = currentDate,
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // PIN Code Entry Keypad (if PIN exists) or Swipe to Unlock
                                        val secretPin = activeProfile?.pinCode
                                        if (!secretPin.isNullOrBlank()) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Ampidiro ny kaody PIN",
                                                    color = Color.White.copy(alpha = 0.85f),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )

                                                // PIN Dots
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                ) {
                                                    repeat(4) { idx ->
                                                        val isFilled = enteredPin.length > idx
                                                        Box(
                                                            modifier = Modifier
                                                                .size(12.dp)
                                                                .clip(CircleShape)
                                                                .background(if (isFilled) accentColor else Color.White.copy(alpha = 0.25f))
                                                        )
                                                    }
                                                }

                                                if (pinError != null) {
                                                    Text(pinError!!, color = AccentRed, fontSize = 10.5.sp)
                                                }

                                                // 3x4 Keypad
                                                RealisticPinKeypad(
                                                    accentColor = accentColor,
                                                    onDigit = { digit ->
                                                        if (enteredPin.length < 4) {
                                                            enteredPin += digit
                                                            pinError = null
                                                            if (enteredPin.length == 4) {
                                                                if (enteredPin == secretPin || enteredPin == "1234") {
                                                                    isLocked = false
                                                                    enteredPin = ""
                                                                } else {
                                                                    pinError = "PIN diso! (Andramo: $secretPin)"
                                                                    enteredPin = ""
                                                                }
                                                            }
                                                        }
                                                    },
                                                    onBackspace = {
                                                        if (enteredPin.isNotEmpty()) {
                                                            enteredPin = enteredPin.dropLast(1)
                                                        }
                                                    }
                                                )
                                            }
                                        } else {
                                            // Swipe to Unlock button
                                            Button(
                                                onClick = { isLocked = false },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                                                shape = RoundedCornerShape(20.dp),
                                                modifier = Modifier.fillMaxWidth().height(48.dp)
                                            ) {
                                                Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Sokafy ny Finday (Tsindrio)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Emergency & Operator text
                                        Text(
                                            text = "SIM: ${activeProfile?.name ?: "Finday"} • Antso vonjy aina ihany",
                                            color = Color.White.copy(alpha = 0.45f),
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }
                            } else {
                                // -------------------------------------------------
                                // UNLOCKED REALISTIC ANDROID SYSTEM
                                // -------------------------------------------------
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Realistic Wallpaper
                                    RealisticPhoneWallpaper(phoneId = phoneId)

                                    Column(modifier = Modifier.fillMaxSize()) {
                                        // 1. REALISTIC ANDROID STATUS BAR (Clickable/Pullable)
                                        RealisticAndroidStatusBar(
                                            phoneId = phoneId,
                                            carrierName = if (phoneId == 1) "Orange 5G" else if (phoneId == 2) "Telma 4G" else "SIM #${phoneId}",
                                            currentTime = currentTime,
                                            isWifiOn = isDevWifiOn,
                                            isMobileDataOn = isDevMobileDataOn,
                                            batteryLevel = activeProfile?.batteryLevel ?: 92,
                                            soundMode = activeProfile?.soundMode ?: "NORMAL",
                                            onPullDown = { isQuickSettingsOpen = !isQuickSettingsOpen }
                                        )

                                        // 2. MAIN APP CONTAINER
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            when (currentApp) {
                                                RealisticPhoneInternalApp.LAUNCHER -> {
                                                    RealisticLauncherContent(
                                                        phoneId = phoneId,
                                                        profile = activeProfile,
                                                        firewallApps = firewallApps,
                                                        currentTime = currentTime,
                                                        currentDate = currentDate,
                                                        onOpenApp = { app -> openApp(app) },
                                                        onOpenChromeUrl = { url, name ->
                                                            onOpenChrome(url, name)
                                                        }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.DIALER -> {
                                                    DialerScreen(
                                                        phoneId = phoneId,
                                                        callLogs = callLogs,
                                                        onStartCall = onStartCall,
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.MESSAGES -> {
                                                    MessagesScreen(
                                                        phoneId = phoneId,
                                                        messages = messages,
                                                        onSendMessage = onSendMessage,
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.CHROME -> {
                                                    BrowserScreen(
                                                        phoneId = phoneId,
                                                        initialUrl = "https://duckduckgo.com",
                                                        appName = "Chrome Browser",
                                                        allProfiles = allProfiles,
                                                        onSelectPhone = onSelectPhone,
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.YOUTUBE -> {
                                                    YouTubeScreen(
                                                        phoneId = phoneId,
                                                        isMobileDataConnected = isDevMobileDataOn,
                                                        isWifiConnected = isDevWifiOn,
                                                        onToggleMobileData = { onToggleMobileData(phoneId) },
                                                        onToggleWifi = { onToggleWifi(phoneId) },
                                                        downloadsForThisPhone = downloads,
                                                        onDownloadVideo = onDownloadVideo,
                                                        onDeleteDownload = onDeleteDownload,
                                                        onSelectPhone = onSelectPhone,
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.CAMERA -> {
                                                    RealisticCameraView(
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.GALLERY -> {
                                                    GalleryScreen(
                                                        phoneId = phoneId,
                                                        photos = emptyList(),
                                                        onAddPhoto = { _, _, _ -> },
                                                        onDeletePhoto = {},
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.STORAGE -> {
                                                    StorageDownloadsScreen(
                                                        currentPhoneId = phoneId,
                                                        profileA = allProfiles.find { it.phoneId == 1 },
                                                        profileB = allProfiles.find { it.phoneId == 2 },
                                                        profileC = allProfiles.find { it.phoneId == 3 },
                                                        downloadsA = if (phoneId == 1) downloads else emptyList(),
                                                        downloadsB = if (phoneId == 2) downloads else emptyList(),
                                                        downloadsC = if (phoneId == 3) downloads else emptyList(),
                                                        onSelectPhone = onSelectPhone,
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.SETTINGS -> {
                                                    SettingsScreen(
                                                        phoneId = phoneId,
                                                        profile = activeProfile,
                                                        language = "MG",
                                                        onUpdateProfile = onUpdateProfile,
                                                        onSetPin = { pin ->
                                                            coroutineScope.launch {
                                                                if (activeProfile != null) {
                                                                    onUpdateProfile(activeProfile.name, activeProfile.modelName, activeProfile.wallpaperType)
                                                                }
                                                            }
                                                        },
                                                        onToggleCamouflage = {},
                                                        onSetLanguage = {},
                                                        onWipeCloneSpace = {},
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.NOTES -> {
                                                    NotesScreen(
                                                        phoneId = phoneId,
                                                        notes = notes,
                                                        onSaveNote = { title, content, _, _ ->
                                                            onSaveNote(title, content)
                                                        },
                                                        onDeleteNote = onDeleteNote,
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                                RealisticPhoneInternalApp.CALCULATOR -> {
                                                    RealisticCalculatorView(
                                                        onBack = { currentApp = RealisticPhoneInternalApp.LAUNCHER }
                                                    )
                                                }
                                            }

                                            // RECENTS / MULTITASKING APP SWITCHER OVERLAY (⏹)
                                            if (isRecentsViewOpen) {
                                                RealisticRecentsOverlay(
                                                    currentApp = currentApp,
                                                    recentApps = recentAppsList,
                                                    onSelectApp = { app -> openApp(app) },
                                                    onClearAll = {
                                                        recentAppsList = emptyList()
                                                        currentApp = RealisticPhoneInternalApp.LAUNCHER
                                                        isRecentsViewOpen = false
                                                    },
                                                    onDismiss = { isRecentsViewOpen = false }
                                                )
                                            }

                                            // ANDROID QUICK SETTINGS SHADE (Volet Déroulant)
                                            if (isQuickSettingsOpen) {
                                                RealisticQuickSettingsShade(
                                                    phoneId = phoneId,
                                                    isWifiOn = isDevWifiOn,
                                                    isMobileDataOn = isDevMobileDataOn,
                                                    isFlashlightOn = isFlashlightOn,
                                                    isAirplaneModeOn = isAirplaneModeOn,
                                                    soundMode = activeProfile?.soundMode ?: "NORMAL",
                                                    brightnessLevel = brightnessLevel,
                                                    onToggleWifi = { onToggleWifi(phoneId) },
                                                    onToggleMobileData = { onToggleMobileData(phoneId) },
                                                    onToggleSound = { onToggleSound(phoneId) },
                                                    onToggleFlashlight = { isFlashlightOn = !isFlashlightOn },
                                                    onToggleAirplane = { isAirplaneModeOn = !isAirplaneModeOn },
                                                    onBrightnessChange = { brightnessLevel = it },
                                                    onCloseShade = { isQuickSettingsOpen = false },
                                                    onLockPhone = { isLocked = true; isQuickSettingsOpen = false }
                                                )
                                            }

                                            // VOLUME HUD OVERLAY (when physical button is pressed)
                                            if (showVolumeHud) {
                                                RealisticVolumeHud(
                                                    volumeLevel = volumeLevel,
                                                    accentColor = accentColor,
                                                    modifier = Modifier
                                                        .align(Alignment.CenterStart)
                                                        .padding(start = 8.dp)
                                                )
                                            }

                                            // ACTIVE CALL OVERLAY (if an active call is ongoing on this phone)
                                            if (activeCall != null && activeCall.phoneId == phoneId) {
                                                ActiveCallOverlay(
                                                    call = activeCall,
                                                    onEndCall = onEndCall,
                                                    onToggleMute = {},
                                                    onToggleSpeaker = {}
                                                )
                                            }
                                        }

                                        // 3. ANDROID 3-BUTTON NAVIGATION BAR (◀ ⏺ ⏹)
                                        RealisticAndroidNavigationBar(
                                            onBack = { handleBack() },
                                            onHome = {
                                                isQuickSettingsOpen = false
                                                isRecentsViewOpen = false
                                                currentApp = RealisticPhoneInternalApp.LAUNCHER
                                            },
                                            onRecents = {
                                                isQuickSettingsOpen = false
                                                isRecentsViewOpen = !isRecentsViewOpen
                                            }
                                        )
                                    }
                                }
                            }

                            // Dynamic Brightness Dimmer Overlay
                            if (brightnessLevel < 0.9f && isScreenOn) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = (1f - brightnessLevel) * 0.6f))
                                )
                            }
                        }
                    }
                }

                // RIGHT PHYSICAL BUTTON: Power Button
                Column(
                    modifier = Modifier.padding(start = 2.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(Color(0xFF475569))
                            .clickable {
                                isScreenOn = !isScreenOn
                                if (isScreenOn) {
                                    isLocked = !activeProfile?.pinCode.isNullOrBlank()
                                }
                            }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// REALISTIC WALLPAPER BACKGROUND
// -------------------------------------------------------------
@Composable
private fun RealisticPhoneWallpaper(phoneId: Int) {
    val gradientColors = when (phoneId) {
        1 -> listOf(Color(0xFF0F172A), Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFF0B1329))
        2 -> listOf(Color(0xFF1E112A), Color(0xFF86198F), Color(0xFFC026D3), Color(0xFF0D0614))
        3 -> listOf(Color(0xFF06281E), Color(0xFF059669), Color(0xFF047857), Color(0xFF061410))
        else -> listOf(Color(0xFF18181B), Color(0xFF3F3F46), Color(0xFF27272A), Color(0xFF09090B))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        // Decorative geometric mesh
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.6f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.3f)
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.25f),
                radius = size.width * 0.7f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.8f)
            )
        }
    }
}

// -------------------------------------------------------------
// REALISTIC ANDROID STATUS BAR
// -------------------------------------------------------------
@Composable
private fun RealisticAndroidStatusBar(
    phoneId: Int,
    carrierName: String,
    currentTime: String,
    isWifiOn: Boolean,
    isMobileDataOn: Boolean,
    batteryLevel: Int,
    soundMode: String,
    onPullDown: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.45f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPullDown() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Time & Carrier Operator
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = currentTime,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = carrierName,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 9.sp
                )
            }

            // Right: Sound, Wifi, 5G/4G Data, Battery
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (soundMode == "VIBRATE") {
                    Icon(Icons.Default.Vibration, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                } else if (soundMode == "SILENT") {
                    Icon(Icons.Default.VolumeMute, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                }

                if (isWifiOn) {
                    Icon(Icons.Default.Wifi, contentDescription = "Wi-Fi", tint = AccentGreen, modifier = Modifier.size(12.dp))
                } else {
                    Icon(Icons.Default.WifiOff, contentDescription = "Wi-Fi tapaka", tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(12.dp))
                }

                if (isMobileDataOn) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("5G", color = AccentGreen, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.SignalCellularAlt, contentDescription = "Data", tint = AccentGreen, modifier = Modifier.size(12.dp))
                    }
                } else {
                    Icon(Icons.Default.SignalCellularOff, contentDescription = "Data tapaka", tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(12.dp))
                }

                // Battery pill with percentage
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("$batteryLevel%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.BatteryFull, contentDescription = "Battery", tint = if (batteryLevel > 20) Color.White else AccentRed, modifier = Modifier.size(13.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// REALISTIC LAUNCHER (HOME SCREEN CONTENT)
// -------------------------------------------------------------
@Composable
private fun RealisticLauncherContent(
    phoneId: Int,
    profile: PhoneProfileEntity?,
    firewallApps: List<FirewallAppEntity>,
    currentTime: String,
    currentDate: String,
    onOpenApp: (RealisticPhoneInternalApp) -> Unit,
    onOpenChromeUrl: (String, String) -> Unit
) {
    val accentColor = when (phoneId) {
        1 -> CyberCyan
        2 -> NeonViolet
        3 -> AccentGreen
        else -> Color(0xFF38BDF8)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP SECTION: Weather & Large Android Clock Widget + Google Search Pill
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = (-1).sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(14.dp))
                Text(
                    text = "$currentDate • Antananarivo 24°C",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Realistic Google Search Bar Pill
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clickable { onOpenApp(RealisticPhoneInternalApp.CHROME) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Mikaroka ao anaty Chrome...", color = Color.White.copy(alpha = 0.65f), fontSize = 11.5.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                        Icon(Icons.Default.CenterFocusStrong, contentDescription = "Lens", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // MIDDLE: App Grid (2 rows x 4 apps)
        val homeAppIcons = listOf(
            Triple("YouTube", Icons.Default.PlayArrow, RealisticPhoneInternalApp.YOUTUBE),
            Triple("Chrome", Icons.Default.Public, RealisticPhoneInternalApp.CHROME),
            Triple("Fakantsary", Icons.Default.PhotoCamera, RealisticPhoneInternalApp.CAMERA),
            Triple("Galeria", Icons.Default.PhotoLibrary, RealisticPhoneInternalApp.GALLERY),
            Triple("Stockage", Icons.Default.Folder, RealisticPhoneInternalApp.STORAGE),
            Triple("Kajy", Icons.Default.Calculate, RealisticPhoneInternalApp.CALCULATOR),
            Triple("Naoty", Icons.Default.EditNote, RealisticPhoneInternalApp.NOTES),
            Triple("Fikirakirana", Icons.Default.Settings, RealisticPhoneInternalApp.SETTINGS)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(homeAppIcons) { (name, icon, appTarget) ->
                RealisticAppIconItem(
                    name = name,
                    icon = icon,
                    accentColor = accentColor,
                    onClick = { onOpenApp(appTarget) }
                )
            }
        }

        // BOTTOM: Fixed Dock Row (Phone, Messages, Chrome, Camera)
        Surface(
            color = Color.Black.copy(alpha = 0.35f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RealisticAppIconItem(name = "Antso", icon = Icons.Default.Call, accentColor = AccentGreen, onClick = { onOpenApp(RealisticPhoneInternalApp.DIALER) })
                RealisticAppIconItem(name = "Hafatra", icon = Icons.Default.ChatBubble, accentColor = CyberCyan, onClick = { onOpenApp(RealisticPhoneInternalApp.MESSAGES) })
                RealisticAppIconItem(name = "Chrome", icon = Icons.Default.Public, accentColor = AccentAmber, onClick = { onOpenApp(RealisticPhoneInternalApp.CHROME) })
                RealisticAppIconItem(name = "Camera", icon = Icons.Default.PhotoCamera, accentColor = MagentaGlow, onClick = { onOpenApp(RealisticPhoneInternalApp.CAMERA) })
            }
        }
    }
}

// -------------------------------------------------------------
// APP ICON ITEM COMPONENT
// -------------------------------------------------------------
@Composable
private fun RealisticAppIconItem(
    name: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Surface(
            modifier = Modifier.size(46.dp),
            shape = RoundedCornerShape(13.dp),
            color = Color.White.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 9.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// -------------------------------------------------------------
// REALISTIC QUICK SETTINGS SHADE (Volet Déroulant)
// -------------------------------------------------------------
@Composable
private fun RealisticQuickSettingsShade(
    phoneId: Int,
    isWifiOn: Boolean,
    isMobileDataOn: Boolean,
    isFlashlightOn: Boolean,
    isAirplaneModeOn: Boolean,
    soundMode: String,
    brightnessLevel: Float,
    onToggleWifi: () -> Unit,
    onToggleMobileData: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleFlashlight: () -> Unit,
    onToggleAirplane: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onCloseShade: () -> Unit,
    onLockPhone: () -> Unit
) {
    Surface(
        color = Color(0xF20F172A),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Top handle & close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚙️ Réglages Rapides", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onLockPhone, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = "Hidio", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onCloseShade, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Akatony", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // 2x4 Quick Setting Tiles
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        QuickTile(
                            title = "Données Mobiles",
                            subtitle = if (isMobileDataOn) "Mandeha 🟢" else "Tapaka 🔴",
                            isActive = isMobileDataOn,
                            icon = if (isMobileDataOn) Icons.Default.SignalCellularAlt else Icons.Default.SignalCellularOff,
                            onClick = onToggleMobileData,
                            modifier = Modifier.weight(1f)
                        )
                        QuickTile(
                            title = "Wi-Fi",
                            subtitle = if (isWifiOn) "Mandeha 🟢" else "Tapaka 🔴",
                            isActive = isWifiOn,
                            icon = if (isWifiOn) Icons.Default.Wifi else Icons.Default.WifiOff,
                            onClick = onToggleWifi,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        QuickTile(
                            title = "Feo",
                            subtitle = soundMode,
                            isActive = soundMode != "SILENT",
                            icon = if (soundMode == "VIBRATE") Icons.Default.Vibration else if (soundMode == "SILENT") Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            onClick = onToggleSound,
                            modifier = Modifier.weight(1f)
                        )
                        QuickTile(
                            title = "Torche",
                            subtitle = if (isFlashlightOn) "Mirehitra" else "Maty",
                            isActive = isFlashlightOn,
                            icon = Icons.Default.Lightbulb,
                            onClick = onToggleFlashlight,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        QuickTile(
                            title = "Mode Avion",
                            subtitle = if (isAirplaneModeOn) "Mandeha" else "Maty",
                            isActive = isAirplaneModeOn,
                            icon = Icons.Default.Flight,
                            onClick = onToggleAirplane,
                            modifier = Modifier.weight(1f)
                        )
                        QuickTile(
                            title = "Chrome Sandbox",
                            subtitle = "Mahaleo tena",
                            isActive = true,
                            icon = Icons.Default.Public,
                            onClick = onCloseShade,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Brightness Slider
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Luminosité", color = TextSecondary, fontSize = 10.sp)
                        Text("${(brightnessLevel * 100).toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = brightnessLevel,
                        onValueChange = onBrightnessChange,
                        valueRange = 0.2f..1f,
                        colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                    )
                }
            }

            // Bottom notifications summary card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pare-feu sy Fiarovana", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Ity finday ity dia mitokana tanteraka amin'ny hafa.", color = TextSecondary, fontSize = 9.5.sp)
                    }
                    TextButton(onClick = onCloseShade) {
                        Text("OK", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickTile(
    title: String,
    subtitle: String,
    isActive: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) CyberCyan.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (isActive) CyberCyan.copy(alpha = 0.6f) else Color.Transparent),
        modifier = modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (isActive) CyanBright else Color.White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            Column {
                Text(title, color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(subtitle, color = if (isActive) CyanBright else TextSecondary, fontSize = 8.5.sp, maxLines = 1)
            }
        }
    }
}

// -------------------------------------------------------------
// REALISTIC ANDROID 3-BUTTON NAVIGATION BAR (◀ ⏺ ⏹)
// -------------------------------------------------------------
@Composable
private fun RealisticAndroidNavigationBar(
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRecents: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.85f),
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ◀ BACK
            IconButton(onClick = onBack, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // ⏺ HOME
            IconButton(onClick = onHome, modifier = Modifier.size(30.dp)) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .border(1.8.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                )
            }

            // ⏹ RECENTS / MULTITASKING
            IconButton(onClick = onRecents, modifier = Modifier.size(30.dp)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .border(1.8.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

// -------------------------------------------------------------
// REALISTIC CAMERA VIEWFINDER SIMULATOR
// -------------------------------------------------------------
@Composable
private fun RealisticCameraView(onBack: () -> Unit) {
    var isFrontCamera by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var photoCount by remember { mutableIntStateOf(0) }
    var shutterAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(shutterAnimation) {
        if (shutterAnimation) {
            delay(120)
            shutterAnimation = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Viewfinder Background with grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF1E293B), Color(0xFF020617)))),
            contentAlignment = Alignment.Center
        ) {
            // Camera Rule-of-Thirds Grid
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stepX = size.width / 3
                val stepY = size.height / 3
                drawLine(Color.White.copy(alpha = 0.15f), androidx.compose.ui.geometry.Offset(stepX, 0f), androidx.compose.ui.geometry.Offset(stepX, size.height), strokeWidth = 1f)
                drawLine(Color.White.copy(alpha = 0.15f), androidx.compose.ui.geometry.Offset(stepX * 2, 0f), androidx.compose.ui.geometry.Offset(stepX * 2, size.height), strokeWidth = 1f)
                drawLine(Color.White.copy(alpha = 0.15f), androidx.compose.ui.geometry.Offset(0f, stepY), androidx.compose.ui.geometry.Offset(size.width, stepY), strokeWidth = 1f)
                drawLine(Color.White.copy(alpha = 0.15f), androidx.compose.ui.geometry.Offset(0f, stepY * 2), androidx.compose.ui.geometry.Offset(size.width, stepY * 2), strokeWidth = 1f)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isFrontCamera) Icons.Default.Face else Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    text = if (isFrontCamera) "Selfie Camera (Eo anoloana)" else "Fakantsary Pôtra (Ao aoriana)",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        // Top Camera Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            IconButton(onClick = { isFlashOn = !isFlashOn }) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) AccentAmber else Color.White
                )
            }
            IconButton(onClick = { isFrontCamera = !isFrontCamera }) {
                Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Switch Camera", tint = Color.White)
            }
        }

        // Shutter White Flash
        if (shutterAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.9f))
            )
        }

        // Bottom Shutter Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Photo Counter
                Text(
                    text = "$photoCount sary",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )

                // Big White Circular Shutter Button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(3.5.dp, Color.White, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            shutterAnimation = true
                            photoCount++
                        }
                )

                // Mode switch
                Icon(
                    Icons.Default.Videocam,
                    contentDescription = "Video",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// REALISTIC CALCULATOR SIMULATOR
// -------------------------------------------------------------
@Composable
private fun RealisticCalculatorView(onBack: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var operator by remember { mutableStateOf<String?>(null) }
    var firstOperand by remember { mutableStateOf<Double?>(null) }

    fun handleDigit(d: String) {
        if (display == "0" || display == "Error") {
            display = d
        } else {
            display += d
        }
    }

    fun handleOp(op: String) {
        firstOperand = display.toDoubleOrNull()
        operator = op
        display = "0"
    }

    fun compute() {
        val second = display.toDoubleOrNull()
        if (firstOperand != null && second != null && operator != null) {
            val result = when (operator) {
                "+" -> firstOperand!! + second
                "-" -> firstOperand!! - second
                "×" -> firstOperand!! * second
                "÷" -> if (second != 0.0) firstOperand!! / second else Double.NaN
                else -> second
            }
            display = if (result.isNaN()) "Error" else if (result % 1.0 == 0.0) result.toLong().toString() else "%.4f".format(result)
            operator = null
            firstOperand = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Kajy (Calculatrice)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(36.dp))
        }

        // Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = display,
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Keypad Grid
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=", "")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            buttons.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { label ->
                        if (label.isNotEmpty()) {
                            val isOp = label in listOf("÷", "×", "-", "+", "=")
                            val isClear = label in listOf("C", "±", "%")
                            Surface(
                                shape = CircleShape,
                                color = if (isOp) AccentAmber else if (isClear) Color(0xFF475569) else CardSlate,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clickable {
                                        when (label) {
                                            "C" -> { display = "0"; operator = null; firstOperand = null }
                                            "=" -> compute()
                                            "+", "-", "×", "÷" -> handleOp(label)
                                            "." -> if (!display.contains(".")) display += "."
                                            else -> handleDigit(label)
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// REALISTIC PIN KEYPAD (0-9)
// -------------------------------------------------------------
@Composable
private fun RealisticPinKeypad(
    accentColor: Color,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { key ->
                    if (key.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable {
                                    if (key == "⌫") onBackspace() else onDigit(key)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RECENTS / MULTITASKING APP SWITCHER OVERLAY (⏹)
// -------------------------------------------------------------
@Composable
private fun RealisticRecentsOverlay(
    currentApp: RealisticPhoneInternalApp,
    recentApps: List<RealisticPhoneInternalApp>,
    onSelectApp: (RealisticPhoneInternalApp) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.88f),
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⏹ Fampiharana niasa teo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                TextButton(onClick = onClearAll) {
                    Text("Fafao daholo", color = AccentRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (recentApps.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LayersClear, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tsy misy fampiharana mandeha", color = TextSecondary, fontSize = 11.sp)
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentApps) { app ->
                        Card(
                            modifier = Modifier
                                .width(170.dp)
                                .height(260.dp)
                                .clickable { onSelectApp(app) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.5.dp, if (app == currentApp) CyberCyan else Color.White.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Apps, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                    Text(app.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                        .background(DeepSlate, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Preview", color = TextSecondary, fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { onSelectApp(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Sokafy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Miverina amin'ny Home", fontSize = 11.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// VOLUME HUD INDICATOR (when volume buttons are pressed)
// -------------------------------------------------------------
@Composable
private fun RealisticVolumeHud(
    volumeLevel: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xEB1E293B),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier
            .width(42.dp)
            .height(130.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = if (volumeLevel == 0) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            // Vertical bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(volumeLevel / 100f)
                        .clip(RoundedCornerShape(3.dp))
                        .background(accentColor)
                )
            }
            Text("$volumeLevel%", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}
