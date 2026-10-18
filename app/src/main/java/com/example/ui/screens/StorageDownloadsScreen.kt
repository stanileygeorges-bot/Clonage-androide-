package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadedVideoEntity
import com.example.data.model.PhoneProfileEntity
import com.example.ui.theme.*

@Composable
fun StorageDownloadsScreen(
    currentPhoneId: Int,
    profileA: PhoneProfileEntity?,
    profileB: PhoneProfileEntity?,
    profileC: PhoneProfileEntity?,
    downloadsA: List<DownloadedVideoEntity>,
    downloadsB: List<DownloadedVideoEntity>,
    downloadsC: List<DownloadedVideoEntity>,
    onSelectPhone: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewedPhoneId by remember { mutableIntStateOf(currentPhoneId) }
    val activeProfile = when (viewedPhoneId) {
        1 -> profileA
        2 -> profileB
        else -> profileC
    }
    val activeDownloads = when (viewedPhoneId) {
        1 -> downloadsA
        2 -> downloadsB
        else -> downloadsC
    }
    val accentColor = when (viewedPhoneId) {
        1 -> CyberCyan
        2 -> NeonViolet
        else -> AccentGreen
    }

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
                text = "Fitantanana ny Stockage (Finday 3)",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

        // Device Selection Bar (Finday A, Finday B, Finday C)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeviceStorageTab("Finday A", "${downloadsA.size} rakitra", isSelected = viewedPhoneId == 1, accentColor = CyberCyan) {
                viewedPhoneId = 1
                onSelectPhone(1)
            }
            DeviceStorageTab("Finday B", "${downloadsB.size} rakitra", isSelected = viewedPhoneId == 2, accentColor = NeonViolet) {
                viewedPhoneId = 2
                onSelectPhone(2)
            }
            DeviceStorageTab("Finday C", "${downloadsC.size} rakitra", isSelected = viewedPhoneId == 3, accentColor = AccentGreen) {
                viewedPhoneId = 3
                onSelectPhone(3)
            }
        }

        // Explanation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.85f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Stockage an'i ${activeProfile?.name ?: "Finday $viewedPhoneId"}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kaonty ampiasaina: ${activeProfile?.googleAccount ?: "user@gmail.com"}",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Fanazavana: Raha misintona vidéo i A, dia ao amin'ny fitaovan'i A ihany no voatahiry ilay téléchargement. Tsy miseho amin'i B sy C satria samy mahaleo tena ny stockage an'ireo finday.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Storage Usage Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = CardSlate.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Habaka efa nampiasaina", color = TextSecondary, fontSize = 11.sp)
                    Text(
                        text = "${activeProfile?.storageUsedMb ?: 1200} MB / ${(activeProfile?.storageTotalMb ?: 64000) / 1000} GB",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 0.12f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = accentColor,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        // Downloads List
        Text(
            text = "Ireo rakitra sy vidéo voatahiry ao amin'ity finday ity (${activeDownloads.size}):",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )

        if (activeDownloads.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.35f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tsy misy rakitra voatahiry ao amin'ity finday ity",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Rehefa misintona ao amin'ny YouTube na tranonkala dia eto ihany no hipetraka.",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(activeDownloads) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.75f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(accentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoLibrary,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.channelName} • ${item.fileSize} • ${item.duration}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Voatahiry ao amin'ny Finday $viewedPhoneId ihany",
                                    color = accentColor,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DeviceStorageTab(
    name: String,
    count: String,
    isSelected: Boolean,
    accentColor: Color,
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
            if (isSelected) accentColor else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = count, color = if (isSelected) accentColor else TextSecondary, fontSize = 10.sp)
        }
    }
}
