package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ClonageDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PhoneProfileEntity::class,
        FirewallAppEntity::class,
        DownloadedVideoEntity::class,
        ContactEntity::class,
        MessageEntity::class,
        CallRecordEntity::class,
        NoteEntity::class,
        GalleryPhotoEntity::class,
        ClonedAppEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ClonageDatabase : RoomDatabase() {

    abstract fun clonageDao(): ClonageDao

    companion object {
        @Volatile
        private var INSTANCE: ClonageDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ClonageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClonageDatabase::class.java,
                    "clonage_virtual_phones.db"
                ).fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.clonageDao())
                }
            }
        }
    }
}

suspend fun populateInitialData(dao: ClonageDao) {
    // 1. Phone Profiles for the 3 independent users A, B, C:
    val profileA = PhoneProfileEntity(
        phoneId = 1,
        name = "Finday A (Olona A)",
        ownerName = "Olona A",
        googleAccount = "olona.a@gmail.com",
        modelName = "Samsung Galaxy Note Pro",
        wallpaperType = "WP_CYAN",
        accentColorHex = "#06B6D4",
        pinCode = null,
        isLocked = false,
        batteryLevel = 94,
        wifiConnected = true,
        mobileDataConnected = true,
        soundMode = "NORMAL",
        storageUsedMb = 2300,
        storageTotalMb = 64000
    )

    val profileB = PhoneProfileEntity(
        phoneId = 2,
        name = "Finday B (Olona B)",
        ownerName = "Olona B",
        googleAccount = "olona.b@gmail.com",
        modelName = "Oppo N1",
        wallpaperType = "WP_NEON",
        accentColorHex = "#D946EF",
        pinCode = "1234",
        isLocked = false,
        batteryLevel = 87,
        wifiConnected = true,
        mobileDataConnected = true,
        soundMode = "VIBRATE",
        storageUsedMb = 850,
        storageTotalMb = 128000
    )

    val profileC = PhoneProfileEntity(
        phoneId = 3,
        name = "Finday C (Olona C)",
        ownerName = "Olona C",
        googleAccount = "olona.c@gmail.com",
        modelName = "Redmi Note",
        wallpaperType = "WP_DARK",
        accentColorHex = "#10B981",
        pinCode = null,
        isLocked = false,
        batteryLevel = 76,
        wifiConnected = false,
        mobileDataConnected = true,
        soundMode = "NORMAL",
        storageUsedMb = 512,
        storageTotalMb = 256000
    )

    dao.insertProfile(profileA)
    dao.insertProfile(profileB)
    dao.insertProfile(profileC)

    // 2. Initial Firewall Apps (Connected via Pare-feu)
    dao.insertFirewallApp(FirewallAppEntity(
        appName = "YouTube",
        packageName = "com.google.android.youtube",
        iconKey = "youtube",
        allowMobileData = true,
        allowWifi = true,
        dataUsageMb = 84.2f
    ))
    dao.insertFirewallApp(FirewallAppEntity(
        appName = "Navigateur Web",
        packageName = "com.android.chrome",
        iconKey = "browser",
        allowMobileData = true,
        allowWifi = true,
        dataUsageMb = 32.1f
    ))
    dao.insertFirewallApp(FirewallAppEntity(
        appName = "WhatsApp",
        packageName = "com.whatsapp",
        iconKey = "whatsapp",
        allowMobileData = true,
        allowWifi = true,
        dataUsageMb = 18.6f
    ))
    dao.insertFirewallApp(FirewallAppEntity(
        appName = "Telegram",
        packageName = "org.telegram.messenger",
        iconKey = "telegram",
        allowMobileData = true,
        allowWifi = true,
        dataUsageMb = 12.0f
    ))
    dao.insertFirewallApp(FirewallAppEntity(
        appName = "Facebook",
        packageName = "com.facebook.katana",
        iconKey = "facebook",
        allowMobileData = false, // demo of custom firewall policy
        allowWifi = true,
        dataUsageMb = 25.4f
    ))
    dao.insertFirewallApp(FirewallAppEntity(
        appName = "Gestionnaire de Téléchargement",
        packageName = "com.android.providers.downloads",
        iconKey = "downloads",
        allowMobileData = true,
        allowWifi = true,
        dataUsageMb = 145.0f
    ))

    // 3. Isolated Downloads for A only! (Proof of concept for user's scenario)
    // A downloaded a tech video: it exists ONLY on phone 1!
    dao.insertDownloadedVideo(DownloadedVideoEntity(
        phoneId = 1,
        videoId = "vid_a_1",
        title = "Tetikasa Android & Jetpack Compose 2026",
        channelName = "Tech Gasy Pro",
        fileSize = "48.2 MB",
        duration = "14:20",
        downloadedByAccount = "olona.a@gmail.com"
    ))
    dao.insertDownloadedVideo(DownloadedVideoEntity(
        phoneId = 1,
        videoId = "vid_a_2",
        title = "Hira Gasy Vaovao & Mozika Acoustic",
        channelName = "Mozika Malagasy",
        fileSize = "22.6 MB",
        duration = "04:15",
        downloadedByAccount = "olona.a@gmail.com"
    ))

    // B has its own different download:
    dao.insertDownloadedVideo(DownloadedVideoEntity(
        phoneId = 2,
        videoId = "vid_b_1",
        title = "Torolalana momba ny Crypto & Business",
        channelName = "Finances Malagasy",
        fileSize = "65.0 MB",
        duration = "22:10",
        downloadedByAccount = "olona.b@gmail.com"
    ))

    // Phone C has NO downloads yet: empty storage list! Exactly demonstrating independent storage.

    // 4. Contacts
    dao.insertContact(ContactEntity(phoneId = 1, name = "Neny (Maman)", phoneNumber = "+261 34 12 345 67", category = "Fianakaviana", colorIndex = 0))
    dao.insertContact(ContactEntity(phoneId = 1, name = "Andry Namana", phoneNumber = "+261 32 98 765 43", category = "Namana", colorIndex = 1))

    dao.insertContact(ContactEntity(phoneId = 2, name = "Client VIP - Entreprise", phoneNumber = "+261 34 00 111 22", category = "Business", colorIndex = 3))
    dao.insertContact(ContactEntity(phoneId = 2, name = "Agent Crypto / Finance", phoneNumber = "+261 32 44 888 99", category = "Crypto", colorIndex = 4))

    dao.insertContact(ContactEntity(phoneId = 3, name = "Mpiara-miasa C", phoneNumber = "+261 33 11 222 33", category = "Tetikasa", colorIndex = 2))

    // 5. Cloned Apps
    dao.insertClonedApp(ClonedAppEntity(phoneId = 1, appName = "YouTube A", packageName = "com.google.android.youtube", iconKey = "youtube", category = "Media", badgeCount = 1, isBuiltIn = true))
    dao.insertClonedApp(ClonedAppEntity(phoneId = 1, appName = "WhatsApp A", packageName = "com.whatsapp", iconKey = "whatsapp", category = "Social", badgeCount = 2, isBuiltIn = true))

    dao.insertClonedApp(ClonedAppEntity(phoneId = 2, appName = "YouTube B (Clone)", packageName = "com.google.android.youtube.clone", iconKey = "youtube", category = "Media", badgeCount = 0, isBuiltIn = true))
    dao.insertClonedApp(ClonedAppEntity(phoneId = 2, appName = "WhatsApp B (Clone)", packageName = "com.whatsapp.clone", iconKey = "whatsapp", category = "Social", badgeCount = 5, isBuiltIn = true))

    dao.insertClonedApp(ClonedAppEntity(phoneId = 3, appName = "YouTube C (Clone)", packageName = "com.google.android.youtube.clone3", iconKey = "youtube", category = "Media", badgeCount = 0, isBuiltIn = true))
}
