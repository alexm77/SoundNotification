package org.bug.soundnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceBootstrap extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(getClass().getName(), "Received " + context + " " + intent);
		try {
			if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) &&
					!NotificationService.isStarted()) {
				Intent serviceIntent = new Intent(context, NotificationService.class);
				context.startService(serviceIntent);
				NotificationService.setStarted(true);
			}
		} catch (Exception e) {
			Log.e(getClass().getName(), "Receive error", e);
		}
	}
}
