package com.af.flashlight.component.led.model

import android.net.Uri

enum class LedDirection {
    RIGHT,
    LEFT,
    DOWN,
    UP
}

enum class LedEffect {
    GLOW,
    BLINK,
    NEON,
    FADE
}

data class LedBackgroundItem(
    val id: Int,
    val resId: Int = 0,
    val customUri: Uri? = null,
    val isAddButton: Boolean = false,
    var isSelected: Boolean = false
)
