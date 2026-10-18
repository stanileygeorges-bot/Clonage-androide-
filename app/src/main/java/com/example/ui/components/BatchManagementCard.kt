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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*

/**
 * Batch Management UI component:
 * Allows users to Pause, Resume, or Restart all active Android clones simultaneously,
 * or manage specific selected instances in batch.
 */
@Composable
fun BatchManagementCard(
    profiles: List<PhoneProfileEntity>,
    isBatchRestarting: Boolean,
    onPauseAll: () -> Unit,
    onResumeAll: () -> Unit,
    onRestartAll: () -> Unit,
    onPauseSingle: (Int) -> Unit = {},
    onResumeSingle: (Int) -> Unit = {},
    onRestartSingle: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var selectedCloneIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    val totalCount = profiles.size
    val runningCount = profiles.count { it.cloneStatus == "RUNNING" && !it.isPaused }
    val pausedCount = profiles.count { it.cloneStatus == "PAUSED" || it.isPaused }
    val restartingCount = profiles.count { it.cloneStatus == "RESTARTING" }

    // Spin animation for restart state
    val infiniteTransition = rememberInfiniteTransition(label = "batch_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("batch_management_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header with Icon, Title and Counts
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
                            .background(CyberCyan.copy(alpha = 0.15f))
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Fitantanana Maro (Batch Control)",
                                color = Color.White,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isBatchRestarting) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Restarting",
                                    tint = CyberCyan,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .rotate(spinAngle)
                                )
                            }
                        }
                        Text(
                            text = "Mifehy clones $totalCount miaraka amin'ny tsindry iray",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Expand/Collapse Toggle
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Hanitatra/Hampifintina",
                        tint = TextSecondary
                    )
                }
            }

            // 2. Status Summary Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total
                StatusSummaryPill(
                    label = "Total: $totalCount",
                    color = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                // Running
                StatusSummaryPill(
                    label = "● $runningCount Mandeha",
                    color = AccentGreen,
                    modifier = Modifier.weight(1.2f)
                )
                // Paused
                StatusSummaryPill(
                    label = "⏸ $pausedCount Naato",
                    color = AccentAmber,
                    modifier = Modifier.weight(1.1f)
                )
                // Restarting
                if (restartingCount > 0 || isBatchRestarting) {
                    StatusSummaryPill(
                        label = "🔄 $restartingCount Reboot",
                        color = CyberCyan,
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }

            // 3. Primary Simultaneous Batch Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pause All
                BatchActionButton(
                    icon = Icons.Default.PauseCircle,
                    label = "Pause Daholo",
                    subtext = "Atsaharo ny clones",
                    color = AccentAmber,
                    testTag = "batch_pause_all_button",
                    onClick = onPauseAll,
                    modifier = Modifier.weight(1f)
                )

                // Resume All
                BatchActionButton(
                    icon = Icons.Default.PlayCircle,
                    label = "Resume Daholo",
                    subtext = "Tohizo ny clones",
                    color = AccentGreen,
                    testTag = "batch_resume_all_button",
                    onClick = onResumeAll,
                    modifier = Modifier.weight(1f)
                )

                // Restart All
                BatchActionButton(
                    icon = Icons.Default.RestartAlt,
                    label = "Restart Daholo",
                    subtext = "Avereno novelomina",
                    color = CyberCyan,
                    isLoading = isBatchRestarting,
                    spinAngle = spinAngle,
                    testTag = "batch_restart_all_button",
                    onClick = onRestartAll,
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Detailed Clone Instances List (Expandable)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Toetran'ny Clones Tsirairay",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Azo baikoina tsirairay na miaraka",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    profiles.forEach { profile ->
                        val isPaused = profile.isPaused || profile.cloneStatus == "PAUSED"
                        val isRestarting = profile.cloneStatus == "RESTARTING" || isBatchRestarting

                        CloneInstanceBatchRow(
                            profile = profile,
                            isPaused = isPaused,
                            isRestarting = isRestarting,
                            spinAngle = spinAngle,
                            onTogglePause = {
                                if (isPaused) onResumeSingle(profile.phoneId) else onPauseSingle(profile.phoneId)
                            },
                            onRestart = { onRestartSingle(profile.phoneId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusSummaryPill(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BatchActionButton(
    icon: ImageVector,
    label: String,
    subtext: String,
    color: Color,
    testTag: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    spinAngle: Float = 0f,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, color.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier
                        .size(18.dp)
                        .then(if (isLoading) Modifier.rotate(spinAngle) else Modifier)
                )
            }
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = subtext,
                color = TextSecondary,
                fontSize = 9.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CloneInstanceBatchRow(
    profile: PhoneProfileEntity,
    isPaused: Boolean,
    isRestarting: Boolean,
    spinAngle: Float,
    onTogglePause: () -> Unit,
    onRestart: () -> Unit
) {
    val statusColor = when {
        isRestarting -> CyberCyan
        isPaused -> AccentAmber
        else -> AccentGreen
    }

    val statusText = when {
        isRestarting -> "Mamelona..."
        isPaused -> "Naato"
        else -> "Mandeha"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ID & Name & Resources
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${profile.phoneId}",
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CPU: ${if (isPaused) "1%" else "${profile.cpuLoad}%"}",
                        color = TextSecondary,
                        fontSize = 9.5.sp
                    )
                    Text(
                        text = "RAM: ${if (isPaused) "12%" else "${profile.ramLoad}%"}",
                        color = TextSecondary,
                        fontSize = 9.5.sp
                    )
                }
            }
        }

        // Status Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isRestarting) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier
                            .size(10.dp)
                            .rotate(spinAngle)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Individual Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Individual Pause/Resume
            IconButton(
                onClick = onTogglePause,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isPaused) AccentGreen.copy(alpha = 0.15f) else AccentAmber.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume #${profile.phoneId}" else "Pause #${profile.phoneId}",
                    tint = if (isPaused) AccentGreen else AccentAmber,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Individual Restart
            IconButton(
                onClick = onRestart,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Restart #${profile.phoneId}",
                    tint = CyberCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
