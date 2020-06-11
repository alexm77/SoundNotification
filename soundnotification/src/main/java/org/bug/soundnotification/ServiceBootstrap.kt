package org.bug.soundnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager

class ServiceBootstrap : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Log.d(javaClass.name, "Received $ctx $intent")
        val enabled = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("enable_checkbox", false)
        if (Intent.ACTION_BOOT_COMPLETED == intent.action && enabled) {
            startService(ctx)
        }
    }

    companion object {
        fun startService(ctx: Context) {
            val serviceIntent = Intent(ctx, NotificationService::class.java)
            ctx.startService(serviceIntent)
        }
    }
}