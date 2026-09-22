package com.af.flashlight.component.flashalert.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.af.flashlight.component.flashalert.model.AppItem
import com.af.flashlight.utils.SpManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FlashAlertViewModel @Inject constructor(
    private val spManager: SpManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashAlertUiState())
    val uiState: StateFlow<FlashAlertUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        val callEnabled = spManager.isCallFlashEnabled()
        val callOn = spManager.getCallFlashOnMs()
        val callOff = spManager.getCallFlashOffMs()

        val smsEnabled = spManager.isSmsFlashEnabled()
        val smsOn = spManager.getSmsFlashOnMs()
        val smsOff = spManager.getSmsFlashOffMs()

        val notiEnabled = spManager.isNotiFlashEnabled()
        val notiOn = spManager.getNotiFlashOnMs()
        val notiOff = spManager.getNotiFlashOffMs()
        val selectedPackages = spManager.getSelectedNotiApps()

        viewModelScope.launch {
            val previewIcons = withContext(Dispatchers.IO) {
                resolveSelectedAppIcons(selectedPackages)
            }
            _uiState.update { current ->
                current.copy(
                    isCallEnabled = callEnabled,
                    callOnMs = callOn,
                    callOffMs = callOff,
                    isSmsEnabled = smsEnabled,
                    smsOnMs = smsOn,
                    smsOffMs = smsOff,
                    isNotiEnabled = notiEnabled,
                    notiOnMs = notiOn,
                    notiOffMs = notiOff,
                    selectedAppPackages = selectedPackages,
                    selectedAppIcons = previewIcons
                )
            }
        }
    }

    private fun resolveSelectedAppIcons(packageNames: Set<String>): List<Drawable> {
        val pm = context.packageManager
        val icons = mutableListOf<Drawable>()
        for (pkg in packageNames) {
            try {
                val appInfo = pm.getApplicationInfo(pkg, 0)
                icons.add(appInfo.loadIcon(pm))
                if (icons.size >= 5) break // Limit preview to 5 icons
            } catch (_: Exception) {
                // Ignore missing app
            }
        }
        return icons
    }

    fun setCallEnabled(enabled: Boolean) {
        spManager.setCallFlashEnabled(enabled)
        _uiState.update { it.copy(isCallEnabled = enabled) }
    }

    fun setCallOnMs(ms: Long) {
        spManager.setCallFlashOnMs(ms)
        _uiState.update { it.copy(callOnMs = ms) }
    }

    fun setCallOffMs(ms: Long) {
        spManager.setCallFlashOffMs(ms)
        _uiState.update { it.copy(callOffMs = ms) }
    }

    fun setSmsEnabled(enabled: Boolean) {
        spManager.setSmsFlashEnabled(enabled)
        _uiState.update { it.copy(isSmsEnabled = enabled) }
    }

    fun setSmsOnMs(ms: Long) {
        spManager.setSmsFlashOnMs(ms)
        _uiState.update { it.copy(smsOnMs = ms) }
    }

    fun setSmsOffMs(ms: Long) {
        spManager.setSmsFlashOffMs(ms)
        _uiState.update { it.copy(smsOffMs = ms) }
    }

    fun setNotiEnabled(enabled: Boolean) {
        spManager.setNotiFlashEnabled(enabled)
        _uiState.update { it.copy(isNotiEnabled = enabled) }
    }

    fun setNotiOnMs(ms: Long) {
        spManager.setNotiFlashOnMs(ms)
        _uiState.update { it.copy(notiOnMs = ms) }
    }

    fun setNotiOffMs(ms: Long) {
        spManager.setNotiFlashOffMs(ms)
        _uiState.update { it.copy(notiOffMs = ms) }
    }

    fun loadInstalledApps() {
        _uiState.update { it.copy(isLoadingApps = true) }
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
                val currentSelected = spManager.getSelectedNotiApps()

                val appList = mutableListOf<AppItem>()
                val seenPackages = mutableSetOf<String>()

                for (resolve in resolveInfos) {
                    val pkg = resolve.activityInfo.packageName
                    if (pkg == context.packageName) continue // Skip our own app
                    if (seenPackages.add(pkg)) {
                        val name = resolve.loadLabel(pm).toString()
                        val icon = resolve.loadIcon(pm)
                        val isSelected = currentSelected.contains(pkg)
                        appList.add(AppItem(pkg, name, icon, isSelected))
                    }
                }
                appList.sortBy { it.appName.lowercase() }
                appList
            }

            _uiState.update {
                it.copy(
                    installedApps = apps,
                    isLoadingApps = false
                )
            }
        }
    }

    fun toggleAppSelection(packageName: String) {
        _uiState.update { state ->
            val updated = state.installedApps.map { app ->
                if (app.packageName == packageName) {
                    app.copy(isSelected = !app.isSelected)
                } else {
                    app
                }
            }
            state.copy(installedApps = updated)
        }
    }

    fun selectAllApps(selectAll: Boolean) {
        _uiState.update { state ->
            val updated = state.installedApps.map { app ->
                app.copy(isSelected = selectAll)
            }
            state.copy(installedApps = updated)
        }
    }

    fun saveSelectedApps() {
        val selected = _uiState.value.installedApps
            .filter { it.isSelected }
            .map { it.packageName }
            .toSet()

        spManager.setSelectedNotiApps(selected)
        loadSettings()
    }
}
