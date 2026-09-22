package com.af.flashlight.component.main.setting

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.af.flashlight.BuildConfig
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.language.activity.LanguageActivity
import com.af.flashlight.component.main.setting.viewmodel.SettingViewModel
import com.af.flashlight.databinding.ActivitySettingBinding
import com.af.flashlight.dialog.RateDialog
import com.af.flashlight.utils.Constant
import com.af.flashlight.utils.openBrowser
import com.af.flashlight.utils.share
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>(), View.OnClickListener {

    private val viewModel: SettingViewModel by viewModels()

    override fun provideViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        toolBar.tvTitle.text = getString(R.string.settings)
        toolBar.btnBack.setOnClickListener { onBack() }

        btnLanguage.setOnClickListener(this@SettingActivity)
        btnShareApp.setOnClickListener(this@SettingActivity)
        btnRateUs.setOnClickListener(this@SettingActivity)
        btnPrivacyPolicy.setOnClickListener(this@SettingActivity)
        btnPolicySetting.setOnClickListener(this@SettingActivity)
    }

    override fun initObserver() {
        super.initObserver()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    viewBinding.tvCurrentLanguage.setText(state.currentLanguageNameRes)
                    viewBinding.btnPolicySetting.isVisible = state.isShowPolicySetting
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSettings()
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
                openBrowser(Constant.LINK_POLICY)
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
