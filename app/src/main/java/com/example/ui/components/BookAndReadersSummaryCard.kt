package com.example.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Dashboard card displaying "The Book and the Readers" analogy and quick simulator trigger.
 */
@Composable
fun BookAndReadersSummaryCard(
    onOpenSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenSimulator() }
            .testTag("book_and_readers_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.92f)),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(listOf(CyberCyan, OceanBlue, NeonViolet))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(CyberCyan.copy(alpha = 0.25f), NeonViolet.copy(alpha = 0.25f))
                                )
                            )
                            .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = CyanBright,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Ny Boky sy ny Mpamaky 📘",
                            color = Color.White,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "The Book & Readers • Fanoharana Telemetry & Shareit",
                            color = TextSecondary,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+1 sy +1 Pings",
                        color = AccentGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Explanatory highlights row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Samsung
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(OceanBlue)
                    )
                    Text(
                        text = "Samsung (Antananarivo)",
                        color = Color.White,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Shareit",
                    tint = NeonViolet,
                    modifier = Modifier.size(14.dp)
                )

                // Tecno
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NeonViolet)
                    )
                    Text(
                        text = "Tecno (Shareit Copy)",
                        color = Color.White,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Summary quote
            Text(
                text = "« Na dia photocopy aza no eny an-tanan'ny namana, dia misy ati-doha 2 sy maso 4 mamaky. Rehefa misy internet dia samy manisa +1 sy +1 ny servieran'ny mpanoratra! »",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            // Interactive Button
            Button(
                onClick = onOpenSimulator,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .testTag("open_book_simulator_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan.copy(alpha = 0.2f),
                    contentColor = CyberCyan
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.7f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Sokafy ny Simulator Mivantana & Fanoharana Feno 🚀",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
