package com.example.data.repository

import com.example.data.dao.ClonageDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class ClonageRepository(private val dao: ClonageDao) {

    fun getProfileFlow(phoneId: Int): Flow<PhoneProfileEntity?> = dao.getProfileFlow(phoneId)

    suspend fun getProfile(phoneId: Int): PhoneProfileEntity? = dao.getProfile(phoneId)

    fun getAllProfilesFlow(): Flow<List<PhoneProfileEntity>> = dao.getAllProfilesFlow()

    suspend fun updateProfile(profile: PhoneProfileEntity) = dao.updateProfile(profile)

    suspend fun saveProfile(profile: PhoneProfileEntity) = dao.insertProfile(profile)

    suspend fun deleteProfileById(phoneId: Int) {
        dao.deleteProfileById(phoneId)
        dao.deleteFirewallAppsByPhone(phoneId)
        wipePhoneSpace(phoneId)
    }

    suspend fun getAllProfilesList(): List<PhoneProfileEntity> = dao.getAllProfilesList()

    // Firewall Apps
    fun getAllFirewallApps(): Flow<List<FirewallAppEntity>> = dao.getAllFirewallApps()

    suspend fun getAllFirewallAppsList(): List<FirewallAppEntity> = dao.getAllFirewallAppsList()

    suspend fun addFirewallApp(app: FirewallAppEntity) = dao.insertFirewallApp(app)

    suspend fun updateFirewallApp(app: FirewallAppEntity) = dao.updateFirewallApp(app)

    suspend fun deleteFirewallApp(app: FirewallAppEntity) = dao.deleteFirewallApp(app)

    suspend fun deleteFirewallAppsByPhone(phoneId: Int) = dao.deleteFirewallAppsByPhone(phoneId)

    // Downloads per phone (strictly isolated storage!)
    fun getDownloadsForPhone(phoneId: Int): Flow<List<DownloadedVideoEntity>> =
        dao.getDownloadsForPhone(phoneId)

    fun getDownloadsCountForPhone(phoneId: Int): Flow<Int> =
        dao.getDownloadsCountForPhone(phoneId)

    suspend fun addDownloadedVideo(video: DownloadedVideoEntity) = dao.insertDownloadedVideo(video)

    suspend fun deleteDownloadedVideo(video: DownloadedVideoEntity) = dao.deleteDownloadedVideo(video)

    suspend fun clearDownloadsForPhone(phoneId: Int) = dao.clearDownloadsForPhone(phoneId)

    // Contacts
    fun getContacts(phoneId: Int): Flow<List<ContactEntity>> = dao.getContacts(phoneId)

    suspend fun addContact(contact: ContactEntity) = dao.insertContact(contact)

    suspend fun deleteContact(contact: ContactEntity) = dao.deleteContact(contact)

    // Messages
    fun getMessages(phoneId: Int): Flow<List<MessageEntity>> = dao.getMessages(phoneId)

    fun getThreadMessages(phoneId: Int, phoneNumber: String): Flow<List<MessageEntity>> =
        dao.getThreadMessages(phoneId, phoneNumber)

    suspend fun sendMessage(message: MessageEntity) = dao.insertMessage(message)

    suspend fun deleteMessage(message: MessageEntity) = dao.deleteMessage(message)

    // Calls
    fun getCallRecords(phoneId: Int): Flow<List<CallRecordEntity>> = dao.getCallRecords(phoneId)

    suspend fun logCall(callRecord: CallRecordEntity) = dao.insertCallRecord(callRecord)

    suspend fun clearCallLogs(phoneId: Int) = dao.clearCallLogs(phoneId)

    // Notes
    fun getNotes(phoneId: Int): Flow<List<NoteEntity>> = dao.getNotes(phoneId)

    suspend fun saveNote(note: NoteEntity) = dao.insertNote(note)

    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    // Gallery
    fun getGalleryPhotos(phoneId: Int): Flow<List<GalleryPhotoEntity>> = dao.getGalleryPhotos(phoneId)

    suspend fun addGalleryPhoto(photo: GalleryPhotoEntity) = dao.insertGalleryPhoto(photo)

    suspend fun deleteGalleryPhoto(photo: GalleryPhotoEntity) = dao.deleteGalleryPhoto(photo)

    // Cloned Apps
    fun getClonedApps(phoneId: Int): Flow<List<ClonedAppEntity>> = dao.getClonedApps(phoneId)

    suspend fun addClonedApp(app: ClonedAppEntity) = dao.insertClonedApp(app)

    suspend fun deleteClonedApp(app: ClonedAppEntity) = dao.deleteClonedApp(app)

    // Wipe Phone Space
    suspend fun wipePhoneSpace(phoneId: Int) {
        dao.deleteContactsByPhone(phoneId)
        dao.deleteMessagesByPhone(phoneId)
        dao.deleteNotesByPhone(phoneId)
        dao.deletePhotosByPhone(phoneId)
        dao.deleteCustomAppsByPhone(phoneId)
        dao.clearCallLogs(phoneId)
        dao.clearDownloadsForPhone(phoneId)
    }
}
