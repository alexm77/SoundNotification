package org.bug.soundnotification

import android.app.Notification
import android.service.notification.StatusBarNotification
import android.util.Log

fun StatusBarNotification.isAttentionWorthy(): Boolean {
    Log.d(javaClass.name, "Clearable? ${this.isClearable}, priority=${this.notification.priority} -> $this")
    return this.isClearable && this.notification.priority >= Notification.PRIORITY_DEFAULT
}