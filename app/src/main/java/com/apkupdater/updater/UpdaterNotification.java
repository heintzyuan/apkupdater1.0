package com.apkupdater.updater;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.apkupdater.R;
import com.apkupdater.model.Constants;

import java.util.concurrent.atomic.AtomicInteger;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class UpdaterNotification
{
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotificationBuilder;
	private Context mContext;
	private int mMaxApps;
	private AtomicInteger mNumApps;
	private UpdaterOptions mOptions;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public UpdaterNotification(
		Context context,
	    int maxApps
	) {
		mContext = context;
		mMaxApps = maxApps;
		mOptions = new UpdaterOptions(mContext);
		mNumApps = new AtomicInteger(0);

		// Check if we should do notifications
		if (!doNotification(1)) {
			return;
		}

		createNotification();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setMaxApps(
		int maxApps
	) {
		mMaxApps = maxApps;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void increaseProgress(
		int numberOfUpdates
	) {
		int num = mNumApps.incrementAndGet();

		// Check if we should do notifications
		if (!doNotification(numberOfUpdates)) {
			return;
		}

		updateNotification(mMaxApps, num);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void finishNotification(
		int numberOfUpdates
	) {
		// Prevent NPE
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		mNotificationManager.cancelAll();

		// Check if we should do notifications
		if (!doNotification(numberOfUpdates)) {
			return;
		}

		String s = mContext.getString(R.string.notification_update_content_finished).replace("$1", String.valueOf(numberOfUpdates));
		mNotificationBuilder.setProgress(0, 0, false);
		mNotificationBuilder.setContentTitle(mContext.getString(R.string.notification_update_title_finished));
		mNotificationBuilder.setContentText(s);
		mNotificationManager.notify(Constants.UpdaterNotificationId, mNotificationBuilder.build());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void failNotification(
	) {
		// Check if we should do notifications
		if (!doNotification(1)) {
			return;
		}

		mNotificationBuilder.setProgress(0, 0, false);
		mNotificationBuilder.setContentTitle(mContext.getString(R.string.notification_update_title_failed));
		mNotificationBuilder.setContentText(null);
		
		// Prevent NPE
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		mNotificationManager.notify(Constants.UpdaterNotificationId, mNotificationBuilder.build());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private boolean doNotification(
		int numberOfUpdates
	) {
		if (mOptions.getNotificationOption().equals(mContext.getString(R.string.notification_always))) {
			return true;
		} else if (mOptions.getNotificationOption().equals(mContext.getString(R.string.notification_never))) {
			return false;
		} else if (mOptions.getNotificationOption().equals(mContext.getString(R.string.notification_if_at_least_1))){
			return numberOfUpdates > 0;
		} else {
			// TODO: Check for background option
			return false;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void createNotification(
	) {
		mNotificationBuilder = new NotificationCompat.Builder(mContext);
		mNotificationBuilder.setContentTitle(mContext.getString(R.string.notification_update_title));
		mNotificationBuilder.setSmallIcon(R.drawable.ic_update);
		mNotificationBuilder.setContentIntent(createPendingIntent());
		mNotificationBuilder.setAutoCancel(true);
		mNotificationBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
		
		// Prevent NPE
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		mNotificationManager.notify(Constants.UpdaterNotificationId, mNotificationBuilder.build());
		updateNotification(mMaxApps, 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void updateNotification(
		int max,
		int progress
	) {
		mNotificationBuilder.setContentText(getNotificationProgressString(max, progress));
		mNotificationBuilder.setProgress(max, progress, false);
		
		// Prevent NPE
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		mNotificationManager.notify(Constants.UpdaterNotificationId, mNotificationBuilder.build());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private PendingIntent createPendingIntent(
	) {
		Intent intent = new Intent("com.apkupdater.notification");
		intent.setFlags(0);
		return PendingIntent.getBroadcast(mContext, 0, intent, 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private String getNotificationProgressString(
		int max,
		int progress
	) {
		String s = mContext.getString(R.string.notification_update_content);
		s = s.replace("$1", String.valueOf(progress));
		s = s.replace("$2", String.valueOf(max));
		return s;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
