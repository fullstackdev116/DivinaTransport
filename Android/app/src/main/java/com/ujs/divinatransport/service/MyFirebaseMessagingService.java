package com.ujs.divinatransport.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.Utils.MyUtils;

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
        String sender_id = data.get("sender_id");
        String receiver_type = data.get("receiver_type");
        String body = data.get("body");
        String title = data.get("title");

        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentClass = cn.getClassName();
        final String mainActivityDriver = getPackageName() + ".MainActivityDriver";
        final String mainActivityCustomer = getPackageName() + ".MainActivityCustomer";
        final String chatActivity = getPackageName() + ".ChatActivity";

        if (currentClass.equals(chatActivity) && push_type.equals(MyUtils.PUSH_CHAT)) {
            return;
        }

        MyUtils.sendNotification(getApplicationContext(), title, body, data, receiver_type);

        if (push_type.equals(MyUtils.PUSH_CHAT)) {
            int cnt = App.readPreferenceInt(App.NewMessage, 0);
            App.setPreferenceInt(App.NewMessage, cnt+1);
        } else if (push_type.equals(MyUtils.PUSH_RIDE)) {
            int cnt = App.readPreferenceInt(App.NewRide, 0);
            App.setPreferenceInt(App.NewRide, cnt+1);
        }

        if (receiver_type.equals(MyUtils.DRIVER)) {
            App.notificationCallbackDriver.OnReceivedNotification();
        } else {
            App.notificationCallbackCustomer.OnReceivedNotification();
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
