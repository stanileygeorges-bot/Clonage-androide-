package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PhoneStatusBar(
    profile: PhoneProfileEntity?,
    phoneId: Int,
    onToggleWifi: () -> Unit = {},
    onToggleSound: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    val currentTime = remember {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date())
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Clock & Phone Identity Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.25f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (phoneId == 1) "SIM 1 • FOTOTRA" else "SIM 2 • CLONAGE",
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Right side: Icons (Wifi, Signal, Sound, Battery)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sound mode
            val soundIcon = when (profile?.soundMode) {
                "VIBRATE" -> Icons.Default.Vibration
                "SILENT" -> Icons.Default.VolumeMute
                else -> Icons.Default.VolumeUp
            }
            Icon(
                imageVector = soundIcon,
                contentDescription = "Sound Mode",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .size(15.dp)
                    .clickable { onToggleSound() }
            )

            // Wifi
            val isWifi = profile?.wifiConnected ?: true
            Icon(
                imageVector = if (isWifi) Icons.Default.Wifi else Icons.Default.WifiOff,
                contentDescription = "Wifi",
                tint = if (isWifi) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(15.dp)
                    .clickable { onToggleWifi() }
            )

            // Cellular 5G
            Icon(
                imageVector = Icons.Default.NetworkCell,
                contentDescription = "5G Signal",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(15.dp)
            )

            // Battery
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${profile?.batteryLevel ?: 90}%",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.BatteryFull,
                    contentDescription = "Battery",
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
