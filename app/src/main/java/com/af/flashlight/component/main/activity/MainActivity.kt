package com.af.flashlight.component.main.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.main.setting.SettingActivity
import com.af.flashlight.component.main.viewmodel.LightMode
import com.af.flashlight.component.main.viewmodel.MainUiState
import com.af.flashlight.component.main.viewmodel.MainViewModel
import com.af.flashlight.component.main.viewmodel.NavigationTab
import com.af.flashlight.databinding.ActivityMainBinding
import com.af.flashlight.component.screenlight.ScreenLightFragment
import com.af.flashlight.utils.SpManager
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private val viewModel: MainViewModel by viewModels()

    override fun provideViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setFullscreen()
        spManager.setLanguageChosen()

        // Settings Button
        btnSettings.setOnClickListener {
            SettingActivity.start(this@MainActivity)
        }

        // Main Power Button
        btnPower.setOnClickListener {
            triggerHapticFeedback()
            viewModel.togglePower()
        }

        // Mode Selector
        btnModeFlashlight.setOnClickListener {
            viewModel.selectMode(LightMode.FLASHLIGHT)
        }
        btnModeSos.setOnClickListener {
            viewModel.selectMode(LightMode.SOS)
        }
        btnModeDj.setOnClickListener {
            viewModel.selectMode(LightMode.DJ)
        }

        // Bottom Navigation Tabs
        bottomNavView.tabFlashlight.setOnClickListener {
            viewModel.selectTab(NavigationTab.FLASHLIGHT)
        }
        bottomNavView.tabScreenlight.setOnClickListener {
            viewModel.selectTab(NavigationTab.SCREEN_LIGHT)
        }
        bottomNavView.tabLed.setOnClickListener {
            viewModel.selectTab(NavigationTab.LED)
            showToast(getString(R.string.coming_soon))
        }
        bottomNavView.tabFlashAlert.setOnClickListener {
            viewModel.selectTab(NavigationTab.FLASH_ALERT)
            showToast(getString(R.string.coming_soon))
        }
    }

    override fun initObserver() {
        super.initObserver()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    renderUi(state)
                }
            }
        }
    }

    private fun renderUi(state: MainUiState) = with(viewBinding) {
        // 1. Handle Active Tab Content & Header Title
        when (state.currentTab) {
            NavigationTab.FLASHLIGHT -> {
                layoutFlashlightContent.visibility = View.VISIBLE
                fragmentContainer.visibility = View.GONE
                btnSettings.visibility = View.VISIBLE
                tvHeaderTitle.text = when (state.currentMode) {
                    LightMode.FLASHLIGHT -> getString(R.string.flashlight)
                    LightMode.SOS -> getString(R.string.sos_light)
                    LightMode.DJ -> getString(R.string.dj_mode)
                }
            }
            NavigationTab.SCREEN_LIGHT -> {
                layoutFlashlightContent.visibility = View.GONE
                fragmentContainer.visibility = View.VISIBLE
                btnSettings.visibility = View.GONE
                tvHeaderTitle.text = getString(R.string.screenlight_title)
                showScreenLightFragment()
            }
            else -> {
                layoutFlashlightContent.visibility = View.VISIBLE
                fragmentContainer.visibility = View.GONE
                btnSettings.visibility = View.VISIBLE
            }
        }

        // 2. Update Power Button & Glow Effect
        if (state.isLightOn) {
            viewPowerGlow.visibility = View.VISIBLE
            tvPowerState.text = getString(R.string.state_on).uppercase()
            tvPowerState.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            ivPowerIcon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this@MainActivity, R.color.white)
            )

            when (state.currentMode) {
                LightMode.FLASHLIGHT -> {
                    viewPowerGlow.setBackgroundResource(R.drawable.bg_power_glow_flashlight)
                    btnPower.setBackgroundResource(R.drawable.bg_power_button_flashlight_on)
                    ivPowerIcon.setImageResource(R.drawable.ic_flashlight)
                }
                LightMode.SOS -> {
                    viewPowerGlow.setBackgroundResource(R.drawable.bg_power_glow_sos)
                    btnPower.setBackgroundResource(R.drawable.bg_power_button_sos_on)
                    ivPowerIcon.setImageResource(R.drawable.ic_soslight)
                }
                LightMode.DJ -> {
                    viewPowerGlow.setBackgroundResource(R.drawable.bg_power_glow_dj)
                    btnPower.setBackgroundResource(R.drawable.bg_power_button_dj_on)
                    ivPowerIcon.setImageResource(R.drawable.ic_djmode)
                }
            }
        } else {
            viewPowerGlow.visibility = View.GONE
            btnPower.setBackgroundResource(R.drawable.bg_power_button_off)
            tvPowerState.text = getString(R.string.state_off).uppercase()
            val offColor = ContextCompat.getColor(this@MainActivity, R.color.gray)
            tvPowerState.setTextColor(offColor)
            ivPowerIcon.imageTintList = ColorStateList.valueOf(offColor)

            when (state.currentMode) {
                LightMode.FLASHLIGHT -> ivPowerIcon.setImageResource(R.drawable.ic_flashlight)
                LightMode.SOS -> ivPowerIcon.setImageResource(R.drawable.ic_soslight)
                LightMode.DJ -> ivPowerIcon.setImageResource(R.drawable.ic_djmode)
            }
        }

        // 3. Update Mode Selector Tabs
        updateModeTabs(state.currentMode)

        // 4. Update Bottom Navigation Tabs
        updateBottomNavTabs(state.currentTab)

        // Flash availability notification
        if (!state.isFlashAvailable) {
            btnPower.isEnabled = false
            showToast(getString(R.string.flash_not_supported))
        } else {
            btnPower.isEnabled = true
        }
    }

    private fun updateModeTabs(currentMode: LightMode) = with(viewBinding) {
        btnModeFlashlight.isSelected = (currentMode == LightMode.FLASHLIGHT)
        btnModeSos.isSelected = (currentMode == LightMode.SOS)
        btnModeDj.isSelected = (currentMode == LightMode.DJ)
    }

    private fun updateBottomNavTabs(currentTab: NavigationTab) = with(viewBinding.bottomNavView) {
        tabFlashlight.isSelected = (currentTab == NavigationTab.FLASHLIGHT)
        tabScreenlight.isSelected = (currentTab == NavigationTab.SCREEN_LIGHT)
        tabLed.isSelected = (currentTab == NavigationTab.LED)
        tabFlashAlert.isSelected = (currentTab == NavigationTab.FLASH_ALERT)
    }

    private fun triggerHapticFeedback() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {
            // Ignore if vibration unavailable
        }
    }

    private fun showScreenLightFragment() {
        val tag = ScreenLightFragment::class.java.simpleName
        val existing = supportFragmentManager.findFragmentByTag(tag)
        if (existing == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, ScreenLightFragment.newInstance(), tag)
            }
        }
        viewModel.turnOffLight()
    }

    override fun onStop() {
        super.onStop()
        // Safely turn off torch to prevent battery drain or overheating when app goes to background
        viewModel.turnOffLight()
    }

    companion object {
        fun startNewTask(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }

        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
