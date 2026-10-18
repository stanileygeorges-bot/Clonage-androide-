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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
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
 * Modes of viewing the hierarchy:
 * - TREE: Full accordion progressive disclosure (Groups -> Devices -> Apps)
 * - GROUP_FOCUS: Flattened grouped summary (Macro cluster management)
 * - DEVICE_FOCUS: Device-first view (Hardware, status, & running apps)
 * - APP_FOCUS: App-first matrix (Find which devices are running YouTube, Facebook, etc.)
 */
enum class HierarchyViewMode {
    NESTED_TREE,
    GROUP_FOCUS,
    DEVICE_FOCUS,
    APP_FOCUS
}

/**
 * Isolated AI Automation Task states for an app inside a virtual clone
 */
enum class AiAppTaskState(val label: String, val icon: ImageVector, val color: Color) {
    VIDEO("Vidéo", Icons.Default.PlayCircle, CyberCyan),
    ADVERTISING("Publicité", Icons.Default.Campaign, AccentAmber),
    IDLE("Rien", Icons.Default.DoNotDisturb, TextSecondary)
}

/**
 * UI State representation of an App within a Device
 */
data class HierarchyAppItem(
    val id: String,
    val name: String,
    val packageName: String,
    val iconKey: String,
    val taskState: AiAppTaskState = AiAppTaskState.IDLE,
    val dataUsageMb: Float = 12.4f,
    val isSandboxIsolated: Boolean = true
)

/**
 * UI State representation of a Virtual Device
 */
data class HierarchyDeviceItem(
    val deviceId: Int,
    val name: String,
    val model: String,
    val isRunning: Boolean = true,
    val isPaused: Boolean = false,
    val cpuLoad: Int = 34,
    val ramLoad: Int = 56,
    val battery: Int = 92,
    val apps: List<HierarchyAppItem>
)

/**
 * UI State representation of a Device Group / Cluster
 */
data class HierarchyGroupItem(
    val groupId: String,
    val groupName: String,
    val tagColor: Color,
    val devices: List<HierarchyDeviceItem>
)

/**
 * Reusable Tree View / Nested Expansion UI Pattern Component for Jetpack Compose.
 * Demonstrates how to organize complex Groups ➡️ Devices ➡️ Applications hierarchies
 * on mobile without overwhelming screen real estate.
 */
@Composable
fun HierarchyTreeViewCard(
    profiles: List<PhoneProfileEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(HierarchyViewMode.NESTED_TREE) }
    var searchQuery by remember { mutableStateOf("") }

    // Expanded states
    var expandedGroupIds by remember { mutableStateOf(setOf("grp-alpha")) }
    var expandedDeviceIds by remember { mutableStateOf(setOf(1)) }

    // Dynamic state for isolated app tasks (Device ID + App ID -> Task)
    val appTaskStates = remember {
        mutableStateMapOf<String, AiAppTaskState>().apply {
            put("1-youtube", AiAppTaskState.VIDEO)
            put("1-whatsapp", AiAppTaskState.IDLE)
            put("1-browser", AiAppTaskState.ADVERTISING)
            put("2-youtube", AiAppTaskState.ADVERTISING)
            put("2-facebook", AiAppTaskState.VIDEO)
            put("2-telegram", AiAppTaskState.IDLE)
            put("3-youtube", AiAppTaskState.IDLE)
            put("3-browser", AiAppTaskState.VIDEO)
        }
    }

    // Mock/derived groups based on real profiles
    val groups = remember(profiles, appTaskStates) {
        val p1 = profiles.find { it.phoneId == 1 }
        val p2 = profiles.find { it.phoneId == 2 }
        val p3 = profiles.find { it.phoneId == 3 }

        listOf(
            HierarchyGroupItem(
                groupId = "grp-alpha",
                groupName = "Cluster Alpha (Production Farms)",
                tagColor = CyberCyan,
                devices = listOf(
                    HierarchyDeviceItem(
                        deviceId = 1,
                        name = p1?.name ?: "Appareil A (Samsung S24)",
                        model = p1?.modelName ?: "SM-S928B",
                        isRunning = p1?.cloneStatus == "RUNNING" && !(p1?.isPaused ?: false),
                        isPaused = p1?.isPaused ?: false,
                        cpuLoad = p1?.cpuLoad ?: 32,
                        ramLoad = p1?.ramLoad ?: 48,
                        battery = p1?.batteryLevel ?: 95,
                        apps = listOf(
                            HierarchyAppItem("youtube", "YouTube Cloned", "com.google.android.youtube", "youtube", appTaskStates["1-youtube"] ?: AiAppTaskState.VIDEO, 120.5f),
                            HierarchyAppItem("whatsapp", "WhatsApp Cloned", "com.whatsapp", "whatsapp", appTaskStates["1-whatsapp"] ?: AiAppTaskState.IDLE, 34.2f),
                            HierarchyAppItem("browser", "Chrome Sandboxed", "com.android.chrome", "browser", appTaskStates["1-browser"] ?: AiAppTaskState.ADVERTISING, 85.0f)
                        )
                    ),
                    HierarchyDeviceItem(
                        deviceId = 2,
                        name = p2?.name ?: "Appareil B (Tecno Camon 30)",
                        model = p2?.modelName ?: "CL6",
                        isRunning = p2?.cloneStatus == "RUNNING" && !(p2?.isPaused ?: false),
                        isPaused = p2?.isPaused ?: false,
                        cpuLoad = p2?.cpuLoad ?: 58,
                        ramLoad = p2?.ramLoad ?: 62,
                        battery = p2?.batteryLevel ?: 88,
                        apps = listOf(
                            HierarchyAppItem("youtube", "YouTube Cloned", "com.google.android.youtube", "youtube", appTaskStates["2-youtube"] ?: AiAppTaskState.ADVERTISING, 98.1f),
                            HierarchyAppItem("facebook", "Facebook Lite", "com.facebook.lite", "facebook", appTaskStates["2-facebook"] ?: AiAppTaskState.VIDEO, 45.3f),
                            HierarchyAppItem("telegram", "Telegram Sandboxed", "org.telegram.messenger", "telegram", appTaskStates["2-telegram"] ?: AiAppTaskState.IDLE, 18.0f)
                        )
                    )
                )
            ),
            HierarchyGroupItem(
                groupId = "grp-beta",
                groupName = "Cluster Bêta (Staging & Testing)",
                tagColor = NeonViolet,
                devices = listOf(
                    HierarchyDeviceItem(
                        deviceId = 3,
                        name = p3?.name ?: "Appareil C (Pixel 8 Pro)",
                        model = p3?.modelName ?: "GC368",
                        isRunning = p3?.cloneStatus == "RUNNING" && !(p3?.isPaused ?: false),
                        isPaused = p3?.isPaused ?: false,
                        cpuLoad = p3?.cpuLoad ?: 14,
                        ramLoad = p3?.ramLoad ?: 29,
                        battery = p3?.batteryLevel ?: 72,
                        apps = listOf(
                            HierarchyAppItem("youtube", "YouTube Cloned", "com.google.android.youtube", "youtube", appTaskStates["3-youtube"] ?: AiAppTaskState.IDLE, 12.0f),
                            HierarchyAppItem("browser", "DuckDuckGo Sandboxed", "com.duckduckgo.mobile.android", "browser", appTaskStates["3-browser"] ?: AiAppTaskState.VIDEO, 67.4f)
                        )
                    )
                )
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hierarchy_tree_view_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.95f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberCyan.copy(alpha = 0.15f))
                            .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = "Tree View",
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Hiérarchie & Tree View",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Groupes ➔ Appareils ➔ Applications",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick expand/collapse all
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            if (expandedGroupIds.isEmpty()) {
                                expandedGroupIds = groups.map { it.groupId }.toSet()
                                expandedDeviceIds = groups.flatMap { it.devices.map { d -> d.deviceId } }.toSet()
                            } else {
                                expandedGroupIds = emptySet()
                                expandedDeviceIds = emptySet()
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DeepSlate)
                    ) {
                        Icon(
                            imageVector = if (expandedGroupIds.isNotEmpty()) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = "Toggle All",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Perspective Switcher Tabs (Zoom Level Controller)
            PerspectiveSegmentedControl(
                currentMode = viewMode,
                onModeSelected = { viewMode = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search filter bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filtrer groupe, appareil ou app...", fontSize = 12.sp, color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("hierarchy_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = LightSlate.copy(alpha = 0.5f),
                    focusedContainerColor = DeepSlate,
                    unfocusedContainerColor = DeepSlate,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Content according to perspective mode
            when (viewMode) {
                HierarchyViewMode.NESTED_TREE -> {
                    NestedTreeRenderer(
                        groups = groups,
                        searchQuery = searchQuery,
                        expandedGroupIds = expandedGroupIds,
                        expandedDeviceIds = expandedDeviceIds,
                        onToggleGroup = { gId ->
                            expandedGroupIds = if (expandedGroupIds.contains(gId)) {
                                expandedGroupIds - gId
                            } else {
                                expandedGroupIds + gId
                            }
                        },
                        onToggleDevice = { dId ->
                            expandedDeviceIds = if (expandedDeviceIds.contains(dId)) {
                                expandedDeviceIds - dId
                            } else {
                                expandedDeviceIds + dId
                            }
                        },
                        onUpdateAppTask = { dId, appId, task ->
                            appTaskStates["$dId-$appId"] = task
                        }
                    )
                }
                HierarchyViewMode.GROUP_FOCUS -> {
                    GroupFocusRenderer(groups = groups)
                }
                HierarchyViewMode.DEVICE_FOCUS -> {
                    DeviceFocusRenderer(
                        groups = groups,
                        onUpdateAppTask = { dId, appId, task ->
                            appTaskStates["$dId-$appId"] = task
                        }
                    )
                }
                HierarchyViewMode.APP_FOCUS -> {
                    AppMatrixFocusRenderer(
                        groups = groups,
                        onUpdateAppTask = { dId, appId, task ->
                            appTaskStates["$dId-$appId"] = task
                        }
                    )
                }
            }
        }
    }
}

/**
 * Segmented toggle pills for fast zooming between perspectives without losing place.
 */
@Composable
private fun PerspectiveSegmentedControl(
    currentMode: HierarchyViewMode,
    onModeSelected: (HierarchyViewMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DeepSlate,
        border = androidx.compose.foundation.BorderStroke(1.dp, LightSlate.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val modes = listOf(
                HierarchyViewMode.NESTED_TREE to "Arbre Complet",
                HierarchyViewMode.GROUP_FOCUS to "Groupes",
                HierarchyViewMode.DEVICE_FOCUS to "Appareils",
                HierarchyViewMode.APP_FOCUS to "Apps / IA"
            )

            modes.forEach { (mode, label) ->
                val isSelected = currentMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.22f) else Color.Transparent)
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) CyberCyan.copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(9.dp)
                        )
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) CyanBright else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 1. NESTED TREE RENDERER:
 * Solves the "horizontal squish" problem by using a vertical connective line guide
 * with minimal 10dp indentation and card-in-card visual container nesting.
 */
@Composable
private fun NestedTreeRenderer(
    groups: List<HierarchyGroupItem>,
    searchQuery: String,
    expandedGroupIds: Set<String>,
    expandedDeviceIds: Set<Int>,
    onToggleGroup: (String) -> Unit,
    onToggleDevice: (Int) -> Unit,
    onUpdateAppTask: (Int, String, AiAppTaskState) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        groups.forEach { group ->
            val isGroupExpanded = expandedGroupIds.contains(group.groupId)
            val filteredDevices = group.devices.filter { device ->
                if (searchQuery.isBlank()) true
                else {
                    device.name.contains(searchQuery, ignoreCase = true) ||
                    device.apps.any { it.name.contains(searchQuery, ignoreCase = true) } ||
                    group.groupName.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredDevices.isNotEmpty() || searchQuery.isBlank()) {
                GroupTreeNodeCard(
                    group = group,
                    isExpanded = isGroupExpanded,
                    onToggleExpand = { onToggleGroup(group.groupId) },
                    expandedDeviceIds = expandedDeviceIds,
                    onToggleDevice = onToggleDevice,
                    onUpdateAppTask = onUpdateAppTask
                )
            }
        }
    }
}

/**
 * Level 1 Node: Group Card
 */
@Composable
private fun GroupTreeNodeCard(
    group: HierarchyGroupItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    expandedDeviceIds: Set<Int>,
    onToggleDevice: (Int) -> Unit,
    onUpdateAppTask: (Int, String, AiAppTaskState) -> Unit
) {
    val totalDevices = group.devices.size
    val runningDevices = group.devices.count { it.isRunning && !it.isPaused }
    val totalApps = group.devices.sumOf { it.apps.size }

    // Summary count of tasks across group
    val videoCount = group.devices.flatMap { it.apps }.count { it.taskState == AiAppTaskState.VIDEO }
    val adCount = group.devices.flatMap { it.apps }.count { it.taskState == AiAppTaskState.ADVERTISING }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DeepSlate.copy(alpha = 0.9f),
        border = androidx.compose.foundation.BorderStroke(1.dp, group.tagColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Group Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(group.tagColor)
                    )

                    Text(
                        text = group.groupName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Compact Macro Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Devices counter pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CardSlate)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$runningDevices/$totalDevices En ligne",
                            color = if (runningDevices > 0) AccentGreen else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Task counters
                    if (!isExpanded) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🎬 $videoCount | 📢 $adCount",
                                color = CyanBright,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Chevron indicator
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expand Group",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(if (isExpanded) 180f else 0f)
                    )
                }
            }

            // Expanded Children: Devices connected by visual timeline guide
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        // Left visual connective rail to show tree hierarchy without indent penalty
                        .drawBehind {
                            drawLine(
                                color = group.tagColor.copy(alpha = 0.35f),
                                start = Offset(x = 10.dp.toPx(), y = 0f),
                                end = Offset(x = 10.dp.toPx(), y = size.height - 12.dp.toPx()),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    group.devices.forEach { device ->
                        val isDeviceExpanded = expandedDeviceIds.contains(device.deviceId)
                        DeviceTreeNodeCard(
                            device = device,
                            isExpanded = isDeviceExpanded,
                            onToggleExpand = { onToggleDevice(device.deviceId) },
                            onUpdateAppTask = onUpdateAppTask
                        )
                    }
                }
            }
        }
    }
}

/**
 * Level 2 Node: Virtual Device Card
 */
@Composable
private fun DeviceTreeNodeCard(
    device: HierarchyDeviceItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onUpdateAppTask: (Int, String, AiAppTaskState) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardSlate,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (device.isRunning) CyberCyan.copy(alpha = 0.3f) else AccentAmber.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Device",
                        tint = if (device.isRunning) CyberCyan else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )

                    Column {
                        Text(
                            text = device.name,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "CPU: ${device.cpuLoad}% | RAM: ${device.ramLoad}% | Bat: ${device.battery}%",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Apps count badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepSlate)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${device.apps.size} Apps",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Toggle Device",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (isExpanded) 90f else 0f)
                    )
                }
            }

            // Expanded Children: Applications with Isolated Task Selectors
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .drawBehind {
                            drawLine(
                                color = LightSlate.copy(alpha = 0.5f),
                                start = Offset(x = 8.dp.toPx(), y = 0f),
                                end = Offset(x = 8.dp.toPx(), y = size.height - 8.dp.toPx()),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    device.apps.forEach { appItem ->
                        AppTaskRowItem(
                            deviceId = device.deviceId,
                            app = appItem,
                            onUpdateTask = { newTask ->
                                onUpdateAppTask(device.deviceId, appItem.id, newTask)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Level 3 Node: Application Row with 3-way Isolated Task Segment (Vidéo | Publicité | Rien)
 */
@Composable
private fun AppTaskRowItem(
    deviceId: Int,
    app: HierarchyAppItem,
    onUpdateTask: (AiAppTaskState) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DeepSlate,
        border = androidx.compose.foundation.BorderStroke(1.dp, LightSlate.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App name + sandbox badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )

                Column {
                    Text(
                        text = app.name,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Sandbox Isolée • ${app.dataUsageMb} Mo",
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            // 3-Way Task Selector for this isolated app: [Vidéo] [Pub] [Rien]
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AiAppTaskState.values().forEach { taskState ->
                    val isSelected = app.taskState == taskState
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) taskState.color.copy(alpha = 0.25f)
                                else LightSlate.copy(alpha = 0.2f)
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) taskState.color.copy(alpha = 0.8f) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onUpdateTask(taskState) }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = taskState.label,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) taskState.color else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2. GROUP FOCUS VIEW:
 * High-level dashboard for cluster health, aggregate loads, and batch operations.
 */
@Composable
private fun GroupFocusRenderer(groups: List<HierarchyGroupItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        groups.forEach { group ->
            val allApps = group.devices.flatMap { it.apps }
            val videoApps = allApps.count { it.taskState == AiAppTaskState.VIDEO }
            val adApps = allApps.count { it.taskState == AiAppTaskState.ADVERTISING }
            val idleApps = allApps.count { it.taskState == AiAppTaskState.IDLE }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepSlate,
                border = androidx.compose.foundation.BorderStroke(1.dp, group.tagColor.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = group.groupName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(group.tagColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${group.devices.size} Appareils Actifs",
                                color = group.tagColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Tâches IA distribuées sur ce cluster :", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskSummaryPill(label = "Vidéo IA", count = videoApps, color = CyberCyan, modifier = Modifier.weight(1f))
                        TaskSummaryPill(label = "Publicités", count = adApps, color = AccentAmber, modifier = Modifier.weight(1f))
                        TaskSummaryPill(label = "Inactif", count = idleApps, color = TextSecondary, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * 3. DEVICE FOCUS VIEW:
 * Flat grid or list focusing on individual clone specs, hardware, and assigned tasks.
 */
@Composable
private fun DeviceFocusRenderer(
    groups: List<HierarchyGroupItem>,
    onUpdateAppTask: (Int, String, AiAppTaskState) -> Unit
) {
    val allDevices = groups.flatMap { it.devices }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        allDevices.forEach { device ->
            DeviceTreeNodeCard(
                device = device,
                isExpanded = true,
                onToggleExpand = {},
                onUpdateAppTask = onUpdateAppTask
            )
        }
    }
}

/**
 * 4. APP FOCUS MATRIX VIEW:
 * Inverted perspective (Pivot Table): lists each Application and shows which devices are currently running it
 * and what isolated task mode is assigned.
 */
@Composable
private fun AppMatrixFocusRenderer(
    groups: List<HierarchyGroupItem>,
    onUpdateAppTask: (Int, String, AiAppTaskState) -> Unit
) {
    // Unique app types
    val appNames = listOf("YouTube Cloned", "Chrome Sandboxed", "WhatsApp Cloned", "Facebook Lite")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        appNames.forEach { targetAppName ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DeepSlate,
                border = androidx.compose.foundation.BorderStroke(1.dp, LightSlate.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = CyanBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = targetAppName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Find all devices containing this app
                    val matchingInstances = groups.flatMap { group ->
                        group.devices.mapNotNull { device ->
                            val app = device.apps.find { it.name.startsWith(targetAppName.take(5)) }
                            if (app != null) Triple(device, app, group.groupName) else null
                        }
                    }

                    if (matchingInstances.isEmpty()) {
                        Text("Aucune instance active", color = TextSecondary, fontSize = 11.sp)
                    } else {
                        matchingInstances.forEach { (device, app, groupName) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${device.name} ($groupName)",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Current assigned task
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(app.taskState.color.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = app.taskState.label,
                                        fontSize = 10.sp,
                                        color = app.taskState.color,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskSummaryPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardSlate)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 9.sp, color = TextSecondary)
        }
    }
}
