package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ClonageDatabase
import com.example.data.database.populateInitialData
import com.example.data.model.*
import com.example.data.repository.ClonageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class VirtualScreen {
    HOME,
    PARE_FEU_DASHBOARD,
    YOUTUBE,
    STORAGE_DOWNLOADS,
    DIALER,
    MESSAGES,
    CHAT_THREAD,
    CONTACTS,
    NOTES,
    GALLERY,
    BROWSER,
    CLONED_APPS,
    SETTINGS,
    DEVICE_COMPARATOR
}

data class ActiveCall(
    val contactName: String,
    val phoneNumber: String,
    val phoneId: Int,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeaker: Boolean = false
)

data class UiNotification(
    val id: Long = System.currentTimeMillis(),
    val sourcePhoneId: Int,
    val targetPhoneId: Int,
    val title: String,
    val message: String
)

data class YouTubeVideoItem(
    val id: String,
    val title: String,
    val channel: String,
    val views: String,
    val duration: String,
    val sizeMb: String,
    val category: String,
    val description: String
)

class ClonageViewModel(application: Application) : AndroidViewModel(application) {

    // 0. Physical Host Device Real Internet Connectivity
    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    fun checkRealDeviceInternet(): Boolean {
        val cm = connectivityManager ?: return true
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun detectRealNetworkType(): String {
        val cm = connectivityManager ?: return "Internet Mavitrika"
        val activeNetwork = cm.activeNetwork ?: return "Tsy misy Tambazotra"
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return "Tsy fantatra"
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi (Hafainganam-pandeha Ambony)"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Données Mobiles 4G/5G"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Tambazotra Mavitrika"
        }
    }

    private val _isHostInternetAvailable = MutableStateFlow(checkRealDeviceInternet())
    val isHostInternetAvailable: StateFlow<Boolean> = _isHostInternetAvailable.asStateFlow()

    private val _hostNetworkType = MutableStateFlow(detectRealNetworkType())
    val hostNetworkType: StateFlow<String> = _hostNetworkType.asStateFlow()

    private val repository: ClonageRepository

    init {
        val database = ClonageDatabase.getDatabase(application, viewModelScope)
        repository = ClonageRepository(database.clonageDao())

        viewModelScope.launch {
            val p1 = repository.getProfile(1)
            if (p1 == null) {
                populateInitialData(database.clonageDao())
            }
        }

        // Background AI reading loop for active apps:
        viewModelScope.launch {
            val insights = listOf(
                "IA Mpamaky: Mamaky ny fonosana data.",
                "IA Fiarovana: Sandbox mahaleo tena.",
                "IA Réseau: Protocole TLS 1.3.",
                "IA Optimisation: Mandeha tsara.",
                "IA Mpandinika: 320 Ko novakiana."
            )
            var index = 0
            while (true) {
                delay(3000) // Update simulation more frequently

                // Update resource simulation for all profiles
                allProfiles.value.forEach { profile ->
                    val newCpu = (10..95).random()
                    val newRam = (20..90).random()
                    repository.updateProfile(profile.copy(cpuLoad = newCpu, ramLoad = newRam))
                }

                if (_isGlobalMobileDataConnected.value) {
                    val appNames = listOf("YouTube", "WhatsApp", "Chrome", "Telegram", "Facebook", "TikTok")
                    val chosenApp = appNames.random()
                    val bytes = (80..520).random()
                    val log = AiInspectionLog(
                        appName = chosenApp,
                        status = "MAMAKY...",
                        aiInsight = "IA Mpamaky ($chosenApp): ${insights[index % insights.size]}",
                        bytesRead = "$bytes Ko",
                        isSecure = true
                    )
                    index++
                    _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
                }
            }
        }

        // Live host physical network callback registration
        try {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager?.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isHostInternetAvailable.value = true
                    _hostNetworkType.value = detectRealNetworkType()
                }

                override fun onLost(network: Network) {
                    _isHostInternetAvailable.value = checkRealDeviceInternet()
                    _hostNetworkType.value = detectRealNetworkType()
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    _isHostInternetAvailable.value = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    _hostNetworkType.value = detectRealNetworkType()
                }
            })
        } catch (_: Exception) {
            // fallback if network callback not permitted
        }
    }

    // 1. Pare-feu Gateway state (Initial screen: Démarrer, Annuler, Continuer)
    private val _isFirewallGateActive = MutableStateFlow(true)
    val isFirewallGateActive: StateFlow<Boolean> = _isFirewallGateActive.asStateFlow()

    // 2. Global Mobile Data status for the Pare-feu:
    // User requested: "eny aminy farany ambony eny kosa misy sary donne mobile ngezabe miloko mena rehefa déconnecter
    // fa rehefa tsindrina ilay donne mobile ngezabe indray de lasa connecté ilay donne mobile ary miova maintso ny lokony"
    private val _isGlobalMobileDataConnected = MutableStateFlow(false) // default disconnected (RED)
    val isGlobalMobileDataConnected: StateFlow<Boolean> = _isGlobalMobileDataConnected.asStateFlow()

    // Global Wi-Fi status for Pare-feu status bar
    private val _isGlobalWifiConnected = MutableStateFlow(true) // default connected
    val isGlobalWifiConnected: StateFlow<Boolean> = _isGlobalWifiConnected.asStateFlow()

    // Active phone profile: 1 (A), 2 (B), 3 (C)
    private val _activePhoneId = MutableStateFlow(1)
    val activePhoneId: StateFlow<Int> = _activePhoneId.asStateFlow()

    // Current virtual screens per device
    private val _phone1Screen = MutableStateFlow(VirtualScreen.HOME)
    val phone1Screen: StateFlow<VirtualScreen> = _phone1Screen.asStateFlow()

    private val _phone2Screen = MutableStateFlow(VirtualScreen.HOME)
    val phone2Screen: StateFlow<VirtualScreen> = _phone2Screen.asStateFlow()

    private val _phone3Screen = MutableStateFlow(VirtualScreen.HOME)
    val phone3Screen: StateFlow<VirtualScreen> = _phone3Screen.asStateFlow()

    // Split / comparator view
    private val _isSplitView = MutableStateFlow(false)
    val isSplitView: StateFlow<Boolean> = _isSplitView.asStateFlow()

    // Stealth Calculator mode
    private val _isCamouflageActive = MutableStateFlow(false)
    val isCamouflageActive: StateFlow<Boolean> = _isCamouflageActive.asStateFlow()

    // Active Call
    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // Currently selected Cloned Application to open in full screen
    private val _selectedFirewallApp = MutableStateFlow<FirewallAppEntity?>(null)
    val selectedFirewallApp: StateFlow<FirewallAppEntity?> = _selectedFirewallApp.asStateFlow()

    // Last opened app to resume when CONTINUER is clicked:
    // "De rehefa miditra ao aminy continue avy dia mitohy ny zavatra natao rehetra"
    private val _lastOpenedFirewallApp = MutableStateFlow<FirewallAppEntity?>(null)
    val lastOpenedFirewallApp: StateFlow<FirewallAppEntity?> = _lastOpenedFirewallApp.asStateFlow()

    fun selectFirewallApp(app: FirewallAppEntity?) {
        _selectedFirewallApp.value = app
        if (app != null) {
            _lastOpenedFirewallApp.value = app
        }
    }

    // New Independent Android Device Window:
    // "Ary eny ambony à gauche eny kosa misy otrany plus de rehefa tsindrina ilay plus de mamoaka fenêtre vaovao le izy
    // ary mampisafidy app izay apetaka amin'ilay apareil vaovao na Android izy izay mahaleo tena izay tsy mifandray amin'ny ilay voalohany fa mahaleo tena tanteraka
    // ary azo atao ihany koa ny mametraka app maro be ao anatiny apareil Android anakiray"
    private val _isNewDeviceWindowOpen = MutableStateFlow(false)
    val isNewDeviceWindowOpen: StateFlow<Boolean> = _isNewDeviceWindowOpen.asStateFlow()

    fun openNewDeviceWindow() {
        _isNewDeviceWindowOpen.value = true
    }

    fun closeNewDeviceWindow() {
        _isNewDeviceWindowOpen.value = false
    }

    // AI Inspector / IA Mamaky ny fampiharana:
    // "ary avy dia miasa daholo rehefa tsindrina ilay donne mobile misy anilay app napetaka ary IA no mamaky azy fa azo atao ihany koa ny miditra ao anatiny ilay app rehefa mandeha izy"
    private val _aiInspectionLogs = MutableStateFlow<List<AiInspectionLog>>(
        listOf(
            AiInspectionLog(
                appName = "Système & Clonage",
                status = "MAMAKY...",
                aiInsight = "IA Mpamaky: Fiarovana vonona. Ireo fitaovana mahaleo tena dia tsy mifandray amin'ny voalohany.",
                bytesRead = "140 Ko",
                isSecure = true
            )
        )
    )
    val aiInspectionLogs: StateFlow<List<AiInspectionLog>> = _aiInspectionLogs.asStateFlow()

    private val _isAiAnalysisRunning = MutableStateFlow(false)
    val isAiAnalysisRunning: StateFlow<Boolean> = _isAiAnalysisRunning.asStateFlow()

    // Batch Management state
    private val _isBatchRestarting = MutableStateFlow(false)
    val isBatchRestarting: StateFlow<Boolean> = _isBatchRestarting.asStateFlow()

    // "The Book and the Readers" Analogy modal state
    private val _isBookAnalogyOpen = MutableStateFlow(false)
    val isBookAnalogyOpen: StateFlow<Boolean> = _isBookAnalogyOpen.asStateFlow()

    // Chrome / Browser sandbox launcher
    private val _activeBrowserUrl = MutableStateFlow<String?>(null)
    val activeBrowserUrl: StateFlow<String?> = _activeBrowserUrl.asStateFlow()

    private val _activeBrowserAppName = MutableStateFlow<String?>(null)
    val activeBrowserAppName: StateFlow<String?> = _activeBrowserAppName.asStateFlow()

    // AI App Discovery & Search modal state
    private val _isAiAppDiscoveryOpen = MutableStateFlow(false)
    val isAiAppDiscoveryOpen: StateFlow<Boolean> = _isAiAppDiscoveryOpen.asStateFlow()

    // Language setting
    private val _language = MutableStateFlow("MG")
    val language: StateFlow<String> = _language.asStateFlow()

    // Realistic Physical Smartphone Simulator Mode:
    // "Ataovy tena vrai vrai appareil mihintsy ny appareil ato anatiny clonage fa"
    private val _inspectedRealDeviceId = MutableStateFlow<Int?>(null)
    val inspectedRealDeviceId: StateFlow<Int?> = _inspectedRealDeviceId.asStateFlow()

    fun openRealisticDevice(phoneId: Int) {
        _activePhoneId.value = phoneId
        _inspectedRealDeviceId.value = phoneId
    }

    fun closeRealisticDevice() {
        _inspectedRealDeviceId.value = null
    }

    fun getCallLogsFlow(phoneId: Int): kotlinx.coroutines.flow.Flow<List<CallRecordEntity>> = repository.getCallRecords(phoneId)
    fun getMessagesFlow(phoneId: Int): kotlinx.coroutines.flow.Flow<List<MessageEntity>> = repository.getMessages(phoneId)
    fun getNotesFlow(phoneId: Int): kotlinx.coroutines.flow.Flow<List<NoteEntity>> = repository.getNotes(phoneId)
    fun getContactsFlow(phoneId: Int): kotlinx.coroutines.flow.Flow<List<ContactEntity>> = repository.getContacts(phoneId)
    fun getDownloadsFlow(phoneId: Int): kotlinx.coroutines.flow.Flow<List<DownloadedVideoEntity>> = repository.getDownloadsForPhone(phoneId)
    fun getGalleryFlow(phoneId: Int): kotlinx.coroutines.flow.Flow<List<GalleryPhotoEntity>> = repository.getGalleryPhotos(phoneId)

    fun updateProfileSettings(phoneId: Int, name: String, model: String, wallpaper: String) {
        viewModelScope.launch {
            val p = repository.getProfile(phoneId)
            if (p != null) {
                repository.updateProfile(p.copy(name = name, modelName = model, wallpaperType = wallpaper))
            }
        }
    }

    // Notifications
    private val _currentNotification = MutableStateFlow<UiNotification?>(null)
    val currentNotification: StateFlow<UiNotification?> = _currentNotification.asStateFlow()

    // Currently playing YouTube video per device
    private val _currentPlayingVideo = MutableStateFlow<YouTubeVideoItem?>(null)
    val currentPlayingVideo: StateFlow<YouTubeVideoItem?> = _currentPlayingVideo.asStateFlow()

    // All Profiles
    val allProfiles = repository.getAllProfilesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile1 = repository.getProfileFlow(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val profile2 = repository.getProfileFlow(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val profile3 = repository.getProfileFlow(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Firewall Apps Flow
    val firewallApps = repository.getAllFirewallApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Downloads Flow per device (Phone 1, 2, 3)
    val downloadsPhone1 = repository.getDownloadsForPhone(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadsPhone2 = repository.getDownloadsForPhone(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadsPhone3 = repository.getDownloadsForPhone(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Contacts
    val contactsPhone1 = repository.getContacts(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contactsPhone2 = repository.getContacts(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Messages
    val messagesPhone1 = repository.getMessages(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messagesPhone2 = repository.getMessages(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Call logs
    val callLogsPhone1 = repository.getCallRecords(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callLogsPhone2 = repository.getCallRecords(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notes
    val notesPhone1 = repository.getNotes(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notesPhone2 = repository.getNotes(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gallery
    val galleryPhone1 = repository.getGalleryPhotos(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val galleryPhone2 = repository.getGalleryPhotos(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cloned Apps
    val clonedAppsPhone1 = repository.getClonedApps(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clonedAppsPhone2 = repository.getClonedApps(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- PARE-FEU ACTIONS ---
    fun onFirewallDemarrer() {
        // "rehefa manindry anilay démarre de fenêtre vaovao misokatra de eo aminy izay mbola mitovy aminy Pare-feu ihany ny endriny"
        _isFirewallGateActive.value = false
        _selectedFirewallApp.value = null
        navigateTo(VirtualScreen.PARE_FEU_DASHBOARD, _activePhoneId.value)
    }

    fun onFirewallAnnuler() {
        _isGlobalMobileDataConnected.value = false
        _currentNotification.value = UiNotification(
            sourcePhoneId = 0,
            targetPhoneId = _activePhoneId.value,
            title = "Firewall Annulé",
            message = "Najanona ny firewall ary tapaka ny données mobiles."
        )
    }

    fun onFirewallContinuer() {
        _isFirewallGateActive.value = false
        _selectedFirewallApp.value = null
        navigateTo(VirtualScreen.PARE_FEU_DASHBOARD, _activePhoneId.value)
    }

    fun showFirewallGate() {
        _isFirewallGateActive.value = true
        _selectedFirewallApp.value = null
    }

    // Toggle the BIG Mobile Data button at the very top:
    // "Rehefa tsindrina ilay sary donne mobile ngezabe io de avy dia mandeha daholo ny connexion ny appareil rehetra
    // fa azo atao tsara anefa manazanona ny afa aminy alalany donne mobile manokana eo aminy de otrany izay ihany koa ny WiFi"
    fun toggleGlobalMobileData() {
        val newState = !_isGlobalMobileDataConnected.value
        _isGlobalMobileDataConnected.value = newState

        _currentNotification.value = UiNotification(
            sourcePhoneId = 0,
            targetPhoneId = _activePhoneId.value,
            title = if (newState) "Données mobiles : CONNECTÉ (Maintso)" else "Données mobiles : DÉCONNECTÉ (Mena)",
            message = if (newState) "Mandeha daholo ny connexion ny appareil rehetra!" else "Najanona ny données mobiles ho an'ny fitaovana rehetra."
        )

        viewModelScope.launch {
            val all = repository.getAllProfilesList()
            all.forEach { p ->
                repository.updateProfile(p.copy(mobileDataConnected = newState))
            }
            firewallApps.value.forEach { app ->
                repository.updateFirewallApp(app.copy(allowMobileData = newState))
            }
            val log = AiInspectionLog(
                appName = "Données Mobiles Global",
                status = if (newState) "MANDEHA DAHOLO" else "NAJANONA DAHOLO",
                aiInsight = if (newState) "Nampidirina avokoa ny fifandraisana données mobiles amin'ny fitaovana rehetra." else "Najanona ny données mobiles rehetra.",
                bytesRead = if (newState) "256 Ko/s" else "0 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }

    // Toggle global Wi-Fi status in Clonage ("de otrany izay ihany koa ny WiFi")
    fun toggleGlobalWifi() {
        val newState = !_isGlobalWifiConnected.value
        _isGlobalWifiConnected.value = newState

        _currentNotification.value = UiNotification(
            sourcePhoneId = 0,
            targetPhoneId = _activePhoneId.value,
            title = if (newState) "Wi-Fi : CONNECTÉ (Maintso)" else "Wi-Fi : DÉSACTIVÉ (Mena)",
            message = if (newState) "Mandeha daholo ny Wi-Fi amin'ny appareil rehetra!" else "Novonoina ny Wi-Fi ho an'ny fitaovana rehetra."
        )

        viewModelScope.launch {
            val all = repository.getAllProfilesList()
            all.forEach { p ->
                repository.updateProfile(p.copy(wifiConnected = newState))
            }
            firewallApps.value.forEach { app ->
                repository.updateFirewallApp(app.copy(allowWifi = newState))
            }
            val log = AiInspectionLog(
                appName = "Wi-Fi Global",
                status = if (newState) "MANDEHA DAHOLO" else "NAJANONA DAHOLO",
                aiInsight = if (newState) "Nampidirina avokoa ny Wi-Fi amin'ny fitaovana rehetra." else "Novonoina ny Wi-Fi rehetra.",
                bytesRead = if (newState) "512 Ko/s" else "0 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }

    // Toggle individual device mobile data:
    // "fa azo atao tsara anefa manazanona ny afa aminy alalany donne mobile manokana eo aminy"
    fun toggleDeviceMobileData(phoneId: Int) {
        viewModelScope.launch {
            val p = repository.getProfile(phoneId) ?: return@launch
            val newMobileData = !p.mobileDataConnected
            repository.updateProfile(p.copy(mobileDataConnected = newMobileData))
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = if (newMobileData) "🟢 Donnée Mobile Mandeha (#$phoneId)" else "🔴 Donnée Mobile Najanona (#$phoneId)",
                message = if (newMobileData) "Mandeha ny données mobiles ho an'i ${p.name}." else "Najanona ny données mobiles ho an'i ${p.name} (mitohy ny an'ny hafa)."
            )
        }
    }

    // Toggle individual device Wi-Fi:
    // "de otrany izay ihany koa ny WiFi"
    fun toggleDeviceWifi(phoneId: Int) {
        viewModelScope.launch {
            val p = repository.getProfile(phoneId) ?: return@launch
            val newWifi = !p.wifiConnected
            repository.updateProfile(p.copy(wifiConnected = newWifi))
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = if (newWifi) "🟢 Wi-Fi Mandeha (#$phoneId)" else "🔴 Wi-Fi Najanona (#$phoneId)",
                message = if (newWifi) "Mandeha ny Wi-Fi ho an'i ${p.name}." else "Najanona ny Wi-Fi ho an'i ${p.name} (mitohy ny an'ny hafa)."
            )
        }
    }

    fun toggleAllAppsMobileData(enable: Boolean) {
        viewModelScope.launch {
            firewallApps.value.forEach { app ->
                repository.updateFirewallApp(app.copy(allowMobileData = enable))
            }
        }
    }

    fun toggleAllAppsWifi(enable: Boolean) {
        viewModelScope.launch {
            firewallApps.value.forEach { app ->
                repository.updateFirewallApp(app.copy(allowWifi = enable))
            }
        }
    }

    // Toggle Mobile Data per app (far right)
    fun toggleAppMobileData(app: FirewallAppEntity) {
        viewModelScope.launch {
            repository.updateFirewallApp(app.copy(allowMobileData = !app.allowMobileData))
        }
    }

    // Toggle WiFi per app (far right)
    fun toggleAppWifi(app: FirewallAppEntity) {
        viewModelScope.launch {
            repository.updateFirewallApp(app.copy(allowWifi = !app.allowWifi))
        }
    }

    // Add a new app to the device
    fun addFirewallApp(name: String, iconKey: String) {
        viewModelScope.launch {
            repository.addFirewallApp(
                FirewallAppEntity(
                    phoneId = _activePhoneId.value,
                    appName = name,
                    packageName = "com.custom.${name.lowercase().replace(" ", "")}",
                    iconKey = iconKey,
                    allowMobileData = true,
                    allowWifi = true,
                    dataUsageMb = 0.5f,
                    isCustom = true
                )
            )
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "App Tafapetraka",
                message = "Nampidirina tao amin'ity apareil ity ny $name."
            )
        }
    }

    // Retirer / Delete an already placed cloned app
    fun deleteFirewallApp(app: FirewallAppEntity) {
        viewModelScope.launch {
            repository.deleteFirewallApp(app)
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = app.phoneId,
                title = "Fampiharana Nesorina",
                message = "Nesorina (retiré) soa aman-tsara ny fampiharana ${app.appName}."
            )
        }
    }

    // User request: "Azo atao tsara ihany koa ny manova anireo App mipetaka aminy appareil rehetra
    // raha tsindrina ela be ilay izy de misy soratra hoe suprimer sy remplacer ary singer
    // raha hoatra ka suprimer izy de ny appareil mihintsy no suprimer raha remplacer izy de ny application fotsiny no ovaina"
    fun replaceAppOnDevice(phoneId: Int, newAppName: String, newIconKey: String, newPackageName: String) {
        viewModelScope.launch {
            val allApps = repository.getAllFirewallAppsList()
            val existingApp = allApps.firstOrNull { it.phoneId == phoneId || (phoneId == 1 && it.phoneId == 0) }
            if (existingApp != null) {
                repository.updateFirewallApp(
                    existingApp.copy(
                        appName = newAppName,
                        iconKey = newIconKey,
                        packageName = newPackageName
                    )
                )
            } else {
                repository.addFirewallApp(
                    FirewallAppEntity(
                        phoneId = phoneId,
                        appName = newAppName,
                        packageName = newPackageName,
                        iconKey = newIconKey,
                        allowMobileData = true,
                        allowWifi = true,
                        dataUsageMb = (5..45).random().toFloat()
                    )
                )
            }
            val profile = repository.getProfile(phoneId)
            val devName = profile?.name ?: if (phoneId == 1) "Finday Voalohany" else "Apareil #$phoneId"
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = "Application Nosoloina (Remplacé)",
                message = "Nosoloina ho $newAppName ny application eo amin'i $devName (ny appareil kosa dia tsy niova)."
            )
            val log = AiInspectionLog(
                appName = newAppName,
                status = "REMPLACÉ & MIASA",
                aiInsight = "Nosoloina soa aman-tsara ho $newAppName ny fampiharana eo amin'i $devName. Ny fitaovana sy ny firafiny dia tsy niova.",
                bytesRead = "42 Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }


    // Remove / Delete a virtual Android clone instance
    fun deleteIndependentDevice(phoneId: Int) {
        if (phoneId <= 1) return // Do not delete primary phone 1
        viewModelScope.launch {
            repository.deleteProfileById(phoneId)
            if (_activePhoneId.value == phoneId) {
                _activePhoneId.value = 1
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = 1,
                title = "Apareil Nofafana",
                message = "Nofafana soa aman-tsara ny Apareil Android #$phoneId sy ireo angona rehetra tao aminy."
            )
        }
    }

    // Remove / Delete multiple virtual Android clone instances
    fun deleteMultipleIndependentDevices(phoneIds: Collection<Int>) {
        val targets = phoneIds.filter { it > 1 }
        if (targets.isEmpty()) return
        viewModelScope.launch {
            targets.forEach { id ->
                repository.deleteProfileById(id)
            }
            if (targets.contains(_activePhoneId.value)) {
                _activePhoneId.value = 1
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = 1,
                title = "Apareil Nofafana (${targets.size})",
                message = "Nesorina (retirer) soa aman-tsara ireo Apareil virtoaly miisa ${targets.size}."
            )
            val log = AiInspectionLog(
                appName = "Fitantanana Apareil",
                status = "NESORINA DAHOLO",
                aiInsight = "Nesorina avokoa ireo fitaovana virtoaly ${targets.size} nofidian'ny mpampiasa.",
                bytesRead = "0 Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }

    // Stop/cut off ALL connections (Manajanona ny connexion rehetra)
    fun disconnectAllConnections() {
        viewModelScope.launch {
            _isGlobalMobileDataConnected.value = false
            _isGlobalWifiConnected.value = false
            val profiles = repository.getAllProfilesList()
            profiles.forEach { p ->
                repository.updateProfile(p.copy(mobileDataConnected = false, wifiConnected = false, isPaused = true))
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "🔴 Najanona ny Fifandraisana Rehetra",
                message = "Tapaka avokoa ny Données Mobiles sy Wi-Fi ho an'ny fitaovana rehetra."
            )
            val log = AiInspectionLog(
                appName = "Coupe-Circuit Pare-Feu",
                status = "DÉCONNECTÉ DAHOLO",
                aiInsight = "Najanona avokoa ny fifandraisana Internet ho an'ny fitaovana rehetra ho fiarovana tanteraka.",
                bytesRead = "0 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }

    // Reconnect ALL connections (Mampifandray daholo)
    fun connectAllConnections() {
        viewModelScope.launch {
            _isGlobalMobileDataConnected.value = true
            _isGlobalWifiConnected.value = true
            val profiles = repository.getAllProfilesList()
            profiles.forEach { p ->
                repository.updateProfile(p.copy(mobileDataConnected = true, wifiConnected = true, isPaused = false))
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "🟢 Mampifandray Daholo",
                message = "Mandeha avokoa ny fifandraisana ho an'ny fitaovana rehetra."
            )
            val log = AiInspectionLog(
                appName = "Coupe-Circuit Pare-Feu",
                status = "CONNECTÉ DAHOLO",
                aiInsight = "Nampifandraisina indray ny fitaovana rehetra amin'ny tambazotra.",
                bytesRead = "128 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }

    // Toggle connection for an individual device (sy ny tsirairay)
    fun toggleSingleDeviceConnection(phoneId: Int) {
        viewModelScope.launch {
            val p = repository.getProfile(phoneId) ?: return@launch
            val newConnected = !p.mobileDataConnected
            repository.updateProfile(p.copy(mobileDataConnected = newConnected, wifiConnected = newConnected))
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = if (newConnected) "🟢 Mifandray ny Apareil #$phoneId" else "🔴 Najanona ny Apareil #$phoneId",
                message = if (newConnected) "Mandeha ny fifandraisana ho an'i ${p.name}." else "Najanona ny fifandraisana ho an'i ${p.name}."
            )
        }
    }

    // --- BATCH MANAGEMENT CONTROLS FOR ACTIVE ANDROID CLONES ---
    fun pauseAllClones() {
        viewModelScope.launch {
            val profiles = repository.getAllProfilesList()
            profiles.forEach { p ->
                repository.updateProfile(
                    p.copy(
                        isPaused = true,
                        cloneStatus = "PAUSED",
                        cpuLoad = (1..5).random(),
                        ramLoad = 12
                    )
                )
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "⏸ Naato Daholo ny Clones (Pause All)",
                message = "Naato avokoa ny processeur sy ny fampandehanana ny clones Android miisa ${profiles.size}."
            )
            val log = AiInspectionLog(
                appName = "Batch Control",
                status = "PAUSE DAHOLO",
                aiInsight = "Mpiandraikitra Batch: Naato ny fampandehanana ho an'ireo clones Android ${profiles.size} miasa. Nitsitsy angovo sy CPU.",
                bytesRead = "0 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }

    fun resumeAllClones() {
        viewModelScope.launch {
            val profiles = repository.getAllProfilesList()
            profiles.forEach { p ->
                repository.updateProfile(
                    p.copy(
                        isPaused = false,
                        cloneStatus = "RUNNING",
                        cpuLoad = (25..65).random(),
                        ramLoad = (35..70).random()
                    )
                )
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "▶ Namerina Niasa Daholo (Resume All)",
                message = "Mandeha sy miasa avokoa ireo clones Android rehetra miisa ${profiles.size}."
            )
            val log = AiInspectionLog(
                appName = "Batch Control",
                status = "RESUME DAHOLO",
                aiInsight = "Mpiandraikitra Batch: Nampandehanina indray ny clones Android ${profiles.size}. Miasa amin'ny fomba feno ny tambajotra sy ny fampiharana.",
                bytesRead = "180 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(20)
        }
    }

    fun restartAllClones() {
        viewModelScope.launch {
            _isBatchRestarting.value = true
            val profiles = repository.getAllProfilesList()

            // Step 1: Set status to RESTARTING
            profiles.forEach { p ->
                repository.updateProfile(
                    p.copy(
                        cloneStatus = "RESTARTING",
                        cpuLoad = 95,
                        ramLoad = 85
                    )
                )
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "🔄 Mamelona Indray ny Clones...",
                message = "Eo am-pamerenana mamelona (Reboot) miaraka ireo clones Android ${profiles.size}."
            )

            val rebootStartLog = AiInspectionLog(
                appName = "Batch Control",
                status = "REBOOTING...",
                aiInsight = "Mpiandraikitra Batch: Fanombohana vaovao miaraka (Simultaneous reboot sequence) ho an'ny clones Android ${profiles.size}.",
                bytesRead = "50 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(rebootStartLog) + _aiInspectionLogs.value).take(20)

            delay(1800) // Simulated fast reboot sequence

            // Step 2: Restore to fresh RUNNING state
            profiles.forEach { p ->
                repository.updateProfile(
                    p.copy(
                        isPaused = false,
                        cloneStatus = "RUNNING",
                        cpuLoad = (15..35).random(),
                        ramLoad = (25..45).random()
                    )
                )
            }
            _isBatchRestarting.value = false

            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "✅ Naverina Novelomina Daholo (Restarted)",
                message = "Vita soa aman-tsara ny famelomana indray (Reboot) ireo clones Android ${profiles.size}."
            )

            val rebootDoneLog = AiInspectionLog(
                appName = "Batch Control",
                status = "REBOOT VITA",
                aiInsight = "Mpiandraikitra Batch: Vita soa aman-tsara ny famelomana vaovao ny clones ${profiles.size}. Voaova ny telemetry ary madio ny sandbox cache.",
                bytesRead = "75 Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(rebootDoneLog) + _aiInspectionLogs.value).take(20)
        }
    }

    fun pauseSingleClone(phoneId: Int) {
        viewModelScope.launch {
            val p = repository.getProfile(phoneId) ?: return@launch
            repository.updateProfile(
                p.copy(
                    isPaused = true,
                    cloneStatus = "PAUSED",
                    cpuLoad = (1..5).random(),
                    ramLoad = 14
                )
            )
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = "⏸ Naato ny Apareil #$phoneId",
                message = "Naato ny processeur sy fampandehanana ny Apareil ${p.name}."
            )
        }
    }

    fun resumeSingleClone(phoneId: Int) {
        viewModelScope.launch {
            val p = repository.getProfile(phoneId) ?: return@launch
            repository.updateProfile(
                p.copy(
                    isPaused = false,
                    cloneStatus = "RUNNING",
                    cpuLoad = (25..55).random(),
                    ramLoad = (35..60).random()
                )
            )
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = "▶ Miasa ny Apareil #$phoneId",
                message = "Nampandehanina indray ny Apareil ${p.name}."
            )
        }
    }

    fun restartSingleClone(phoneId: Int) {
        viewModelScope.launch {
            val p = repository.getProfile(phoneId) ?: return@launch
            repository.updateProfile(
                p.copy(
                    cloneStatus = "RESTARTING",
                    cpuLoad = 90,
                    ramLoad = 75
                )
            )
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = "🔄 Mamelona ny Apareil #$phoneId...",
                message = "Eo am-pamerenana mamelona ny Apareil ${p.name}."
            )
            delay(1500)
            repository.updateProfile(
                p.copy(
                    isPaused = false,
                    cloneStatus = "RUNNING",
                    cpuLoad = (15..30).random(),
                    ramLoad = (25..45).random()
                )
            )
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = phoneId,
                title = "✅ Novelomina ny Apareil #$phoneId",
                message = "Vonona sy miasa soa aman-tsara ny Apareil ${p.name}."
            )
        }
    }

    // --- "THE BOOK AND THE READERS" ANALOGY & TELEMETRY SIMULATOR ---
    fun openBookAnalogy() {
        _isBookAnalogyOpen.value = true
    }

    fun closeBookAnalogy() {
        _isBookAnalogyOpen.value = false
    }

    fun logBookAnalogyTelemetry(deviceName: String, location: String, isShareitCopy: Boolean, isBlocked: Boolean) {
        viewModelScope.launch {
            val pingStatus = if (isBlocked) "FIREWALL SAKANA 🛡" else "TELEMETRY +1 📡"
            val message = if (isBlocked) {
                "Sakanana tamin'ny Clonage Firewall ny telemetry avy amin'ny $deviceName ($location). Tsy azon'ny mpanoratra ny fampahalalana!"
            } else {
                "Hafatra voaray tamin'ny servieran'ny mpanoratra: '$deviceName eto $location, misokatra izao.' -> Mpanoratra: +1 ${if (isShareitCopy) "(Shareit Copy)" else "(Original)"}"
            }
            val log = AiInspectionLog(
                appName = "Telemetry: $deviceName",
                status = pingStatus,
                aiInsight = message,
                bytesRead = if (isBlocked) "0 Ko" else "3.8 Ko",
                isSecure = isBlocked
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(25)

            if (!isBlocked) {
                _currentNotification.value = UiNotification(
                    sourcePhoneId = 0,
                    targetPhoneId = _activePhoneId.value,
                    title = "📡 Telemetry Voaray (+1 Mpamaky)",
                    message = "$deviceName: Mampandre ny servieran'ny mpanoratra fa misokatra ny app."
                )
            }
        }
    }

    // Chrome Sandbox launcher
    fun openInChrome(url: String, appName: String? = null, targetPhoneId: Int? = null) {
        if (targetPhoneId != null) {
            _activePhoneId.value = targetPhoneId
        }
        val currentPhoneId = _activePhoneId.value
        _activeBrowserUrl.value = url
        _activeBrowserAppName.value = appName
        val log = AiInspectionLog(
            appName = appName ?: "Chrome Isolé",
            status = "CHROME NISOKATRA 🌐",
            aiInsight = "Navoaka ao anatin'ny navigateur Chrome isolé ny $url ho an'ny Apareil #$currentPhoneId. Sandbox sy cookies mitokana tanteraka tsy mila maka APK.",
            bytesRead = "142 Ko/s",
            isSecure = true
        )
        _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(25)
    }

    // Mampiditra ny fampiharana ao amin'ny Chrome ho an'ny fitaovana tsirairay avy (Multi-device Chrome execution)
    fun launchMultiDeviceChrome(url: String, appName: String) {
        _activeBrowserUrl.value = url
        _activeBrowserAppName.value = appName
        viewModelScope.launch {
            val profiles = repository.getAllProfilesList()
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "🌐 Chrome Nisokatra ho an'ny Fitaovana Rehetra",
                message = "Ny appareil ${profiles.size} tsirairay avy dia mampiasa an'i '$appName' ao anaty Chrome amin'ny alalan'ny IA (tsy mila maka APK)."
            )
            val log = AiInspectionLog(
                appName = appName,
                status = "MULTI-CHROME IA 🚀",
                aiInsight = "Navoaka ao amin'ny Chrome ho an'ny fitaovana ${profiles.size} tsirairay avy ny '$appName'. Ny IA no mitantana ny fampiasana azy amin'ny Chrome amin'ny session mitokana.",
                bytesRead = "${profiles.size * 98} Ko/s",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(25)
        }
    }

    fun closeChrome() {
        _activeBrowserUrl.value = null
        _activeBrowserAppName.value = null
    }

    fun openAiAppDiscovery() {
        _isAiAppDiscoveryOpen.value = true
    }

    fun closeAiAppDiscovery() {
        _isAiAppDiscoveryOpen.value = false
    }

    fun addDiscoveredAppToDevice(name: String, url: String, iconKey: String, phoneId: Int? = null) {
        val targetPhone = phoneId ?: _activePhoneId.value
        viewModelScope.launch {
            repository.addFirewallApp(
                FirewallAppEntity(
                    phoneId = targetPhone,
                    appName = name,
                    packageName = "com.sandboxed.${name.lowercase().replace(" ", "").replace("/", "")}",
                    iconKey = iconKey,
                    allowMobileData = true,
                    allowWifi = true,
                    dataUsageMb = (5..45).random().toFloat(),
                    isCustom = true
                )
            )
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = targetPhone,
                title = "✨ App Voakaroky ny IA Nampidirina",
                message = "Nampidirina tao amin'ny Apareil #$targetPhone ny $name ($url). Vonona hampiasaina ao anaty Chrome."
            )
            val log = AiInspectionLog(
                appName = name,
                status = "IA MIKAROKA APP 🔍",
                aiInsight = "Hitan'ny IA ny app '$name'. URL: $url. Napetaka ao amin'ny Sandbox Apareil #$targetPhone ho an'ny Chrome.",
                bytesRead = "18 Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(25)
            _isAiAppDiscoveryOpen.value = false
        }
    }

    // Mampiditra ny app amin'ny appareil rehetra indray miaraka
    fun addDiscoveredAppToAllDevices(name: String, url: String, iconKey: String) {
        viewModelScope.launch {
            val profiles = repository.getAllProfilesList()
            profiles.forEach { p ->
                repository.addFirewallApp(
                    FirewallAppEntity(
                        phoneId = p.phoneId,
                        appName = name,
                        packageName = "com.sandboxed.${name.lowercase().replace(" ", "").replace("/", "")}",
                        iconKey = iconKey,
                        allowMobileData = true,
                        allowWifi = true,
                        dataUsageMb = (5..45).random().toFloat(),
                        isCustom = true
                    )
                )
            }
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "✨ App Napetaka tamin'ny Appareil Rehetra",
                message = "Nampidirina tamin'ny fitaovana ${profiles.size} rehetra ny $name ho fanafohezana Chrome."
            )
            val log = AiInspectionLog(
                appName = name,
                status = "MULTI-APP NAPETAKA 📱",
                aiInsight = "Napetaka tamin'ny fitaovana ${profiles.size} tsirairay avy ny '$name'. Samy afaka mampiasa azy amin'ny Chrome ny tsirairay.",
                bytesRead = "64 Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(25)
            _isAiAppDiscoveryOpen.value = false
        }
    }

    /**
     * Batch install or update/change apps across ALL devices simultaneously:
     * "Azo atao ihany koa ny manova anireo App mipetaka aminy appareil rehetra App"
     */
    fun batchUpdateAppsAcrossAllDevices(
        selectedAppKeys: List<String>,
        replaceExisting: Boolean = false
    ) {
        viewModelScope.launch {
            val profiles = repository.getAllProfilesList()
            profiles.forEach { p ->
                if (replaceExisting) {
                    repository.deleteFirewallAppsByPhone(p.phoneId)
                }
                val existingApps = repository.getAllFirewallAppsList().filter { it.phoneId == p.phoneId }
                selectedAppKeys.forEach { key ->
                    val appName = when (key.lowercase()) {
                        "vidmat" -> "Vidmat"
                        "youtube" -> "YouTube"
                        "whatsapp" -> "WhatsApp"
                        "tiktok" -> "TikTok"
                        "chatgpt" -> "ChatGPT"
                        "facebook" -> "Facebook"
                        "telegram" -> "Telegram"
                        "instagram" -> "Instagram"
                        "browser" -> "Chrome • Navigateur"
                        "downloads" -> "Téléchargements"
                        else -> key.replaceFirstChar { it.uppercase() }
                    }
                    if (!existingApps.any { it.iconKey.equals(key, ignoreCase = true) || it.appName.equals(appName, ignoreCase = true) }) {
                        repository.addFirewallApp(
                            FirewallAppEntity(
                                phoneId = p.phoneId,
                                appName = appName,
                                packageName = "com.sandboxed.${key.lowercase().replace(" ", "").replace("/", "")}",
                                iconKey = key,
                                allowMobileData = true,
                                allowWifi = true,
                                dataUsageMb = (2..20).random().toFloat(),
                                isCustom = false
                            )
                        )
                    }
                }
            }

            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = _activePhoneId.value,
                title = "🔄 Apps Voaova amin'ny Fitaovana Rehetra!",
                message = "Navaozina tamin'ny fitaovana ${profiles.size} rehetra ireo fampiharana ${selectedAppKeys.size}."
            )

            val log = AiInspectionLog(
                appName = "Batch Apps Update",
                status = "VAOAVAO REHETRA 🔄",
                aiInsight = "Ny IA dia nanavao sy nametraka ireo fampiharana ${selectedAppKeys.size} tamin'ny fitaovana ${profiles.size} rehetra ao anaty sandbox mahaleo tena.",
                bytesRead = "${profiles.size * 54} Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(25)
        }
    }

    /**
     * Install an APK fetched from trusted source into a target device (or all devices) with sandbox guarantee
     */
    fun installApkFromStore(
        appName: String,
        sourceStore: String,
        iconKey: String,
        targetPhoneId: Int?, // null = All devices
        packageSizeMb: Int = 35
    ) {
        viewModelScope.launch {
            val profiles = repository.getAllProfilesList()
            val targetProfiles = if (targetPhoneId == null || targetPhoneId == 0) profiles else profiles.filter { it.phoneId == targetPhoneId }

            targetProfiles.forEach { p ->
                val newApp = FirewallAppEntity(
                    phoneId = p.phoneId,
                    appName = "$appName ($sourceStore)",
                    packageName = "com.sandbox.${sourceStore.lowercase().replace(" ", "")}.${appName.lowercase().replace(" ", "")}",
                    iconKey = iconKey,
                    allowMobileData = true,
                    allowWifi = true,
                    dataUsageMb = packageSizeMb.toFloat(),
                    isCustom = true
                )
                repository.addFirewallApp(newApp)
            }

            val targetDesc = if (targetPhoneId == null || targetPhoneId == 0) "ny fitaovana ${profiles.size} rehetra" else "ny fitaovana #$targetPhoneId"
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = targetPhoneId ?: _activePhoneId.value,
                title = "📦 APK Voapetraka: $appName",
                message = "Nalain'ny IA avy amin'ny $sourceStore ary napetraka soa aman-tsara tamin'ny $targetDesc."
            )

            val log = AiInspectionLog(
                appName = "$appName ($sourceStore)",
                status = "APK INSTALLED 🛡️",
                aiInsight = "Nalain'ny IA avy amin'ny $sourceStore ny APK '$appName'. Nanamarina ny sonia fiarovana sy nametraka azy tamin'ny $targetDesc ao anaty sandbox mitokana.",
                bytesRead = "${packageSizeMb * 1024} Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(log) + _aiInspectionLogs.value).take(25)
        }
    }

    // Export configuration and state backup to local storage
    fun exportBackupConfiguration() {
        viewModelScope.launch {
            val profiles = repository.getAllProfilesList()
            val apps = repository.getAllFirewallAppsList()
            val timestamp = System.currentTimeMillis()
            val backupJson = buildString {
                append("{\n")
                append("  \"export_timestamp\": $timestamp,\n")
                append("  \"app_version\": \"1.0.0\",\n")
                append("  \"total_virtual_devices\": ${profiles.size},\n")
                append("  \"devices\": [\n")
                profiles.forEachIndexed { i, p ->
                    append("    {\n")
                    append("      \"phoneId\": ${p.phoneId},\n")
                    append("      \"name\": \"${p.name}\",\n")
                    append("      \"owner\": \"${p.ownerName}\",\n")
                    append("      \"model\": \"${p.modelName}\",\n")
                    append("      \"isAiVideoWatch\": ${p.isAiVideoWatchEnabled}\n")
                    append("    }${if (i < profiles.size - 1) "," else ""}\n")
                }
                append("  ],\n")
                append("  \"total_cloned_apps\": ${apps.size},\n")
                append("  \"cloned_apps\": [\n")
                apps.forEachIndexed { i, a ->
                    append("    {\n")
                    append("      \"id\": ${a.id},\n")
                    append("      \"phoneId\": ${a.phoneId},\n")
                    append("      \"appName\": \"${a.appName}\",\n")
                    append("      \"iconKey\": \"${a.iconKey}\",\n")
                    append("      \"allowMobileData\": ${a.allowMobileData},\n")
                    append("      \"allowWifi\": ${a.allowWifi}\n")
                    append("    }${if (i < apps.size - 1) "," else ""}\n")
                }
                append("  ]\n")
                append("}\n")
            }

            val fileName = "backup_clones_${timestamp}.json"
            try {
                val file = java.io.File(getApplication<Application>().filesDir, fileName)
                file.writeText(backupJson)

                // Also register in local downloads so user can view/manage it
                repository.addDownloadedVideo(
                    DownloadedVideoEntity(
                        phoneId = _activePhoneId.value,
                        videoId = "backup_$timestamp",
                        title = "Backup Configuration Clones ($fileName)",
                        channelName = "Clonage Backup Manager",
                        fileSize = "${(backupJson.length / 1024f).coerceAtLeast(0.1f).let { String.format(java.util.Locale.US, "%.1f", it) }} Ko",
                        duration = "JSON",
                        downloadedByAccount = "backup@clonage.isolated"
                    )
                )

                _currentNotification.value = UiNotification(
                    sourcePhoneId = 0,
                    targetPhoneId = _activePhoneId.value,
                    title = "✅ Backup Voatahiry!",
                    message = "Voatahiry soa aman-tsara ny backup ($fileName) ho an'ny apareils ${profiles.size} sy apps ${apps.size}."
                )
            } catch (e: Exception) {
                _currentNotification.value = UiNotification(
                    sourcePhoneId = 0,
                    targetPhoneId = _activePhoneId.value,
                    title = "Fahadisoana Backup",
                    message = "Tsy tontosa ny backup: ${e.message}"
                )
            }
        }
    }

    // Infinite AI Device Generator mode:
    // "mety ho ]1; infini [ izany hoe misy sary infini ny eo ambaniny ilay safidy de azo atao ny manoratra anilay nombre nilay apareil ary azo atao ny misafidy anilay infini izay tsy misy farany fa mamorina apareil foana ny AI"
    private val _isInfiniteModeActive = MutableStateFlow(false)
    val isInfiniteModeActive: StateFlow<Boolean> = _isInfiniteModeActive.asStateFlow()

    private var infiniteJob: Job? = null

    fun toggleInfiniteMode(active: Boolean) {
        _isInfiniteModeActive.value = active
        if (!active) {
            infiniteJob?.cancel()
            infiniteJob = null
        }
    }

    // Mamorona sy Mametraka (PLACE) fampiharana amin'ny apareil iray na maro na INFINI:
    // "De ao anatiny plus ihany rehefa avy misafidy anilay app izy de misy soratra miseo eny aminy farany ambany hoe place de rehefa tsindrina ilay place avy dia mipetraka ao ilay app
    //  fa mbola misy safidy miseo hoe apareil Android firy ny mampiasa azy de azo atao tsara ny misafidy anilay isa ny apareil Android mampiasa azy mety ho ]1; infini [
    //  izany hoe misy sary infini ny eo ambaniny ilay safidy de azo atao ny manoratra anilay nombre nilay apareil ary azo atao ny misafidy anilay infini izay tsy misy farany fa mamorina apareil foana ny AI"
    fun createNewIndependentDevice(
        deviceName: String,
        selectedAppKeys: List<String>
    ) {
        createMultipleIndependentDevices(deviceName, selectedAppKeys, 1, false, false)
    }

    fun createMultipleIndependentDevices(
        deviceNamePrefix: String,
        selectedAppKeys: List<String>,
        deviceCount: Int,
        isInfinite: Boolean,
        isAiVideoWatchEnabled: Boolean,
        isChromeMode: Boolean = false,
        chromeCommandName: String = "Mijery Vidéo",
        chromeTargetAppName: String = "YouTube",
        deviceModelName: String = "Samsung Galaxy Note Pro"
    ) {
        viewModelScope.launch {
            val profiles = allProfiles.value
            var nextId = (profiles.maxOfOrNull { it.phoneId } ?: 1) + 1
            val resolvedCount = if (isInfinite) 3 else deviceCount.coerceIn(1, 100)

            val realHardwareModels = listOf(
                "Samsung Galaxy Note Pro",
                "Oppo N1",
                "Redmi Note 13 Pro+",
                "Google Pixel 9 Pro",
                "Galaxy S24 Ultra",
                "Oppo Find X7",
                "Xiaomi 14 Ultra",
                "OnePlus 12",
                "Redmi 12 5G",
                "Samsung Galaxy Note 20"
            )

            for (i in 1..resolvedCount) {
                val profileName = if (isInfinite) {
                    "Apareil Android #$nextId (Infini ∞)"
                } else if (resolvedCount == 1) {
                    if (deviceNamePrefix.isNotBlank()) deviceNamePrefix else "Apareil Android #$nextId"
                } else {
                    "${if (deviceNamePrefix.isNotBlank()) deviceNamePrefix else "Apareil Android"} #$nextId"
                }

                val chosenModel = if (resolvedCount == 1 && deviceModelName.isNotBlank()) {
                    deviceModelName
                } else if (deviceModelName.isNotBlank() && deviceModelName != "Samsung Galaxy Note Pro") {
                    deviceModelName
                } else {
                    realHardwareModels[(i - 1) % realHardwareModels.size]
                }

                val newProfile = PhoneProfileEntity(
                    phoneId = nextId,
                    name = profileName,
                    ownerName = "Olona $nextId",
                    googleAccount = "device.real$nextId@sandbox.isolated",
                    modelName = chosenModel,
                    wallpaperType = if (isInfinite) "WP_NEON" else "WP_DARK",
                    accentColorHex = when (i % 5) {
                        0 -> "#06B6D4" // Cyan
                        1 -> "#10B981" // Emerald
                        2 -> "#8B5CF6" // Violet
                        3 -> "#F59E0B" // Amber
                        else -> "#EC4899" // Pink
                    },
                    wifiConnected = true,
                    mobileDataConnected = false,
                    storageUsedMb = (12000..38000).random(),
                    storageTotalMb = 256000,
                    isAiVideoWatchEnabled = isAiVideoWatchEnabled
                )
                repository.saveProfile(newProfile)

                // Apetaka ao amin'ity apareil ity ireo fampiharana voafidy
                selectedAppKeys.forEach { key ->
                    val appName = when (key) {
                        "youtube" -> "YouTube"
                        "whatsapp" -> "WhatsApp"
                        "facebook" -> "Facebook"
                        "telegram" -> "Telegram"
                        "browser" -> "Chrome • Navigateur"
                        "tiktok" -> "TikTok"
                        "instagram" -> "Instagram"
                        "downloads" -> "Téléchargements & Fichiers"
                        else -> key.replaceFirstChar { it.uppercase() }
                    }
                    repository.addFirewallApp(
                        FirewallAppEntity(
                            phoneId = nextId,
                            appName = appName,
                            packageName = "com.isolated.$key",
                            iconKey = key,
                            allowMobileData = true,
                            allowWifi = true,
                            dataUsageMb = 0.1f,
                            isCustom = false
                        )
                    )
                }

                nextId++
            }

            val lastCreatedId = nextId - 1
            _activePhoneId.value = lastCreatedId
            _isNewDeviceWindowOpen.value = false

            if (isInfinite) {
                _isInfiniteModeActive.value = true
                startInfiniteSpawner(selectedAppKeys)
            }

            // Fampandrenesana
            _currentNotification.value = UiNotification(
                sourcePhoneId = 0,
                targetPhoneId = lastCreatedId,
                title = "PLACE: Fampiharana Tafapetraka!",
                message = if (isInfinite) {
                    "Nampidirina tamin'ny Apareil Infini (∞) ny fampiharana: mamorona apareil foana ny AI."
                } else {
                    "Nampidirina tamin'ny Apareil Android $resolvedCount ny fampiharana ${selectedAppKeys.size}."
                }
            )

            val log = AiInspectionLog(
                appName = if (isInfinite) "IA Spawner Infini (∞)" else "Multi-Apareils ($resolvedCount)",
                status = "PLACE TONTOSA",
                aiInsight = if (isInfinite) {
                    "IA Mpamaky: Noforonina ny Apareil Infini (∞). Mamorona apareil Android mahaleo tena foana ny AI amin'ny fotoana tena izy."
                } else {
                    "IA Mpamaky: Napetaka soa aman-tsara tamin'ny Apareil Android $resolvedCount ireo fampiharana ${selectedAppKeys.size}."
                },
                bytesRead = "98 Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = listOf(log) + _aiInspectionLogs.value

            // Rehefa Chrome Mode no voafidy: manokatra Chrome avy hatrany ary mampifandray ny fitaovana amin'ny baiko
            if (isChromeMode) {
                val targetUrl = when (chromeTargetAppName.lowercase().trim()) {
                    "youtube", "youtube web" -> "https://m.youtube.com"
                    "tiktok" -> "https://www.tiktok.com"
                    "vidmat" -> "https://duckduckgo.com/?q=vidmat"
                    "chatgpt" -> "https://chatgpt.com"
                    "facebook" -> "https://m.facebook.com"
                    "canva" -> "https://www.canva.com"
                    "aliexpress" -> "https://www.aliexpress.com"
                    else -> if (chromeTargetAppName.startsWith("http://") || chromeTargetAppName.startsWith("https://")) {
                        chromeTargetAppName
                    } else {
                        "https://duckduckgo.com/?q=${chromeTargetAppName.replace(" ", "+")}"
                    }
                }
                openInChrome(targetUrl, chromeTargetAppName, lastCreatedId)

                val chromeAiLog = AiInspectionLog(
                    appName = "Chrome ($chromeTargetAppName)",
                    status = "IA CHROME RUNNER 🌐",
                    aiInsight = "Ny IA dia mampifandray ny fitaovana $resolvedCount rehetra amin'ny Chrome Sandbox. Baiko tanterahina: [$chromeCommandName]. Samy manana session sy cookies mitokana ny fitaovana tsirairay avy.",
                    bytesRead = "${resolvedCount * 128} Ko",
                    isSecure = true
                )
                _aiInspectionLogs.value = listOf(chromeAiLog) + _aiInspectionLogs.value

                _currentNotification.value = UiNotification(
                    sourcePhoneId = 0,
                    targetPhoneId = lastCreatedId,
                    title = "🌐 Chrome Nampifandraisin'ny IA ($resolvedCount Apareils)",
                    message = "Samy manana connexion ao anaty Chrome amin'ny baiko [$chromeCommandName] ho an'i $chromeTargetAppName ireo fitaovana."
                )
            } else {
                // REHEFA APPLICATION MODE:
                // "Izany hoe avy dia miditra ao anatiny ilay app tendrena iasahany ny IA fa tsy mizanona fotsiny ao anatiny chrome fa tena miditra ao anatiny ilay app mihintsy"
                val primaryKey = selectedAppKeys.firstOrNull() ?: "vidmat"
                val createdApps = repository.getAllFirewallAppsList().filter { it.phoneId == lastCreatedId }
                val targetApp = createdApps.find { it.iconKey.contains(primaryKey, ignoreCase = true) } ?: createdApps.firstOrNull()
                if (targetApp != null) {
                    _selectedFirewallApp.value = targetApp
                }

                val appAiLog = AiInspectionLog(
                    appName = targetApp?.appName ?: primaryKey,
                    status = "IA MIASA AO ANATY APP ⚡",
                    aiInsight = "Ny IA dia efa niditra mivantana ao anatin'ny ${targetApp?.appName ?: primaryKey} amin'ny fitaovana $lastCreatedId. Miasa mivantana ao anaty app ny IA (tonga dia miditra ao anatiny fa tsy mijanona ao anaty Chrome fotsiny).",
                    bytesRead = "${resolvedCount * 145} Ko",
                    isSecure = true
                )
                _aiInspectionLogs.value = listOf(appAiLog) + _aiInspectionLogs.value

                _currentNotification.value = UiNotification(
                    sourcePhoneId = 0,
                    targetPhoneId = lastCreatedId,
                    title = "⚡ IA: Niditra mivantana tao anaty ${targetApp?.appName ?: primaryKey}!",
                    message = "Tonga dia tafiditra miasa ao anatin'ilay app ny IA ho an'ny fitaovana vaovao."
                )
            }
        }
    }

    private fun startInfiniteSpawner(selectedAppKeys: List<String>) {
        infiniteJob?.cancel()
        infiniteJob = viewModelScope.launch {
            while (_isInfiniteModeActive.value) {
                delay(12000) // Spawns a new independent Android device continuously
                val currentMax = (allProfiles.value.maxOfOrNull { it.phoneId } ?: 1) + 1
                if (allProfiles.value.size < 30) {
                    val newProfile = PhoneProfileEntity(
                        phoneId = currentMax,
                        name = "Apareil Android #$currentMax (Infini ∞)",
                        ownerName = "Olona $currentMax",
                        googleAccount = "infinity.$currentMax@ai.sandbox",
                        modelName = "AI Auto-Spawned Android #$currentMax",
                        wallpaperType = "WP_NEON",
                        accentColorHex = "#D946EF",
                        wifiConnected = true,
                        mobileDataConnected = true,
                        storageUsedMb = 0,
                        storageTotalMb = 128000
                    )
                    repository.saveProfile(newProfile)

                    selectedAppKeys.forEach { key ->
                        repository.addFirewallApp(
                            FirewallAppEntity(
                                phoneId = currentMax,
                                appName = when (key) {
                                    "youtube" -> "YouTube"
                                    "whatsapp" -> "WhatsApp"
                                    "facebook" -> "Facebook"
                                    "telegram" -> "Telegram"
                                    "browser" -> "Chrome • Navigateur"
                                    "tiktok" -> "TikTok"
                                    "instagram" -> "Instagram"
                                    "downloads" -> "Téléchargements"
                                    else -> key.replaceFirstChar { it.uppercase() }
                                },
                                packageName = "com.isolated.$key",
                                iconKey = key,
                                allowMobileData = true,
                                allowWifi = true,
                                dataUsageMb = 0.1f,
                                isCustom = false
                            )
                        )
                    }

                    val spawnLog = AiInspectionLog(
                        appName = "IA Spawner Infini",
                        status = "APAREIL VAOVAO",
                        aiInsight = "🤖 IA: Noforonin'ny AI ho azy ny Apareil Android #$currentMax (Infini ∞). Mahaleo tena tanteraka, tsy misy fifandraisana amin'ny voalohany.",
                        bytesRead = "140 Ko",
                        isSecure = true
                    )
                    _aiInspectionLogs.value = (listOf(spawnLog) + _aiInspectionLogs.value).take(20)
                }
            }
        }
    }

    // IA no mamaky azy: Fanadihadiana ataon'ny IA amin'ny fotoana tena izy
    fun askAiToInspect(appName: String? = null) {
        viewModelScope.launch {
            _isAiAnalysisRunning.value = true
            delay(700)
            val target = appName ?: "Fampiharana Rehetra"
            val randomBytes = (120..750).random()
            val newLog = AiInspectionLog(
                appName = target,
                status = "VITA NY FAMAKIANA",
                aiInsight = "IA Mpamaky sy Mpandinika: Novakiana ny flux de données an'ny $target ($randomBytes Ko). Voamarina fa mahaleo tena 100% ary voasakana tsy hisy fifandraisana amin'ny voalohany.",
                bytesRead = "$randomBytes Ko",
                isSecure = true
            )
            _aiInspectionLogs.value = (listOf(newLog) + _aiInspectionLogs.value).take(20)
            _isAiAnalysisRunning.value = false
        }
    }

    // --- DEVICE SWITCHING (A, B, C) ---
    fun switchPhone(targetPhoneId: Int) {
        _activePhoneId.value = targetPhoneId
    }

    fun navigateTo(screen: VirtualScreen, phoneId: Int) {
        when (phoneId) {
            1 -> _phone1Screen.value = screen
            2 -> _phone2Screen.value = screen
            3 -> _phone3Screen.value = screen
        }
    }

    fun navigateBack(phoneId: Int): Boolean {
        val current = when (phoneId) {
            1 -> _phone1Screen.value
            2 -> _phone2Screen.value
            else -> _phone3Screen.value
        }
        if (current != VirtualScreen.HOME) {
            navigateTo(VirtualScreen.HOME, phoneId)
            return true
        }
        return false
    }

    // --- YOUTUBE & INDEPENDENT STORAGE ---
    // User requested: "Raha misintona vidéo/antontan-taratasy i A, dia ao amin'ny fitaovan'i A ihany no voatahiry ilay téléchargement.
    // Tsy miseho ho azy amin'i B sy C ilay téléchargement, satria tsy mitovy ny stockage an'ireo finday."
    fun downloadVideoForCurrentPhone(video: YouTubeVideoItem) {
        val phoneId = _activePhoneId.value
        val profileName = when (phoneId) {
            1 -> "Finday A (Olona A)"
            2 -> "Finday B (Olona B)"
            else -> "Finday C (Olona C)"
        }
        val account = when (phoneId) {
            1 -> "olona.a@gmail.com"
            2 -> "olona.b@gmail.com"
            else -> "olona.c@gmail.com"
        }

        viewModelScope.launch {
            repository.addDownloadedVideo(
                DownloadedVideoEntity(
                    phoneId = phoneId,
                    videoId = video.id,
                    title = video.title,
                    channelName = video.channel,
                    fileSize = video.sizeMb,
                    duration = video.duration,
                    downloadedByAccount = account
                )
            )

            // Update storage used for this phone
            val p = repository.getProfile(phoneId)
            if (p != null) {
                val addedMb = video.sizeMb.replace(" MB", "").toFloatOrNull()?.toInt() ?: 30
                repository.updateProfile(p.copy(storageUsedMb = p.storageUsedMb + addedMb))
            }

            _currentNotification.value = UiNotification(
                sourcePhoneId = phoneId,
                targetPhoneId = phoneId,
                title = "Téléchargement vita tao amin'ny $profileName!",
                message = "Voatahiry ao amin'ny stockage manokan'i $profileName ihany ity vidéo ity (tsy miseho amin'ny hafa)."
            )
        }
    }

    fun deleteDownloadedVideo(video: DownloadedVideoEntity) {
        viewModelScope.launch {
            repository.deleteDownloadedVideo(video)
        }
    }

    fun playVideo(video: YouTubeVideoItem) {
        _currentPlayingVideo.value = video
    }

    fun stopVideo() {
        _currentPlayingVideo.value = null
    }

    // Calls
    fun startCall(name: String, number: String, phoneId: Int) {
        callTimerJob?.cancel()
        _activeCall.value = ActiveCall(name, number, phoneId, 0, isMuted = false, isSpeaker = false)

        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let { call ->
                    call.copy(durationSeconds = call.durationSeconds + 1)
                }
            }
        }
    }

    fun toggleCallMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeaker = !it.isSpeaker) }
    }

    fun endCall() {
        callTimerJob?.cancel()
        val current = _activeCall.value
        if (current != null) {
            viewModelScope.launch {
                repository.logCall(
                    CallRecordEntity(
                        phoneId = current.phoneId,
                        contactName = current.contactName,
                        phoneNumber = current.phoneNumber,
                        callType = "OUTGOING",
                        timestamp = System.currentTimeMillis(),
                        durationSeconds = current.durationSeconds
                    )
                )
            }
        }
        _activeCall.value = null
    }

    // Messages
    fun sendMessage(contactName: String, number: String, text: String, phoneId: Int) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(
                MessageEntity(
                    phoneId = phoneId,
                    contactName = contactName,
                    phoneNumber = number,
                    content = text,
                    timestamp = System.currentTimeMillis(),
                    isFromMe = true
                )
            )

            delay(1200)
            val replyText = "Voaray ny hafatrao ao amin'ny habaka finday #$phoneId."
            repository.sendMessage(
                MessageEntity(
                    phoneId = phoneId,
                    contactName = contactName,
                    phoneNumber = number,
                    content = replyText,
                    timestamp = System.currentTimeMillis(),
                    isFromMe = false
                )
            )

            _currentNotification.value = UiNotification(
                sourcePhoneId = phoneId,
                targetPhoneId = phoneId,
                title = contactName,
                message = replyText
            )
            delay(3500)
            _currentNotification.value = null
        }
    }

    // Contacts
    fun addContact(name: String, number: String, category: String, phoneId: Int) {
        viewModelScope.launch {
            repository.addContact(
                ContactEntity(
                    phoneId = phoneId,
                    name = name,
                    phoneNumber = number,
                    category = category,
                    colorIndex = (0..5).random()
                )
            )
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch { repository.deleteContact(contact) }
    }

    // Notes
    fun saveNote(title: String, content: String, category: String, isSecret: Boolean, phoneId: Int) {
        viewModelScope.launch {
            repository.saveNote(
                NoteEntity(
                    phoneId = phoneId,
                    title = title,
                    content = content,
                    category = category,
                    isSecret = isSecret,
                    colorIndex = if (phoneId == 1) 0 else 3
                )
            )
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    // Gallery
    fun addGalleryPhoto(title: String, uri: String, category: String, phoneId: Int) {
        viewModelScope.launch {
            repository.addGalleryPhoto(
                GalleryPhotoEntity(
                    phoneId = phoneId,
                    title = title,
                    imagePathOrUri = uri,
                    category = category
                )
            )
        }
    }

    fun deleteGalleryPhoto(photo: GalleryPhotoEntity) {
        viewModelScope.launch { repository.deleteGalleryPhoto(photo) }
    }

    // Cloned Apps
    fun cloneNewApp(name: String, iconKey: String, category: String, customUrl: String?, phoneId: Int) {
        viewModelScope.launch {
            repository.addClonedApp(
                ClonedAppEntity(
                    phoneId = phoneId,
                    appName = name,
                    packageName = "com.cloned.${name.lowercase().replace(" ", "")}",
                    iconKey = iconKey,
                    category = category,
                    customUrl = customUrl,
                    badgeCount = 0,
                    isBuiltIn = false
                )
            )
        }
    }

    fun deleteClonedApp(app: ClonedAppEntity) {
        viewModelScope.launch { repository.deleteClonedApp(app) }
    }

    // Camouflage
    fun toggleCamouflage(active: Boolean) {
        _isCamouflageActive.value = active
    }

    fun toggleSplitView() {
        _isSplitView.value = !_isSplitView.value
    }

    fun toggleWifi(phoneId: Int) {
        viewModelScope.launch {
            val current = repository.getProfile(phoneId)
            if (current != null) {
                repository.updateProfile(current.copy(wifiConnected = !current.wifiConnected))
            }
        }
    }

    fun toggleSound(phoneId: Int) {
        viewModelScope.launch {
            val current = repository.getProfile(phoneId)
            if (current != null) {
                val nextMode = when (current.soundMode) {
                    "NORMAL" -> "VIBRATE"
                    "VIBRATE" -> "SILENT"
                    else -> "NORMAL"
                }
                repository.updateProfile(current.copy(soundMode = nextMode))
            }
        }
    }

    fun wipeCloneSpace(phoneId: Int) {
        viewModelScope.launch { repository.wipePhoneSpace(phoneId) }
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }
}
