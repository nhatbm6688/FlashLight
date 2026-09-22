package com.af.flashlight.component.flashalert.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.flashalert.model.FlashAlertType
import com.af.flashlight.databinding.ActivityFlashAlertSimulationBinding
import com.af.flashlight.manager.FlashlightManager
import com.af.flashlight.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FlashAlertSimulationActivity : BaseActivity<ActivityFlashAlertSimulationBinding>() {

    @Inject
    lateinit var flashlightManager: FlashlightManager

    @Inject
    lateinit var spManager: SpManager

    private var alertType: FlashAlertType = FlashAlertType.CALL
    private var isSimulationRunning = false

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun provideViewBinding(): ActivityFlashAlertSimulationBinding =
        ActivityFlashAlertSimulationBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()

        val typeName = intent.getStringExtra(EXTRA_ALERT_TYPE)
        alertType = try {
            FlashAlertType.valueOf(typeName ?: FlashAlertType.CALL.name)
        } catch (_: Exception) {
            FlashAlertType.CALL
        }

        btnBack.setOnClickListener { finish() }

        when (alertType) {
            FlashAlertType.CALL -> setupCallSimulation()
            FlashAlertType.SMS -> setupSmsSimulation()
            FlashAlertType.NOTIFICATION -> setupNotificationSimulation()
        }
    }

    private fun setupCallSimulation() = with(viewBinding) {
        tvSimulationTitle.text = getString(R.string.incoming_calls)
        tvSimulationSubtitle.visibility = View.VISIBLE
        tvSimulationSubtitle.text = getString(R.string.test_incoming_call_number)

        layoutCallSimulation.visibility = View.VISIBLE
        layoutSmsSimulation.visibility = View.GONE
        layoutNotiSimulation.visibility = View.GONE

        // Pulsing animation on the call button & arrows
        val pulseAnim = AlphaAnimation(0.4f, 1.0f).apply {
            duration = 600
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        btnAnswerCall.startAnimation(pulseAnim)
        layoutArrows.startAnimation(pulseAnim)

        btnAnswerCall.setOnClickListener { finish() }
    }

    private fun setupSmsSimulation() = with(viewBinding) {
        tvSimulationTitle.text = getString(R.string.sms)
        tvSimulationSubtitle.visibility = View.GONE

        layoutCallSimulation.visibility = View.GONE
        layoutSmsSimulation.visibility = View.VISIBLE
        layoutNotiSimulation.visibility = View.GONE
    }

    private fun setupNotificationSimulation() = with(viewBinding) {
        tvSimulationTitle.text = getString(R.string.notification)
        tvSimulationSubtitle.visibility = View.GONE

        layoutCallSimulation.visibility = View.GONE
        layoutSmsSimulation.visibility = View.GONE
        layoutNotiSimulation.visibility = View.VISIBLE

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        tvLockscreenClock.text = timeFormat.format(Date())
    }

    override fun onResume() {
        super.onResume()
        startFlashSimulation()
    }

    override fun onPause() {
        super.onPause()
        stopFlashSimulation()
    }

    private fun startFlashSimulation() {
        if (isSimulationRunning) return
        isSimulationRunning = true

        startAudioAndVibration()

        when (alertType) {
            FlashAlertType.CALL -> {
                val onMs = spManager.getCallFlashOnMs()
                val offMs = spManager.getCallFlashOffMs()
                flashlightManager.startCustomBlink(onMs, offMs, repeatCount = -1)
            }
            FlashAlertType.SMS -> {
                val onMs = spManager.getSmsFlashOnMs().coerceAtLeast(1000L)
                flashlightManager.startCustomBlink(onMs, 0L, repeatCount = 1)
            }
            FlashAlertType.NOTIFICATION -> {
                val onMs = spManager.getNotiFlashOnMs().coerceAtLeast(1000L)
                flashlightManager.startCustomBlink(onMs, 0L, repeatCount = 1)
            }
        }
    }

    private fun stopFlashSimulation() {
        if (!isSimulationRunning) return
        isSimulationRunning = false

        stopAudioAndVibration()
        flashlightManager.turnOff()
    }

    private fun startAudioAndVibration() {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val ringerMode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL

            // 1. Play actual system ringtone or notification sound if not in silent/vibrate mode
            if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                val uri = when (alertType) {
                    FlashAlertType.CALL -> {
                        RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
                            ?: Settings.System.DEFAULT_RINGTONE_URI
                    }
                    FlashAlertType.SMS, FlashAlertType.NOTIFICATION -> {
                        RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION)
                            ?: Settings.System.DEFAULT_NOTIFICATION_URI
                    }
                }
                if (uri != null) {
                    ringtone = RingtoneManager.getRingtone(this, uri)
                    if (alertType == FlashAlertType.CALL && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ringtone?.isLooping = true
                    }
                    ringtone?.play()
                }
            }

            // 2. Play vibration if not in silent mode
            if (ringerMode != AudioManager.RINGER_MODE_SILENT) {
                vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }

                if (alertType == FlashAlertType.CALL) {
                    val pattern = longArrayOf(0, 1000, 1000)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(pattern, 0)
                    }
                } else {
                    val pattern = longArrayOf(0, 200, 150, 200)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(pattern, -1)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Simulation", "Error playing sound/vibration: ${e.message}")
        }
    }

    private fun stopAudioAndVibration() {
        try {
            ringtone?.stop()
            ringtone = null
        } catch (_: Exception) {}

        try {
            vibrator?.cancel()
            vibrator = null
        } catch (_: Exception) {}
    }

    companion object {
        private const val EXTRA_ALERT_TYPE = "EXTRA_ALERT_TYPE"

        fun start(activity: Activity, type: FlashAlertType) {
            val intent = Intent(activity, FlashAlertSimulationActivity::class.java).apply {
                putExtra(EXTRA_ALERT_TYPE, type.name)
            }
            activity.startActivity(intent)
        }
    }
}
