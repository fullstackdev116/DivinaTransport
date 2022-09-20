package com.ujs.divinatransport.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ujs.divinatransport.Utils.MyUtils;

public class AlarmBroadcast extends BroadcastReceiver {
   @Override
   public void onReceive(Context context, Intent intent) {
       MyUtils.sendNotification(context, "Ride Order", "You have order(s) to ride today!", null, MyUtils.PASSENGER);
   }
}