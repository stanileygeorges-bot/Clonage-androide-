package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    phoneId: Int,
    profile: PhoneProfileEntity?,
    language: String,
    onUpdateProfile: (name: String, model: String, wallpaper: String) -> Unit,
    onSetPin: (String?) -> Unit,
    onToggleCamouflage: (Boolean) -> Unit,
    onSetLanguage: (String) -> Unit,
    onWipeCloneSpace: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    var showRenameDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showWipeConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
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
            Text(
                text = "Fikirakirana (Settings)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.85f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (phoneId == 1) Icons.Default.PhoneAndroid else Icons.Default.Security,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile?.name ?: "Finday $phoneId",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Model: ${profile?.modelName ?: "Android Virtual Device"}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Toetra: Fitaovana mahaleo tena tanteraka",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(onClick = { showRenameDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Hanova anarana",
                            tint = Color.White
                        )
                    }
                }
            }

            // Section 1: Security & Protection
            Text(
                text = "Fiarovana & Tsiambaratelo",
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.7f))
            ) {
                Column {
                    SettingsItemRow(
                        icon = Icons.Default.Pin,
                        title = "Kaody PIN ho an'ny Finday 2",
                        subtitle = if (profile?.pinCode.isNullOrEmpty()) "Tsy misy PIN (Misokatra avy hatrany)" else "Voaaro amin'ny PIN (${profile?.pinCode})",
                        onClick = { showPinDialog = true }
                    )

                    Divider(color = Color.White.copy(alpha = 0.05f))

                    SettingsItemRow(
                        icon = Icons.Default.Calculate,
                        title = "Fomba Fiafenana (Calculator Stealth)",
                        subtitle = "Miseho ho toy ny calculator tsotra ny app Clonage",
                        onClick = { onToggleCamouflage(true) }
                    )
                }
            }

            // Section 2: Personalization & Themes
            Text(
                text = "Endrika & Haingo",
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Sary ambadika (Wallpaper)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WallpaperChoiceChip("Cyan Fluid", phoneId == 1, CyberCyan) {
                            onUpdateProfile(profile?.name ?: "Finday 1", profile?.modelName ?: "Pixel 9", "WP_CYAN")
                        }
                        WallpaperChoiceChip("Neon Matrix", phoneId == 2, NeonViolet) {
                            onUpdateProfile(profile?.name ?: "Finday 2", profile?.modelName ?: "Galaxy S24", "WP_NEON")
                        }
                    }
                }
            }

            // Section 3: Language
            Text(
                text = "Teny ampiasaina (Langue / Language)",
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LanguageChip("🇲🇬 Malagasy", language == "MG", accentColor) { onSetLanguage("MG") }
                LanguageChip("🇫🇷 Français", language == "FR", accentColor) { onSetLanguage("FR") }
                LanguageChip("🇬🇧 English", language == "EN", accentColor) { onSetLanguage("EN") }
            }

            // Section 4: Clonage Space Isolation & Reset
            Text(
                text = "Fitantanana ny Clonage",
                color = AccentRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Fanamasinana sy famafana ny angona",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Ity hetsika ity dia mamafa tanteraka ny hafatra, antso, naoty, ary sary voatahiry ao amin'ity habaka finday ity.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    Button(
                        onClick = { showWipeConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.85f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hamafa ny angon'ity finday ity")
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (showRenameDialog) {
        var newName by remember { mutableStateOf(profile?.name ?: "") }
        var newModel by remember { mutableStateOf(profile?.modelName ?: "") }

        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Hanova anaran'ny Finday", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Anaran'ny finday") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newModel,
                        onValueChange = { newModel = it },
                        label = { Text("Modely (oh: Pixel 9 Pro, Galaxy S24 Ultra)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onUpdateProfile(newName, newModel, profile?.wallpaperType ?: "WP_CYAN")
                            showRenameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                ) {
                    Text("Tehirizo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }

    // PIN Setup Dialog
    if (showPinDialog) {
        var enteredPin by remember { mutableStateOf(profile?.pinCode ?: "") }

        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Fiarovana amin'ny PIN", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ampidiro ny PIN 4 isa hiarovana ny Finday 2 (Avelao ho foana raha tsy asiana PIN):",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = { if (it.length <= 4) enteredPin = it },
                        label = { Text("PIN (isa 4)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetPin(enteredPin.ifBlank { null })
                        showPinDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                ) {
                    Text("Tehirizo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }

    // Wipe Confirmation Dialog
    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            title = { Text("Tena hofafana ve?", color = Color.White) },
            text = {
                Text(
                    text = "Hofafana daholo ny hafatra, antso, naoty, ary sary amin'ity finday ity. Tsy azo averina intsony rehefa voafafa.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onWipeCloneSpace()
                        showWipeConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Eny, Fafao")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirmDialog = false }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }
}

@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun WallpaperChoiceChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) color.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) color else Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun LanguageChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.25f) else CardSlate)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) accentColor else Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
