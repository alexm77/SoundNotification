package org.bug.soundnotification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class JobSender {
    companion object {
        fun sendJob(ctx: Context): String {
            val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
            val calc = DelayCalculator(prefs)
            val intentId = UUID.randomUUID().toString()

            sendJob(ctx, calc, intentId)

            return intentId
        }

        fun sendJob(ctx: Context, calc: DelayCalculator, intentId: String) {
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val delay = calc.nextDelay()
            if (delay > 0) {
                val intent = from(ctx, JobReceiver::class.java, calc, intentId)
                Log.d(JobSender::class.java.name, "Sending ${intent.identifier}")
                Log.d(JobSender::class.java.name, "Sending ${intent.extras}")

                val pendingIntent =
                    PendingIntent.getBroadcast(ctx, Random().nextInt(), intent, PendingIntent.FLAG_ONE_SHOT)
                val alarmTimeAtUTC = ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(delay.toLong())

                Log.d(JobSender::class.java.name, "Will fire at $alarmTimeAtUTC")
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeAtUTC.toInstant().toEpochMilli(),
                    pendingIntent
                )
            }
        }

        private fun from(ctx: Context, clazz: Class<JobReceiver>, calc: DelayCalculator, id: String): Intent {
            return Intent(ctx, clazz).apply {
                action = ctx.getString(R.string.intent_name)
                identifier = id
                putExtra("bundle", Bundle().apply {
                    putSerializable(ctx.getString(R.string.delay_calculator), calc)
                })
            }
        }
    }
}
