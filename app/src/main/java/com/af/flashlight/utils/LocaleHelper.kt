package com.af.flashlight.utils

import android.content.Context
import java.util.Locale

object LocaleHelper {

    fun onAttach(context: Context?, defaultLanguage: String): Context? {
        return setLocale(context, defaultLanguage)
    }

    fun setLocale(context: Context?, language: String): Context? {
        return updateResources(context, language)
    }


    private fun updateResources(context: Context?, language: String): Context? {
        val locale = localeFrom(language)
        Locale.setDefault(locale)
        val configuration = context?.resources?.configuration
        configuration?.setLocale(locale)
        configuration?.setLayoutDirection(locale)
        return configuration?.let { context.createConfigurationContext(it) }
    }

    private fun updateResourcesLegacy(context: Context?, language: String): Context? {
        val locale = localeFrom(language)
        Locale.setDefault(locale)
        val resources = context?.resources
        val configuration = resources?.configuration
        configuration?.locale = locale
        configuration?.setLayoutDirection(locale)
        resources?.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    private fun localeFrom(language: String): Locale {
        val parts = language.split("-")
        return when (parts.size) {
            2 -> Locale(parts[0], parts[1])
            else -> Locale(language)
        }
    }
}
