package com.ujs.divinatransport.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    //    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static FirebaseUser mUser = auth.getCurrentUser();

    public static String tbl_user = "tbl_user";
    public static String tbl_car = "tbl_car";
    public static String tbl_geo_car = "tbl_geo_car";
    public static String tbl_geo_customer = "tbl_geo_customer";
    public static String tbl_driving_license = "tbl_driving_license";
    public static String storage_user = "user/";
    public static String storage_car = "car/";
    public static String storage_driving_license = "driving_license/";

    public static GeoFire geo_customer = new GeoFire(Utils.mDatabase.child(Utils.tbl_geo_customer));
    public static GeoFire geo_car = new GeoFire(Utils.mDatabase.child(Utils.tbl_geo_car));

    public static int[] carTypes = new int[]{
        R.drawable.car_auris, R.drawable.car_avensis, R.drawable.car_camry, R.drawable.car_corolla, R.drawable.car_gt86,
                R.drawable.car_hiace, R.drawable.car_highlander, R.drawable.car_hilux, R.drawable.car_land_cruiser_200, R.drawable.car_land_cruiser_prado,
                R.drawable.car_prius, R.drawable.car_rav4, R.drawable.car_yaris};
    public static String[] carNames = new String[]{"Auris", "Avensis", "Camry", "Corolla", "GT86", "Hiace", "Highlander",
            "Hilux", "Land Cruiser 200", "Land Cruiser Prado", "Prius", "RAV4", "Yaris"};

    public static float geo_radius = 1; // km
    public static Location cur_location;
    public static User cur_user;
    public static String PHONE = "phone";
    public static String TYPE = "type";
    static Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+");

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
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
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
    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
}
