package com.af.flashlight.component.flashalert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.af.flashlight.component.flashalert.service.FlashAlertManager

class SmsFlashReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            FlashAlertManager.onSmsReceived(context)
        }
    }
}
