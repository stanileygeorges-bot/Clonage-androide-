package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallRecordEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DialerScreen(
    phoneId: Int,
    callLogs: List<CallRecordEntity>,
    onStartCall: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Keypad, 1: Call Log
    var dialedNumber by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
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
                text = if (phoneId == 1) "Antso (Finday 1)" else "Antso (Clonage 2)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Switch Tab Chips
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(3.dp)
            ) {
                TabChip(
                    label = "Clavier",
                    isSelected = selectedTab == 0,
                    accentColor = accentColor
                ) { selectedTab = 0 }

                TabChip(
                    label = "Tantara (${callLogs.size})",
                    isSelected = selectedTab == 1,
                    accentColor = accentColor
                ) { selectedTab = 1 }
            }
        }

        Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

        if (selectedTab == 0) {
            // Keypad Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Dialed Number Display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (dialedNumber.isEmpty()) "Ampidiro laharana..." else dialedNumber,
                        color = if (dialedNumber.isEmpty()) TextSecondary else Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    if (dialedNumber.isNotEmpty()) {
                        IconButton(onClick = { dialedNumber = dialedNumber.dropLast(1) }) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Fafao",
                                tint = TextSecondary
                            )
                        }
                    }
                }

                // Digits Grid
                val keyRows = listOf(
                    listOf(KeyItem("1", ""), KeyItem("2", "ABC"), KeyItem("3", "DEF")),
                    listOf(KeyItem("4", "GHI"), KeyItem("5", "JKL"), KeyItem("6", "MNO")),
                    listOf(KeyItem("7", "PQRS"), KeyItem("8", "TUV"), KeyItem("9", "WXYZ")),
                    listOf(KeyItem("*", ""), KeyItem("0", "+"), KeyItem("#", ""))
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    for (row in keyRows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (item in row) {
                                KeypadDialButton(item = item) {
                                    dialedNumber += item.digit
                                }
                            }
                        }
                    }
                }

                // Call Action Button
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                        .clickable {
                            val target = if (dialedNumber.isNotBlank()) dialedNumber else "+261 34 00 000 00"
                            onStartCall("Laharana antsoina", target)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Antsoy",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        } else {
            // Call Log Screen
            if (callLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhoneMissed,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tsy misy antso voarakitra ato",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(callLogs) { log ->
                        CallRecordCard(
                            log = log,
                            dateFormat = dateFormat,
                            accentColor = accentColor,
                            onCallBack = { onStartCall(log.contactName, log.phoneNumber) }
                        )
                    }
                }
            }
        }
    }
}

private data class KeyItem(val digit: String, val letters: String)

@Composable
private fun KeypadDialButton(
    item: KeyItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = item.digit,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
            if (item.letters.isNotEmpty()) {
                Text(
                    text = item.letters,
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun CallRecordCard(
    log: CallRecordEntity,
    dateFormat: SimpleDateFormat,
    accentColor: Color,
    onCallBack: () -> Unit
) {
    val callTypeIcon = when (log.callType) {
        "MISSED" -> Icons.Default.CallMissed
        "OUTGOING" -> Icons.Default.CallMade
        else -> Icons.Default.CallReceived
    }
    val callTypeColor = when (log.callType) {
        "MISSED" -> AccentRed
        "OUTGOING" -> CyberCyan
        else -> AccentGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(callTypeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = callTypeIcon,
                        contentDescription = log.callType,
                        tint = callTypeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = log.contactName,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${log.phoneNumber} • ${dateFormat.format(Date(log.timestamp))}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    if (log.durationSeconds > 0) {
                        Text(
                            text = "Faharetana: ${log.durationSeconds} segondra",
                            color = accentColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            IconButton(onClick = onCallBack) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Antsoy indray",
                    tint = AccentGreen
                )
            }
        }
    }
}

@Composable
private fun TabChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.25f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) accentColor else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
