package com.ujs.divinatransport.CustomerMainFragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.R;
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
import com.ujs.divinatransport.App;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Fragment_customer_ride extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final int PAGE_SIZE_LIMIT = 100;
    private static final int PAGINATION_OVERLAP = 5;

    MainActivityCustomer activity;
    BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    List<LatLng> latLngList_user = new LinkedList<>();
    List<LatLng> latLngList_driver = new LinkedList<>();
    List<LatLng> latLngList_place = new LinkedList<>();
    Marker marker_start, marker_target, marker_me;
    Circle circle_me;
    Polyline roadLine;
    ProgressBar mProgressBar;
    GeoApiContext mContext;
    private long pulseDuration = 1000;
    private ValueAnimator lastPulseAnimator;
    LinearLayout ly_top;
    RelativeLayout ly_topContent;
    boolean toggle = false;
    ArrayList<Marker> arrayMarker = new ArrayList<>();
    Timer timer;
    TextView txt_state;
    ImageButton btn_down;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_fragment_ride, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mContext = new GeoApiContext().setApiKey(getString(R.string.google_api_key));

        mProgressBar = v.findViewById(R.id.progress_bar);
        txt_state = v.findViewById(R.id.txt_state);
//        latLngList_place.add(new LatLng(37.42792293, -122.06936845));
//        latLngList_place.add(new LatLng(37.41892293, -122.06736845));
//
        latLngList_driver.add(new LatLng(33.980805, -118.2641));
        latLngList_driver.add(new LatLng(33.990101,-118.270445));

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
                findMyLocation();
            }
        });
        ly_top = v.findViewById(R.id.ly_top);
        ly_topContent = v.findViewById(R.id.ly_topContent);
        ly_topContent.setVisibility(View.GONE);
        btn_down = v.findViewById(R.id.btn_down);
        btn_down.setTag(0);
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTopViewState();
            }
        });
        txt_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTopViewState();
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

        v.findViewById(R.id.btn_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txt_state.setText("What do you want?");
                displayChoiceDialog();
            }
        });

        openSupportDialog();
        return v;
    }
    private void rideCar() {
        changeTopViewState();
        txt_state.setText("Which taxi to ride?");
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(blinkCarPin);
            }

        }, 0, 500);
    }
    private void openDatePicker() {
//        txt_state.setText("Please select your order day");
        Calendar calendar = Calendar.getInstance();
        Dialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                openPaymentDialog();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                txt_state.setText("Where to go?");
            }
        });
        datePickerDialog.show();
    }
    public void openPaymentDialog() {
//        txt_state.setText("Please confirm your payment");
        final Dialog dlg = new Dialog(activity, R.style.Theme_Transparent);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        TextView txt_payment = view.findViewById(R.id.txt_payment);
        ImageButton btn_ediapay = view.findViewById(R.id.btn_ediapay);
        ImageButton btn_cache = view.findViewById(R.id.btn_cache);
        ImageButton btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
//                txt_state.setText("Where to go?");
            }
        });
        btn_ediapay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable buttonDrawable = btn_ediapay.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.red_dark));
                btn_ediapay.setBackground(buttonDrawable);

                buttonDrawable = btn_cache.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray));
                btn_cache.setBackground(buttonDrawable);

                txt_payment.setText("I want to pay via EDIAPAY");
            }
        });
        btn_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable buttonDrawable = btn_cache.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.red_dark));
                btn_cache.setBackground(buttonDrawable);

                buttonDrawable = btn_ediapay.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray));
                btn_ediapay.setBackground(buttonDrawable);

                txt_payment.setText("I want to pay via CACHE");
            }
        });

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.70);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.setCancelable(true);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dlg.show();
    }
    private void displayChoiceDialog() {
        String choiceString[] = new String[] {"I want ride a car" ,"I want to order a car"};
        AlertDialog.Builder dialog= new AlertDialog.Builder(getActivity());
        dialog.setItems(choiceString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0) {
                    rideCar();
                } else {
                    openDatePicker();
                }

            }
        }).show();
    }
    void changeTopViewState() {
        if ((int)(btn_down.getTag()) == 0) {
            btn_down.animate().rotationX(180).start();
            openTopView();
            btn_down.setTag(1);
        } else {
            btn_down.animate().rotationX(0).start();
            closeTopView();
            btn_down.setTag(0);
        }
    }
    public void openSupportDialog() {
        final Dialog dlg = new Dialog(activity, R.style.Theme_Transparent);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_support, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.15);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        ly_top.setVisibility(View.GONE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dlg.dismiss();
                ly_top.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }
    private Runnable blinkCarPin = new Runnable() {
        public void run() {
            toggle = !toggle;
            for (int i = 0; i < arrayMarker.size(); i++) {
                arrayMarker.get(i).remove();
                Marker marker;
                if (toggle) {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new com.google.android.gms.maps.model.LatLng(latLngList_driver.get(i).lat, latLngList_driver.get(i).lng))
                            .title("Taxi "+String.valueOf(i))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi1)));
                } else {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new com.google.android.gms.maps.model.LatLng(latLngList_driver.get(i).lat, latLngList_driver.get(i).lng))
                            .title("Taxi 1"+String.valueOf(i))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi)));
                }
                arrayMarker.set(i, marker);
            }
        }
    };
    public void openTopView() {
        ly_topContent.setVisibility(View.VISIBLE);
        App.RunAnimation(ly_top, AnimationUtils.loadAnimation(activity, R.anim.slidedown));
    }
    public void closeTopView() {
        App.RunAnimation(ly_top, AnimationUtils.loadAnimation(activity, R.anim.slideup1));
        ly_topContent.setVisibility(View.GONE);
    }
    /*
    private void addPulsatingEffect(LatLng userLatlng){
        if(lastPulseAnimator != null){
            lastPulseAnimator.cancel();
            Log.d("onLocationUpdated: ","cancelled" );
        }
        if(circle_me != null)
            circle_me.setCenter(userLatlng);
        lastPulseAnimator = valueAnimate(userLatlng.getAccuracy(), pulseDuration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(lastUserCircle != null)
                    lastUserCircle.setRadius((Float) animation.getAnimatedValue());
                else {
                    lastUserCircle = map.addCircle(new CircleOptions()
                            .center(userLatlng)
                            .radius((Float) animation.getAnimatedValue())
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));
                }
            }
        });

    }
    protected ValueAnimator valueAnimate(float accuracy,long duration, ValueAnimator.AnimatorUpdateListener updateListener){
        Log.d( "valueAnimate: ", "called");
        ValueAnimator va = ValueAnimator.ofFloat(0,accuracy);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);

        va.start();
        return va;
    }
     */
    void findMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(), "Please enable location service.", Snackbar.LENGTH_LONG)
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
                if (marker_me != null) {
                    marker_me.remove();
                }
                if (circle_me != null) {
                    circle_me.remove();
                }
                marker_me = mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin0)));
                circle_me = mMap.addCircle(new CircleOptions()
                        .center(new com.google.android.gms.maps.model.LatLng(arg0.getLatitude(), arg0.getLongitude()))
                        .radius(1000)
                        .strokeColor(Color.TRANSPARENT)
                        .fillColor(Color.argb(100, 0, 255, 0)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(arg0.getLatitude(), arg0.getLongitude()), 14));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(false);
            }
        });
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

        findMyLocation();

        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(latLngList_driver.get(0).lat, latLngList_driver.get(0).lng))
                .title("Taxi 1")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi)));

        Marker marker2 = googleMap.addMarker(new MarkerOptions()
                .position(new com.google.android.gms.maps.model.LatLng(latLngList_driver.get(1).lat, latLngList_driver.get(1).lng))
                .title("Taxi 2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi)));

        arrayMarker.add(marker1); arrayMarker.add(marker2);
/*
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

*/
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
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityCustomer) context;
    }
}