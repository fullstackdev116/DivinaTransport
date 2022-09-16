package com.ujs.divinatransport.Utils;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.arsy.maps_library.MapRipple;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.directionhelpers.TaskLoadedCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String fbServerKey = "AAAAELRrj3c:APA91bEIkxYtkwb1iS3zjmJmsh9YBF2HYL4dqoxU1gh5h30q8dNl4pdRznMuH9aYfPhuRye1IM5IldM6OlMzUYMTiCjsxvoXiNxw0By7RJcz_MtJdIHaX75836tklpel6qXkYeQyz2yp";

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    //    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static FirebaseUser mUser = auth.getCurrentUser();

    public static String tbl_user = "tbl_user";
    public static String tbl_car = "tbl_car";
    public static String tbl_ride = "tbl_ride";
    public static String tbl_chat = "tbl_chat";
    public static String tbl_order = "tbl_order";
    public static String tbl_history = "tbl_history";
    public static String tbl_ride_reject = "tbl_ride_reject";
    public static String tbl_geo_driver = "tbl_geo_driver";
    public static String tbl_geo_passenger = "tbl_geo_passenger";
    public static String tbl_driving_license = "tbl_driving_license";
    public static String tbl_transaction = "tbl_transaction";
    public static String storage_user = "user/";
    public static String storage_car = "car/";
    public static String storage_chat = "chat/";
    public static String storage_driving_license = "driving_license/";

    public static GeoFire geo_passenger = new GeoFire(Utils.mDatabase.child(Utils.tbl_geo_passenger));
    public static GeoFire geo_driver = new GeoFire(Utils.mDatabase.child(Utils.tbl_geo_driver));

    public static LatLng basePos = new LatLng(5.3941, -3.9716);
    public static int RIDE_STEP0 = 0, RIDE_STEP1 = 1, RIDE_STEP2 = 2, RIDE_STEP3 = 3, RIDE_STEP4 = 4;
    // ---------------- post ------------- apply --------- pay ---------- arrive --------- review ---------

    public static int[] carTypes = new int[]{
        R.drawable.car_auris, R.drawable.car_avensis, R.drawable.car_camry, R.drawable.car_corolla, R.drawable.car_gt86,
                R.drawable.car_hiace, R.drawable.car_highlander, R.drawable.car_hilux, R.drawable.car_land_cruiser_200, R.drawable.car_land_cruiser_prado,
                R.drawable.car_prius, R.drawable.car_rav4, R.drawable.car_yaris};
    public static String[] carNames = new String[]{"Auris", "Avensis", "Camry", "Corolla", "GT86", "Hiace", "Highlander",
            "Hilux", "Land Cruiser 200", "Land Cruiser Prado", "Prius", "RAV4", "Yaris"};

    static Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+");

    public static int geo_radius = 1000; // m
    public static Location cur_location;
    public static User cur_user;
    public static String PHONE = "phone";
    public static String TYPE = "type";
    public static String MERCHANTID = "MERCHANTID";
    public static String DRIVER = "DRIVER";
    public static String PASSENGER = "PASSENGER";

    public static String PUSH_CHAT = "PUSH_CHAT";
    public static String PUSH_RIDE = "PUSH_RIDE";

    public static String MY_CAR = "MY_CAR";
    public static String MY_DRIVER = "MY_DRIVER";
    public static String MY_RIDE = "MY_RIDE";
    public static String MY_PASSENGER = "MY_PASSENGER";
//    public static String MY_RIDE_SELECTED = "MY_RIDE_SELECTED";
//    public static String MY_PASSENGER_SELECTED = "MY_PASSENGER_SELECTED";

    public static MapRipple initRadar(GoogleMap mMap, LatLng latLng, Context context, int color){
        MapRipple mapRipple = new MapRipple(mMap, latLng, context);
//        mapRipple.withNumberOfRipples(3);
//        mapRipple.withFillColor(color);
        mapRipple.withStrokeColor(color);
        mapRipple.withStrokewidth(50);      // 10dp
        mapRipple.withDistance(Utils.geo_radius);
//        mapRipple.withRippleDuration(12000);    //12000ms
//        mapRipple.withTransparency(0.5f);
        mapRipple.startRippleMapAnimation();
        return mapRipple;
    }

    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }
    public static void sendNotification(Context context, String title, String body, Map<String, String> data, String userType) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_my_taxi);
        Intent intent;
        if (userType.equals(Utils.DRIVER)) {
            intent = new Intent(context, MainActivityDriver.class);
        } else {
            intent = new Intent(context, MainActivityCustomer.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo(title)
                .setLargeIcon(icon)
                .setColor(Color.BLUE)
                .setLights(Color.BLUE, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_my_taxi);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel is required for Android O and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
//            );
//            channel.setDescription("channel description");
//            channel.setShowBadge(true);
//            channel.canShowBadge();
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
//            notificationManager.createNotificationChannel(channel);
//        }

        notificationManager.notify(0, notificationBuilder.build());
    }
    public static void FirebaseLogout() {
        FirebaseAuth.getInstance().signOut();
    }
    public static boolean isValidEmail(String email)
    {
        Matcher m = emailPattern.matcher(email);
        return m.matches();
    }
    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    public static String getChatUserId(String roomId) {
        String user_id;
        int index = roomId.indexOf(Utils.cur_user.uid);
        if (index == 0) {
            user_id = roomId.substring(Utils.cur_user.uid.length());
        } else {
            user_id = roomId.substring(0, roomId.length()-Utils.cur_user.uid.length());
        }
        return user_id;
    }

    public static void showTextViewMessage(TextView textView, String message) {
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.GONE);
            }
        }, 3000);
    }
    public static Boolean isEmptyEditText(EditText editText) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return true;
        } else {
            return false;
        }
    }
    public static String getDeviceToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("DEVICE_TOKEN", "");
    }
    public Bitmap createCustomMarker(Context context, int layout) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(20, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }
    public static String getDirectionUrl(LatLng origin, LatLng dest, String directionMode, Context context) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + context.getResources().getString(R.string.google_api_key_billed);
        return url;
    }
    public static String getAddressByLatLng(LatLng latLng, Context context) {
        Geocoder geocoder;
        String address = "undefined address";
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
//            Toast.makeText(context, "The address can't be recognized.", Toast.LENGTH_SHORT).show();
        }
        return address;
    }
    public static class GetAddressTask extends AsyncTask<String, Void, String> {
        Context mContext;
        String address = "";
        LatLng latLng;
        AutoCompleteTextView autoCompleteTextView;
        TextView textView;
        String UNDEFILED_ADDRESS = "undefined address, try again please!";

        public GetAddressTask(Context context, AutoCompleteTextView autoCompleteTextView, LatLng latLng) {
            this.mContext = context;
            this.latLng = latLng;
            this.autoCompleteTextView = autoCompleteTextView;
        }
        public GetAddressTask(Context context, TextView textView, LatLng latLng) {
            this.mContext = context;
            this.latLng = latLng;
            this.textView = textView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {
            Geocoder geocoder;
            String address = UNDEFILED_ADDRESS;
            List<Address> addresses;
            geocoder = new Geocoder(mContext, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                address = addresses.get(0).getAddressLine(0);
            } catch (Exception e) {
//                Toast.makeText(mContext, "The address can't be recognized. Please try again!", Toast.LENGTH_SHORT).show();
            }
            this.address = address;
            return address;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            if (autoCompleteTextView != null) {
                if (address.equals(UNDEFILED_ADDRESS)) {
                    autoCompleteTextView.setHint(address);
                } else {
                    autoCompleteTextView.setText(address);
                }
            }
            if (textView != null)
                if (address.equals(UNDEFILED_ADDRESS)) {
                    textView.setHint(address);
                } else {
                    textView.setText(address);
                }

        }
    }

    public static String getDistanceStr(long distance) {
        if (distance > 1000) {
            return String.valueOf((float)distance/1000)+"Km";
        } else {
            return String.valueOf(distance) + "m";
        }
    }

    public static String getDateString(Date date) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String sdt = df.format(date);
        return sdt;
    }
    public static String getTimeString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(date);
    }
    public static String getRandomStringUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    public static void setAlarm(Context context, Date date, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,intent, PendingIntent.FLAG_ONE_SHOT);

        Calendar c = Calendar.getInstance();//TimeZone.getTimeZone("GMT"));
        date.setHours(7);
        date.setMinutes(0);
        c.setTime(date);
        c.set(Calendar.SECOND, 0);

        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }
    public static void copy_text(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }
    public static String getDurationStr(long seconds) {
        String str = "";
        int d, h, m, s;
        d = (int)seconds/(3600*24);
        h = ((int)seconds%(3600*24))/3600;
        m = ((int)seconds%(3600*24)%3600)/60+1;
        s = (((int)seconds%(3600*24)%3600))%60;
        if (d > 0) {
            if (d > 1) {
                str += String.valueOf(d)+"days ";
            } else {
                str += String.valueOf(d)+"day ";
            }
        }
        if (h > 0) {
            if (h > 1) {
                str += String.valueOf(h)+"hours ";
            } else {
                str += String.valueOf(h)+"hour ";
            }
        }
        if (m > 0) {
            if (m > 1) {
                str += String.valueOf(m)+"mins";
            } else {
                str += String.valueOf(m)+"min";
            }
        }
//        if (s > 0) str += String.valueOf(s)+"Seconds";
        return str;
    }
}
