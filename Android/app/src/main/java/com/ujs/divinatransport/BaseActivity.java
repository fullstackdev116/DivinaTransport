package com.ujs.divinatransport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getName();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static boolean isAppWentToBg = false;

    public static boolean isWindowFocused = false;

    public static boolean isBackPressed = false;

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart isAppWentToBg " + isAppWentToBg);

        applicationWillEnterForeground();

        super.onStart();
    }

    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            App.setStatus(1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop ");
        applicationdidenterbackground();
    }

    public void applicationdidenterbackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            App.setStatus(0);
        }
    }

    @Override
    public void onBackPressed() {

        if ((this instanceof MainActivityDriver) | (this instanceof MainActivityCustomer)) {

        } else {
            isBackPressed = true;
        }

        Log.d(TAG,
                "onBackPressed " + isBackPressed + ""
                        + this.getLocalClassName());
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }

        super.onWindowFocusChanged(hasFocus);
    }

}