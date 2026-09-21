package com.af.flashlight.component.screenlight

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.databinding.ActivityScreenLightPlayBinding
import com.af.flashlight.utils.Constant

class ScreenLightPlayActivity : BaseActivity<ActivityScreenLightPlayBinding>() {

    override val shouldShowNoInternetDialog: Boolean = false

    override fun provideViewBinding(): ActivityScreenLightPlayBinding =
        ActivityScreenLightPlayBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()

        // Keep screen on continuously while in play mode
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Immersive Sticky Fullscreen: hide status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        // Extract light color & brightness from intent
        val color = intent.getIntExtra(EXTRA_COLOR, Constant.DEFAULT_SCREEN_LIGHT_COLOR)
        val brightness = intent.getIntExtra(EXTRA_BRIGHTNESS, Constant.DEFAULT_SCREEN_LIGHT_BRIGHTNESS)

        // Apply background color to entire screen
        containerLightPlay.setBackgroundColor(color)

        // Apply screen brightness
        val lp = window.attributes
        lp.screenBrightness = brightness.coerceIn(5, 100) / 100f
        window.attributes = lp

        // Close button click
        btnClose.setOnClickListener {
            finish()
        }

        // Tap screen to toggle close button visibility (fade in / fade out)
        containerLightPlay.setOnClickListener {
            val willShow = !btnClose.isVisible
            if (willShow) {
                btnClose.alpha = 0f
                btnClose.isVisible = true
                btnClose.animate().alpha(1f).setDuration(200).start()
            } else {
                btnClose.animate().alpha(0f).setDuration(200).withEndAction {
                    btnClose.isVisible = false
                }.start()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    companion object {
        private const val EXTRA_COLOR = "EXTRA_COLOR"
        private const val EXTRA_BRIGHTNESS = "EXTRA_BRIGHTNESS"

        fun start(context: Context, color: Int, brightness: Int) {
            val intent = Intent(context, ScreenLightPlayActivity::class.java).apply {
                putExtra(EXTRA_COLOR, color)
                putExtra(EXTRA_BRIGHTNESS, brightness)
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        }
    }
}
