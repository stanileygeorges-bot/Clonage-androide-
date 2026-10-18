package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*
import com.example.viewmodel.ActiveCall

@Composable
fun ActiveCallOverlay(
    call: ActiveCall,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phoneAccent = if (call.phoneId == 1) CyberCyan else NeonViolet
    val minutes = call.durationSeconds / 60
    val seconds = call.durationSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xF0090D16))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                modifier = Modifier.padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (call.phoneId == 1) "Antso finday 1 (Fototra)" else "Antso finday 2 (Clonage)",
                    color = phoneAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = call.contactName,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = call.phoneNumber,
                    color = TextSecondary,
                    fontSize = 15.sp
                )

                Text(
                    text = timeFormatted,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // Big Avatar Circle
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(phoneAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(phoneAccent.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            // Call Controls Row & End Call Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Mute
                    CallControlButton(
                        icon = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (call.isMuted) "Nangina" else "Mute",
                        isActive = call.isMuted,
                        onClick = onToggleMute
                    )

                    // Speaker
                    CallControlButton(
                        icon = if (call.isSpeaker) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        label = "Hafanam-peo",
                        isActive = call.isSpeaker,
                        onClick = onToggleSpeaker
                    )

                    // Dialpad
                    CallControlButton(
                        icon = Icons.Default.Dialpad,
                        label = "Clavier",
                        isActive = false,
                        onClick = { }
                    )
                }

                // End Call Red Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AccentRed)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onEndCall) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Tapaho ny antso",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isActive) Color.White else Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}
