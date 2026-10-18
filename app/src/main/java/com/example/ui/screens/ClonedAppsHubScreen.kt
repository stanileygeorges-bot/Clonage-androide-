package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClonedAppEntity
import com.example.ui.theme.*

@Composable
fun ClonedAppsHubScreen(
    phoneId: Int,
    clonedApps: List<ClonedAppEntity>,
    onCloneNewApp: (name: String, iconKey: String, category: String, url: String?) -> Unit,
    onDeleteClonedApp: (ClonedAppEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    var showCloneDialog by remember { mutableStateOf(false) }
    var launchedAppForSimulation by remember { mutableStateOf<ClonedAppEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCloneDialog = true },
                containerColor = accentColor,
                contentColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Hanao clonage fampiharana vaovao"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
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
                    text = if (phoneId == 1) "Fampiharana Fototra" else "Clonage Apps (Mahaleo tena)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${clonedApps.size} apps",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = CardSlate.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (phoneId == 1) "Kaonty Voalohany (Profil 1)" else "Kaonty Faharoa Mitokana (Clonage)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ny fampiharana ato dia tsy mampiasa ny angona na ny fidirana amin'ny finday hafa.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Divider(
                color = Color.White.copy(alpha = 0.08f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Grid of Cloned Apps
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(clonedApps) { app ->
                    ClonedAppGridCard(
                        app = app,
                        accentColor = accentColor,
                        onClick = { launchedAppForSimulation = app },
                        onDelete = { onDeleteClonedApp(app) }
                    )
                }
            }
        }
    }

    // Clone App Dialog
    if (showCloneDialog) {
        var appName by remember { mutableStateOf("") }
        var selectedIcon by remember { mutableStateOf("whatsapp") }
        var selectedCategory by remember { mutableStateOf("Social") }

        val presetIcons = listOf(
            "whatsapp" to "WhatsApp Clone",
            "telegram" to "Telegram Clone",
            "facebook" to "Facebook Clone",
            "instagram" to "Instagram Clone",
            "crypto" to "Crypto & Bank",
            "game" to "Lalao (Gaming)"
        )

        AlertDialog(
            onDismissRequest = { showCloneDialog = false },
            title = {
                Text(
                    text = "Manao Clonage Fampiharana",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Safidio ny karazana app tianao hatao clone:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for ((key, label) in presetIcons.take(3)) {
                            PresetSelectChip(
                                label = label.split(" ").first(),
                                isSelected = selectedIcon == key,
                                accentColor = accentColor
                            ) {
                                selectedIcon = key
                                appName = label
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for ((key, label) in presetIcons.drop(3)) {
                            PresetSelectChip(
                                label = label.split(" ").first(),
                                isSelected = selectedIcon == key,
                                accentColor = accentColor
                            ) {
                                selectedIcon = key
                                appName = label
                            }
                        }
                    }

                    OutlinedTextField(
                        value = appName,
                        onValueChange = { appName = it },
                        label = { Text("Anaran'ny Fampiharana") },
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
                        if (appName.isNotBlank()) {
                            onCloneNewApp(appName, selectedIcon, selectedCategory, null)
                            showCloneDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                ) {
                    Text("Clone-o")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloneDialog = false }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }

    // App Simulation Dialog (Shows sandboxed running environment)
    launchedAppForSimulation?.let { app ->
        AlertDialog(
            onDismissRequest = { launchedAppForSimulation = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForApp(app.iconKey),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(text = app.appName, color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundDark)
                            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Habaka mitokana (Sandbox) mavitrika",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kaonty: Profil #${app.phoneId} (Mahaleo tena)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "Ity app ity dia afaka ampiasaina miaraka amin'ny kaonty vaovao (WhatsApp 2, Facebook 2, sns.) tsy mampifangaro ny angona amin'ny Finday 1.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { launchedAppForSimulation = null },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                ) {
                    Text("Ok, Fantatro")
                }
            },
            containerColor = CardSlate
        )
    }
}

@Composable
private fun ClonedAppGridCard(
    app: ClonedAppEntity,
    accentColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForApp(app.iconKey),
                        contentDescription = app.appName,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Dual Badge marker
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (app.phoneId == 1) CyberCyan else NeonViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${app.phoneId}",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = app.appName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = "Mahaleo tena",
                color = TextSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PresetSelectChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) accentColor else Color.White,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun getIconForApp(key: String): ImageVector {
    return when (key.lowercase()) {
        "whatsapp" -> Icons.Default.Chat
        "telegram" -> Icons.Default.Send
        "facebook" -> Icons.Default.ThumbUp
        "instagram" -> Icons.Default.CameraAlt
        "crypto" -> Icons.Default.CurrencyBitcoin
        "game" -> Icons.Default.SportsEsports
        "browser" -> Icons.Default.Public
        else -> Icons.Default.Widgets
    }
}
