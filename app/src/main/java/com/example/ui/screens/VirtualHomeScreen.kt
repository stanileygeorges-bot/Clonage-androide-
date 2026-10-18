package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*
import com.example.viewmodel.VirtualScreen
import java.text.SimpleDateFormat
import java.util.*

data class AppShortcutItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val screen: VirtualScreen,
    val badge: Int = 0,
    val iconColor: Color,
    val isCloneBadge: Boolean = false
)

@Composable
fun VirtualHomeScreen(
    phoneId: Int,
    profile: PhoneProfileEntity?,
    unreadMessagesCount: Int,
    missedCallsCount: Int,
    onOpenScreen: (VirtualScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    val wallpaperRes = if (phoneId == 1) R.drawable.wp_phone1 else R.drawable.wp_phone2

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale("mg", "MG")) }
    val now = remember { Date() }

    val phoneLetter = when (phoneId) {
        1 -> "A"
        2 -> "B"
        else -> "C"
    }

    val homeApps = remember(phoneId, unreadMessagesCount, missedCallsCount) {
        listOf(
            AppShortcutItem(
                id = "youtube",
                name = "YouTube ($phoneLetter)",
                icon = Icons.Default.PlayArrow,
                screen = VirtualScreen.YOUTUBE,
                badge = 0,
                iconColor = Color(0xFFFF0000),
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "parefeu",
                name = "Pare-feu Réseau",
                icon = Icons.Default.Security,
                screen = VirtualScreen.PARE_FEU_DASHBOARD,
                badge = 0,
                iconColor = CyberCyan,
                isCloneBadge = false
            ),
            AppShortcutItem(
                id = "storage",
                name = "Stockage ($phoneLetter)",
                icon = Icons.Default.FolderShared,
                screen = VirtualScreen.STORAGE_DOWNLOADS,
                badge = 0,
                iconColor = AccentAmber,
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "dialer",
                name = "Antso ($phoneLetter)",
                icon = Icons.Default.Call,
                screen = VirtualScreen.DIALER,
                badge = missedCallsCount,
                iconColor = AccentGreen,
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "messages",
                name = "Hafatra ($phoneLetter)",
                icon = Icons.Default.ChatBubble,
                screen = VirtualScreen.MESSAGES,
                badge = unreadMessagesCount,
                iconColor = CyberCyan,
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "contacts",
                name = "Fifandraisana ($phoneLetter)",
                icon = Icons.Default.Contacts,
                screen = VirtualScreen.CONTACTS,
                iconColor = OceanBlue,
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "cloned_apps",
                name = "Apps Cloned",
                icon = Icons.Default.Apps,
                screen = VirtualScreen.CLONED_APPS,
                iconColor = NeonViolet,
                isCloneBadge = true
            ),
            AppShortcutItem(
                id = "browser",
                name = "Tranonkala ($phoneLetter)",
                icon = Icons.Default.Public,
                screen = VirtualScreen.BROWSER,
                iconColor = CyberPurple,
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "gallery",
                name = "Sary ($phoneLetter)",
                icon = Icons.Default.PhotoLibrary,
                screen = VirtualScreen.GALLERY,
                iconColor = MagentaGlow,
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "notes",
                name = "Naoty ($phoneLetter)",
                icon = Icons.Default.EditNote,
                screen = VirtualScreen.NOTES,
                iconColor = AccentAmber,
                isCloneBadge = phoneId > 1
            ),
            AppShortcutItem(
                id = "settings",
                name = "Fikirakirana",
                icon = Icons.Default.Settings,
                screen = VirtualScreen.SETTINGS,
                iconColor = TextSecondary,
                isCloneBadge = false
            )
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Wallpaper background image
        Image(
            painter = painterResource(id = wallpaperRes),
            contentDescription = "Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Clock & Status Widget
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Clock
                Text(
                    text = timeFormat.format(now),
                    color = Color.White,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = (-1).sp
                )

                // Date
                Text(
                    text = dateFormat.format(now).replaceFirstChar { it.uppercase() },
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Dual Space Status Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .fillMaxWidth(),
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
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (phoneId == 1) Icons.Default.PhoneAndroid else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = profile?.name ?: if (phoneId == 1) "Finday 1 - Fototra" else "Finday 2 - Clonage",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (phoneId == 1) "Habaka fototra mahaleo tena" else "Habaka faharoa voaaro sy mitokana",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Status Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(accentColor.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (phoneId == 1) "FOTOTRA" else "CLONED",
                                color = accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Middle Section: App Grid (4 columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(homeApps) { app ->
                    AppIconCell(
                        app = app,
                        onClick = { onOpenScreen(app.screen) }
                    )
                }
            }

            // Bottom Section: Persistent Dock with Glass Effect
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DockIcon(Icons.Default.Call, AccentGreen, "Antso") { onOpenScreen(VirtualScreen.DIALER) }
                    DockIcon(Icons.Default.ChatBubble, CyberCyan, "Hafatra") { onOpenScreen(VirtualScreen.MESSAGES) }
                    DockIcon(Icons.Default.Apps, NeonViolet, "Clonage") { onOpenScreen(VirtualScreen.CLONED_APPS) }
                    DockIcon(Icons.Default.Public, CyberPurple, "Tranonkala") { onOpenScreen(VirtualScreen.BROWSER) }
                    DockIcon(Icons.Default.Settings, TextSecondary, "Settings") { onOpenScreen(VirtualScreen.SETTINGS) }
                }
            }
        }
    }
}

@Composable
private fun AppIconCell(
    app: AppShortcutItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            // Main Icon container
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                app.iconColor.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .border(1.dp, app.iconColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = app.icon,
                    contentDescription = app.name,
                    tint = app.iconColor,
                    modifier = Modifier.size(28.dp)
                )

                // Clone marker badge (tiny "2" icon at bottom right)
                if (app.isCloneBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(NeonViolet),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Notification count badge
            if (app.badge > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(AccentRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${app.badge}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.name,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DockIcon(
    icon: ImageVector,
    tint: Color,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}
