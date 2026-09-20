package com.af.flashlight.data.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.af.flashlight.utils.Permission

@Keep
data class PermissionModel(
    @param:StringRes
    val title: Int,
    val permissions: List<Permission>,
    var isAllowed: Boolean = false,
)
