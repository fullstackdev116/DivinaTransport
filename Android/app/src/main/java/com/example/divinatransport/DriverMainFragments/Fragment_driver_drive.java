package com.example.divinatransport.DriverMainFragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.divinatransport.MainActivity;
import com.example.divinatransport.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.SnappedPoint;
import com.kienht.bottomsheetbehavior.BottomSheetBehavior;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Fragment_driver_drive extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final int PAGE_SIZE_LIMIT = 100;
    private static final int PAGINATION_OVERLAP = 5;

    MainActivity activity;
    BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    List<LatLng> latLngList_user = new LinkedList<>();
    List<LatLng> latLngList_driver = new LinkedList<>();
    List<LatLng> latLngList_place = new LinkedList<>();
    Marker marker_start, marker_target;
    Polyline roadLine;
    ProgressBar mProgressBar;
    GeoApiContext mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_drive, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mContext = new GeoApiContext().setApiKey(getString(R.string.google_api_key));

        mProgressBar = v.findViewById(R.id.progress_bar);

        latLngList_user.add(new LatLng(37.42692293, -122.06936845));
        latLngList_user.add(new LatLng(37.41192293, -122.0736845));

        latLngList_place.add(new LatLng(37.42792293, -122.06936845));
        latLngList_place.add(new LatLng(37.41892293, -122.06736845));

        latLngList_driver.add(new LatLng(37.4233438, -122.0728817));
        latLngList_driver.add(new LatLng(37.4329101,-122.0749094));

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
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(v, "Please enable location service.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
                    }
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    }
                });
            }
        });

        ImageView img_expand = v.findViewById(R.id.expand_icon);
        bottomSheetBehavior = BottomSheetBehavior.from(v.findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setSkipCollapsed(true);
//        bottomSheetBehavior.setPeekHeight(150);
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
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
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
                removeRoad();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        return v;
    }
    private List<SnappedPoint> snapToRoads(GeoApiContext context) throws Exception {
        List<SnappedPoint> snappedPoints = new ArrayList<>();

        int offset = 0;
        while (offset < latLngList_place.size()) {
            // Calculate which points to include in this request. We can't exceed the APIs
            // maximum and we want to ensure some overlap so the API can infer a good location for
            // the first few points in each request.
            if (offset > 0) {
                offset -= PAGINATION_OVERLAP;   // Rewind to include some previous points
            }
            int lowerBound = offset;
            int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, latLngList_place.size());

            // Grab the data we need for this page.
            LatLng[] page = latLngList_place
                    .subList(lowerBound, upperBound)
                    .toArray(new LatLng[upperBound - lowerBound]);

            // Perform the request. Because we have interpolate=true, we will get extra data points
            // between our originally requested path. To ensure we can concatenate these points, we
            // only start adding once we've hit the first new point (i.e. skip the overlap).
            SnappedPoint[] points = RoadsApi.snapToRoads(context, true, page).await();
            boolean passedOverlap = false;
            for (SnappedPoint point : points) {
                if (offset == 0 || point.originalIndex >= PAGINATION_OVERLAP) {
                    passedOverlap = true;
                }
                if (passedOverlap) {
                    snappedPoints.add(point);
                }
            }

            offset = upperBound;
        }

        return snappedPoints;
    }

    AsyncTask<Void, Void, List<SnappedPoint>> mTaskSnapToRoads =
            new AsyncTask<Void, Void, List<SnappedPoint>>() {
                @Override
                protected void onPreExecute() {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setIndeterminate(true);
                }

                @Override
                protected List<SnappedPoint> doInBackground(Void... params) {
                    try {
                        return snapToRoads(mContext);
                    } catch (final Exception ex) {
                        Snackbar.make(getView(), ex.toString(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        ex.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<SnappedPoint> snappedPoints) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    com.google.android.gms.maps.model.LatLng[] mapPoints =
                            new com.google.android.gms.maps.model.LatLng[snappedPoints.size()];
                    int i = 0;
                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                    for (SnappedPoint point : snappedPoints) {
                        mapPoints[i] = new com.google.android.gms.maps.model.LatLng(point.location.lat,
                                point.location.lng);
                        bounds.include(mapPoints[i]);
                        i += 1;
                    }

                    mMap.addPolyline(new PolylineOptions().add(mapPoints).color(Color.BLUE));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0));
                }
            };
    void removeRoad() {
        if (marker_start != null) marker_start.remove();
        if (marker_target != null) marker_target.remove();
        if (roadLine != null) roadLine.remove();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] _permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, _permissions, grantResults);

        if (grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
                    }
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
                }
            }
        }
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
        // Add a marker in Sydney and move the camera
        com.google.android.gms.maps.model.LatLng sydney = new com.google.android.gms.maps.model.LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        googleMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(latLngList_driver.get(0).lat, latLngList_driver.get(0).lng))
                .title("Taxi 1")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi)));

        googleMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(latLngList_driver.get(1).lat, latLngList_driver.get(1).lng))
                .title("Taxi 2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi)));

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(latLngList_user.get(0).lat, latLngList_user.get(0).lng))
                .title("Allen Deal")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_passenger)));


        googleMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(latLngList_user.get(1).lat, latLngList_user.get(1).lng))
                .title("Moril Bictor")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_passenger)));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(latLngList_user.get(0).lat, latLngList_user.get(0).lng), 14));

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new com.google.android.gms.maps.model.LatLng(latLngList_user.get(0).lat, latLngList_user.get(0).lng))
                .radius(1000)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(Color.argb(100, 0, 255, 0)));

        mMap.addCircle(new CircleOptions()
                .center(new com.google.android.gms.maps.model.LatLng(latLngList_user.get(1).lat, latLngList_user.get(0).lng))
                .radius(1000)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(Color.argb(100, 255, 0, 0)));


    }
    @Override
    public boolean onMarkerClick(final Marker marker) {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        removeRoad();
//        mTaskSnapToRoads.execute();
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}