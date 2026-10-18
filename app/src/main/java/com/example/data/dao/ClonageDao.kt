package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClonageDao {

    // Phone Profiles
    @Query("SELECT * FROM phone_profiles WHERE phoneId = :phoneId")
    fun getProfileFlow(phoneId: Int): Flow<PhoneProfileEntity?>

    @Query("SELECT * FROM phone_profiles WHERE phoneId = :phoneId")
    suspend fun getProfile(phoneId: Int): PhoneProfileEntity?

    @Query("SELECT * FROM phone_profiles ORDER BY phoneId ASC")
    fun getAllProfilesFlow(): Flow<List<PhoneProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PhoneProfileEntity)

    @Update
    suspend fun updateProfile(profile: PhoneProfileEntity)

    @Query("DELETE FROM phone_profiles WHERE phoneId = :phoneId")
    suspend fun deleteProfileById(phoneId: Int)

    @Query("SELECT * FROM phone_profiles ORDER BY phoneId ASC")
    suspend fun getAllProfilesList(): List<PhoneProfileEntity>

    // Firewall Apps
    @Query("SELECT * FROM firewall_apps ORDER BY id ASC")
    fun getAllFirewallApps(): Flow<List<FirewallAppEntity>>

    @Query("SELECT * FROM firewall_apps ORDER BY id ASC")
    suspend fun getAllFirewallAppsList(): List<FirewallAppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFirewallApp(app: FirewallAppEntity): Long

    @Update
    suspend fun updateFirewallApp(app: FirewallAppEntity)

    @Delete
    suspend fun deleteFirewallApp(app: FirewallAppEntity)

    @Query("DELETE FROM firewall_apps WHERE phoneId = :phoneId")
    suspend fun deleteFirewallAppsByPhone(phoneId: Int)

    // Downloaded Videos / Documents per Phone
    @Query("SELECT * FROM downloaded_videos WHERE phoneId = :phoneId ORDER BY downloadDate DESC")
    fun getDownloadsForPhone(phoneId: Int): Flow<List<DownloadedVideoEntity>>

    @Query("SELECT COUNT(*) FROM downloaded_videos WHERE phoneId = :phoneId")
    fun getDownloadsCountForPhone(phoneId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedVideo(video: DownloadedVideoEntity): Long

    @Delete
    suspend fun deleteDownloadedVideo(video: DownloadedVideoEntity)

    @Query("DELETE FROM downloaded_videos WHERE phoneId = :phoneId")
    suspend fun clearDownloadsForPhone(phoneId: Int)

    // Contacts
    @Query("SELECT * FROM contacts WHERE phoneId = :phoneId ORDER BY name ASC")
    fun getContacts(phoneId: Int): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    // Messages
    @Query("SELECT * FROM messages WHERE phoneId = :phoneId ORDER BY timestamp DESC")
    fun getMessages(phoneId: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE phoneId = :phoneId AND phoneNumber = :phoneNumber ORDER BY timestamp ASC")
    fun getThreadMessages(phoneId: Int, phoneNumber: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    // Call Records
    @Query("SELECT * FROM call_records WHERE phoneId = :phoneId ORDER BY timestamp DESC")
    fun getCallRecords(phoneId: Int): Flow<List<CallRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRecord(callRecord: CallRecordEntity): Long

    @Query("DELETE FROM call_records WHERE phoneId = :phoneId")
    suspend fun clearCallLogs(phoneId: Int)

    // Notes
    @Query("SELECT * FROM notes WHERE phoneId = :phoneId ORDER BY timestamp DESC")
    fun getNotes(phoneId: Int): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // Gallery Photos
    @Query("SELECT * FROM gallery_photos WHERE phoneId = :phoneId ORDER BY timestamp DESC")
    fun getGalleryPhotos(phoneId: Int): Flow<List<GalleryPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryPhoto(photo: GalleryPhotoEntity): Long

    @Delete
    suspend fun deleteGalleryPhoto(photo: GalleryPhotoEntity)

    // Cloned Apps
    @Query("SELECT * FROM cloned_apps WHERE phoneId = :phoneId")
    fun getClonedApps(phoneId: Int): Flow<List<ClonedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClonedApp(app: ClonedAppEntity): Long

    @Delete
    suspend fun deleteClonedApp(app: ClonedAppEntity)

    // Wipe specific phone space data
    @Query("DELETE FROM contacts WHERE phoneId = :phoneId")
    suspend fun deleteContactsByPhone(phoneId: Int)

    @Query("DELETE FROM messages WHERE phoneId = :phoneId")
    suspend fun deleteMessagesByPhone(phoneId: Int)

    @Query("DELETE FROM notes WHERE phoneId = :phoneId")
    suspend fun deleteNotesByPhone(phoneId: Int)

    @Query("DELETE FROM gallery_photos WHERE phoneId = :phoneId")
    suspend fun deletePhotosByPhone(phoneId: Int)

    @Query("DELETE FROM cloned_apps WHERE phoneId = :phoneId AND isBuiltIn = 0")
    suspend fun deleteCustomAppsByPhone(phoneId: Int)
}
