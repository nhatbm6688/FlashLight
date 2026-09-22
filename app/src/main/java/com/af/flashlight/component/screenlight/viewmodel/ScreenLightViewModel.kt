package com.af.flashlight.component.screenlight.viewmodel

import com.af.flashlight.base.viewmodel.BaseViewModel
import com.af.flashlight.utils.Constant
import com.af.flashlight.utils.SpManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * UI State representation for the ScreenLight feature.
 */
data class ScreenLightUiState(
    val currentColor: Int = Constant.DEFAULT_SCREEN_LIGHT_COLOR,
    val currentBrightness: Int = Constant.DEFAULT_SCREEN_LIGHT_BRIGHTNESS
)

/**
 * ViewModel for ScreenLight adhering strictly to MVVM and Clean Architecture principles.
 * Decouples state management and data storage (SpManager) from the UI layer.
 */
@HiltViewModel
class ScreenLightViewModel @Inject constructor(
    private val spManager: SpManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        ScreenLightUiState(
            currentColor = spManager.getScreenLightColor(),
            currentBrightness = spManager.getScreenLightBrightness()
        )
    )
    val uiState: StateFlow<ScreenLightUiState> = _uiState.asStateFlow()

    fun initDefaultColorIfEmpty(defaultColor: Int) {
        if (_uiState.value.currentColor == 0) {
            selectColor(defaultColor)
        }
    }

    fun selectColor(color: Int) {
        _uiState.value = _uiState.value.copy(currentColor = color)
        spManager.setScreenLightColor(color)
    }

    fun updateBrightness(brightness: Int) {
        val clamped = brightness.coerceIn(5, 100)
        _uiState.value = _uiState.value.copy(currentBrightness = clamped)
        spManager.setScreenLightBrightness(clamped)
    }

    fun previewBrightness(brightness: Int) {
        val clamped = brightness.coerceIn(5, 100)
        _uiState.value = _uiState.value.copy(currentBrightness = clamped)
    }
}
