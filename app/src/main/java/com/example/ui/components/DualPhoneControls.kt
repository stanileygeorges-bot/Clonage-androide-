package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DualPhoneControls(
    activePhoneId: Int,
    isSplitView: Boolean,
    isMobileDataConnected: Boolean,
    onSelectPhone: (Int) -> Unit,
    onToggleSplitView: () -> Unit,
    onOpenPareFeu: () -> Unit,
    onOpenStorage: () -> Unit,
    onOpenCrossPhoneMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = CardSlate.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Devices Selector Tabs: Finday A, Finday B, Finday C (Olona 3 samy mahaleo tena)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundDark.copy(alpha = 0.8f))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Finday A (Olona A)
                DeviceSelectorTab(
                    label = "Finday A",
                    isSelected = activePhoneId == 1 && !isSplitView,
                    color = CyberCyan,
                    onClick = { onSelectPhone(1) }
                )

                // Finday B (Olona B)
                DeviceSelectorTab(
                    label = "Finday B",
                    isSelected = activePhoneId == 2 && !isSplitView,
                    color = NeonViolet,
                    onClick = { onSelectPhone(2) }
                )

                // Finday C (Olona C)
                DeviceSelectorTab(
                    label = "Finday C",
                    isSelected = activePhoneId == 3 && !isSplitView,
                    color = AccentGreen,
                    onClick = { onSelectPhone(3) }
                )
            }

            // Quick Tool Icons: Pare-feu Dashboard, Stockage, Split View
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Pare-feu Button with live Data connection indicator (Green when connected, Red when disconnected)
                val dataIndicatorColor = if (isMobileDataConnected) AccentGreen else AccentRed
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(dataIndicatorColor.copy(alpha = 0.18f))
                        .border(1.dp, dataIndicatorColor.copy(alpha = 0.6f), CircleShape)
                        .clickable { onOpenPareFeu() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Pare-feu & Données Mobiles",
                        tint = dataIndicatorColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Storage & Downloads Explorer Button
                IconButton(
                    onClick = onOpenStorage,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderShared,
                        contentDescription = "Stockage A, B, C",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Split View Toggle
                val splitTint = if (isSplitView) AccentGreen else TextSecondary
                IconButton(
                    onClick = onToggleSplitView,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewAgenda,
                        contentDescription = "Fijerena Miaraka",
                        tint = splitTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceSelectorTab(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent)
    val textCol = if (isSelected) color else TextSecondary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isSelected) color else Color.Transparent)
        )
        Text(
            text = label,
            color = textCol,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
