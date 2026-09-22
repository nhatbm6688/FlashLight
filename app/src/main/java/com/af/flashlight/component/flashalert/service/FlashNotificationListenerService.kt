package com.af.flashlight.component.flashalert.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class FlashNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val pkg = sbn.packageName ?: return
        if (pkg == packageName) return // Ignore own app

        FlashAlertManager.onNotificationReceived(
            context = applicationContext,
            packageName = pkg,
            isOngoing = sbn.isOngoing
        )
    }
}
