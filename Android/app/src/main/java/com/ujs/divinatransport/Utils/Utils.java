package com.ujs.divinatransport.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ujs.divinatransport.Model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    //    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static FirebaseUser mUser = auth.getCurrentUser();

    public static String tbl_user = "tbl_user";
    public static String tbl_driving_license = "tbl_driving_license";
    public static String storage_user = "user/";
    public static String storage_driving_license = "driving_license/";

    public static User cur_user;
    public static String PHONE = "phone";
    public static String TYPE = "type";
    static Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+");

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
}
