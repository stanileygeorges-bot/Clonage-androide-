package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone_profiles")
data class PhoneProfileEntity(
    @PrimaryKey val phoneId: Int, // 1 for Device A, 2 for Device B, 3 for Device C
    val name: String,
    val ownerName: String = "Olona",
    val googleAccount: String = "user@gmail.com",
    val modelName: String,
    val wallpaperType: String = "WP_CYAN",
    val accentColorHex: String = "#06B6D4",
    val pinCode: String? = null,
    val isLocked: Boolean = false,
    val batteryLevel: Int = 92,
    val wifiConnected: Boolean = true,
    val mobileDataConnected: Boolean = false,
    val soundMode: String = "NORMAL",
    val storageUsedMb: Int = 1240,
    val storageTotalMb: Int = 64000,
    val cpuLoad: Int = 0,
    val ramLoad: Int = 0,
    val isAiVideoWatchEnabled: Boolean = false,
    val isPaused: Boolean = false,
    val cloneStatus: String = "RUNNING" // "RUNNING", "PAUSED", "RESTARTING"
)

@Entity(tableName = "firewall_apps")
data class FirewallAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int = 0, // 0 for all devices or specific to device
    val appName: String,
    val packageName: String,
    val iconKey: String, // "youtube", "browser", "whatsapp", "telegram", "facebook", "downloads", "custom"
    val allowMobileData: Boolean = true,
    val allowWifi: Boolean = true,
    val dataUsageMb: Float = 14.5f,
    val isCustom: Boolean = false
)

@Entity(tableName = "downloaded_videos")
data class DownloadedVideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int, // 1 for A, 2 for B, 3 for C (Strictly isolated!)
    val videoId: String,
    val title: String,
    val channelName: String,
    val fileSize: String,
    val duration: String,
    val downloadDate: Long = System.currentTimeMillis(),
    val downloadedByAccount: String = "user@gmail.com"
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int,
    val name: String,
    val phoneNumber: String,
    val email: String = "",
    val category: String = "General",
    val colorIndex: Int = 0
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int,
    val contactName: String,
    val phoneNumber: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = true,
    val isRead: Boolean = true
)

@Entity(tableName = "call_records")
data class CallRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int,
    val contactName: String,
    val phoneNumber: String,
    val callType: String, // "INCOMING", "OUTGOING", "MISSED"
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int,
    val title: String,
    val content: String,
    val category: String = "Memos",
    val timestamp: Long = System.currentTimeMillis(),
    val colorIndex: Int = 0,
    val isSecret: Boolean = false
)

@Entity(tableName = "gallery_photos")
data class GalleryPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int,
    val title: String,
    val imagePathOrUri: String,
    val category: String = "Personal",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cloned_apps")
data class ClonedAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneId: Int,
    val appName: String,
    val packageName: String,
    val iconKey: String,
    val category: String = "Social",
    val customUrl: String? = null,
    val badgeCount: Int = 0,
    val isBuiltIn: Boolean = true
)

data class AiInspectionLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val appName: String,
    val status: String,
    val aiInsight: String,
    val bytesRead: String,
    val isSecure: Boolean = true
)
