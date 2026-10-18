package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadedVideoEntity
import com.example.ui.components.ClonageTopStatusBar
import com.example.ui.theme.*
import com.example.viewmodel.YouTubeVideoItem

@Composable
fun YouTubeScreen(
    phoneId: Int,
    isMobileDataConnected: Boolean,
    isWifiConnected: Boolean = true,
    onToggleMobileData: () -> Unit,
    onToggleWifi: () -> Unit = {},
    downloadsForThisPhone: List<DownloadedVideoEntity>,
    onDownloadVideo: (YouTubeVideoItem) -> Unit,
    onDeleteDownload: (DownloadedVideoEntity) -> Unit,
    onSelectPhone: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phoneLetter = when (phoneId) {
        1 -> "A"
        2 -> "B"
        else -> "C"
    }
    val phoneName = "Finday $phoneLetter"
    val ownerEmail = "olona.${phoneLetter.lowercase()}@gmail.com"
    val accentColor = if (phoneId == 1) CyberCyan else if (phoneId == 2) NeonViolet else AccentGreen

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Accueil, 1: Téléchargements de cette phone, 2: Comparateur A, B, C
    var activePlayingVideo by remember {
        mutableStateOf<YouTubeVideoItem?>(
            YouTubeVideoItem(
                id = "vid_demo_1",
                title = "Tetikasa Android & Fitaovana Mahaleo Tena 2026",
                channel = "Tech Gasy Channel",
                views = "24K vues",
                duration = "12:45",
                sizeMb = "45 MB",
                category = "Teknolojia",
                description = "Fomba fiasan'ny fitaovana roa na telo samy mahaleo tena ao anaty finday iray tsy mifampiankina."
            )
        )
    }

    val sampleVideos = remember {
        listOf(
            YouTubeVideoItem("vid_1", "Tetikasa Android & Fitaovana Mahaleo Tena 2026", "Tech Gasy Channel", "24K vues", "12:45", "45 MB", "Teknolojia", "Fanazavana ny clonage sy stockage mitokana."),
            YouTubeVideoItem("vid_2", "Hira Gasy Vaovao & Mozika Acoustic Madagasikara", "Mozika Malagasy", "110K vues", "04:20", "22 MB", "Mozika", "Hira faneva sy mozika an-tserasera."),
            YouTubeVideoItem("vid_3", "Fiarovana Pare-feu & Données Mobiles", "Cyber Security MG", "18K vues", "08:15", "34 MB", "Sécurité", "Fifehezana ny données mobiles sy WiFi amin'ny finday."),
            YouTubeVideoItem("vid_4", "Torolalana momba ny Fandraharahana & Asa", "Entrepreneurs Gasy", "45K vues", "16:02", "58 MB", "Asa", "Torohevitra momba ny vola sy ny tetikasa vaovao.")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP STATUS BAR (Mobile Data & Wi-Fi Icons with Toggles)
        ClonageTopStatusBar(
            isMobileDataConnected = isMobileDataConnected,
            isWifiConnected = isWifiConnected,
            onToggleMobileData = onToggleMobileData,
            onToggleWifi = onToggleWifi,
            titlePill = "YOUTUBE CLONE • FINDAY $phoneLetter"
        )

        // App Bar (YouTube Header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F1F1F))
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

            // YouTube Logo & Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFF0000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "YouTube",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "YouTube",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                // Current Device Identity Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = phoneName,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Current user account avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = phoneLetter,
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // IA MPAMAKY BANNER AO ANATY YOUTUBE (IA NO MAMAKY AZY REHEFA MANDÉHA)
        if (isMobileDataConnected || isWifiConnected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF07121C))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "IA Mpamaky: Miasa ny YouTube • Kaonty sy fitahirizana mitokana 100%",
                        color = CyberCyan,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Connection Warning Banner if Mobile Data is Disconnected
        if (!isMobileDataConnected) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AccentRed.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalCellularOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Pare-feu: Données mobiles déconnectées (miloko mena)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onToggleMobileData,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AccentRed),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Ampifandraiso", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Switch Tabs: Accueil, Téléchargements de cette phone, Comparateur
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141414))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            YouTubeNavChip("Vidéo (Accueil)", isSelected = selectedTab == 0, accentColor = accentColor) { selectedTab = 0 }
            YouTubeNavChip("Stockage & Téléchargements (${downloadsForThisPhone.size})", isSelected = selectedTab == 1, accentColor = accentColor) { selectedTab = 1 }
            YouTubeNavChip("Fampitahana A/B/C", isSelected = selectedTab == 2, accentColor = accentColor) { selectedTab = 2 }
        }

        if (selectedTab == 0) {
            // TAB 0: Active Video Player + Video Feed
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Active Playing Video Pane
                activePlayingVideo?.let { video ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(0.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black)
                        ) {
                            Column {
                                // Simulated Video Player Frame (Independent screen!)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(Color(0xFF18181B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFF0000).copy(alpha = 0.85f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                tint = Color.White,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Mandeha eo amin'ny efijerin'i $phoneName ihany ity vidéo ity",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Time badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Black.copy(alpha = 0.8f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = video.duration, color = Color.White, fontSize = 10.sp)
                                    }
                                }

                                // Video Title & Actions
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = video.title,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "${video.channel} • ${video.views}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )

                                    // Action Bar: SINTOMY NY VIDÉO (TÉLÉCHARGER), J'AIME, PARTAGER
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        // THE PRIMARY ACTION: SINTOMY NY VIDÉO (TÉLÉCHARGER)
                                        val isAlreadyDownloaded = downloadsForThisPhone.any { it.title == video.title }
                                        Button(
                                            onClick = { onDownloadVideo(video) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isAlreadyDownloaded) AccentGreen else Color.White.copy(alpha = 0.15f),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(20.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isAlreadyDownloaded) Icons.Default.Check else Icons.Default.Download,
                                                contentDescription = "Sintomy",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isAlreadyDownloaded) "Voasintona ato" else "Sintomy (${video.sizeMb})",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // J'aime
                                        AssistActionButton(Icons.Default.ThumbUp, "J'aime")
                                        // Partager
                                        AssistActionButton(Icons.Default.Share, "Partager")
                                    }

                                    // Explanatory Note on Isolation
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "💡 Rehefa manindry 'Sintomy' ianao dia ao amin'ny fitaovan'i $phoneName ihany no voatahiry ilay vidéo. Tsy miseho amin'ny finday hafa izany satria samy mahaleo tena ny stockage!",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Video Feed List
                item {
                    Text(
                        text = "Vidéo hafa azo jerena sy sintomina:",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                items(sampleVideos) { video ->
                    YouTubeVideoCard(
                        video = video,
                        accentColor = accentColor,
                        onSelect = { activePlayingVideo = video },
                        onDownload = { onDownloadVideo(video) }
                    )
                }
            }
        } else if (selectedTab == 1) {
            // TAB 1: Isolated Storage / Downloads for this specific phone
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.8f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Stockage anatiny ho an'i $phoneName",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kaonty: $ownerEmail • Mahaleo tena tanteraka",
                            color = accentColor,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Ireo antontan-taratasy sy vidéo eto dia tsy miseho amin'ny finday hafa mihitsy.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (downloadsForThisPhone.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Mbola tsy misy vidéo voasintona ato amin'i $phoneName",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Mandehana any amin'ny 'Vidéo' dia tsindrio 'Sintomy' mba hanandramana azy.",
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(downloadsForThisPhone) { download ->
                            DownloadedVideoCard(
                                download = download,
                                accentColor = accentColor,
                                onDelete = { onDeleteDownload(download) }
                            )
                        }
                    }
                }
            }
        } else {
            // TAB 2: Direct Comparison between Phone A, B, C
            DeviceComparisonTab(
                currentPhoneId = phoneId,
                onSelectPhone = onSelectPhone
            )
        }
    }
}

@Composable
private fun YouTubeVideoCard(
    video: YouTubeVideoItem,
    accentColor: Color,
    onSelect: () -> Unit,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFF27272A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(44.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = video.duration, color = Color.White, fontSize = 10.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${video.channel} • ${video.sizeMb}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = onDownload) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Sintomy",
                        tint = accentColor
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadedVideoCard(
    download: DownloadedVideoEntity,
    accentColor: Color,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.85f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFF0000).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = download.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${download.channelName} • ${download.fileSize}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Voatahiry ho an'ny ${download.downloadedByAccount}",
                        color = accentColor,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Fafao",
                    tint = AccentRed.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun DeviceComparisonTab(
    currentPhoneId: Int,
    onSelectPhone: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Mampitaha ny Olona 3 (Finday A, B, C)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Samy manana ny findainy, ny connexion-ny, ary ny compte-ny izy ireo:\n" +
                           "• Raha misintona vidéo i A -> Ao amin'ny findain'i A ihany no voatahiry ilay téléchargement.\n" +
                           "• Tsy miseho ho azy amin'i B sy C ilay vidéo satria tsy mitovy ny stockage.\n" +
                           "• Na miditra amin'ny compte Google iray aza izy ireo, mbola tsy mitovy ny zavatra voatahiry ao amin'ny finday tsirairay.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Text(
            text = "Kitiho eto raha hanova ny finday ampiasaina:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ComparisonDeviceCard("Finday A", "Olona A", "Pixel 9", CyberCyan, isSelected = currentPhoneId == 1) { onSelectPhone(1) }
            ComparisonDeviceCard("Finday B", "Olona B", "Galaxy S24", NeonViolet, isSelected = currentPhoneId == 2) { onSelectPhone(2) }
            ComparisonDeviceCard("Finday C", "Olona C", "Xiaomi 14", AccentGreen, isSelected = currentPhoneId == 3) { onSelectPhone(3) }
        }
    }
}

@Composable
private fun RowScope.ComparisonDeviceCard(
    name: String,
    owner: String,
    model: String,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.2f) else CardSlate.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) accentColor else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = owner, color = TextSecondary, fontSize = 10.sp)
            Text(text = model, color = accentColor, fontSize = 9.sp)
        }
    }
}

@Composable
private fun AssistActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(16.dp))
            Text(text = label, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
private fun YouTubeNavChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) accentColor else Color.White,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
