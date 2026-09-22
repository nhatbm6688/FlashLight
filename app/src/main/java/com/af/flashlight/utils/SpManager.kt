package com.af.flashlight.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.af.flashlight.App
import com.af.flashlight.R
import com.af.flashlight.data.model.Language

class SpManager(private val preferences: SharedPreferences) {
    companion object {
        private var instance: SpManager? = null

        fun getInstance(context: Context): SpManager {
            if (instance == null) {
                instance = SpManager(PreferenceManager.getDefaultSharedPreferences(context))
            }
            return instance!!
        }

        fun getInstance(): SpManager {
            if (instance == null) {
                instance = App.context?.let {
                    PreferenceManager.getDefaultSharedPreferences(
                        it
                    )
                }?.let { SpManager(it) }
            }
            return instance!!
        }

        fun resolveLanguageByCode(code: String): Language {
            return when (code) {
                "es" -> Language("es", R.string.spanish, R.mipmap.ic_flag_es)
                "hi" -> Language("hi", R.string.hindi, R.mipmap.ic_flag_in)
                "ko" -> Language("ko", R.string.korean, R.mipmap.ic_flag_kr)
                "ja" -> Language("ja", R.string.japanese, R.mipmap.ic_flag_jp)
                "de" -> Language("de", R.string.german, R.mipmap.ic_flag_de)
                "pt" -> Language("pt", R.string.portuguese, R.mipmap.ic_flag_pt)
                "fr" -> Language("fr", R.string.french, R.mipmap.ic_flag_fr)
                "it" -> Language("it", R.string.italian, R.mipmap.ic_flag_it)
                "in" -> Language("in", R.string.indonesian, R.mipmap.ic_flag_id)
                "vi" -> Language("vi", R.string.vietnamese, R.mipmap.ic_flag_vn)
                "ru" -> Language("ru", R.string.russian, R.mipmap.ic_flag_ru)
                "tr" -> Language("tr", R.string.turkish, R.mipmap.ic_flag_tr)
                "zh-TW" -> Language("zh-TW", R.string.chinese, R.mipmap.ic_flag_cn)
                else -> Language("en", R.string.english, R.mipmap.ic_flag_us)
            }
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit {
            putBoolean(key, value)
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        preferences.getBoolean(key, defaultValue)

    fun getInt(key: String, defaultValue: Int): Int = preferences.getInt(key, defaultValue)

    fun putInt(key: String, value: Int) {
        preferences.edit {
            putInt(key, value)
        }
    }

    fun getLong(key: String, defaultValue: Long): Long =
        preferences.getLong(key, defaultValue)

    fun putLong(key: String, value: Long) {
        preferences.edit {
            putLong(key, value)
        }
    }

    fun getString(key: String, defaultValue: String): String? =
        preferences.getString(key, defaultValue)

    fun putString(key: String, value: String) {
        preferences.edit {
            putString(key, value)
        }
    }

    fun saveLanguage(language: Language) {
        preferences.edit {
            putString(Constant.KEY_SP_CURRENT_LANGUAGE, language.toJson())
        }
    }

    fun getLanguage(): Language {
        val saved = preferences.getString(Constant.KEY_SP_CURRENT_LANGUAGE, "")?.toLanguageModel()
        val code = saved?.languageCode ?: "en"
        return resolveLanguageByCode(code)
    }


    fun setLanguageChosen() {
        preferences.edit { putBoolean(Constant.KEY_SP_LANGUAGE_CHOSEN, true) }
    }

    fun isLanguageChosen(): Boolean {
        return preferences.getBoolean(Constant.KEY_SP_LANGUAGE_CHOSEN, false)
    }

    fun setPurchased(isPurchased: Boolean) {
        preferences.edit { putBoolean(Constant.KEY_SP_IS_PURCHASED, isPurchased) }
    }

    fun isPurchased(): Boolean {
        return preferences.getBoolean(Constant.KEY_SP_IS_PURCHASED, false)
    }

    fun getScreenLightColor(): Int {
        return preferences.getInt(Constant.KEY_SP_SCREEN_LIGHT_COLOR, Constant.DEFAULT_SCREEN_LIGHT_COLOR)
    }

    fun setScreenLightColor(color: Int) {
        preferences.edit { putInt(Constant.KEY_SP_SCREEN_LIGHT_COLOR, color) }
    }

    fun getScreenLightBrightness(): Int {
        return preferences.getInt(Constant.KEY_SP_SCREEN_LIGHT_BRIGHTNESS, Constant.DEFAULT_SCREEN_LIGHT_BRIGHTNESS)
    }

    fun setScreenLightBrightness(brightness: Int) {
        preferences.edit { putInt(Constant.KEY_SP_SCREEN_LIGHT_BRIGHTNESS, brightness) }
    }

    fun isCallFlashEnabled(): Boolean =
        preferences.getBoolean(Constant.KEY_SP_FLASH_CALL_ENABLED, false)

    fun setCallFlashEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(Constant.KEY_SP_FLASH_CALL_ENABLED, enabled) }
    }

    fun getCallFlashOnMs(): Long =
        preferences.getLong(Constant.KEY_SP_FLASH_CALL_ON_MS, Constant.DEFAULT_CALL_FLASH_ON_MS)

    fun setCallFlashOnMs(ms: Long) {
        preferences.edit { putLong(Constant.KEY_SP_FLASH_CALL_ON_MS, ms) }
    }

    fun getCallFlashOffMs(): Long =
        preferences.getLong(Constant.KEY_SP_FLASH_CALL_OFF_MS, Constant.DEFAULT_CALL_FLASH_OFF_MS)

    fun setCallFlashOffMs(ms: Long) {
        preferences.edit { putLong(Constant.KEY_SP_FLASH_CALL_OFF_MS, ms) }
    }

    fun isSmsFlashEnabled(): Boolean =
        preferences.getBoolean(Constant.KEY_SP_FLASH_SMS_ENABLED, false)

    fun setSmsFlashEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(Constant.KEY_SP_FLASH_SMS_ENABLED, enabled) }
    }

    fun getSmsFlashOnMs(): Long =
        preferences.getLong(Constant.KEY_SP_FLASH_SMS_ON_MS, Constant.DEFAULT_SMS_FLASH_ON_MS)

    fun setSmsFlashOnMs(ms: Long) {
        preferences.edit { putLong(Constant.KEY_SP_FLASH_SMS_ON_MS, ms) }
    }

    fun getSmsFlashOffMs(): Long =
        preferences.getLong(Constant.KEY_SP_FLASH_SMS_OFF_MS, Constant.DEFAULT_SMS_FLASH_OFF_MS)

    fun setSmsFlashOffMs(ms: Long) {
        preferences.edit { putLong(Constant.KEY_SP_FLASH_SMS_OFF_MS, ms) }
    }

    fun isNotiFlashEnabled(): Boolean =
        preferences.getBoolean(Constant.KEY_SP_FLASH_NOTI_ENABLED, false)

    fun setNotiFlashEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(Constant.KEY_SP_FLASH_NOTI_ENABLED, enabled) }
    }

    fun getNotiFlashOnMs(): Long =
        preferences.getLong(Constant.KEY_SP_FLASH_NOTI_ON_MS, Constant.DEFAULT_NOTI_FLASH_ON_MS)

    fun setNotiFlashOnMs(ms: Long) {
        preferences.edit { putLong(Constant.KEY_SP_FLASH_NOTI_ON_MS, ms) }
    }

    fun getNotiFlashOffMs(): Long =
        preferences.getLong(Constant.KEY_SP_FLASH_NOTI_OFF_MS, Constant.DEFAULT_NOTI_FLASH_OFF_MS)

    fun setNotiFlashOffMs(ms: Long) {
        preferences.edit { putLong(Constant.KEY_SP_FLASH_NOTI_OFF_MS, ms) }
    }

    fun getSelectedNotiApps(): Set<String> =
        preferences.getStringSet(Constant.KEY_SP_FLASH_NOTI_SELECTED_APPS, emptySet()) ?: emptySet()

    fun setSelectedNotiApps(apps: Set<String>) {
        preferences.edit { putStringSet(Constant.KEY_SP_FLASH_NOTI_SELECTED_APPS, apps) }
    }
}
