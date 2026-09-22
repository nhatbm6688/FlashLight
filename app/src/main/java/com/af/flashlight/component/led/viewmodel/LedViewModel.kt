package com.af.flashlight.component.led.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.af.flashlight.component.led.model.LedBackgroundItem
import com.af.flashlight.component.led.model.LedDirection
import com.af.flashlight.component.led.model.LedEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Maximum number of custom (user-picked) background images allowed in memory. */
private const val MAX_CUSTOM_BACKGROUNDS = 5

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
        if (item.isAddButton) return

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
     * Add a custom background image picked by the user.
     *
     * Rules:
     * - Custom images are always inserted right after the "Add" button (index 1).
     * - If the number of custom images would exceed [MAX_CUSTOM_BACKGROUNDS],
     *   the OLDEST custom image (furthest from index 1) is removed first.
     *   This keeps memory bounded while preserving the most recently added images.
     *
     * @return [AddCustomResult] describing what changed so the Fragment can update the adapter.
     */
    fun addCustomBackground(uri: Uri): AddCustomResult {
        val items = _state.value.backgroundItems.toMutableList()

        val customCount = items.count { it.customUri != null }
        var removedIndex = -1

        if (customCount >= MAX_CUSTOM_BACKGROUNDS) {
            // Remove the oldest custom image (last custom item in the list)
            removedIndex = items.indexOfLast { it.customUri != null }
            if (removedIndex >= 0) items.removeAt(removedIndex)
        }

        val newItem = LedBackgroundItem(
            id = System.currentTimeMillis().toInt(),
            customUri = uri,
            isSelected = true
        )
        // Always insert right after the "Add" button at index 0
        items.add(1, newItem)

        _state.value = _state.value.copy(
            backgroundItems = items,
            bgRes = 0,
            bgUri = uri,
            selectedBgPosition = 1
        )

        return AddCustomResult(insertedPosition = 1, removedPosition = removedIndex)
    }
}

/** Result from [LedViewModel.addCustomBackground] for adapter notification. */
data class AddCustomResult(
    val insertedPosition: Int,
    val removedPosition: Int  // -1 if nothing was removed
)
