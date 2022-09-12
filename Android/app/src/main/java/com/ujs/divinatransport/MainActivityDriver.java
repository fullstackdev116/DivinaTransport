package com.ujs.divinatransport;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.ujs.divinatransport.service.LocationService;
import com.ujs.divinatransport.Utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.ujs.divinatransport.service.NotificationCallbackDriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivityDriver extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    View header;
    DrawerLayout drawer;
    Toolbar mtoolbar;
    public View parentLayout;
    public ProgressDialog progressDialog;

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
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
        RatingBar ratingBar = header.findViewById(R.id.rate);
        ratingBar.setRating(Utils.cur_user.rate);
        ((TextView) header.findViewById(R.id.txt_name)).setText(Utils.cur_user.name);
        CircleImageView img_photo = header.findViewById(R.id.img_photo);
        Glide.with(this).load(Utils.cur_user.photo)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_avatar).centerCrop().dontAnimate()).into(img_photo);
        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_profile);
                closeDrawer();
            }
        });


        IntentFilter locationIntent = new IntentFilter("LocationIntent");
        registerReceiver(myReceiver, locationIntent);
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
            Utils.cur_location = location;
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
                Utils.FirebaseLogout();
                Utils.mUser = null;
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                requestPermissionLocation();
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