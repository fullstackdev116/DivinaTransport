package com.ujs.divinatransport;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.ujs.divinatransport.service.LocationService;
import com.ujs.divinatransport.Utils.MyUtils;
import com.google.android.material.navigation.NavigationView;
import com.ujs.divinatransport.service.NotificationCallbackDriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivityDriver extends BaseActivity {
    private AppBarConfiguration mAppBarConfiguration;
    View header;
    DrawerLayout drawer;
    Toolbar mtoolbar;
    public View parentLayout;
    public ProgressDialog progressDialog;
    TextView new_message, new_ride;
    CircleImageView img_photo;

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int MY_PERMISSION_STORAGE = 201;
    public final static int MY_PERMISSION_CALL = 301;
    Intent locationIntent;

    public interface LocationUpdateCallback {
        void locationUpdateCallback();
    }
    public LocationUpdateCallback locationUpdateCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        progressDialog = new ProgressDialog(this);
        parentLayout = findViewById(android.R.id.content);
        mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);//(binding.appBarMain.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_drive, R.id.nav_set_car, R.id.nav_orders, R.id.nav_history, R.id.nav_message, R.id.nav_rewards)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main1);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.nav_drive) {
                    mtoolbar.setVisibility(View.GONE);
                    return;
                }
                mtoolbar.setVisibility(View.VISIBLE);
                toggle.setDrawerIndicatorEnabled(false);
                toggle.setHomeAsUpIndicator(R.drawable.ic_back);
            }
        });
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle.setDrawerIndicatorEnabled(true);
                navController.navigate(R.id.nav_drive);
            }
        });

        // Header View
        header = navigationView.getHeaderView(0);
        header.findViewById(R.id.img_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                do_logout();
            }
        });
        img_photo = header.findViewById(R.id.img_photo);
        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_profile);
                closeDrawer();
            }
        });
        setProfile();

        IntentFilter locationIntent = new IntentFilter("LocationIntent");
        registerReceiver(myReceiver, locationIntent);

        setStoragePermission();
        setPhoneCallPermission();

        new_message =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_message));
        new_message.setGravity(Gravity.CENTER_VERTICAL);
        new_message.setTypeface(null, Typeface.BOLD);
        new_message.setText("");
        new_message.setTextColor(Color.RED);

        new_ride =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_orders));
        new_ride.setGravity(Gravity.CENTER_VERTICAL);
        new_ride.setTypeface(null, Typeface.BOLD);
        new_ride.setText("");
        new_ride.setTextColor(Color.RED);

        App.notificationCallbackDriver = new NotificationCallbackDriver() {
            @Override
            public void OnReceivedNotification() {
                refreshMenuBadge();
            }
        };
    }
    public void setProfile() {
        RatingBar ratingBar = header.findViewById(R.id.rate);
        ratingBar.setRating(MyUtils.cur_user.rate);
        ((TextView) header.findViewById(R.id.txt_name)).setText(MyUtils.cur_user.name);
        Glide.with(this).load(MyUtils.cur_user.photo).override(150, 150)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_avatar_white).centerCrop().dontAnimate()).into(img_photo);
    }
    public void refreshMenuBadge() {
        int cnt = App.readPreferenceInt(App.NewMessage, 0);
        int cnt1 = App.readPreferenceInt(App.NewRide, 0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cnt > 0) {
                    new_message.setText(String.valueOf(cnt)+"+");
                } else {
                    new_message.setText("");
                }
                if (cnt1 > 0) {
                    new_ride.setText(String.valueOf(cnt1)+"+");
                } else {
                    new_ride.setText("");
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(parentLayout, getResources().getString(R.string.please_enable_location_service), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            requestPermissionLocation();
            return;
        }
        locationIntent = new Intent(this, LocationService.class);
        startForegroundService(locationIntent);
    }
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = (Location) intent.getParcelableExtra("loc");
            MyUtils.cur_location = location;
            locationUpdateCallback.locationUpdateCallback();
        }
    };

    void do_logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to logout?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (locationIntent != null) {
                    unregisterReceiver(myReceiver);
                    stopService(locationIntent);
                }
                App.setStatus(0);
                MyUtils.FirebaseLogout();
                MyUtils.mUser = null;
                Intent intent = new Intent(MainActivityDriver.this, SplashActivity.class);
                startActivity(intent);
                ActivityCompat.finishAffinity(MainActivityDriver.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure to quite the app?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.finishAffinity(MainActivityDriver.this);
                    System.exit(0);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

    }
    public void setPhoneCallPermission() {
        if (ContextCompat.checkSelfPermission(this, CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ArrayList<String> arrPermissionRequests = new ArrayList<>();
            arrPermissionRequests.add(CALL_PHONE);
            ActivityCompat.requestPermissions(this, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), MY_PERMISSION_CALL);
        } else {

        }
    }
    public void setStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ArrayList<String> arrPermissionRequests = new ArrayList<>();
            arrPermissionRequests.add(WRITE_EXTERNAL_STORAGE);
            arrPermissionRequests.add(READ_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), MY_PERMISSION_STORAGE);
            return;
        } else {
            createDirectory();
        }
    }
    void createDirectory() {
        getExternalFilesDir(null);
        File assets = getExternalFilesDir("assets");
        if (!assets.exists()) {
            assets.mkdir();
        }
        App.MY_APP_PATH = assets.getAbsolutePath();
        File f3 = new File(App.MY_APP_PATH, "audio");
        if (!f3.exists()) {
            f3.mkdir();
        }
        App.MY_AUDIO_PATH = f3.getAbsolutePath();

        File pictures = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures", "DivinaTransport");
        if (!pictures.exists()) {
            pictures.mkdir();
        }
        App.MY_IMAGE_PATH = pictures.getAbsolutePath();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main1);
        assert fragment != null;
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] _permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, _permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == MY_PERMISSION_FINE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    requestPermissionLocation();
                }
            } else if (requestCode == MY_PERMISSION_STORAGE) {
                createDirectory();
            } else if (requestCode == MY_PERMISSION_CALL) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivityDriver.this, "Please allow phone call permission!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void requestPermissionLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
        }
    }
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}