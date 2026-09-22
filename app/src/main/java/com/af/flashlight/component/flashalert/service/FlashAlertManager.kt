package com.af.flashlight.component.flashalert.service

import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.af.flashlight.component.flashalert.di.FlashAlertEntryPoint
import dagger.hilt.android.EntryPointAccessors

object FlashAlertManager {

    private const val TAG = "FlashAlertManager"
    private var wakeLock: PowerManager.WakeLock? = null
    private var isCallBlinking = false

    private fun acquireWakeLock(context: Context, timeoutMs: Long) {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = pm?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "FlashLight:AlertWakeLock"
            )?.apply {
                setReferenceCounted(false)
                acquire(timeoutMs)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error acquiring wake lock: ${e.message}")
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
            wakeLock = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing wake lock: ${e.message}")
        }
    }

    fun onCallRinging(context: Context) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                FlashAlertEntryPoint::class.java
            )
            val spManager = entryPoint.spManager()
            val flashlightManager = entryPoint.flashlightManager()

            if (!spManager.isCallFlashEnabled()) return
            if (isCallBlinking) return // Avoid restarting if already blinking for current call

            isCallBlinking = true
            acquireWakeLock(context, 60_000L) // 60 seconds max timeout for call ring

            val onMs = spManager.getCallFlashOnMs()
            val offMs = spManager.getCallFlashOffMs()
            flashlightManager.startCustomBlink(onMs, offMs, repeatCount = -1) {
                isCallBlinking = false
                releaseWakeLock()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error onCallRinging: ${e.message}")
        }
    }

    fun onCallEnded(context: Context) {
        try {
            isCallBlinking = false
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                FlashAlertEntryPoint::class.java
            )
            entryPoint.flashlightManager().turnOff()
            releaseWakeLock()
        } catch (e: Exception) {
            Log.e(TAG, "Error onCallEnded: ${e.message}")
        }
    }

    fun onSmsReceived(context: Context) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                FlashAlertEntryPoint::class.java
            )
            val spManager = entryPoint.spManager()
            val flashlightManager = entryPoint.flashlightManager()

            if (!spManager.isSmsFlashEnabled()) return
            if (isCallBlinking) return // Do not interrupt incoming call

            acquireWakeLock(context, 10_000L)

            // Nháy 1 lần duy nhất với quãng sáng dài phù hợp (tối thiểu 1.0s)
            val onMs = spManager.getSmsFlashOnMs().coerceAtLeast(1000L)
            flashlightManager.startCustomBlink(onMs, 0L, repeatCount = 1) {
                releaseWakeLock()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error onSmsReceived: ${e.message}")
        }
    }

    fun onNotificationReceived(context: Context, packageName: String, isOngoing: Boolean) {
        try {
            if (isOngoing) return

            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                FlashAlertEntryPoint::class.java
            )
            val spManager = entryPoint.spManager()
            val flashlightManager = entryPoint.flashlightManager()

            if (!spManager.isNotiFlashEnabled()) return
            if (isCallBlinking) return // Do not interrupt incoming call

            val selectedApps = spManager.getSelectedNotiApps()
            if (selectedApps.isNotEmpty() && !selectedApps.contains(packageName)) {
                return // Not selected by user
            }

            acquireWakeLock(context, 10_000L)

            // Nháy 1 lần duy nhất với quãng sáng dài phù hợp (tối thiểu 1.0s)
            val onMs = spManager.getNotiFlashOnMs().coerceAtLeast(1000L)
            flashlightManager.startCustomBlink(onMs, 0L, repeatCount = 1) {
                releaseWakeLock()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error onNotificationReceived: ${e.message}")
        }
    }
}
