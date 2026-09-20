package com.af.flashlight.base.resultcontract

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import com.af.flashlight.BuildConfig

class SettingPermissionResultContract : ActivityResultContract<Int, Int>() {
    val applicationId = BuildConfig.APPLICATION_ID

    override fun createIntent(context: Context, input: Int): Intent {
        return Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", applicationId, null)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }
}
