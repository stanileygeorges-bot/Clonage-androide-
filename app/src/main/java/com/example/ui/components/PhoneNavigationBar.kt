package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PhoneNavigationBar(
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRecents: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(22.dp)
            )
        }

        // Home Button (Pill or Circle)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { onHome() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.9f))
            )
        }

        // Recents / Apps Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { onRecents() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Apps & Recents",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
