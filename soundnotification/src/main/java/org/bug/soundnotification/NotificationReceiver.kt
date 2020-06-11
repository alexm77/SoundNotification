package org.bug.soundnotification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.preference.PreferenceManager


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (ctx.getString(R.string.intent_name) == intent.action) {
            val intentActiveId = intent.identifier
            val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
            val enabled = prefs.getBoolean(PREF_ENABLED, false)
            val activeNotificationId = prefs.getString(PREF_ACTIVE_NOTIFICATION, "") ?: ""
            val unreadNotifications = prefs.getString(PREF_UNREAD, "") == "true"

            val mgr = ctx.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Log.d(
                javaClass.name,
                "same id? ${intentActiveId == activeNotificationId} enabled? $enabled unread? $unreadNotifications interruptFilter=${mgr.currentInterruptionFilter}"
            )
            Log.d(javaClass.name, "Received ${intent.identifier}")
            Log.d(javaClass.name, "Received ${intent.extras}")

            if (intentActiveId == activeNotificationId && enabled && unreadNotifications && mgr.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                val calc = intent.getBundleExtra("bundle")!!
                    .getSerializable(ctx.getString(R.string.delay_calculator)) as DelayCalculator

                beep(ctx)

                NotificationSender.sendNotification(ctx, calc, intentActiveId)
            }
        }
    }

    companion object {
        private fun beep(ctx: Context) {
            val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
            val overriddenVolume = (maxVolume * .8f).toInt()

            val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
            val volOverride = prefs.getBoolean(PREF_VOLUME_OVERRIDE, false)
            Log.d(NotificationReceiver::class.java.name, "Beeping. Vol override=$volOverride")
            val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)

            if (volOverride) {
                Log.d(
                    NotificationReceiver::class.java.name,
                    "Original volume: $originalVolume, temp volume: $overriddenVolume"
                )
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, overriddenVolume, 0)
                beep(overriddenVolume, maxVolume, ctx)
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalVolume, 0)
            } else {
                beep(
                    if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE) -1 else originalVolume,
                    maxVolume,
                    ctx
                )
            }
        }

        private fun beep(volume: Int, maxVolume: Int, ctx: Context) {
            val vib = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (volume > 0) {
                val convertedVolume = (volume / maxVolume.toFloat() * 100).toInt()
                Log.d(NotificationReceiver::class.java.name, "Volume: $convertedVolume ($volume/$maxVolume)")
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, convertedVolume)
                toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
            } else if (volume < 0) {
                Log.d(NotificationReceiver::class.java.name, "Vibrating")
                vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }
}