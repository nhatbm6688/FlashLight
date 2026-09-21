package com.af.flashlight.data.model

data class Language(
    val languageCode: String,
    val nameRes: Int,
    val flagRes: Int = 0,
    var selected: Boolean = false
)
