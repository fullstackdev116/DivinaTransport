package com.ujs.divinatransport;

import static android.content.ContentValues.TAG;

import static com.ujs.divinatransport.Utils.MyUtils.tbl_geo_driver;
import static com.ujs.divinatransport.Utils.MyUtils.tbl_geo_passenger;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Toast;

//import com.microblink.MicroblinkSDK;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.ujs.divinatransport.Model.Message;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.Utils.MyUtils;
import com.ujs.divinatransport.service.NotificationCallbackCustomer;
import com.ujs.divinatransport.service.NotificationCallbackDriver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class App extends Application {

    public static App app;
    public static SharedPreferences prefs;
    private static Context mContext;
    public static NotificationCallbackDriver notificationCallbackDriver;
    public static NotificationCallbackCustomer notificationCallbackCustomer;
    public static String App_launched = "App_launched";
    public static String MY_APP_PATH = "";
    public static String MY_IMAGE_PATH = "";
    public static String MY_AUDIO_PATH = "";
    public static String ediapayUrl = "https://api.ediapay.com/api/";
    public static String rezomobiUrl = "https://api.rezo.mobi/api/";
    public static String ediaMerchantId = "4517";
    public static String ediaMobile_MNT = "2250564906603";
    public static String ediaMobile_ORANGE = "2250564906603";
    public static String ediaMobile_MOOV = "2250101019014";
    public static String ediaSMSUrl = "https://smpp1.valorisetelecom.com/api/api_http.php";
    public static String NewMessage = "NewMessage";
    public static String NewRide = "NewRide";


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        app = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        getFCMToken();
    }
    public static void goToChatPage(final Context context, final String user_id) {
        String myUid = MyUtils.cur_user.uid;
        final String roomId;
        int compare = myUid.compareTo(user_id);
        final String user1, user2;
        if (compare < 0) {
            user1 = myUid;
            user2 = user_id;
        } else {
            user1 = user_id;
            user2 = myUid;
        }
        roomId = user1 + user2;

        MyUtils.mDatabase.child(MyUtils.tbl_chat).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    MyUtils.mDatabase.child(MyUtils.tbl_chat).child(roomId).child("messages").push().setValue(new Message());
                    MyUtils.mDatabase.child(MyUtils.tbl_chat).child(roomId).child("isTyping").child(user1).setValue(false);
                    MyUtils.mDatabase.child(MyUtils.tbl_chat).child(roomId).child("isTyping").child(user2).setValue(false);
                }
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("roomId", roomId);
                context.startActivity(intent);

                setPreferenceInt(App.NewMessage, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void goToMainPage(final Activity activity, ProgressDialog progressDialog) {
        //get user info into model
        if (MyUtils.auth == null) return;
        MyUtils.mUser = MyUtils.auth.getCurrentUser();
        String phone_num = MyUtils.mUser.getPhoneNumber();
        if (phone_num.length() == 0) {
            MyUtils.FirebaseLogout();
            progressDialog.dismiss();
            return;
        }
        phone_num = phone_num.substring(1);
        MyUtils.mDatabase.child(MyUtils.tbl_user).orderByChild(MyUtils.PHONE).equalTo(phone_num)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            progressDialog.dismiss();
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                MyUtils.cur_user = datas.getValue(User.class);
                                MyUtils.cur_user.uid = datas.getKey();

                                DatabaseReference ref_status = MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("status");
                                OnDisconnect onDisconnect = ref_status.onDisconnect();
                                onDisconnect.setValue(0);
//token update
                                String token = MyUtils.getDeviceToken(activity);
                                MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("token").setValue(token);

                                if (MyUtils.cur_user.type.equals(MyUtils.DRIVER)) {
                                    if (MyUtils.cur_user.state == 0) { // missing signup license
                                        Intent myIntent = new Intent(activity, SignupActivityDriver.class);
                                        myIntent.putExtra("index_step", 2);
                                        activity.startActivity(myIntent);
                                    } else if (MyUtils.cur_user.state == 1) { // missing car info
                                        Intent myIntent = new Intent(activity, SignupActivityDriver.class);
                                        myIntent.putExtra("index_step", 3);
                                        activity.startActivity(myIntent);
                                    } else if (MyUtils.cur_user.state == 2) { // disabled
                                        MyUtils.FirebaseLogout();
                                        MyUtils.showAlert(activity, activity.getResources().getString(R.string.warning), activity.getResources().getString(R.string.please_wait_admin_enable_login));
                                    } else {  // enabled
                                        setStatus(1);
                                        DatabaseReference ref_geofire = MyUtils.mDatabase.child(tbl_geo_driver).child(MyUtils.cur_user.uid);
                                        OnDisconnect onDisconnectGeofire = ref_geofire.onDisconnect();
                                        onDisconnectGeofire.setValue(null);

                                        Intent myIntent = new Intent(activity, MainActivityDriver.class);
                                        activity.startActivity(myIntent);
                                        activity.finishAffinity();
                                    }
                                } else if (MyUtils.cur_user.type.equals(MyUtils.PASSENGER)) {
                                    if (MyUtils.cur_user.state == 2) { // disabled
                                        MyUtils.FirebaseLogout();
                                        MyUtils.showAlert(activity, activity.getResources().getString(R.string.warning), activity.getResources().getString(R.string.please_wait_admin_enable_login));
                                    } else {  // enabled
                                        setStatus(1);
                                        DatabaseReference ref_geofire = MyUtils.mDatabase.child(tbl_geo_passenger).child(MyUtils.cur_user.uid);
                                        OnDisconnect onDisconnectGeofire = ref_geofire.onDisconnect();
                                        onDisconnectGeofire.setValue(null);

                                        Intent myIntent = new Intent(activity, MainActivityCustomer.class);
                                        activity.startActivity(myIntent);
                                        activity.finishAffinity();
                                    }
                                }
                            }
                        } else { // go to signup intro
                            MyUtils.FirebaseLogout();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }
    public static void sendPushMessage(final String token, final String title, final String body, final String key, final Context context, String push_type, String sender_id, String receiver_type) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put("push_type", push_type);
                    data.put("sender_id", sender_id);
                    data.put("receiver_type", receiver_type);
                    data.put("body", body);
                    data.put("title", title);
                    data.put("key", key);
                    root.put("data", data);
                    root.put("to", token);

                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
//                    Toast.makeText(context, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(context, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    static String postToFCM(String bodyString) throws IOException {
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + MyUtils.fbServerKey)
                .build();
        OkHttpClient mClient = new OkHttpClient();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
    void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        setPreference("DEVICE_TOKEN", token);
                    }
                });
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d("MyApp", "App in background");
//        Toast.makeText(getApplicationContext(), "Background", Toast.LENGTH_SHORT).show();
        setStatus(2);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.d("MyApp", "App in foreground");
//        Toast.makeText(getApplicationContext(), "Foreground", Toast.LENGTH_SHORT).show();
        setStatus(1);
    }
    public static void setStatus(int status) {
        if (MyUtils.cur_user != null) {
            MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("status").setValue(status);
            MyUtils.cur_user.status = status;
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void RunAnimation(View v, Animation a) {
        a.reset();
        v.clearAnimation();
        v.startAnimation(a);
    }
    public static void setPreferenceInt(String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putInt(key, value)
                .commit();
    }
    public static int readPreferenceInt(String key, int defaultValue) {
        int value = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getInt(key, defaultValue);
        return value;
    }
    public static void setPreference(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(key, value)
                .commit();
    }
    public static String readPreference(String key, String defaultValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(key, defaultValue);
        return value;
    }
    public static void removePreference(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
    public static void setObjectPreference(String key, Object myObject) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myObject);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }
    public static Object readObjectPreference(String key, Type typeofT) {
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        Object obj = gson.fromJson(json, typeofT);
        return obj;
    }

    public static void social_share(Context context, String url) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
    }
    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.ujs.acer.Oyoo",  PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.e("hashkey -------------", hashKey); /// CkLR2IIEs9xCrDGJbKOQ/Jr3exE=
// release key hash: w6gx2BgXV0ybPMNC4PfbKnfpu50=
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    public static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static void dialNumber(String number, Context context)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
//            Uri uri = ussdToCallableUri(number);
//            intent.setData(uri);
            context.startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    private static Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }
    public static void openUrl (String url, Context context) {
        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(context, "Invalid url", Toast.LENGTH_SHORT);
            return;
        }
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static String getTimestampString()
    {
        long tsLong = System.currentTimeMillis();
        String ts =  Long.toString(tsLong);
        return ts;
    }
    public static int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = mContext.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        return primaryColor;
    }


}