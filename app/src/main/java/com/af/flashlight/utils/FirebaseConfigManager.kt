package com.af.flashlight.utils

import com.af.flashlight.domain.layer.AdConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson


class FirebaseConfigManager {

    companion object {
        const val KEY_ADS_STATUS_ENABLE = "ad_config"
        const val KEY_DISABLE_ALL_ADS = "disable_all_ads"

        const val KEY_VERSION_FORCE = "version_force"
        const val KEY_IS_FORCE_UPDATE = "is_force_update"

        private var mInstance: FirebaseConfigManager = FirebaseConfigManager()
        fun instance(): FirebaseConfigManager {
            return mInstance
        }
    }

    private val mMaxTryTime = 6
    private var mTryTime = 0

    private var isFetchDone = false
    private val pendingActions: MutableList<() -> Unit> = mutableListOf()

    var versionForce = "1.0.0"
    var isForceUpdate = false
    var isDisableAllAds = false


    var adConfig: AdConfig = AdConfig()

    fun doAfterFetch(action: () -> Unit) {
        if (isFetchDone) {
            action()
        } else {
            pendingActions.add(action)
        }
    }

    fun fetch(onFetchComplete: (() -> Unit)? = null) {
        val longCache = 15 * 60L
        FirebaseRemoteConfig.getInstance().fetch(longCache).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Logger.e("load config success")
                FirebaseRemoteConfig.getInstance().activate()
                versionForce = FirebaseRemoteConfig.getInstance().getString(KEY_VERSION_FORCE)
                isForceUpdate = FirebaseRemoteConfig.getInstance().getBoolean(KEY_IS_FORCE_UPDATE)
                isDisableAllAds = FirebaseRemoteConfig.getInstance().getBoolean(KEY_DISABLE_ALL_ADS)
                val json = FirebaseRemoteConfig.getInstance().getString(KEY_ADS_STATUS_ENABLE)
                Logger.e("FirebaseConfigManager : $json")
                if (json.isEmpty()) {
                    tryFetchAgain(onFetchComplete)
                } else {
                    try {
                        adConfig = Gson().fromJson(json, AdConfig::class.java)
                        if (isDisableAllAds) {
                            adConfig = AdConfig(
                                useInterSplash = false,
                                enableBannerSplash = false,
                                enableBannerSplash2f = false,
                                enableInterSplash = false,
                                enableInterSplash2f = false,
                                enableNativeFullSplash = false,
                                enableNativeFullSplash2f = false,
                                enableAppOpenSplash = false,
                                enableAppOpenResume = false,
                                enableNativeLanguage = false,
                                enableNativeLanguage2f = false,
                                enableNativeLanguageSelect = false,
                                enableNativeLanguageSelect2f = false,
                                enableNativeObd1 = false,
                                enableNativeObd12f = false,
                                enableNativeOb12Full = false,
                                enableNativeObd2 = false,
                                enableNativeObd22f = false,
                                enableNativeObd3 = false,
                                enableNativeObd32f = false,
                                enableNativeOb23Full = false,
                                enableNativeOb23Full2f = false,
                                enableNativeOb34Full = false,
                                enableNativeObd4 = false,
                                enableInterFunction = false,
                                enableInterBack = false,
                                enableBannerAll = false,
                                enableRewardFilm = false,
                                enableRewardTopFilm = false,
                                enableNativeFullPlayFilm = false,
                                enableNativeFullForYou = false,
                                enableInterCancelPaywall = false,
                                enableNativeUninstall = false,
                                enableBannerCollapse = false,
                                enableInterHome = false,
                                enableRewardQuality = false,
                                enableNativeFullRemove = false,
                                enableNativeSetting = false,
                                enableNativeExit = false,
                                enableInterExit = false
                            )
                        }
                        onFetchFinished(onFetchComplete)
                    } catch (e: Exception) {
                        tryFetchAgain(onFetchComplete)
                        Logger.e(e.message)
                    }
                }

            } else {
                tryFetchAgain(onFetchComplete)
                Logger.e("load config fail")
            }
        }
    }

    private fun onFetchFinished(onFetchComplete: (() -> Unit)?) {
        isFetchDone = true
        onFetchComplete?.invoke()
        pendingActions.forEach { it() }
        pendingActions.clear()
    }

    private fun tryFetchAgain(onFetchComplete: (() -> Unit)? = null) {
        ++mTryTime
        if (mTryTime < mMaxTryTime) {
            fetch(onFetchComplete)
        } else {
            mTryTime = 0
            onFetchFinished(onFetchComplete)
        }
    }
}
