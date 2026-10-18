package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiAppDiscoveryModal
import com.example.ui.components.BookAndReadersSimulatorModal
import com.example.ui.components.NotificationBanner
import com.example.ui.screens.*
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ClonageTheme
import com.example.viewmodel.ClonageViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ClonageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isFirewallGateActive by viewModel.isFirewallGateActive.collectAsStateWithLifecycle()
            val isGlobalMobileDataConnected by viewModel.isGlobalMobileDataConnected.collectAsStateWithLifecycle()
            val isGlobalWifiConnected by viewModel.isGlobalWifiConnected.collectAsStateWithLifecycle()
            val firewallApps by viewModel.firewallApps.collectAsStateWithLifecycle()
            val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
            val isNewDeviceWindowOpen by viewModel.isNewDeviceWindowOpen.collectAsStateWithLifecycle()
            val aiInspectionLogs by viewModel.aiInspectionLogs.collectAsStateWithLifecycle()
            val isAiAnalysisRunning by viewModel.isAiAnalysisRunning.collectAsStateWithLifecycle()
            val selectedFirewallApp by viewModel.selectedFirewallApp.collectAsStateWithLifecycle()
            val activePhoneId by viewModel.activePhoneId.collectAsStateWithLifecycle()
            val notification by viewModel.currentNotification.collectAsStateWithLifecycle()
            val isBatchRestarting by viewModel.isBatchRestarting.collectAsStateWithLifecycle()
            val isBookAnalogyOpen by viewModel.isBookAnalogyOpen.collectAsStateWithLifecycle()
            val activeBrowserUrl by viewModel.activeBrowserUrl.collectAsStateWithLifecycle()
            val activeBrowserAppName by viewModel.activeBrowserAppName.collectAsStateWithLifecycle()
            val isAiAppDiscoveryOpen by viewModel.isAiAppDiscoveryOpen.collectAsStateWithLifecycle()
            val inspectedRealDeviceId by viewModel.inspectedRealDeviceId.collectAsStateWithLifecycle()
            val isHostInternetAvailable by viewModel.isHostInternetAvailable.collectAsStateWithLifecycle()
            val hostNetworkType by viewModel.hostNetworkType.collectAsStateWithLifecycle()

            val targetPhoneId = inspectedRealDeviceId ?: activePhoneId
            val callLogs by viewModel.getCallLogsFlow(targetPhoneId).collectAsStateWithLifecycle(emptyList())
            val phoneMessages by viewModel.getMessagesFlow(targetPhoneId).collectAsStateWithLifecycle(emptyList())
            val phoneNotes by viewModel.getNotesFlow(targetPhoneId).collectAsStateWithLifecycle(emptyList())
            val phoneContacts by viewModel.getContactsFlow(targetPhoneId).collectAsStateWithLifecycle(emptyList())
            val phoneDownloads by viewModel.getDownloadsFlow(targetPhoneId).collectAsStateWithLifecycle(emptyList())
            val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()

            val downloads1 by viewModel.downloadsPhone1.collectAsStateWithLifecycle()
            val downloads2 by viewModel.downloadsPhone2.collectAsStateWithLifecycle()
            val downloads3 by viewModel.downloadsPhone3.collectAsStateWithLifecycle()

            val activeDownloads = when (activePhoneId) {
                1 -> downloads1
                2 -> downloads2
                else -> downloads3
            }

            // Android Hardware Back Handler
            BackHandler(enabled = isBookAnalogyOpen || isAiAppDiscoveryOpen || activeBrowserUrl != null || !isFirewallGateActive || selectedFirewallApp != null || inspectedRealDeviceId != null) {
                if (isBookAnalogyOpen) {
                    viewModel.closeBookAnalogy()
                } else if (isAiAppDiscoveryOpen) {
                    viewModel.closeAiAppDiscovery()
                } else if (inspectedRealDeviceId != null) {
                    viewModel.closeRealisticDevice()
                } else if (activeBrowserUrl != null) {
                    viewModel.closeChrome()
                } else if (selectedFirewallApp != null) {
                    viewModel.selectFirewallApp(null)
                } else if (!isFirewallGateActive) {
                    viewModel.showFirewallGate()
                }
            }

            ClonageTheme(phoneId = activePhoneId) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            // 1. ALOHAN'NY HIDITRA AO ANATY CLONAGE:
                            // App Fiarovana misy soratra DÉMARRER sy ANNULÉ ary CONTINUER
                            isFirewallGateActive -> {
                                FirewallGateScreen(
                                    onDemarrer = { viewModel.onFirewallDemarrer() },
                                    onAnnuler = { viewModel.onFirewallAnnuler() },
                                    onContinuer = { viewModel.onFirewallContinuer() },
                                    isMobileDataConnected = isGlobalMobileDataConnected,
                                    activePhoneName = "Clonage Sécurisé"
                                )
                            }

                            // 2. REHEFA MISOKATRA NY CHROME / BROWSER ISOLÉ
                            activeBrowserUrl != null -> {
                                BrowserScreen(
                                    phoneId = activePhoneId,
                                    initialUrl = activeBrowserUrl ?: "https://duckduckgo.com",
                                    appName = activeBrowserAppName,
                                    allProfiles = allProfiles,
                                    onSelectPhone = { id -> viewModel.switchPhone(id) },
                                    onBack = { viewModel.closeChrome() }
                                )
                            }

                            // 3. REHEFA MISOKATRA NY APPLICATION CLONAGE IRAY (oh: YouTube, WhatsApp, Facebook, Browser...)
                            selectedFirewallApp != null -> {
                                val currentApp = selectedFirewallApp!!
                                if (currentApp.iconKey == "youtube") {
                                    YouTubeScreen(
                                        phoneId = activePhoneId,
                                        isMobileDataConnected = isGlobalMobileDataConnected && currentApp.allowMobileData,
                                        isWifiConnected = isGlobalWifiConnected && currentApp.allowWifi,
                                        onToggleMobileData = { viewModel.toggleGlobalMobileData() },
                                        onToggleWifi = { viewModel.toggleGlobalWifi() },
                                        downloadsForThisPhone = activeDownloads,
                                        onDownloadVideo = { video ->
                                            viewModel.downloadVideoForCurrentPhone(video)
                                        },
                                        onDeleteDownload = { d ->
                                            viewModel.deleteDownloadedVideo(d)
                                        },
                                        onSelectPhone = { id ->
                                            viewModel.switchPhone(id)
                                        },
                                        onBack = { viewModel.selectFirewallApp(null) }
                                    )
                                } else if (currentApp.iconKey == "browser") {
                                    BrowserScreen(
                                        phoneId = activePhoneId,
                                        initialUrl = "https://duckduckgo.com",
                                        appName = currentApp.appName,
                                        allProfiles = allProfiles,
                                        onSelectPhone = { id -> viewModel.switchPhone(id) },
                                        onBack = { viewModel.selectFirewallApp(null) }
                                    )
                                } else {
                                    ClonedAppDetailScreen(
                                        app = currentApp,
                                        isGlobalMobileDataConnected = isGlobalMobileDataConnected,
                                        isGlobalWifiConnected = isGlobalWifiConnected,
                                        onToggleGlobalMobileData = { viewModel.toggleGlobalMobileData() },
                                        onToggleGlobalWifi = { viewModel.toggleGlobalWifi() },
                                        onToggleAppMobileData = { viewModel.toggleAppMobileData(currentApp) },
                                        onToggleAppWifi = { viewModel.toggleAppWifi(currentApp) },
                                        onOpenInBrowser = { url ->
                                            viewModel.openInChrome(url, currentApp.appName)
                                        },
                                        onBack = { viewModel.selectFirewallApp(null) }
                                    )
                                }
                            }

                            // 4. FENÊTRE VAOVAO MISOKATRA REHEFA MANINDRY DÉMARRER (Dashboard)
                            else -> {
                                FirewallDashboardScreen(
                                    isGlobalMobileDataConnected = isGlobalMobileDataConnected,
                                    isGlobalWifiConnected = isGlobalWifiConnected,
                                    isHostInternetAvailable = isHostInternetAvailable,
                                    hostNetworkType = hostNetworkType,
                                    onToggleGlobalMobileData = { viewModel.toggleGlobalMobileData() },
                                    onToggleGlobalWifi = { viewModel.toggleGlobalWifi() },
                                    firewallApps = firewallApps,
                                    allProfiles = allProfiles,
                                    activePhoneId = activePhoneId,
                                    onSelectPhone = { id -> viewModel.switchPhone(id) },
                                    isNewDeviceWindowOpen = isNewDeviceWindowOpen,
                                    onOpenNewDeviceWindow = { viewModel.openNewDeviceWindow() },
                                    onCloseNewDeviceWindow = { viewModel.closeNewDeviceWindow() },
                                    onCreateIndependentDevice = { name, apps, count, isInfinite, isAiVideoWatchEnabled, isChrome, cmdName, targetApp, modelName ->
                                        viewModel.createMultipleIndependentDevices(
                                            name, apps, count, isInfinite, isAiVideoWatchEnabled, isChrome, cmdName, targetApp, modelName
                                        )
                                    },
                                    aiLogs = aiInspectionLogs,
                                    isAiAnalyzing = isAiAnalysisRunning,
                                    onTriggerAiAnalysis = { viewModel.askAiToInspect(null) },
                                    onToggleAppMobileData = { app -> viewModel.toggleAppMobileData(app) },
                                    onToggleAppWifi = { app -> viewModel.toggleAppWifi(app) },
                                    onToggleAllAppsMobileData = { enable -> viewModel.toggleAllAppsMobileData(enable) },
                                    onToggleAllAppsWifi = { enable -> viewModel.toggleAllAppsWifi(enable) },
                                    onAddFirewallApp = { name, icon -> viewModel.addFirewallApp(name, icon) },
                                    onReplaceApp = { phoneId, newAppName, newIconKey, newPackageName ->
                                        viewModel.replaceAppOnDevice(phoneId, newAppName, newIconKey, newPackageName)
                                    },
                                    onDeleteFirewallApp = { app -> viewModel.deleteFirewallApp(app) },
                                    onDeleteDevice = { phoneId -> viewModel.deleteIndependentDevice(phoneId) },
                                    onDeleteMultipleDevices = { ids -> viewModel.deleteMultipleIndependentDevices(ids) },
                                    onDisconnectAllConnections = { viewModel.disconnectAllConnections() },
                                    onConnectAllConnections = { viewModel.connectAllConnections() },
                                    onToggleSingleDeviceConnection = { id -> viewModel.toggleSingleDeviceConnection(id) },
                                    onToggleDeviceMobileData = { id -> viewModel.toggleDeviceMobileData(id) },
                                    onToggleDeviceWifi = { id -> viewModel.toggleDeviceWifi(id) },
                                    onOpenRealisticDevice = { id -> viewModel.openRealisticDevice(id) },
                                    onExportBackup = { viewModel.exportBackupConfiguration() },
                                    isBatchRestarting = isBatchRestarting,
                                    onPauseAllClones = { viewModel.pauseAllClones() },
                                    onResumeAllClones = { viewModel.resumeAllClones() },
                                    onRestartAllClones = { viewModel.restartAllClones() },
                                    onPauseSingleClone = { id -> viewModel.pauseSingleClone(id) },
                                    onResumeSingleClone = { id -> viewModel.resumeSingleClone(id) },
                                    onRestartSingleClone = { id -> viewModel.restartSingleClone(id) },
                                    onOpenBookAnalogy = { viewModel.openBookAnalogy() },
                                    onOpenAiAppDiscovery = { viewModel.openAiAppDiscovery() },
                                    onOpenClonedApp = { app -> viewModel.selectFirewallApp(app) },
                                    onBackToGate = { viewModel.showFirewallGate() }
                                )
                            }
                        }

                        // "The Book and the Readers" Simulator & Analogy Modal
                        if (isBookAnalogyOpen) {
                            BookAndReadersSimulatorModal(
                                onDismiss = { viewModel.closeBookAnalogy() },
                                onLogTelemetry = { d, l, s, b ->
                                    viewModel.logBookAnalogyTelemetry(d, l, s, b)
                                }
                            )
                        }

                        // AI App Discovery Modal (Any App in the world + Chrome Sandbox)
                        if (isAiAppDiscoveryOpen) {
                            AiAppDiscoveryModal(
                                activePhoneId = activePhoneId,
                                allProfiles = allProfiles,
                                onDismiss = { viewModel.closeAiAppDiscovery() },
                                onOpenInChrome = { url, name, targetPhoneId ->
                                    viewModel.openInChrome(url, name, targetPhoneId)
                                    viewModel.closeAiAppDiscovery()
                                },
                                onOpenMultiDeviceChrome = { url, name ->
                                    viewModel.launchMultiDeviceChrome(url, name)
                                    viewModel.closeAiAppDiscovery()
                                },
                                onAddAppToDevice = { name, url, iconKey, targetPhoneId ->
                                    viewModel.addDiscoveredAppToDevice(name, url, iconKey, targetPhoneId)
                                },
                                onAddAppToAllDevices = { name, url, iconKey ->
                                    viewModel.addDiscoveredAppToAllDevices(name, url, iconKey)
                                }
                            )
                        }

                        // Notification Banner at Top
                        NotificationBanner(
                            notification = notification,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}
