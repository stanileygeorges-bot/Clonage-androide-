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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

data class SelectableAppItem(
    val key: String,
    val name: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * Safidy roa lehibe (Two execution choices):
 * - CHROME_MODE: Ao anatin'ny Chrome no ananan'ny appareil connexion sy ampiasan'ny IA azy (Vidéo, Publicité, Rien)
 * - APP_MODE: Fampiharana mahaleo tena tsirairay fidina na karohina mivantana
 */
enum class DeviceExecutionMode {
    CHROME_MODE,
    APP_MODE
}

/**
 * Baiko ho an'ny IA rehefa mampiasa an'ilay app ao anatin'ny Chrome (Vidéo, Publicité, Rien)
 */
enum class ChromeAiCommand(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
) {
    VIDEO(
        title = "Mijery Vidéo",
        subtitle = "Ny IA no mizery horonan-tsary ho azy ao anaty Chrome",
        icon = Icons.Default.PlayCircle,
        color = CyberCyan
    ),
    ADVERTISING(
        title = "Publicité",
        subtitle = "Ny IA no mizery sy manara-maso dokambarotra ao anaty Chrome",
        icon = Icons.Default.Campaign,
        color = AccentAmber
    ),
    IDLE(
        title = "Rien (Tsotra)",
        subtitle = "Fitetezana tsotra tsy misy baiko manokana",
        icon = Icons.Default.DoNotDisturb,
        color = TextSecondary
    )
}

@Composable
fun NewIndependentDeviceModal(
    onDismiss: () -> Unit,
    onPlace: (
        deviceName: String,
        selectedAppKeys: List<String>,
        deviceCount: Int,
        isInfinite: Boolean,
        isAiVideoWatchEnabled: Boolean,
        isChromeMode: Boolean,
        chromeCommandName: String,
        chromeTargetAppName: String,
        deviceModelName: String
    ) -> Unit,
    nextDeviceNumber: Int = 2
) {
    var deviceName by remember {
        mutableStateOf("Apareil Android #$nextDeviceNumber")
    }
    var selectedModelName by remember {
        mutableStateOf("Samsung Galaxy Note Pro")
    }

    var isInfiniteMode by remember { mutableStateOf(false) }
    var deviceCountText by remember { mutableStateOf("1") }
    val deviceCount = deviceCountText.toIntOrNull()?.coerceIn(1, 100) ?: 1

    // Safidy Roa: CHROME vs APPLICATION
    var executionMode by remember { mutableStateOf(DeviceExecutionMode.CHROME_MODE) }

    // Chrome AI Options
    var chromeCommand by remember { mutableStateOf(ChromeAiCommand.VIDEO) }
    var chromeTargetAppName by remember { mutableStateOf("YouTube Web") }

    // Pre-curated catalog for APP mode (incorporating vidmat from user's screen)
    val defaultCatalog = remember {
        listOf(
            SelectableAppItem("vidmat", "vidmat", "Horonan-tsary", "Fampidinana horonan-tsary sy mozika", Icons.Default.Download, CyberCyan),
            SelectableAppItem("youtube", "YouTube", "Vidéo & Médias", "Clone mahaleo tena", Icons.Default.PlayCircle, AccentRed),
            SelectableAppItem("whatsapp", "WhatsApp", "Social & Messagerie", "Kaonty finday vaovao", Icons.Default.Chat, AccentGreen),
            SelectableAppItem("browser", "Chrome / Navigateur", "Tranonkala", "Fitetezana tranonkala", Icons.Default.Public, CyberCyan),
            SelectableAppItem("telegram", "Telegram", "Fifandraisana", "Kaonty Telegram mitokana", Icons.Default.Send, Color(0xFF38BDF8)),
            SelectableAppItem("facebook", "Facebook", "Tambazotra", "Session Facebook mahaleo tena", Icons.Default.People, Color(0xFF1877F2)),
            SelectableAppItem("tiktok", "TikTok", "Horonan-tsary", "Feeds sy horonan-tsary fohy", Icons.Default.Movie, Color(0xFFEC4899)),
            SelectableAppItem("instagram", "Instagram", "Sary & Horonan-tsary", "Kaonty vaovao", Icons.Default.CameraAlt, Color(0xFFF59E0B)),
            SelectableAppItem("downloads", "Téléchargements & Files", "Fitahirizana", "Stockage mitokana", Icons.Default.Folder, Color(0xFFA855F7))
        )
    }

    val catalogList = remember { mutableStateListOf<SelectableAppItem>().apply { addAll(defaultCatalog) } }
    // Pre-checked as in user's screenshot: vidmat, youtube, whatsapp, browser
    val selectedKeys = remember { mutableStateListOf("vidmat", "youtube", "whatsapp", "browser") }
    var customAppSearchInput by remember { mutableStateOf("") }
    var customAppStoreLocation by remember { mutableStateOf("Google Play Store") }
    val storePresets = remember {
        listOf("Google Play Store", "APKPure", "Uptodown", "Apple App Store", "F-Droid", "Rohy / URL Manokana")
    }

    val quickWebApps = listOf("YouTube", "TikTok", "Vidmat", "ChatGPT", "Facebook", "Canva", "AliExpress", "DuckDuckGo")

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
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Apareil Android Vaovao", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Hikatona", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Device Name
                    item {
                        OutlinedTextField(
                            value = deviceName,
                            onValueChange = { deviceName = it },
                            label = { Text("Anaran'ny Apareil Android", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberCyan
                            )
                        )
                    }

                    // 1c. MODÈLE APPAREIL TENA IZY (Samsung Galaxy Note Pro, Oppo N1, Redmi Note...):
                    // "mety ho Samsung Galaxy note pro io na mety Oppo N1 na Redmi ect ect ect"
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardSlate.copy(alpha = 0.7f))
                                .border(1.dp, CyberCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = CyanBright, modifier = Modifier.size(16.dp))
                                Text("Modely Apareil Tena Izy (Model Hardware)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "Safidio na soraty ny modely finday (Samsung, Oppo, Redmi, sns.):",
                                color = TextSecondary,
                                fontSize = 10.5.sp
                            )

                            val presetModels = listOf(
                                "Samsung Galaxy Note Pro",
                                "Oppo N1",
                                "Redmi Note",
                                "Galaxy S24 Ultra",
                                "Pixel 9 Pro",
                                "Oppo Find X7"
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                items(presetModels) { m ->
                                    val isSel = selectedModelName == m
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { selectedModelName = m },
                                        label = { Text(m, fontSize = 10.5.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                            selectedLabelColor = CyanBright,
                                            containerColor = DeepSlate
                                        )
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = selectedModelName,
                                onValueChange = { selectedModelName = it },
                                label = { Text("Anaran'ny Modely Finday", fontSize = 10.5.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberCyan
                                )
                            )
                        }
                    }

                    // 1b. SARY FAMANTARANA NY APP VOAFIDY EO AMIN'NY SISINY:
                    // "Dia rehefa voafidy ilay ilay App de tokony iseo eo aminy sisiny eo ny sarininy mba afatarana azy"
                    item {
                        val isChromeMode = executionMode == DeviceExecutionMode.CHROME_MODE
                        val primarySelectedApp = if (isChromeMode) {
                            SelectableAppItem(
                                key = "browser",
                                name = if (chromeTargetAppName.isNotBlank()) "Chrome ($chromeTargetAppName)" else "Chrome Navigateur",
                                category = "Navigateur",
                                description = "Miasa ao anaty Chrome",
                                icon = Icons.Default.Public,
                                color = CyberCyan
                            )
                        } else {
                            val firstKey = selectedKeys.firstOrNull() ?: "youtube"
                            catalogList.find { it.key == firstKey } ?: SelectableAppItem(
                                key = firstKey,
                                name = firstKey.replaceFirstChar { it.uppercase() },
                                category = "App",
                                description = "Fampiharana",
                                icon = Icons.Default.Apps,
                                color = NeonViolet
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("selected_app_side_preview_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, primarySelectedApp.color.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // ANKAVIA: Fampahafantarana ny Apareil
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(DeepSlate)
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (deviceName.isNotBlank()) deviceName else "Apareil Android #$nextDeviceNumber",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (isChromeMode) "Hiasa ao anaty Chrome amin'ny IA" else "Fampiharana mahaleo tena",
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // AN-KAVANANA (EO AMIN'NY SISINY): SARY FAMANTARANA NY APP VOAFIDY
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(primarySelectedApp.color.copy(alpha = 0.18f))
                                        .border(1.5.dp, primarySelectedApp.color, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = primarySelectedApp.icon,
                                            contentDescription = "Sarin'ny App voafidy",
                                            tint = primarySelectedApp.color,
                                            modifier = Modifier.size(26.dp)
                                        )
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "APP VOAFIDY",
                                                color = primarySelectedApp.color,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = primarySelectedApp.name.take(12),
                                                color = Color.White,
                                                fontSize = 11.sp,
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

                    // 2. Isan'ny Apareil (Clone Count & Infinite Mode Card)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.95f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Isan'ny Apareil Android",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        )
                                        Text(
                                            text = if (isInfiniteMode) "Mode Infini (∞) : Mamorona clones foana ny AI" else "Safidy: ]1 ; infini [",
                                            color = if (isInfiniteMode) NeonViolet else CyberCyan,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Infini (∞)",
                                            color = if (isInfiniteMode) NeonViolet else TextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Switch(
                                            checked = isInfiniteMode,
                                            onCheckedChange = { isInfiniteMode = it }
                                        )
                                    }
                                }

                                if (!isInfiniteMode) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val current = deviceCountText.toIntOrNull() ?: 1
                                                if (current > 1) deviceCountText = (current - 1).toString()
                                            },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.1f))
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Minus", tint = Color.White)
                                        }

                                        OutlinedTextField(
                                            value = deviceCountText,
                                            onValueChange = { input ->
                                                if (input.all { it.isDigit() } && input.length <= 3) {
                                                    deviceCountText = input
                                                }
                                            },
                                            label = { Text("Isan'ny Clones", fontSize = 10.5.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = CyberCyan
                                            )
                                        )

                                        IconButton(
                                            onClick = {
                                                val current = deviceCountText.toIntOrNull() ?: 1
                                                if (current < 50) deviceCountText = (current + 1).toString()
                                            },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(CyberCyan.copy(alpha = 0.2f))
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Plus", tint = CyberCyan)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. SAFIDY ROA (MODE SELECTOR): CHROME vs APPLICATION
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepSlate),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Safidy Fampiasana ny Fampiharana :",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // Dual Segmented Buttons: [ 🌐 Chrome (IA Automate) ] [ 📱 Application ]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black.copy(alpha = 0.35f))
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Option 1: Chrome (IA Automate)
                                    val isChrome = executionMode == DeviceExecutionMode.CHROME_MODE
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isChrome) CyberCyan else Color.Transparent)
                                            .clickable { executionMode = DeviceExecutionMode.CHROME_MODE }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = null,
                                                tint = if (isChrome) Color.Black else CyanBright,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "🌐 Chrome (IA)",
                                                color = if (isChrome) Color.Black else Color.White,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Option 2: Application (Standalone Apps)
                                    val isApp = executionMode == DeviceExecutionMode.APP_MODE
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isApp) NeonViolet else Color.Transparent)
                                            .clickable { executionMode = DeviceExecutionMode.APP_MODE }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Apps,
                                                contentDescription = null,
                                                tint = if (isApp) Color.White else NeonViolet,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "📱 Application",
                                                color = Color.White,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4A. REHEFA CHROME NO VOAFIDY:
                    // Ao anatin'ny Chrome no ananan'ny appareil connexion ary ny IA no mampiasa azy (Vidéo, Publicité, Rien)
                    if (executionMode == DeviceExecutionMode.CHROME_MODE) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.95f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Title & Explanation
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = "Fampiasana ao anaty Chrome amin'ny IA",
                                            color = CyanBright,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Text(
                                        text = "Ao anatin'ny Chrome no ananan'ny appareil connexion. Ny IA avokoa no mampiasa an'ilay app ary manao ny baiko nomena azy ho an'ny fitaovana rehetra mipetaka ao.",
                                        color = TextSecondary,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp
                                    )

                                    // Baiko nomena ny IA: Vidéo, Publicité, Rien
                                    Text(
                                        text = "Baiko hotanterahin'ny IA ao anaty Chrome :",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        ChromeAiCommand.values().forEach { cmd ->
                                            val isSelected = chromeCommand == cmd
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { chromeCommand = cmd },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) cmd.color.copy(alpha = 0.2f) else DeepSlate
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isSelected) cmd.color else Color.Transparent
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Icon(cmd.icon, contentDescription = null, tint = cmd.color, modifier = Modifier.size(20.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(cmd.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        Text(cmd.subtitle, color = TextSecondary, fontSize = 9.5.sp)
                                                    }
                                                    RadioButton(
                                                        selected = isSelected,
                                                        onClick = { chromeCommand = cmd },
                                                        colors = RadioButtonDefaults.colors(selectedColor = cmd.color)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Fampiharana na Tranonkala sokafana ao anaty Chrome
                                    Text(
                                        text = "Toerana misy an'ilay App / Rohy (URL) ao anaty Chrome :",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    OutlinedTextField(
                                        value = chromeTargetAppName,
                                        onValueChange = { chromeTargetAppName = it },
                                        label = { Text("Toerana misy azy (URL rohy na Anarana)", fontSize = 10.5.sp) },
                                        placeholder = { Text("oh: https://m.youtube.com, https://tiktok.com, na tranonkala hafa...", fontSize = 10.5.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = CyberCyan
                                        )
                                    )

                                    // Quick Web App Chips
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(quickWebApps) { appName ->
                                            FilterChip(
                                                selected = chromeTargetAppName == appName,
                                                onClick = { chromeTargetAppName = appName },
                                                label = { Text(appName, fontSize = 10.5.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = CyberCyan.copy(alpha = 0.3f),
                                                    selectedLabelColor = CyanBright
                                                )
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.35f))
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "💡 Azo atao tsara ny manoratra ny toerana misy azy (URL rohy na tranonkala) fa tsy voatery mifidy amin'ireo efa voaomana. Ny IA no miasa sy mamaha azy mivantana.",
                                            color = CyanBright,
                                            fontSize = 10.sp,
                                            lineHeight = 13.5.sp
                                        )
                                    }

                                    // Telemetry & Sandbox guarantee badge
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DeepSlate)
                                            .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "🚀 Ny IA dia mampifandray ny appareil $deviceCount amin'ny Chrome miaraka amin'ny baiko [${chromeCommand.title}] ao anaty sandbox mitokana.",
                                            color = CyanBright,
                                            fontSize = 10.sp,
                                            lineHeight = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4B. REHEFA APPLICATION NO VOAFIDY:
                    // Fikarohana sy lisitry ny apps tsirairay miaraka amin'ny checkboxes
                    if (executionMode == DeviceExecutionMode.APP_MODE) {
                        // AI APP SEARCH & DISCOVERY FOR ANY APPLICATION
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CardSlate.copy(alpha = 0.8f))
                                    .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "✨ Mikaroka na mampiditra app hafa amin'ny IA",
                                    color = NeonViolet,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Azo atao ny manoratra ny toerana misy an'ilay app (Google Play, APKPure, Uptodown, App Store, sns.) na mametraka ny rohy mivantana fa tsy voatery.",
                                    color = TextSecondary,
                                    fontSize = 10.5.sp
                                )

                                // 1. Anaran'ny App
                                OutlinedTextField(
                                    value = customAppSearchInput,
                                    onValueChange = { customAppSearchInput = it },
                                    label = { Text("Anaran'ny App (oh: ChatGPT, Snaptube, Canva...)", fontSize = 10.5.sp) },
                                    placeholder = { Text("Soraty ny anaran'ilay app...", fontSize = 10.5.sp, color = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonViolet,
                                        unfocusedBorderColor = LightSlate.copy(alpha = 0.5f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                // 2. Toerana misy an'ilay App (Store / Source)
                                Text(
                                    text = "Toerana misy an'ilay App (Safidio na Soraty ny Store / Rohy fakana azy) :",
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(storePresets) { store ->
                                        FilterChip(
                                            selected = customAppStoreLocation == store,
                                            onClick = { customAppStoreLocation = store },
                                            label = { Text(store, fontSize = 9.5.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = NeonViolet.copy(alpha = 0.35f),
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = customAppStoreLocation,
                                        onValueChange = { customAppStoreLocation = it },
                                        label = { Text("Toerana misy azy (Store, Rohy na URL)", fontSize = 10.sp) },
                                        placeholder = { Text("oh: Google Play, APKPure, Uptodown, na URL...", fontSize = 10.sp, color = TextSecondary) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonViolet,
                                            unfocusedBorderColor = LightSlate.copy(alpha = 0.5f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            if (customAppSearchInput.isNotBlank()) {
                                                val name = customAppSearchInput.trim()
                                                val storeTag = if (customAppStoreLocation.isNotBlank()) customAppStoreLocation.trim() else "Google Play"
                                                val key = (name + "_" + storeTag).lowercase().replace(" ", "").replace("/", "").replace(":", "")
                                                val newApp = SelectableAppItem(
                                                    key = key,
                                                    name = "$name ($storeTag)",
                                                    category = storeTag,
                                                    description = "Toerana: $storeTag • IA no mikaroka sy mametraka azy",
                                                    icon = when {
                                                        storeTag.contains("Play", ignoreCase = true) -> Icons.Default.Shop
                                                        storeTag.contains("Apple", ignoreCase = true) || storeTag.contains("App Store", ignoreCase = true) -> Icons.Default.Apps
                                                        storeTag.contains("APKPure", ignoreCase = true) || storeTag.contains("Uptodown", ignoreCase = true) -> Icons.Default.Download
                                                        else -> Icons.Default.SmartToy
                                                    },
                                                    color = when {
                                                        storeTag.contains("Play", ignoreCase = true) -> AccentGreen
                                                        storeTag.contains("APKPure", ignoreCase = true) -> Color(0xFF00BA55)
                                                        storeTag.contains("Uptodown", ignoreCase = true) -> Color(0xFF0084FF)
                                                        else -> NeonViolet
                                                    }
                                                )
                                                catalogList.add(0, newApp)
                                                selectedKeys.add(key)
                                                customAppSearchInput = ""
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                        modifier = Modifier.height(52.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ampidiro", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.35f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "💡 Azo atao tsara ny manoratra ny toerana misy an'ilay app (Google Play, APKPure, Uptodown, App Store, sns.) na mametraka ny rohy mivantana fa tsy voatery. Ny IA ihany mo no miasa amin'ny fakana sy fampandehanana azy ao anaty sandbox mahaleo tena.",
                                        color = NeonViolet,
                                        fontSize = 10.sp,
                                        lineHeight = 13.5.sp
                                    )
                                }
                            }
                        }

                        // Checklist of selectable apps (vidmat, youtube, whatsapp, browser, etc.)
                        items(catalogList) { item ->
                            val isSelected = selectedKeys.contains(item.key)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) selectedKeys.remove(item.key) else selectedKeys.add(item.key)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) item.color.copy(alpha = 0.12f) else CardSlate
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.name,
                                        tint = item.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(item.name, color = Color.White, modifier = Modifier.weight(1f))
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            if (isSelected) selectedKeys.remove(item.key) else selectedKeys.add(item.key)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // PLACE BUTTON
                Button(
                    onClick = {
                        val isChrome = executionMode == DeviceExecutionMode.CHROME_MODE
                        val finalAppKeys = if (isChrome) {
                            // If Chrome mode, ensure browser and the designated Chrome app are added
                            val targetKey = chromeTargetAppName.lowercase().replace(" ", "").replace("/", "")
                            (listOf("browser", targetKey) + selectedKeys).distinct()
                        } else {
                            selectedKeys.toList()
                        }
                        val isVideoWatching = if (isChrome) {
                            chromeCommand == ChromeAiCommand.VIDEO
                        } else {
                            false
                        }
                        onPlace(
                            deviceName,
                            finalAppKeys,
                            deviceCount,
                            isInfiniteMode,
                            isVideoWatching,
                            isChrome,
                            chromeCommand.title,
                            chromeTargetAppName,
                            if (selectedModelName.isNotBlank()) selectedModelName else "Samsung Galaxy Note Pro"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (executionMode == DeviceExecutionMode.CHROME_MODE) CyberCyan else NeonViolet,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (executionMode == DeviceExecutionMode.CHROME_MODE) {
                            "PLACE (Ampifandraiso amin'ny Chrome amin'ny IA)"
                        } else {
                            "PLACE (Apetaho ireo Fampiharana)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}
