package com.ujs.divinatransport.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.Utils;

import java.util.ArrayList;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String push_type = data.get("push_type");
        String user_id = data.get("user_id");
        String receiver_type = data.get("user_type");
        String body = data.get("body");
        String title = data.get("title");
        String room = data.get("room");

        if (push_type.equals(Utils.PUSH_CHAT)) {
            //App.setPreference(App.NewMessage, "true");
        } else if (push_type.equals(Utils.PUSH_RIDE)) {
//            App.setPreference(App.NewTest, "true");
        }

        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentClass = cn.getClassName();
        final String mainActivityDriver = getPackageName() + ".MainActivityDriver";
        final String mainActivityCustomer = getPackageName() + ".MainActivityCustomer";
        final String chatActivity = getPackageName() + ".ChatActivity";

        Utils.sendNotification(getApplicationContext(), title, body, data, receiver_type);
        if (currentClass.equals(mainActivityDriver)) {
            App.notificationCallbackDriver.OnReceivedNotification();
            return;
        } else if (currentClass.equals(mainActivityCustomer)) {
            App.notificationCallbackCustomer.OnReceivedNotification();
            return;
        }


    }
    @Override
    public void onNewToken(String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
        App.setPreference("DEVICE_TOKEN", token);
    }


}
