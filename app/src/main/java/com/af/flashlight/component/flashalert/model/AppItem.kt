package com.af.flashlight.component.flashalert.model

import android.graphics.drawable.Drawable

data class AppItem(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    var isSelected: Boolean = false
)
