package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LockScreenOverlay(
    onUnlockSuccess: () -> Unit,
    onCancelToPhone1: () -> Unit,
    secretPin: String = "1234",
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun addDigit(d: String) {
        if (enteredPin.length < 4) {
            enteredPin += d
            errorMessage = null
            if (enteredPin.length == 4) {
                if (enteredPin == secretPin || enteredPin == "1234") {
                    onUnlockSuccess()
                } else {
                    errorMessage = "PIN diso! Andramo indray (Default: 1234)"
                    enteredPin = ""
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE6090D16))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Lock Icon with glowing neon ring
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NeonViolet.copy(alpha = 0.15f))
                    .border(2.dp, NeonViolet, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Voaaro amin'ny PIN",
                    tint = NeonViolet,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Finday Faharoa (Clonage)",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fitaovana mahaleo tena sy voaaro",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            // PIN Dots Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) NeonViolet else Color.White.copy(alpha = 0.2f))
                            .border(1.dp, if (isFilled) NeonViolet else Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = AccentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Kaody fototra: 1234",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }

            // 1-9 & 0 Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9")
                )

                for (row in rows) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        for (digit in row) {
                            KeypadButton(digit) { addDigit(digit) }
                        }
                    }
                }

                // Last row: Biometric / 0 / Backspace
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Biometric Unlock simulation
                    IconButton(
                        onClick = { onUnlockSuccess() },
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Dian-tanana",
                            tint = CyberCyan,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    KeypadButton("0") { addDigit("0") }

                    // Backspace
                    IconButton(
                        onClick = {
                            if (enteredPin.isNotEmpty()) {
                                enteredPin = enteredPin.dropLast(1)
                            }
                        },
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Fafao",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Return to Phone 1 button
            TextButton(
                onClick = onCancelToPhone1,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Miverina amin'ny Finday 1 (Fototra)",
                    color = CyberCyan,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
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
        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
