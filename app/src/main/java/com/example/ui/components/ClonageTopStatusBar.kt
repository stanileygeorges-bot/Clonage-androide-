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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * STATUS BAR ENY AMIN'NY FARANY AMBONY HO AN'NY CLONAGE:
 * - Ora (Clock) amin'ny fotoana tena izy
 * - Badge "Clonage Isolé • Pare-feu"
 * - Sary DONNÉE MOBILE: Tsindrina mba hivadika CONNECTÉ (Maintso) na DÉCONNECTÉ (Mena)
 * - Sary WI-FI: Tsindrina mba hivadika CONNECTÉ (Cyan/Maintso) na DÉCONNECTÉ (Gris/Mena)
 * - Faraingo hafainganam-pifandraisana (Speed: KB/s na MB/s)
 * - Batterie (e.g. 96%)
 */
@Composable
fun ClonageTopStatusBar(
    isMobileDataConnected: Boolean,
    isWifiConnected: Boolean,
    onToggleMobileData: () -> Unit,
    onToggleWifi: () -> Unit,
    modifier: Modifier = Modifier,
    activePhoneId: Int = 1,
    titlePill: String = "CLONAGE ISOLÉ",
    onNewDeviceClick: (() -> Unit)? = null
) {
    // Ora tena izy (Clock) izay mihavao isaky ny minitra
    var currentTime by remember {
        mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30000)
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
    }

    // Loko sy sary ho an'ny Donnée Mobile
    val mobileDataColor = if (isMobileDataConnected) AccentGreen else AccentRed
    val wifiColor = if (isWifiConnected) CyberCyan else Color.White.copy(alpha = 0.35f)

    // Animated data speed indicator (simulated live rate)
    val speedText = when {
        isMobileDataConnected && isWifiConnected -> "⚡ 2.4 MB/s"
        isMobileDataConnected -> "⚡ 1.2 MB/s"
        isWifiConnected -> "⚡ 850 KB/s"
        else -> "⚡ 0.0 B/s"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF070B14))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ankavia: BOKOTRA PLUS (+) ENY AMBONY À GAUCHE sy Ora ary ny Badge Clonage
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (onNewDeviceClick != null) {
                // BOKOTRA PLUS (+) ENY AMBONY À GAUCHE
                // "Ary eny ambony à gauche eny kosa misy otrany plus de rehefa tsindrina ilay plus de mamoaka fenêtre vaovao le izy"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCyan.copy(alpha = 0.22f))
                        .border(1.2.dp, CyberCyan, RoundedCornerShape(8.dp))
                        .clickable { onNewDeviceClick() }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Hamorona Apareil Android Vaovao Mahaleo Tena (+)",
                            tint = CyberCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "+ Apareil",
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            // Badge Espace Isolé / Clonage
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberCyan.copy(alpha = 0.18f))
                    .border(0.8.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = titlePill,
                        color = CyberCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Speed indicator
            Text(
                text = speedText,
                color = if (isMobileDataConnected || isWifiConnected) AccentGreen else Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Ankavanana: Sary DONNÉE MOBILE sy WI-FI (Azo tsindrina mivantana!) + Batterie
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. SARY DONNÉE MOBILE AO AMIN'NY STATUS BAR
            // Tsindrina mba hamelona (Maintso) na hamonoana (Mena) ny données mobiles
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(mobileDataColor.copy(alpha = 0.18f))
                    .border(1.dp, mobileDataColor.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                    .clickable { onToggleMobileData() }
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = if (isMobileDataConnected) Icons.Default.SignalCellularAlt else Icons.Default.SignalCellularOff,
                        contentDescription = "Données Mobiles (Tsindrio raha hanova)",
                        tint = mobileDataColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isMobileDataConnected) "4G ON" else "DATA OFF",
                        color = mobileDataColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // 2. SARY WI-FI AO AMIN'NY STATUS BAR
            // Tsindrina mba hamelona na hamonoana ny Wi-Fi
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isWifiConnected) CyberCyan.copy(alpha = 0.18f)
                        else Color.White.copy(alpha = 0.06f)
                    )
                    .border(
                        1.dp,
                        if (isWifiConnected) CyberCyan.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onToggleWifi() }
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = if (isWifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = "Wi-Fi (Tsindrio raha hanova)",
                        tint = wifiColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isWifiConnected) "WIFI" else "OFF",
                        color = wifiColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3. BATTERIE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "96%",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = "Batterie",
                    tint = AccentGreen,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
