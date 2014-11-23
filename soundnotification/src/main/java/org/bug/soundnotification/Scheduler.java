package org.bug.soundnotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {
	private final NotificationListenerService srv;
	private final ScheduledExecutorService schExSrv = Executors.newScheduledThreadPool(1);
	private final DelayCalculator delay;
	private ScheduledFuture<?> currentTask;

	public Scheduler(NotificationListenerService srv) {
		this.srv = srv;
		this.delay = new DelayCalculator();
	}

	public void init() {
		cancelCurrentTask();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(srv.getBaseContext());
		int start = Integer.parseInt(prefs.getString("start_list", "2"));
		int increment = Integer.parseInt(prefs.getString("increment_list", "0"));
		int limit = Integer.parseInt(prefs.getString("limit_list", "0"));

		delay.init(start, increment, limit);
		int nextDelay = delay.nextDelay();
		Runnable notificationTask = new NotificationTask();

		scheduleTask(nextDelay, notificationTask);
	}

	private void scheduleTask(int delay, Runnable task) {
		if (delay > 0) {
			Log.d(getClass().getName(), String.format("Scheduling notification after %d minutes", delay));

			currentTask = schExSrv.schedule(task, delay, TimeUnit.MINUTES);
		}
	}

	private void cancelCurrentTask() {
		Log.i(getClass().getName(), "cancelling");
		if (currentTask != null) {
			currentTask.cancel(false);
		}
	}

	private class NotificationTask implements Runnable {
		private final AudioManager audioManager = (AudioManager) srv.getSystemService(Context.AUDIO_SERVICE);
		private final Vibrator vib = (Vibrator) srv.getSystemService(Context.VIBRATOR_SERVICE);
		private final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
		private final int overriddenVolume = (int) (maxVolume * .8f);

		@Override
		public void run() {
			Log.d(getClass().getName(), "running");
			int nextDelay = delay.nextDelay();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(srv.getBaseContext());
			Log.d(getClass().getName(), String.format("nextDelay=%d", nextDelay));
			boolean enabled = prefs.getBoolean("enable_checkbox", false);

			Log.d(getClass().getName(), String.format("enabled=%b", enabled));
			if (enabled && unreadNotifications()) {
				boolean volOverride = prefs.getBoolean("vol_override", false);
				Log.d(getClass().getName(), String.format("Beeping. Vol override=%b", volOverride));
				int originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
				if (volOverride) {
					Log.d(getClass().getName(),
							String.format("Original volume: %d, temp volume: %d", originalVolume, overriddenVolume));
					audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, overriddenVolume, 0);
					beep(overriddenVolume);
					audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalVolume, 0);
				} else {
					beep(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE ? -1 : originalVolume);
				}

				scheduleTask(nextDelay, this);
			}
		}

		private void beep(int volume) {
			if (volume > 0) {
				int convertedVolume = (int) (volume / (float) maxVolume * 100);
				Log.d(getClass().getName(), String.format("Volume: %d (%d/%d)", convertedVolume, volume, maxVolume));
				ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, convertedVolume);
				toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
			} else if (volume < 0) {
				Log.d(getClass().getName(), "Vibrating");
				vib.vibrate(200);
			}
		}

		private boolean unreadNotifications() {
			boolean unread = false;
			StatusBarNotification[] notifications = srv.getActiveNotifications();

			if (notifications != null) {
				for (StatusBarNotification notification : notifications) {
					if (notification.isClearable()) {
						unread = true;
						break;
					}
				}
			}

			return unread;
		}
	}

}
