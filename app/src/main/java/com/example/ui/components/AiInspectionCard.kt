package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.example.data.model.AiInspectionLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * AI MONITOR & READER (IA NO MAMAKY AZY):
 * "ary avy dia miasa daholo rehefa tsindrina ilay donne mobile misy anilay app napetaka ary IA no mamaky azy
 *  fa azo atao ihany koa ny miditra ao anatiny ilay app rehefa mandeha izy"
 */
@Composable
fun AiInspectionCard(
    isDataActive: Boolean,
    aiLogs: List<AiInspectionLog>,
    isAiAnalyzing: Boolean,
    onTriggerAiAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Pulsing alpha for active AI reading
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val latestLog = aiLogs.firstOrNull()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.95f)),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (isDataActive) CyberCyan.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. HEADER ROW: IA Status & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = if (isDataActive) pulseAlpha * 0.25f else 0.1f))
                            .border(
                                1.5.dp,
                                if (isDataActive) CyberCyan else Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "IA Mpamaky",
                            tint = if (isDataActive) CyberCyan else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "IA MPAMAKY SY MPANDINIKA",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (isDataActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "● MAMAKY...",
                                        color = AccentGreen,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isDataActive) {
                                "Mamaky ny fampiharana miasa • Fiarovana sandbox 100%"
                            } else {
                                "Miandry données mobiles mba hamakiana ny fampiharana"
                            },
                            color = if (isDataActive) CyberCyan else TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Bokotra hanaovana fanadihadiana IA
                Button(
                    onClick = onTriggerAiAnalysis,
                    enabled = isDataActive && !isAiAnalyzing,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan.copy(alpha = 0.2f),
                        contentColor = CyberCyan,
                        disabledContainerColor = Color.White.copy(alpha = 0.05f),
                        disabledContentColor = Color.White.copy(alpha = 0.3f)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    if (isAiAnalyzing) {
                        CircularProgressIndicator(
                            color = CyberCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Text(
                            text = "Asaivo mamaky",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. LATEST AI INSIGHT BANNER
            if (latestLog != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090E17))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = latestLog.appName,
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Data: ${latestLog.bytesRead}",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = latestLog.aiInsight,
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // 3. TOGGLE RECENT AI LOGS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isExpanded) "Afeno ny tantaran'ny famakian'ny IA" else "Jereo ny tantaran'ny famakian'ny IA (${aiLogs.size})",
                    color = CyberCyan,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    aiLogs.take(5).forEach { log ->
                        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .padding(8.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = timeStr,
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = log.aiInsight,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                lineHeight = 13.5.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
