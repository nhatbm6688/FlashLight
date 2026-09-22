package com.af.flashlight.component.main.setting.viewmodel

import androidx.lifecycle.ViewModel
import com.af.flashlight.R
import com.af.flashlight.utils.Constant
import com.af.flashlight.utils.SpManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingUiState(
    val currentLanguageNameRes: Int = R.string.english,
    val isShowPolicySetting: Boolean = false
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val spManager: SpManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        val currentLanguage = spManager.getLanguage()
        _uiState.value = SettingUiState(
            currentLanguageNameRes = currentLanguage.nameRes,
            isShowPolicySetting = true
        )
    }
}
