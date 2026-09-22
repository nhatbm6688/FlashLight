package com.af.flashlight.component.flashalert.viewmodel

import android.graphics.drawable.Drawable
import com.af.flashlight.component.flashalert.model.AppItem

data class FlashAlertUiState(
    // Incoming Calls
    val isCallEnabled: Boolean = false,
    val callOnMs: Long = 500L,
    val callOffMs: Long = 500L,

    // SMS
    val isSmsEnabled: Boolean = false,
    val smsOnMs: Long = 1000L,
    val smsOffMs: Long = 500L,

    // Notification
    val isNotiEnabled: Boolean = false,
    val notiOnMs: Long = 1000L,
    val notiOffMs: Long = 500L,
    val selectedAppPackages: Set<String> = emptySet(),
    val selectedAppIcons: List<Drawable> = emptyList(),

    // Select App Screen
    val installedApps: List<AppItem> = emptyList(),
    val isLoadingApps: Boolean = false
)
