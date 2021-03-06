package com.stephapps.smsxposed.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.stephapps.smsxposed.R;

public class DisplayNotificationReceiver extends BroadcastReceiver
{
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.i("ShowNotificationReceiver","onReceive");
		String[] actions = context.getResources().getStringArray(R.array.action_buttons_array);
		Bundle extras = intent.getExtras();
	   
		Notification paramNotif = extras.getParcelable("notification");
		Integer notificationId = extras.getInt("notification_id");
		boolean addShowBtn = extras.getBoolean("add_show_btn");
		boolean showSender = extras.getBoolean("show_sender");
		boolean showNotificationAction = extras.getBoolean("show_notification_action");
		boolean silentUpdatedNotification = extras.getBoolean("silent_notification",false);
		
		final String bigText = extras.getString("big_text", null);
		final String smsMsg = extras.getString("sms_msg", null);
    	final String smsSender = extras.getString("sms_sender", null);
    	Log.i("ShowNotificationReceiver","msg:"+smsMsg+", sender:"+smsSender);		
 		CharSequence sender;
 		if (!showSender) 	sender = extras.getCharSequence("content_title");
 		else 				sender = "    ";

 		Notification.Builder notifBuilder;
 		
 		if (addShowBtn)
 		{
	 		Intent showIntent = new Intent();
	 		showIntent.setAction("com.stephapps.smsxposed.shownotification_receiver");
	 		showIntent.putExtra("notification_id", notificationId);
	 		showIntent.putExtra("notification", (Notification)paramNotif);
	 		showIntent.putExtra("show_notification_action", showNotificationAction);
	 		showIntent.putExtra("add_show_btn", false);
	 		showIntent.putExtra("sms_sender", smsSender);
	 		showIntent.putExtra("sms_msg", smsMsg);
	 		showIntent.putExtra("package_name", extras.getString("package_name"));
	 		showIntent.putExtra("ticker", paramNotif.tickerText);
	 		showIntent.putExtra("content_title", extras.getCharSequence("content_title"));
	 		showIntent.putExtra("content_text", extras.getCharSequence("content_text"));
	 		showIntent.putExtra("silent_notification", true);
  	 		PendingIntent pendingShowIntent = PendingIntent.getBroadcast(context, 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT );		
 	 		
  	 		notifBuilder = new Notification.Builder(context)
			.setWhen(paramNotif.when)
	        .setTicker("    ")
	        .setLargeIcon(paramNotif.largeIcon)
	        .setContentTitle(sender)
	        .setAutoCancel(true)
	        .setContentIntent(paramNotif.contentIntent)
	        .setPriority(paramNotif.priority)
	        .setDeleteIntent(paramNotif.deleteIntent)
	        .setContentText("    ")
	        .addAction(android.R.drawable.ic_menu_view, context.getResources().getStringArray(R.array.action_buttons_array)[3], pendingShowIntent);
 		}
 		else
 		{
 			if (extras.getBoolean("show_notification_action")==true)
 			{				
 				Intent callIntent = new Intent(Intent.ACTION_CALL);
 				callIntent.setData(Uri.parse("tel:" + smsSender));
 				PendingIntent pendingCallIntent = PendingIntent.getActivity(context, 0, callIntent, 0);
 				
 				Intent respondIntent = new Intent(); 
 				respondIntent.putExtra("sms_sender", smsSender);
 				respondIntent.putExtra("sms_msg", smsMsg);
 				respondIntent.putExtra("notification_id", notificationId);
 				respondIntent.putExtra("package_name", extras.getString("package_name"));	
 				respondIntent.setAction("com.stephapps.smsxposed.quickresponse_receiver");
 				PendingIntent pendingRespondIntent = PendingIntent.getBroadcast(context, 0, respondIntent, PendingIntent.FLAG_UPDATE_CURRENT);		    	     
 		
 				Intent markAsReadIntent = new Intent();
 				markAsReadIntent.putExtra("sms_sender", smsSender);
 				markAsReadIntent.putExtra("sms_msg", smsMsg);
 				markAsReadIntent.putExtra("notification_id", notificationId);
 				markAsReadIntent.putExtra("package_name", extras.getString("package_name"));
 				markAsReadIntent.setAction("com.stephapps.smsxposed.markasread_receiver");
 			    PendingIntent pendingIntentMarkAsRead = PendingIntent.getBroadcast(context, 0, markAsReadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
 		
 				notifBuilder = new Notification.Builder(context)
 				.setWhen(paramNotif.when)
 		        .setTicker(extras.getCharSequence("ticker"))
 		        .setLargeIcon(paramNotif.largeIcon)
 		        .setContentTitle(extras.getCharSequence("content_title"))
 		        .setContentIntent(paramNotif.contentIntent)
 		        .setPriority(paramNotif.priority)
 		        .setAutoCancel(true)       
 		        .setDeleteIntent(paramNotif.deleteIntent)
 		        .setContentText(extras.getCharSequence("content_text"))
 		        .addAction(android.R.drawable.ic_menu_call, actions[0], pendingCallIntent)
 		        .addAction(android.R.drawable.ic_menu_send, actions[1], pendingRespondIntent)
 		        .addAction(android.R.drawable.checkbox_on_background, actions[2], pendingIntentMarkAsRead);
 			}
 			else
 			{
 				notifBuilder = new Notification.Builder(context)
 				.setWhen(paramNotif.when)
 		        .setTicker(extras.getCharSequence("ticker"))
 		        .setLargeIcon(paramNotif.largeIcon)
 		        .setContentTitle(extras.getCharSequence("content_title"))
 		        .setContentIntent(paramNotif.contentIntent)
 		        .setPriority(paramNotif.priority)
 		        .setAutoCancel(true)
 		        .setDeleteIntent(paramNotif.deleteIntent)
 		        .setContentText(extras.getCharSequence("content_text"));		
 			}
 			
 			if ( (bigText!=null) && (!bigText.equals("")) )
					notifBuilder.setStyle(new Notification.BigTextStyle().bigText(extras.getCharSequence("big_text"))); 	
 			
 			if (silentUpdatedNotification==false)
			{
				notifBuilder.setSound(paramNotif.sound);
				notifBuilder.setDefaults(paramNotif.defaults);
			}
 		}
 		
 		if (extras.getString("package_name").equals("com.google.android.talk"))
			notifBuilder.setSmallIcon(R.drawable.stat_notify_chat);
		else
			notifBuilder.setSmallIcon(R.drawable.stat_notify_sms);
			
			
 		
 		Notification newNotif = notifBuilder.build();

		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId,newNotif);	
	}
	
	
}