package com.af.flashlight.component.main.viewmodel

import com.af.flashlight.base.viewmodel.BaseViewModel
import com.af.flashlight.manager.FlashlightManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class LightMode {
    FLASHLIGHT,
    SOS,
    DJ
}

enum class NavigationTab {
    FLASHLIGHT,
    SCREEN_LIGHT,
    LED,
    FLASH_ALERT
}

data class MainUiState(
    val currentMode: LightMode = LightMode.FLASHLIGHT,
    val isLightOn: Boolean = false,
    val currentTab: NavigationTab = NavigationTab.FLASHLIGHT,
    val isFlashAvailable: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(
    val flashlightManager: FlashlightManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(isFlashAvailable = flashlightManager.isFlashAvailable)
    )
    val uiState = _uiState.asStateFlow()

    init {
        flashlightManager.registerTorchCallback { enabled ->
            if (!enabled && _uiState.value.isLightOn && _uiState.value.currentMode == LightMode.FLASHLIGHT) {
                _uiState.value = _uiState.value.copy(isLightOn = false)
            }
        }
    }

    fun togglePower() {
        if (!_uiState.value.isFlashAvailable) return

        val newLightState = !_uiState.value.isLightOn
        _uiState.value = _uiState.value.copy(isLightOn = newLightState)
        applyFlashlightState(newLightState, _uiState.value.currentMode)
    }

    fun selectMode(mode: LightMode) {
        if (_uiState.value.currentMode == mode) return

        _uiState.value = _uiState.value.copy(currentMode = mode)

        // If light is currently ON, switch the hardware behavior immediately to the new mode
        if (_uiState.value.isLightOn) {
            applyFlashlightState(true, mode)
        }
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    private fun applyFlashlightState(isOn: Boolean, mode: LightMode) {
        if (!isOn) {
            flashlightManager.turnOff()
            return
        }

        when (mode) {
            LightMode.FLASHLIGHT -> flashlightManager.turnOn()
            LightMode.SOS -> flashlightManager.startSos()
            LightMode.DJ -> flashlightManager.startDjStrobe()
        }
    }

    fun turnOffLight() {
        if (_uiState.value.isLightOn) {
            _uiState.value = _uiState.value.copy(isLightOn = false)
            flashlightManager.turnOff()
        }
    }

    override fun onCleared() {
        super.onCleared()
        flashlightManager.release()
    }
}
