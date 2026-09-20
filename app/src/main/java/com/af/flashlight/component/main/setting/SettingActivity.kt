package com.af.flashlight.component.main.setting

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import com.af.flashlight.BuildConfig
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.language.activity.LanguageActivity
import com.af.flashlight.databinding.ActivitySettingBinding
import com.af.flashlight.dialog.RateDialog
import com.af.flashlight.utils.Constant
import com.af.flashlight.utils.SpManager
import com.af.flashlight.utils.openBrowser
import com.af.flashlight.utils.share
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>(), View.OnClickListener {
    @Inject
    lateinit var spManager: SpManager

    override fun provideViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        toolBar.tvTitle.text = getString(R.string.settings)
        toolBar.btnBack.setOnClickListener { onBack() }
        val isShowPolicySettings = spManager.getBoolean(
            Constant.KEY_SP_IS_SHOW_UMP_SETTING, false
        )

        btnPolicySetting.isVisible = isShowPolicySettings

        btnLanguage.setOnClickListener(this@SettingActivity)
        btnShareApp.setOnClickListener(this@SettingActivity)
        btnRateUs.setOnClickListener(this@SettingActivity)
        btnPrivacyPolicy.setOnClickListener(this@SettingActivity)
        btnPolicySetting.setOnClickListener(this@SettingActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLanguage -> {
                LanguageActivity.start(this, false)
            }

            R.id.btnShareApp -> {
                share("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            }

            R.id.btnRateUs -> {
                RateDialog(this).show()
            }

            R.id.btnPrivacyPolicy -> {
                openBrowser(Constant.LINK_POLICY)
            }

            R.id.btnPolicySetting -> {
//                showPolicySetting()
            }

        }
    }

//    private fun showPolicySetting() {
//        UserMessagingPlatform.showPrivacyOptionsForm(this) { formError ->
//            Logger.e("${formError?.errorCode} -- ${formError?.message}")
//        }
//    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
