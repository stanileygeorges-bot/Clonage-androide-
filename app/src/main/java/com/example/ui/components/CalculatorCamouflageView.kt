package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CalculatorCamouflageView(
    onUnlock: () -> Unit,
    secretPin: String = "1234",
    modifier: Modifier = Modifier
) {
    var display by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf<String?>(null) }
    var previousValue by remember { mutableStateOf<Double?>(null) }
    var isNewEntry by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || isNewEntry) {
            display = d
            isNewEntry = false
        } else {
            display += d
        }
    }

    fun onOp(op: String) {
        previousValue = display.toDoubleOrNull()
        operation = op
        isNewEntry = true
    }

    fun onEquals() {
        // Secrecy Check: If typed secret PIN (or 1234), unlock Clonage!
        if (display == secretPin || display == "1234") {
            onUnlock()
            return
        }

        val p = previousValue
        val op = operation
        val current = display.toDoubleOrNull()
        if (p != null && op != null && current != null) {
            val result = when (op) {
                "+" -> p + current
                "-" -> p - current
                "×" -> p * current
                "÷" -> if (current != 0.0) p / current else 0.0
                else -> current
            }
            display = if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                "%.4f".format(result).trimEnd('0').trimEnd('.')
            }
            operation = null
            previousValue = null
            isNewEntry = true
        }
    }

    fun onClear() {
        display = "0"
        operation = null
        previousValue = null
        isNewEntry = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF17171C))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Stealth Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calculator",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )

            // Secret direct unlock button for convenience
            IconButton(onClick = onUnlock) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Unlock Clonage",
                    tint = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Calculation Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (operation != null && previousValue != null) {
                Text(
                    text = "${previousValue ?: ""} $operation",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 20.sp
                )
            }
            Text(
                text = display,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                textAlign = TextAlign.End
            )
            Text(
                text = "💡 Ampidiro '1234' dia tsindrio '=' raha hanokatra an'i Clonage",
                color = Color.White.copy(alpha = 0.25f),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Calculator Keypad
        val buttonSpacing = 12.dp
        Column(
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalcButton("C", Color(0xFFA5A5A5), Color.Black, Modifier.weight(1f)) { onClear() }
                CalcButton("±", Color(0xFFA5A5A5), Color.Black, Modifier.weight(1f)) {
                    display = if (display.startsWith("-")) display.substring(1) else "-$display"
                }
                CalcButton("%", Color(0xFFA5A5A5), Color.Black, Modifier.weight(1f)) {
                    val v = display.toDoubleOrNull() ?: 0.0
                    display = (v / 100).toString()
                }
                CalcButton("÷", Color(0xFFFF9F0A), Color.White, Modifier.weight(1f)) { onOp("÷") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalcButton("7", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("7") }
                CalcButton("8", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("8") }
                CalcButton("9", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("9") }
                CalcButton("×", Color(0xFFFF9F0A), Color.White, Modifier.weight(1f)) { onOp("×") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalcButton("4", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("4") }
                CalcButton("5", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("5") }
                CalcButton("6", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("6") }
                CalcButton("-", Color(0xFFFF9F0A), Color.White, Modifier.weight(1f)) { onOp("-") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalcButton("1", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("1") }
                CalcButton("2", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("2") }
                CalcButton("3", Color(0xFF333333), Color.White, Modifier.weight(1f)) { onDigit("3") }
                CalcButton("+", Color(0xFFFF9F0A), Color.White, Modifier.weight(1f)) { onOp("+") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalcButton("0", Color(0xFF333333), Color.White, Modifier.weight(2f)) { onDigit("0") }
                CalcButton(".", Color(0xFF333333), Color.White, Modifier.weight(1f)) {
                    if (!display.contains(".")) display += "."
                }
                CalcButton("=", Color(0xFFFF9F0A), Color.White, Modifier.weight(1f)) { onEquals() }
            }
        }
    }
}

@Composable
private fun CalcButton(
    text: String,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(bg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
