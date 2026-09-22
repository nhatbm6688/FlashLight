package com.af.flashlight.component.led

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.led.model.LedDirection
import com.af.flashlight.component.led.model.LedEffect
import com.af.flashlight.databinding.ActivityLedPlayBinding

class LedPlayActivity : BaseActivity<ActivityLedPlayBinding>() {

    override val shouldShowNoInternetDialog: Boolean = false

    override fun provideViewBinding(): ActivityLedPlayBinding =
        ActivityLedPlayBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()

        // Force landscape orientation for LED display banner
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Keep screen on continuously while displaying LED
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Immersive Sticky Fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        // Extract parameters from intent
        val text = intent.getStringExtra(EXTRA_TEXT) ?: "HELLO WORLD"
        val color = intent.getIntExtra(EXTRA_COLOR, -1)
        val fontSize = intent.getFloatExtra(EXTRA_FONT_SIZE, 64f)
        val speed = intent.getIntExtra(EXTRA_SPEED, 5)
        val directionName = intent.getStringExtra(EXTRA_DIRECTION) ?: LedDirection.LEFT.name
        val effectName = intent.getStringExtra(EXTRA_EFFECT) ?: LedEffect.GLOW.name
        val bgRes = intent.getIntExtra(EXTRA_BG_RES, 0)
        val bgUriStr = intent.getStringExtra(EXTRA_BG_URI)

        // Configure LedBannerView
        viewLedPlay.text = text
        if (color != -1) viewLedPlay.ledColor = color
        // On landscape fullscreen, font size is slightly enhanced for great visibility
        viewLedPlay.fontSizeSp = (fontSize * 1.3f).coerceIn(32f, 160f)
        viewLedPlay.speedLevel = speed
        viewLedPlay.direction = try {
            LedDirection.valueOf(directionName)
        } catch (_: Exception) {
            LedDirection.LEFT
        }
        viewLedPlay.effect = try {
            LedEffect.valueOf(effectName)
        } catch (_: Exception) {
            LedEffect.GLOW
        }

        if (!bgUriStr.isNullOrBlank()) {
            viewLedPlay.setBackgroundUri(Uri.parse(bgUriStr))
        } else if (bgRes != 0) {
            viewLedPlay.setBackgroundResource(bgRes)
        }

        // Close button action
        btnClose.setOnClickListener {
            finish()
        }

        // Tap screen to toggle close button visibility
        root.setOnClickListener {
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
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_COLOR = "EXTRA_COLOR"
        private const val EXTRA_FONT_SIZE = "EXTRA_FONT_SIZE"
        private const val EXTRA_SPEED = "EXTRA_SPEED"
        private const val EXTRA_DIRECTION = "EXTRA_DIRECTION"
        private const val EXTRA_EFFECT = "EXTRA_EFFECT"
        private const val EXTRA_BG_RES = "EXTRA_BG_RES"
        private const val EXTRA_BG_URI = "EXTRA_BG_URI"

        fun start(
            context: Context,
            text: String,
            color: Int,
            fontSize: Float,
            speed: Int,
            direction: LedDirection,
            effect: LedEffect,
            bgRes: Int = 0,
            bgUri: Uri? = null
        ) {
            val intent = Intent(context, LedPlayActivity::class.java).apply {
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_COLOR, color)
                putExtra(EXTRA_FONT_SIZE, fontSize)
                putExtra(EXTRA_SPEED, speed)
                putExtra(EXTRA_DIRECTION, direction.name)
                putExtra(EXTRA_EFFECT, effect.name)
                putExtra(EXTRA_BG_RES, bgRes)
                putExtra(EXTRA_BG_URI, bgUri?.toString())
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        }
    }
}
