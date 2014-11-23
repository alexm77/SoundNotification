package org.bug.soundnotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationService extends NotificationListenerService {
	private final Scheduler sch = new Scheduler(this);

	private static boolean started;

	@Override
	public void onNotificationPosted(StatusBarNotification statusBarNotification) {
		Log.d(getClass().getName(), "Posted: " + statusBarNotification);
		triggerScheduler();
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
		// don't care
	}

	private void triggerScheduler() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean enabled = prefs.getBoolean("enable_checkbox", false);
		if (enabled) {
			sch.init();
		}
	}

	@Override
	public void onCreate() {
		Log.d(getClass().getName(), "Create");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(getClass().getName(), String.format("Start %s, %d, %d", intent, flags, startId));
//		NotificationCompat.Builder mBuilder =
//				new NotificationCompat.Builder(this)
//						.setSmallIcon(R.drawable.ic_launcher)
//						.setContentTitle("Sound notification")
//						.setContentText("running")
//						.setOngoing(true);
//		NotificationManager notifMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		notifMgr.notify(13, mBuilder.build());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(getClass().getName(), "Destroy");
		super.onDestroy();
		setStarted(false);
	}

	public static void setStarted(boolean started) {
		NotificationService.started = started;
	}

	public static boolean isStarted() {

		return started;
	}
}
