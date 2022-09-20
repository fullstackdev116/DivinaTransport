package com.ujs.divinatransport.DriverMainFragments;

import static android.Manifest.permission.CALL_PHONE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.GeoUser;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.Model.RideReject;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kienht.bottomsheetbehavior.BottomSheetBehavior;
import com.ujs.divinatransport.Utils.MyUtils;
import com.ujs.divinatransport.directionhelpers.FetchURL;
import com.ujs.divinatransport.directionhelpers.TaskLoadedCallback;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRUpdateLocationCallBack;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRUpdateLocationCallBack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_driver_drive extends Fragment implements OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener {
    MainActivityDriver activity;
    BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    ProgressBar mProgressBar;
    Polyline polyline;
    long distance = 0, duration = 0, price = 0;
    CircleImageView img_photo;
    TextView txt_name, txt_cur_location, txt_start, txt_target, txt_distance, txt_duration, txt_price, txt_phone, txt_balance;
    LatLng pos_start, pos_target, pos_current;
    Marker marker_start, marker_target;
    LinearLayout ly_address, ly_sos, ly_ride, ly_button, ly_step_message1, ly_step_message2, ly_step_message3, ly_step_message4;
    RelativeLayout ly_ridingSteps, ly_payment_confirm;
    Ride sel_ride;
    User sel_passenger;

    ArrayList<GeoUser> arr_nearbyCar = new ArrayList<>();
    ArrayList<String> arr_nearbyPassengerKey = new ArrayList<>();
    ArrayList<String> arr_rejectRideKey = new ArrayList<>();
    boolean location_track = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_drive, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mProgressBar = v.findViewById(R.id.progress_bar);
        img_photo = v.findViewById(R.id.img_photo);
        txt_name = v.findViewById(R.id.txt_name);
        txt_cur_location = v.findViewById(R.id.txt_cur_location);
        txt_target = v.findViewById(R.id.txt_target);
        txt_start = v.findViewById(R.id.txt_start);
        txt_phone = v.findViewById(R.id.txt_phone);
        txt_distance = v.findViewById(R.id.txt_distance);
        txt_duration = v.findViewById(R.id.txt_duration);
        txt_price = v.findViewById(R.id.txt_price);
        ly_address = v.findViewById(R.id.ly_address);
        ly_sos = v.findViewById(R.id.ly_sos);
        ly_ride = v.findViewById(R.id.ly_ride);
        ly_ridingSteps = v.findViewById(R.id.ly_ridingSteps);
        ly_step_message1 = v.findViewById(R.id.ly_step_message1);
        ly_step_message2 = v.findViewById(R.id.ly_step_message2);
        ly_step_message3 = v.findViewById(R.id.ly_step_message3);
        ly_step_message4 = v.findViewById(R.id.ly_step_message4);
        ly_button = v.findViewById(R.id.ly_button);
        ly_payment_confirm = v.findViewById(R.id.ly_payment_confirm);
        txt_balance = v.findViewById(R.id.txt_balance);


        v.findViewById(R.id.btn_pay_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_ride.paid = 0;
                MyUtils.mDatabase.child(MyUtils.tbl_ride).child(sel_ride._id).setValue(sel_ride);
            }
        });
        v.findViewById(R.id.btn_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.goToChatPage(activity, sel_ride.passenger_id);
            }
        });
        v.findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ArrayList<String> arrPermissionRequests = new ArrayList<>();
                    arrPermissionRequests.add(CALL_PHONE);
                    ActivityCompat.requestPermissions(activity, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), activity.MY_PERMISSION_CALL);
                } else {
                    App.dialNumber(sel_passenger.phone, activity);
                }
            }
        });
        v.findViewById(R.id.btn_pay_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.warning))
                        .setMessage("Did you confirm the passenger's payment via cache?")
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sel_ride.paid = 2;
                                sel_ride.state = 3;
                                MyUtils.mDatabase.child(MyUtils.tbl_ride).child(sel_ride._id).setValue(sel_ride);
                                ly_payment_confirm.setVisibility(View.GONE);
                            }
                        }).show();
            }
        });

        ImageButton btn_menu = v.findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openDrawer();
            }
        });
        ImageButton btn_location = v.findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location_track = !location_track;
                String msg = "";
                if (!location_track) {
                    btn_location.setBackground(getResources().getDrawable(R.drawable.ic_location_view));
                    msg = "Location Tracking is disabled.";
                } else {
                    btn_location.setBackground(getResources().getDrawable(R.drawable.ic_location_track));
                    msg = "Location Tracking is enabled.";
//                    findMyLocation();
                }
                Snackbar.make(getView(), msg, 3000).show();
            }
        });

        ImageView img_expand = v.findViewById(R.id.expand_icon);
        bottomSheetBehavior = BottomSheetBehavior.from(v.findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                float rotation = 0;
                switch (i) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        rotation = 0;
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        rotation = 180;
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        rotation = 180;
                        break;
                }
                img_expand.animate().rotationX(rotation).start();
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        RelativeLayout ly_clickable = v.findViewById(R.id.clickable);
        ly_clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        View sheet_header_shadow = v.findViewById(R.id.sheet_header_shadow);
        NestedScrollView nestedScrollView = v.findViewById(R.id.description_scrollview);
        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v1, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                sheet_header_shadow.setActivated(v1.canScrollVertically(-1));
            }
        });
        Button btn_apply = v.findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure to apply?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        sel_ride.driver_id = MyUtils.cur_user.uid;
                        sel_ride.state = 1;
                        MyUtils.mDatabase.child(MyUtils.tbl_ride).child(sel_ride._id).setValue(sel_ride);
                        goMyRide();
                   }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        Button btn_reject = v.findViewById(R.id.btn_reject);
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure to reject?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//                        arr_rejectRideKey.add(sel_ride._id);
                        RideReject rideReject = new RideReject("", sel_ride._id, MyUtils.cur_user.uid, new Date());
                        String key = MyUtils.mDatabase.child(MyUtils.tbl_ride_reject).push().getKey();
                        MyUtils.mDatabase.child(MyUtils.tbl_ride_reject).child(key).setValue(rideReject);
                        if (marker_start!=null) marker_start.remove();
                        if (marker_target!=null) marker_target.remove();
                        if (polyline!=null) polyline.remove();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        activity.locationUpdateCallback = new MainActivityDriver.LocationUpdateCallback() {
            @Override
            public void locationUpdateCallback() {
//                Toast.makeText(activity, String.valueOf(Utils.cur_location.toString()), Toast.LENGTH_SHORT).show();
                mLastLocation = MyUtils.cur_location;
                addMarker(mMap, MyUtils.cur_location.getLatitude(), MyUtils.cur_location.getLongitude(), location_track, R.drawable.ic_car_green);
                MyUtils.geo_driver.setLocation(MyUtils.cur_user.uid, new GeoLocation(MyUtils.cur_location.getLatitude(), MyUtils.cur_location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Snackbar.make(getView(), error.getMessage(), 3000).show();
                        } else {
                            getListNearbyCars();
                            getListNearbyPassengers();
                        }
                    }
                });
                if (isFirstLoad) {
                    isFirstLoad = false;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyUtils.cur_location.getLatitude(), MyUtils.cur_location.getLongitude()), 16f));
                }
                checkMyRide1();
            }
        };

        return v;
    }
    void checkMyRide1() {
        if (sel_passenger != null) {
            if (!arr_nearbyPassengerKey.contains(sel_passenger.uid)) {
                sel_passenger = null;
                if (marker_start != null) marker_start.remove();
                if (marker_target != null) marker_target.remove();
                if (polyline != null) polyline.remove();
                if (pos_start != null) pos_start = null;
                if (pos_target != null) pos_target = null;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
        MyUtils.mDatabase.child(MyUtils.tbl_ride).orderByChild("driver_id").equalTo(MyUtils.cur_user.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for(DataSnapshot datas: snapshot.getChildren()){
                        Ride ride = datas.getValue(Ride.class);
                        ride._id = datas.getKey();
                        sel_ride = ride;
                        MyUtils.mDatabase.child(MyUtils.tbl_user).child(sel_ride.passenger_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    sel_passenger = snapshot.getValue(User.class);
                                    sel_passenger.uid = snapshot.getKey();
                                    goMyRide();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("color", "Error: " + error.getMessage());
                            }
                        });
                    }
                } else {
                    if (sel_passenger == null) {
                        ly_button.setVisibility(View.VISIBLE);
                        ly_ridingSteps.setVisibility(View.GONE);
                        ly_step_message1.setVisibility(View.GONE);
                        ly_step_message2.setVisibility(View.GONE);
                        ly_step_message3.setVisibility(View.GONE);
                        ly_step_message4.setVisibility(View.GONE);

                        if (marker_start != null) marker_start.remove();
                        if (marker_target != null) marker_target.remove();
                        if (polyline != null) polyline.remove();
                        if (pos_start != null) pos_start = null;
                        if (pos_target != null) pos_target = null;
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color", "Error: " + error.getMessage());
            }
        });
    }
    boolean isFirstLoad = true;
    void getNearByCarInfo(String key, GeoLocation location, boolean isRemove) {
        MyUtils.mDatabase.child(MyUtils.tbl_user).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    User user = snapshot.getValue(User.class);
                    user.uid = snapshot.getKey();
                    if (!user.uid.equals(MyUtils.cur_user.uid)) {
                        for (GeoUser geoUser : arr_nearbyCar) {
                            if (geoUser.user.uid.equals(user.uid)) {
                                arr_nearbyCar.remove(geoUser);
                                break;
                            }
                        }
                        if (!isRemove) {
                            GeoUser n_geoUser = new GeoUser(user, location);
                            arr_nearbyCar.add(n_geoUser);

                            mGeoLastLocationCar.put(key, location);
                            if (markerCountGeoCar.get(key) == null) {
                                markerCountGeoCar.put(key, 0);
                            }
                            addMarkerGeoCar(user.name, key, mMap, location.latitude, location.longitude, false, R.drawable.ic_car_yellow);
                        } else {
                            if (markerGeoCar.get(key) != null) {
                                markerGeoCar.get(key).remove();
                            }
                            markerCountGeoCar.put(key, 0);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color", "Error: " + error.getMessage());
            }
        });
    }
    void getNearByPassengerInfo(String key, GeoLocation location, boolean isRemove) {
        MyUtils.mDatabase.child(MyUtils.tbl_ride).orderByChild("passenger_id").equalTo(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for(DataSnapshot datas: snapshot.getChildren()){
                        Ride ride = datas.getValue(Ride.class);
                        ride._id = datas.getKey();
                        for (String pKey : arr_nearbyPassengerKey) {
                            if (ride.passenger_id.equals(pKey)) {
                                arr_nearbyPassengerKey.remove(pKey);
                                break;
                            }
                        }
                        if (!isRemove && !arr_rejectRideKey.contains(ride._id)) {
                            arr_nearbyPassengerKey.add(key);

                            mGeoLastLocationPassenger.put(key, location);
                            if (markerCountGeoPassenger.get(key) == null) {
                                markerCountGeoPassenger.put(key, 0);
                            }
                            addMarkerGeoPassenger(MyUtils.PASSENGER, key, mMap, location.latitude, location.longitude, false, R.drawable.ic_passenger);
                            if (ride.driver_id.length() == 0 || ride.driver_id.equals(MyUtils.cur_user.uid)) {
                                if (ride.state < 2) {
                                    int color = Color.BLUE;
                                    if (ride.isSOS) color = Color.RED;
                                    if (rippleGeoPassenger.get(key) == null)
                                        rippleGeoPassenger.put(key, MyUtils.initRadar(mMap, new LatLng(location.latitude, location.longitude), getContext(), color));
                                    else
                                        rippleGeoPassenger.get(key).withLatLng(new LatLng(location.latitude, location.longitude));
                                } else {
                                    if (rippleGeoPassenger.get(key) != null) {
                                        rippleGeoPassenger.get(key).stopRippleMapAnimation();
                                        rippleGeoPassenger.put(key, null);
                                    }
                                }
                            } else {
                                if (rippleGeoPassenger.get(key) != null) {
                                    rippleGeoPassenger.get(key).stopRippleMapAnimation();
                                    rippleGeoPassenger.put(key, null);
                                }
                            }

                        } else {
                            arr_nearbyPassengerKey.remove(key);
                            if (sel_passenger != null) {
                                if (sel_passenger.uid.equals(key)) {
                                    sel_passenger = null;
                                }
                            }
                            if (markerGeoPassenger.get(key) != null) {
                                markerGeoPassenger.get(key).remove();
                            }
                            if (rippleGeoPassenger.get(key) != null) {
                                rippleGeoPassenger.get(key).stopRippleMapAnimation();
                                rippleGeoPassenger.put(key, null);
                            }
                            markerCountGeoPassenger.put(key, 0);
                        }
                    }

                } else {
                    arr_nearbyPassengerKey.remove(key);
                    if (sel_passenger != null) {
                        if (sel_passenger.uid.equals(key)) {
                            sel_passenger = null;
                        }
                    }
                    if (markerGeoPassenger.get(key) != null) {
                        markerGeoPassenger.get(key).remove();
                    }
                    if (rippleGeoPassenger.get(key) != null) {
                        rippleGeoPassenger.get(key).stopRippleMapAnimation();
                        rippleGeoPassenger.put(key, null);
                    }
                    markerCountGeoPassenger.put(key, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color", "Error: " + error.getMessage());
            }
        });
    }

    private Location mLastLocation;
    private Location oldLocation;
    private int markerCount = 0;
    private Marker marker;

    Map<String, GeoLocation> mGeoLastLocationCar = new HashMap<String, GeoLocation>();
    Map<String, GeoLocation> oldGeoLocationCar = new HashMap<String, GeoLocation>();
    Map<String, Integer> markerCountGeoCar = new HashMap<String, Integer>();
    Map<String, Marker> markerGeoCar = new HashMap<String, Marker>();

    Map<String, GeoLocation> mGeoLastLocationPassenger = new HashMap<String, GeoLocation>();
    Map<String, GeoLocation> oldGeoLocationPassenger = new HashMap<String, GeoLocation>();
    Map<String, Integer> markerCountGeoPassenger = new HashMap<String, Integer>();
    Map<String, Marker> markerGeoPassenger = new HashMap<String, Marker>();
    Map<String, MapRipple> rippleGeoPassenger = new HashMap<String, MapRipple>();

    public void addMarker(GoogleMap googleMap, double lat, double lon, boolean isCamera, int drawable) {
        try {
            if (markerCount == 1) {
                if (oldLocation != null) {
                    new HRMarkerAnimation(googleMap, 1000, new HRUpdateLocationCallBack() {
                        @Override
                        public void onHRUpdatedLocation(Location updatedLocation) {
                            oldLocation = updatedLocation;
                        }
                    }).animateMarker(mLastLocation, oldLocation, marker, isCamera, true);
                } else {
                    oldLocation = mLastLocation;
                }
            } else if (markerCount == 0) {
                if (marker != null) {
                    marker.remove();
                }
                mMap = googleMap;

                LatLng latLng = new LatLng(lat, lon);

                marker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(drawable)));
                marker.setTitle("It's me");
                marker.setTag(MyUtils.cur_user.uid);
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCount = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addMarkerGeoCar(String title, String key, GoogleMap googleMap, double lat, double lon, boolean isCamera, int drawable) {
        try {
            if (markerCountGeoCar.get(key) == 1) {
                if (oldGeoLocationCar.get(key) != null) {
                    new GeoHRMarkerAnimation(googleMap, 1000, new GeoHRUpdateLocationCallBack() {
                        @Override
                        public void onGeoHRUpdatedLocation(GeoLocation updatedLocation) {
                            oldGeoLocationCar.put(key, updatedLocation);
                        }
                    }).animateMarkerGeo(mGeoLastLocationCar.get(key), oldGeoLocationCar.get(key), markerGeoCar.get(key), isCamera, true);
                } else {
                    oldGeoLocationCar.put(key, mGeoLastLocationCar.get(key));
                }
            } else if (markerCountGeoCar.get(key) == 0) {
                if (markerGeoCar.get(key) != null) {
                    markerGeoCar.get(key).remove();
                }
                mMap = googleMap;

                LatLng latLng = new LatLng(lat, lon);

                markerGeoCar.put(key, mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(drawable))));
                markerGeoCar.get(key).setTitle(title);
                markerGeoCar.get(key).setTag(key);
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCountGeoCar.put(key, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addMarkerGeoPassenger(String title, String key, GoogleMap googleMap, double lat, double lon, boolean isCamera, int drawable) {
        try {
            if (markerCountGeoPassenger.get(key) == 1) {
                if (oldGeoLocationPassenger.get(key) != null) {
                    new GeoHRMarkerAnimation(googleMap, 1000, new GeoHRUpdateLocationCallBack() {
                        @Override
                        public void onGeoHRUpdatedLocation(GeoLocation updatedLocation) {
                            oldGeoLocationPassenger.put(key, updatedLocation);
                        }
                    }).animateMarkerGeo(mGeoLastLocationPassenger.get(key), oldGeoLocationPassenger.get(key), markerGeoPassenger.get(key), isCamera, false);
                } else {
                    oldGeoLocationPassenger.put(key, mGeoLastLocationPassenger.get(key));
                }
            } else if (markerCountGeoPassenger.get(key) == 0) {
                if (markerGeoPassenger.get(key) != null) {
                    markerGeoPassenger.get(key).remove();
                }
                mMap = googleMap;

                LatLng latLng = new LatLng(lat, lon);

                markerGeoPassenger.put(key, mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(drawable))));
                markerGeoPassenger.get(key).setTitle(title);
                markerGeoPassenger.get(key).setTag(key);
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCountGeoPassenger.put(key, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getListNearbyCars() {
        GeoQuery geoQuery = MyUtils.geo_driver.queryAtLocation(new GeoLocation(MyUtils.cur_location.getLatitude(), MyUtils.cur_location.getLongitude()), MyUtils.geo_radius/1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getNearByCarInfo(key, location, false);
            }

            @Override
            public void onKeyExited(String key) {
                getNearByCarInfo(key, null, true);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                getNearByCarInfo(key, location, false);
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }
    private void getListNearbyPassengers() {
        GeoQuery geoQuery = MyUtils.geo_passenger.queryAtLocation(new GeoLocation(MyUtils.cur_location.getLatitude(), MyUtils.cur_location.getLongitude()), MyUtils.geo_radius/1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getNearByPassengerInfo(key, location, false);
            }

            @Override
            public void onKeyExited(String key) {
                getNearByPassengerInfo(key, null, true);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                getNearByPassengerInfo(key, location, false);
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

//        LatLng latLng = new LatLng(37.422f, -122.12f);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(U, 16f));
        if (MyUtils.cur_location != null) {
            addMarker(mMap, MyUtils.cur_location.getLatitude(), MyUtils.cur_location.getLongitude(), true, R.drawable.ic_car_green);
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });
        if (sel_passenger != null) goMyRide();
    }

    @Override
    public void onTaskDone(long distanceVal, long durationVal, Object... values) {
        if (polyline != null) {
            polyline.remove();
        }
        polyline = mMap.addPolyline((PolylineOptions) values[0]);
        // go to step1
        distance = distanceVal;
        duration = durationVal;
        price = Math.round(((float)distance/1000) * 100);

        DecimalFormat df = new DecimalFormat("0.00");
        txt_distance.setText(String.valueOf(df.format((float)distance/1000))+ " Km");
        txt_duration.setText(MyUtils.getDurationStr(duration));
        txt_price.setText(String.valueOf(price)+ " XOF");
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (marker.getTag() == null) return false;

        if (marker.getTag().toString().equals(MyUtils.cur_user.uid)) {
            marker.showInfoWindow();
            return true;
        }
        String key = marker.getTag().toString();
        if (arr_nearbyPassengerKey.contains(key)) {
            getPassengerInfo(key);

        }
        if (sel_ride != null) {
            ly_button.setVisibility(View.GONE);
        }
        return false;
    }

    void getRideInfo(String key) {
//        activity.showProgress();
        MyUtils.mDatabase.child(MyUtils.tbl_ride).orderByChild("passenger_id").equalTo(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activity.dismissProgress();
                if (snapshot.getValue() != null) {
                    for(DataSnapshot datas: snapshot.getChildren()){
                        sel_ride = datas.getValue(Ride.class);
                        sel_ride._id = datas.getKey();
                        if (sel_ride.driver_id.length() > 0 && !sel_ride.driver_id.equals(MyUtils.cur_user.uid)) {
                            Snackbar.make(getView(), "This passenger has already been taken by other driver!", 2000).show();
                        } else {
                            goMyRide();
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color", "Error: " + error.getMessage());
                activity.dismissProgress();
            }
        });
    }

    void goMyRide() {
        if (sel_ride.from_address.length() > 0 && txt_start.getText().toString().length() == 0) {
            txt_start.setText(sel_ride.from_address);
        }
        if (sel_ride.to_address.length() > 0 && txt_target.getText().toString().length() == 0) {
            txt_target.setText(sel_ride.to_address);
        }
        if (sel_ride.from_lat != 0 && sel_ride.from_lng != 0 && sel_ride.to_lat != 0 && sel_ride.to_lng != 0 &&
                pos_start == null) {
            pos_start = new LatLng(sel_ride.from_lat, sel_ride.from_lng);
            pos_target = new LatLng(sel_ride.to_lat, sel_ride.to_lng);
            if (sel_ride.from_address.length() > 0) {
                txt_start.setText(sel_ride.from_address);
            } else {
                new MyUtils.GetAddressTask(getContext(), txt_start, pos_start).execute();
            }
            if (sel_ride.to_address.length() > 0) {
                txt_target.setText(sel_ride.to_address);
            } else {
                new MyUtils.GetAddressTask(getContext(), txt_target, pos_target).execute();
            }
        }

        if (sel_ride.cur_lat != 0 && pos_current == null && txt_cur_location.getText().toString().length() == 0) {
            pos_current = new LatLng(sel_ride.cur_lat, sel_ride.cur_lng);
            new MyUtils.GetAddressTask(getContext(), txt_cur_location, pos_current).execute();
        }


        if (sel_ride.state > 0) {
            ly_ridingSteps.setVisibility(View.VISIBLE);
            ly_button.setVisibility(View.GONE);
            ly_step_message1.setVisibility(View.GONE);
            ly_step_message2.setVisibility(View.GONE);
            ly_step_message3.setVisibility(View.GONE);
            ly_step_message4.setVisibility(View.GONE);
            switch (sel_ride.state) {
                case 1:
                    ly_step_message1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ly_step_message2.setVisibility(View.VISIBLE);
                    if (sel_ride.paid == 1) {
                        ly_payment_confirm.setVisibility(View.VISIBLE);
                        txt_balance.setText(String.valueOf(sel_ride.price));
                    } else {
                        ly_payment_confirm.setVisibility(View.GONE);
                    }
                    break;
                case 3:
                    ly_step_message3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    ly_step_message4.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            ly_button.setVisibility(View.VISIBLE);
            ly_ridingSteps.setVisibility(View.GONE);
            ly_step_message1.setVisibility(View.GONE);
            ly_step_message2.setVisibility(View.GONE);
            ly_step_message3.setVisibility(View.GONE);
            ly_step_message4.setVisibility(View.GONE);
        }

        if (pos_start != null && pos_target != null) {
            ly_address.setVisibility(View.VISIBLE);
            ly_ride.setVisibility(View.VISIBLE);
            ly_sos.setVisibility(View.GONE);

            if (marker_start != null) marker_start.remove();
            marker_start = mMap.addMarker(new MarkerOptions().position(pos_start)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            marker_start.setTitle(txt_start.getText().toString());

            if (marker_target != null) marker_target.remove();
            marker_target = mMap.addMarker(new MarkerOptions().position(pos_target)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            marker_target.setTitle(txt_target.getText().toString());

            new FetchURL(activity, Fragment_driver_drive.this).execute(MyUtils.getDirectionUrl(pos_start, pos_target, "driving", activity), "driving");
            if (cameraMove) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_start, 16f));
                cameraMove = false;
            }

        } else {
            ly_address.setVisibility(View.GONE);
            ly_ride.setVisibility(View.GONE);
            ly_sos.setVisibility(View.VISIBLE);
        }
    }
    boolean cameraMove = true;
    void getPassengerInfo(String key) {
        activity.showProgress();
        MyUtils.mDatabase.child(MyUtils.tbl_user).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activity.dismissProgress();
                if (snapshot.getValue() != null) {
                    sel_passenger = snapshot.getValue(User.class);
                    sel_passenger.uid = snapshot.getKey();

                    Glide.with(activity).load(sel_passenger.photo).apply(new RequestOptions().placeholder(R.drawable.ic_avatar).centerCrop()).into(img_photo);
                    txt_name.setText(sel_passenger.name);
                    txt_phone.setText("+"+sel_passenger.phone);
                    getRideInfo(key);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color", "Error: " + error.getMessage());
                activity.dismissProgress();
            }
        });
    }

    void getRideRejects() {
        arr_rejectRideKey = new ArrayList<>();
//        Utils.mDatabase.child(Utils.tbl_ride_reject).orderByChild("driver_id").equalTo(Utils.cur_user.uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue() != null) {
//                    for(DataSnapshot datas: snapshot.getChildren()) {
//                        RideReject rideReject = datas.getValue(RideReject.class);
//                        rideReject._id = snapshot.getKey();
//                        arr_rejectRideKey.add(rideReject.ride_id);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("color", "Error: " + error.getMessage());
//            }
//        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getRideRejects();
        activity.refreshMenuBadge();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityDriver) context;
    }
}