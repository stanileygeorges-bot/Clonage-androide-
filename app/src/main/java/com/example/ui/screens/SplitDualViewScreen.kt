package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*

@Composable
fun SplitDualViewScreen(
    profile1: PhoneProfileEntity?,
    profile2: PhoneProfileEntity?,
    onSelectPhoneFull: (Int) -> Unit,
    onOpenMessages: (Int) -> Unit,
    onOpenDialer: (Int) -> Unit,
    onOpenContacts: (Int) -> Unit,
    onOpenClonedApps: (Int) -> Unit,
    onExitSplit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ViewAgenda,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Fijerena Miaraka (Finday 2 Mahaleo Tena)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onExitSplit) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Miverina écran tokana",
                    tint = Color.White
                )
            }
        }

        // Phone 1 Pane (Top Half)
        SplitPhoneCard(
            phoneId = 1,
            profile = profile1,
            accentColor = CyberCyan,
            onOpenFull = { onSelectPhoneFull(1) },
            onOpenMessages = { onOpenMessages(1) },
            onOpenDialer = { onOpenDialer(1) },
            onOpenContacts = { onOpenContacts(1) },
            onOpenApps = { onOpenClonedApps(1) },
            modifier = Modifier.weight(1f)
        )

        // Phone 2 (Clonage) Pane (Bottom Half)
        SplitPhoneCard(
            phoneId = 2,
            profile = profile2,
            accentColor = NeonViolet,
            onOpenFull = { onSelectPhoneFull(2) },
            onOpenMessages = { onOpenMessages(2) },
            onOpenDialer = { onOpenDialer(2) },
            onOpenContacts = { onOpenContacts(2) },
            onOpenApps = { onOpenClonedApps(2) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SplitPhoneCard(
    phoneId: Int,
    profile: PhoneProfileEntity?,
    accentColor: Color,
    onOpenFull: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenDialer: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Pane Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(accentColor)
                    )
                    Text(
                        text = profile?.name ?: "Finday $phoneId",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onOpenFull,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.25f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ampiasao Izy >>",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Info & Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Modely: ${profile?.modelName ?: "Android OS"}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "Bateria: ${profile?.batteryLevel ?: 90}%",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick App Actions Inside this Split Phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SplitActionItem(Icons.Default.Call, "Antso", AccentGreen, onOpenDialer)
                SplitActionItem(Icons.Default.ChatBubble, "Hafatra", CyberCyan, onOpenMessages)
                SplitActionItem(Icons.Default.Contacts, "Boky", OceanBlue, onOpenContacts)
                SplitActionItem(Icons.Default.Apps, "Clonage", NeonViolet, onOpenApps)
            }
        }
    }
}

@Composable
private fun SplitActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
