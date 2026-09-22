package com.af.flashlight.component.flashalert.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class FlashAlertService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_CALL_RINGING -> FlashAlertManager.onCallRinging(this)
            ACTION_CALL_STOP -> {
                FlashAlertManager.onCallEnded(this)
                stopSelf()
            }
            ACTION_SMS_RECEIVED -> {
                FlashAlertManager.onSmsReceived(this)
                stopSelf()
            }
            ACTION_NOTI_RECEIVED -> {
                val pkg = intent.getStringExtra(EXTRA_PACKAGE) ?: ""
                FlashAlertManager.onNotificationReceived(this, pkg, isOngoing = false)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    companion object {
        const val ACTION_CALL_RINGING = "com.af.flashlight.ACTION_CALL_RINGING"
        const val ACTION_CALL_STOP = "com.af.flashlight.ACTION_CALL_STOP"
        const val ACTION_SMS_RECEIVED = "com.af.flashlight.ACTION_SMS_RECEIVED"
        const val ACTION_NOTI_RECEIVED = "com.af.flashlight.ACTION_NOTI_RECEIVED"
        const val EXTRA_PACKAGE = "EXTRA_PACKAGE"

        fun startCallFlash(context: Context) {
            FlashAlertManager.onCallRinging(context)
        }

        fun stopCallFlash(context: Context) {
            FlashAlertManager.onCallEnded(context)
        }

        fun startSmsFlash(context: Context) {
            FlashAlertManager.onSmsReceived(context)
        }

        fun startNotificationFlash(context: Context, packageName: String = "") {
            FlashAlertManager.onNotificationReceived(context, packageName, isOngoing = false)
        }
    }
}
