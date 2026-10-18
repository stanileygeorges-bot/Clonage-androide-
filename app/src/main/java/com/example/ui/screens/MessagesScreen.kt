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
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.MessageEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(
    phoneId: Int,
    messages: List<MessageEntity>,
    onSendMessage: (contactName: String, number: String, text: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    var activeThreadContact by remember { mutableStateOf<String?>(null) }
    var activeThreadNumber by remember { mutableStateOf<String?>(null) }
    var showNewChatDialog by remember { mutableStateOf(false) }

    // Group messages by contact name or phone number
    val threads = remember(messages) {
        messages.groupBy { it.phoneNumber }.map { (number, msgList) ->
            val sorted = msgList.sortedByDescending { it.timestamp }
            val latest = sorted.first()
            ThreadSummary(
                contactName = latest.contactName,
                phoneNumber = number,
                lastMessage = latest.content,
                lastTimestamp = latest.timestamp,
                allMessages = msgList.sortedBy { it.timestamp }
            )
        }.sortedByDescending { it.lastTimestamp }
    }

    if (activeThreadNumber != null && activeThreadContact != null) {
        // Individual Conversation View
        val activeThreadMessages = messages.filter { it.phoneNumber == activeThreadNumber }.sortedBy { it.timestamp }
        ChatConversationView(
            contactName = activeThreadContact!!,
            phoneNumber = activeThreadNumber!!,
            messages = activeThreadMessages,
            accentColor = accentColor,
            onSendMessage = { text ->
                onSendMessage(activeThreadContact!!, activeThreadNumber!!, text)
            },
            onBack = {
                activeThreadNumber = null
                activeThreadContact = null
            }
        )
    } else {
        // Threads List
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = BackgroundDark,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showNewChatDialog = true },
                    containerColor = accentColor,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.AddComment,
                        contentDescription = "Hafatra vaovao"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top App Bar
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
                        text = if (phoneId == 1) "Hafatra (Finday 1)" else "Hafatra Miafina (Clonage 2)",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${threads.size} resaka",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

                if (threads.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tsy misy hafatra ato amin'ity finday ity",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Manomboka resaka vaovao amin'ny alalan'ny bokotra '+'",
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(threads) { thread ->
                            ThreadCard(
                                thread = thread,
                                dateFormat = dateFormat,
                                accentColor = accentColor,
                                onClick = {
                                    activeThreadContact = thread.contactName
                                    activeThreadNumber = thread.phoneNumber
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // New Chat Dialog
    if (showNewChatDialog) {
        var newContactName by remember { mutableStateOf("") }
        var newContactNumber by remember { mutableStateOf("") }
        var initialMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = {
                Text(
                    text = "Handefa hafatra vaovao",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Anaran'ny olona") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newContactNumber,
                        onValueChange = { newContactNumber = it },
                        label = { Text("Laharana finday") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = initialMessage,
                        onValueChange = { initialMessage = it },
                        label = { Text("Hafatra...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newContactName.isNotBlank() && initialMessage.isNotBlank()) {
                            val num = if (newContactNumber.isNotBlank()) newContactNumber else "+261 34 00 000 00"
                            onSendMessage(newContactName, num, initialMessage)
                            showNewChatDialog = false
                            activeThreadContact = newContactName
                            activeThreadNumber = num
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                ) {
                    Text("Alefaso")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text("Aoka ihany", color = TextSecondary)
                }
            },
            containerColor = CardSlate
        )
    }
}

private data class ThreadSummary(
    val contactName: String,
    val phoneNumber: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val allMessages: List<MessageEntity>
)

@Composable
private fun ThreadCard(
    thread: ThreadSummary,
    dateFormat: SimpleDateFormat,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Contact Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = thread.contactName.take(1).uppercase(),
                    color = accentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = thread.contactName,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormat.format(Date(thread.lastTimestamp)),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = thread.lastMessage,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ChatConversationView(
    contactName: String,
    phoneNumber: String,
    messages: List<MessageEntity>,
    accentColor: Color,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Conversation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSlate)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Miverina",
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contactName.take(1).uppercase(),
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contactName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = phoneNumber,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Messages Bubble List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.isFromMe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                )
                            )
                            .background(if (isMe) accentColor.copy(alpha = 0.85f) else CardSlate)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = msg.content,
                                color = if (isMe) Color.Black else Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = timeFormat.format(Date(msg.timestamp)),
                                color = if (isMe) Color.Black.copy(alpha = 0.6f) else TextSecondary,
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Message Composer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSlate)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Manorata hafatra...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp))
            )

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Handefa",
                    tint = Color.Black
                )
            }
        }
    }
}
