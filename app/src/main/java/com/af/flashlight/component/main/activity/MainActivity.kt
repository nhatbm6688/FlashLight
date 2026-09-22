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
import android.view.WindowManager
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
import com.af.flashlight.component.led.LedFragment
import com.af.flashlight.component.flashalert.FlashAlertFragment
import com.af.flashlight.utils.SpManager
import androidx.fragment.app.commitNow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private val viewModel: MainViewModel by viewModels()
    private var currentVisibleTab: NavigationTab? = null

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
        }
        bottomNavView.tabFlashAlert.setOnClickListener {
            viewModel.selectTab(NavigationTab.FLASH_ALERT)
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
        updateTabNavigation(state.currentTab, state.currentMode)

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

    /**
     * Centralized tab navigation with synchronous FragmentTransactions.
     * Prevents UI flicker/blink when switching tabs (e.g. ScreenLight -> Flashlight -> LED)
     * by immediately hiding inactive fragments and resetting brightness before showing the new tab.
     */
    private fun updateTabNavigation(targetTab: NavigationTab, currentMode: LightMode) = with(viewBinding) {
        if (targetTab == NavigationTab.FLASHLIGHT) {
            tvHeaderTitle.text = when (currentMode) {
                LightMode.FLASHLIGHT -> getString(R.string.flashlight)
                LightMode.SOS -> getString(R.string.sos_light)
                LightMode.DJ -> getString(R.string.dj_mode)
            }
        }

        if (currentVisibleTab == targetTab) return@with

        val slTag = ScreenLightFragment::class.java.simpleName
        val ledTag = LedFragment::class.java.simpleName
        val faTag = FlashAlertFragment::class.java.simpleName

        val slFragment = supportFragmentManager.findFragmentByTag(slTag) as? ScreenLightFragment
        val ledFragment = supportFragmentManager.findFragmentByTag(ledTag) as? LedFragment
        val faFragment = supportFragmentManager.findFragmentByTag(faTag) as? FlashAlertFragment

        when (targetTab) {
            NavigationTab.FLASHLIGHT -> {
                layoutFlashlightContent.visibility = View.VISIBLE
                fragmentContainer.visibility = View.GONE
                btnSettings.visibility = View.VISIBLE
                resetScreenBrightness()

                // Immediately hide any fragments so they will never flash on subsequent tab transitions
                if ((slFragment != null && !slFragment.isHidden) || 
                    (ledFragment != null && !ledFragment.isHidden) ||
                    (faFragment != null && !faFragment.isHidden)) {
                    supportFragmentManager.commitNow(allowStateLoss = true) {
                        setReorderingAllowed(true)
                        if (slFragment != null && !slFragment.isHidden) {
                            hide(slFragment)
                            slFragment.resetBrightness()
                        }
                        if (ledFragment != null && !ledFragment.isHidden) {
                            hide(ledFragment)
                        }
                        if (faFragment != null && !faFragment.isHidden) {
                            hide(faFragment)
                        }
                    }
                }
            }
            NavigationTab.SCREEN_LIGHT -> {
                layoutFlashlightContent.visibility = View.GONE
                btnSettings.visibility = View.GONE
                tvHeaderTitle.text = getString(R.string.screenlight_title)
                resetScreenBrightness()

                // Synchronously ensure other fragments are hidden and ScreenLight is ready
                supportFragmentManager.commitNow(allowStateLoss = true) {
                    setReorderingAllowed(true)
                    if (ledFragment != null && !ledFragment.isHidden) {
                        hide(ledFragment)
                    }
                    if (faFragment != null && !faFragment.isHidden) {
                        hide(faFragment)
                    }
                    if (slFragment == null) {
                        add(R.id.fragmentContainer, ScreenLightFragment.newInstance(), slTag)
                    } else {
                        show(slFragment)
                    }
                }
                fragmentContainer.visibility = View.VISIBLE
            }
            NavigationTab.LED -> {
                layoutFlashlightContent.visibility = View.GONE
                btnSettings.visibility = View.GONE
                tvHeaderTitle.text = getString(R.string.led_title)
                resetScreenBrightness()

                // Synchronously ensure other fragments are hidden and brightness reset before making container visible
                supportFragmentManager.commitNow(allowStateLoss = true) {
                    setReorderingAllowed(true)
                    if (slFragment != null && !slFragment.isHidden) {
                        hide(slFragment)
                        slFragment.resetBrightness()
                    }
                    if (faFragment != null && !faFragment.isHidden) {
                        hide(faFragment)
                    }
                    if (ledFragment == null) {
                        add(R.id.fragmentContainer, LedFragment.newInstance(), ledTag)
                    } else {
                        show(ledFragment)
                    }
                }
                fragmentContainer.visibility = View.VISIBLE
            }
            NavigationTab.FLASH_ALERT -> {
                layoutFlashlightContent.visibility = View.GONE
                btnSettings.visibility = View.GONE
                tvHeaderTitle.text = getString(R.string.flash_alert)
                resetScreenBrightness()

                supportFragmentManager.commitNow(allowStateLoss = true) {
                    setReorderingAllowed(true)
                    if (slFragment != null && !slFragment.isHidden) {
                        hide(slFragment)
                        slFragment.resetBrightness()
                    }
                    if (ledFragment != null && !ledFragment.isHidden) {
                        hide(ledFragment)
                    }
                    if (faFragment == null) {
                        add(R.id.fragmentContainer, FlashAlertFragment.newInstance(), faTag)
                    } else {
                        show(faFragment)
                    }
                }
                fragmentContainer.visibility = View.VISIBLE
            }
        }
        currentVisibleTab = targetTab
    }

    private fun resetScreenBrightness() {
        val lp = window.attributes
        if (lp.screenBrightness != WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = lp
        }
    }

    override fun onResume() {
        super.onResume()
        resetScreenBrightness()
        viewModel.syncWithHardware()
    }

    override fun onStop() {
        super.onStop()
        // Flashlight remains in its current state (ON or OFF) when app goes to home screen/background.
        // It only turns off when the app is destroyed or the user explicitly toggles it off.
        resetScreenBrightness()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Flashlight remains on even when MainActivity is recreated/destroyed,
        // unless the app process is terminated or the user toggles it off.
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
