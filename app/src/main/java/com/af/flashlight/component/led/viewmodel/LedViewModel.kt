package com.af.flashlight.component.led.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.af.flashlight.component.led.model.LedBackgroundItem
import com.af.flashlight.component.led.model.LedDirection
import com.af.flashlight.component.led.model.LedEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LedEditorState(
    val text: String = "HELLO WORLD",
    val color: Int = 0,               // 0 = not yet resolved (resolved in Fragment using context)
    val fontSize: Float = 64f,
    val speed: Int = 5,
    val direction: LedDirection = LedDirection.LEFT,
    val effect: LedEffect = LedEffect.GLOW,
    val bgRes: Int = 0,               // 0 = use bgUri or nothing
    val bgUri: Uri? = null,
    val selectedBgPosition: Int = 1,  // Position in RecyclerView (1 = first preset)
    val backgroundItems: List<LedBackgroundItem> = emptyList()
)

class LedViewModel : ViewModel() {

    private val _state = MutableStateFlow(LedEditorState())
    val state: StateFlow<LedEditorState> = _state.asStateFlow()

    // ──────────────────────────────────────────────────────────────────────
    // Called once to populate the initial preset list
    // ──────────────────────────────────────────────────────────────────────
    fun initBackgroundsIfEmpty(presets: List<LedBackgroundItem>, defaultBgRes: Int) {
        if (_state.value.backgroundItems.isNotEmpty()) return
        _state.value = _state.value.copy(
            backgroundItems = presets,
            bgRes = defaultBgRes
        )
    }

    // ──────────────────────────────────────────────────────────────────────
    // State updaters — called from Fragment
    // ──────────────────────────────────────────────────────────────────────
    fun updateText(text: String) { _state.value = _state.value.copy(text = text) }
    fun updateColor(color: Int) { _state.value = _state.value.copy(color = color) }
    fun updateFontSize(fontSize: Float) { _state.value = _state.value.copy(fontSize = fontSize) }
    fun updateSpeed(speed: Int) { _state.value = _state.value.copy(speed = speed) }
    fun updateDirection(direction: LedDirection) { _state.value = _state.value.copy(direction = direction) }
    fun updateEffect(effect: LedEffect) { _state.value = _state.value.copy(effect = effect) }

    fun selectBackground(position: Int) {
        val items = _state.value.backgroundItems
        if (position < 0 || position >= items.size) return
        val item = items[position]
        if (item.isAddButton && item.customUri == null) return

        if (item.customUri != null) {
            _state.value = _state.value.copy(
                bgRes = 0, bgUri = item.customUri, selectedBgPosition = position
            )
        } else {
            _state.value = _state.value.copy(
                bgRes = item.resId, bgUri = null, selectedBgPosition = position
            )
        }
    }

    /**
     * Set or update the single custom background image uploaded from the phone.
     * Overlays the "+" card at index 0 and selects it.
     */
    fun setCustomBackground(uri: Uri) {
        val items = _state.value.backgroundItems.toMutableList()
        if (items.isEmpty()) return

        items[0] = items[0].copy(customUri = uri)

        _state.value = _state.value.copy(
            backgroundItems = items,
            bgRes = 0,
            bgUri = uri,
            selectedBgPosition = 0
        )
    }
}
