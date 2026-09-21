package com.af.flashlight.component.splash

import android.content.Intent
import android.provider.Settings
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.af.flashlight.BuildConfig
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.language.activity.LanguageActivity
import com.af.flashlight.component.main.activity.MainActivity
import com.af.flashlight.component.permission.PermissionActivity
import com.af.flashlight.databinding.ActivitySplashBinding
import com.af.flashlight.dialog.ForceUpdateDialog
import com.af.flashlight.dialog.NoInternetDialog
import com.af.flashlight.utils.FirebaseConfigManager
import com.af.flashlight.utils.Permission
import com.af.flashlight.utils.SpManager
import com.af.flashlight.utils.Utils
import com.af.flashlight.utils.isPermissionGranted
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    @Inject
    lateinit var spManager: SpManager

    override val shouldShowNoInternetDialog: Boolean = false

    private var forceUpdateDialog: ForceUpdateDialog? = null


    companion object {
        private const val REQUEST_CODE_UPDATE = 1001
    }

    override fun provideViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)


    override fun initViews() {
        setFullscreen()
        checkConnection()
        viewBinding.tvAds.isVisible = !spManager.isPurchased()
    }

    override fun onResume() {
        super.onResume()
        if (viewBinding.progressBar.progress == 0 && Utils.isConnected(this)) {
            checkConnection()
        }
    }

    private fun checkConnection() {
        if (Utils.isConnected(this)) {
            lifecycleScope.launch {
                val totalDurationMs = 2000L
                val steps = 100
                val interval = totalDurationMs / steps
                for (p in 1..100) {
                    delay(interval)
                    viewBinding.progressBar.progress = p
                    viewBinding.tvLoadingPercent.text = "$p%"
                }
                if (shouldForceUpdate()) {
                    startForceUpdate()
                } else {
                    goToMainScreen()
                }
            }
        } else {
            NoInternetDialog(this).apply {
                show()
                onGoToSetting = {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                onCancel = {
                    finish()
                }
            }
        }
    }


    private fun shouldForceUpdate(): Boolean {
        val config = FirebaseConfigManager.instance()
        return config.isForceUpdate && config.versionForce != BuildConfig.VERSION_NAME
    }

    private fun startForceUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                val canUpdate = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                if (canUpdate) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        REQUEST_CODE_UPDATE
                    )
                } else {
                    showForceUpdateFallback()
                }
            }
            .addOnFailureListener {
                showForceUpdateFallback()
            }
    }

    private fun showForceUpdateFallback() {
        if (isFinishing || isDestroyed) return
        forceUpdateDialog = ForceUpdateDialog(this).apply {
            onExit = { finish() }
            show()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE && resultCode != RESULT_OK) {
            showForceUpdateFallback()
        }
    }

    override fun onBack() {
        if (forceUpdateDialog?.isShowing == true) return
        super.onBack()
    }

    private fun goToMainScreen() {
        if (spManager.isLanguageChosen()) {
            if (isPermissionGranted(Permission.CAMERA)) {
                MainActivity.start(this)
            } else {
                PermissionActivity.start(this)
            }
        } else {
            LanguageActivity.start(this, true)
        }
        finish()
    }

}
