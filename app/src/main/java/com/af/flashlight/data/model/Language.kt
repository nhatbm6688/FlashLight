package com.af.flashlight.data.model

data class Language (
    val languageCode: String,
    val nameRes: Int,
    var selected: Boolean = false
)
