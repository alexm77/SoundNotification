package org.bug.soundnotification

import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.preference.PreferenceManager

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        Log.d(javaClass.name, "New sbn: $statusBarNotification")
        if (statusBarNotification.isAttentionWorthy()) {
            val intentId = JobSender.sendJob(baseContext)

            with(PreferenceManager.getDefaultSharedPreferences(baseContext).edit()) {
                putString(PREF_ACTIVE_NOTIFICATION, intentId)
                commit()
            }

            setUnread(baseContext)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (!unreadNotifications()) {
            clearUnread(baseContext)
        }
    }

    private fun unreadNotifications() = activeNotifications.any { it.isAttentionWorthy() }

    companion object {
        fun setUnread(ctx: Context) = updateProperty(ctx, true)

        fun clearUnread(ctx: Context) = updateProperty(ctx, false)

        private fun updateProperty(ctx: Context, value: Boolean) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
            if (prefs.getString(PREF_UNREAD, "") != value.toString()) {
                with(prefs.edit()) {
                    putString(PREF_UNREAD, value.toString())
                    commit()
                }
            }
        }
    }
}