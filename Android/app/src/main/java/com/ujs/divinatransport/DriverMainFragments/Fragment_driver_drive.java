package com.ujs.divinatransport.DriverMainFragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.GeoUser;
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
import com.ujs.divinatransport.Utils.Utils;
import com.ujs.divinatransport.directionhelpers.TaskLoadedCallback;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRUpdateLocationCallBack;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRUpdateLocationCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Fragment_driver_drive extends Fragment implements OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener {
    MainActivityDriver activity;
    BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    ProgressBar mProgressBar;
    Polyline polyline;
    long distance = 0, duration = 0, price = 0;


//    GeoQuery geoQuery;
    ArrayList<GeoUser> arr_nearby = new ArrayList<>();
    boolean location_track = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_drive, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mProgressBar = v.findViewById(R.id.progress_bar);

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
        Button btn_cancel = v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                removeRoad();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        activity.locationUpdateCallback = new MainActivityDriver.LocationUpdateCallback() {
            @Override
            public void locationUpdateCallback() {
//                Toast.makeText(activity, String.valueOf(Utils.cur_location.toString()), Toast.LENGTH_SHORT).show();
                mLastLocation = Utils.cur_location;
                addMarker(mMap, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude(), location_track, R.drawable.ic_car_orange);
                Utils.geo_car.setLocation(Utils.cur_user.uid, new GeoLocation(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Snackbar.make(getView(), error.getMessage(), 3000).show();
                        } else {
                            getListNearbyCars();
                        }
                    }
                });
            }
        };
        return v;
    }

    void getNearByInfo(String key, GeoLocation location, boolean isRemove) {
        Utils.mDatabase.child(Utils.tbl_user).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    User user = snapshot.getValue(User.class);
                    user.uid = snapshot.getKey();
                    if (!user.uid.equals(Utils.cur_user.uid)) {
                        for (GeoUser geoUser : arr_nearby) {
                            if (geoUser.user.uid.equals(user.uid)) {
                                arr_nearby.remove(geoUser);
                                break;
                            }
                        }
                        if (!isRemove) {
                            GeoUser n_geoUser = new GeoUser(user, location);
                            arr_nearby.add(n_geoUser);

                            mGeoLastLocation.put(key, location);
                            if (markerCountGeo.get(key) == null) {
                                markerCountGeo.put(key, 0);
                            }
                            addMarkerGeo(user.name, key, mMap, location.latitude, location.longitude, false, R.drawable.ic_car_dark);
                        } else {
                            markerGeo.get(key).remove();
                            markerCountGeo.put(key, 0);
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

    private Location mLastLocation;
    private Location oldLocation;
    private int markerCount = 0;
    private Marker marker;

    Map<String, GeoLocation> mGeoLastLocation = new HashMap<String, GeoLocation>();
    Map<String, GeoLocation> oldGeoLocation = new HashMap<String, GeoLocation>();
    Map<String, Integer> markerCountGeo = new HashMap<String, Integer>();
    Map<String, Marker> markerGeo = new HashMap<String, Marker>();

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
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCount = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addMarkerGeo(String title, String key, GoogleMap googleMap, double lat, double lon, boolean isCamera, int drawable) {
        try {
            if (markerCountGeo.get(key) == 1) {
                if (oldGeoLocation.get(key) != null) {
                    new GeoHRMarkerAnimation(googleMap, 1000, new GeoHRUpdateLocationCallBack() {
                        @Override
                        public void onGeoHRUpdatedLocation(GeoLocation updatedLocation) {
                            oldGeoLocation.put(key, updatedLocation);
                        }
                    }).animateMarkerGeo(mGeoLastLocation.get(key), oldGeoLocation.get(key), markerGeo.get(key), isCamera);
                } else {
                    oldGeoLocation.put(key, mGeoLastLocation.get(key));
                }
            } else if (markerCountGeo.get(key) == 0) {
                if (markerGeo.get(key) != null) {
                    markerGeo.get(key).remove();
                }
                mMap = googleMap;

                LatLng latLng = new LatLng(lat, lon);

                markerGeo.put(key, mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(drawable))));
                markerGeo.get(key).setTitle(title);
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCountGeo.put(key, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getListNearbyCars() {
        GeoQuery geoQuery = Utils.geo_car.queryAtLocation(new GeoLocation(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()), Utils.geo_radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getNearByInfo(key, location, false);
            }

            @Override
            public void onKeyExited(String key) {
                getNearByInfo(key, null, true);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                getNearByInfo(key, location, false);
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

        LatLng latLng = new LatLng(37.422f, -122.12f);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        if (Utils.cur_location != null) {
            addMarker(mMap, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude(), true, R.drawable.ic_car_orange);
        }
    }
    private String getDirectionUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getResources().getString(R.string.google_api_key);
        return url;
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
        price = Math.round(((float)distance/1000) * 2 + 5);
        Toast.makeText(activity, String.valueOf(distance/1000), Toast.LENGTH_SHORT).show();
//        txt_price.setText("RM " + String.valueOf(price));
//        btn_dest_confirm.setVisibility(View.GONE);
//        ly_step1.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        removeRoad();
//        mTaskSnapToRoads.execute();
//        new FetchURL(activity, this).execute(getDirectionUrl(marker_start.getPosition(), marker_target.getPosition(), "driving"), "driving");
        return false;
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