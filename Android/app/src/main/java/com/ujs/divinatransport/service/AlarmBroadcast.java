package com.ujs.divinatransport.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.Utils;

public class AlarmBroadcast extends BroadcastReceiver {
   @Override
   public void onReceive(Context context, Intent intent) {
       Utils.sendNotification(context, "Ride Order", "You have order(s) to ride today!", null, Utils.PASSENGER);
   }
}