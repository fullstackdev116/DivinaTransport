package com.ujs.divinatransport.DriverMainFragments;

import static com.ujs.divinatransport.App.RunAnimation;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.Adapter.DriverOrderListAdapter;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.CustomerMainFragments.Fragment_customer_orders;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ujs.divinatransport.Utils.Utils;
import com.ujs.divinatransport.directionhelpers.FetchURL;
import com.ujs.divinatransport.directionhelpers.TaskLoadedCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Fragment_driver_orders extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {
    ListView listView;
    MainActivityDriver activity;
    RelativeLayout ly_bottom;
    SupportMapFragment mapFragment;
    //    GeoApiContext mContext;
    private GoogleMap mMap;
    Marker markerFrom, markerTo;
    Polyline roadLine;
    ImageButton btn_close;
    ArrayList<Ride> arrayList = new ArrayList<>();
    DriverOrderListAdapter adapter;
    Polyline polyline;
    RelativeLayout ly_detail;
    TextView txt_distance, txt_duration;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_orders, container, false);
        adapter = new DriverOrderListAdapter(activity, this, arrayList);
        ly_bottom = v.findViewById(R.id.ly_bottom);
        ly_bottom.setVisibility(View.GONE);
        ly_detail = v.findViewById(R.id.ly_detail);
        txt_distance = v.findViewById(R.id.txt_distance);
        txt_duration = v.findViewById(R.id.txt_duration);
        ly_detail.setVisibility(View.GONE);
        listView = v.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        btn_close = v.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomView();
                ly_detail.setVisibility(View.GONE);
            }
        });
        loadMap();

        return v;
    }

    public void getOrders() {
        activity.showProgress();
        Utils.mDatabase.child(Utils.tbl_order).orderByChild("driver_id").equalTo(Utils.cur_user.uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        arrayList.clear();
                        if (dataSnapshot.getValue() != null) {
                            Date today = new Date();
                            today.setHours(0);
                            today.setMinutes(0);
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Ride ride = datas.getValue(Ride.class);
                                ride._id = datas.getKey();
                                if (today.after(ride.date)) {
                                    Utils.mDatabase.child(Utils.tbl_order).child(ride._id).setValue(null);
                                } else {
                                    arrayList.add(ride);
                                }
                            }
                        }
                        Collections.sort(arrayList, new Comparator<Ride>() {
                            @Override
                            public int compare(Ride rhs, Ride lhs) {
                                return rhs.date.compareTo(lhs.date);
                            }
                        });
                        if (arrayList.size() == 0) {
                            String[] listItems = {"No orders"};
                            listView.setAdapter(new ArrayAdapter(activity,  android.R.layout.simple_list_item_1, listItems));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                        activity.dismissProgress();
                    }
                });
    }

    void loadMap() {
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
//        mContext = new GeoApiContext().setApiKey(getString(R.string.google_api_key));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng fromLatLan = new LatLng(37.42692293, -122.06936845);
        LatLng toLatLan = new LatLng(37.41192293, -122.0736845);

        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        markerFrom = googleMap.addMarker(new MarkerOptions()
                .position(fromLatLan)
                .title("From Location"));

        markerTo = googleMap.addMarker(new MarkerOptions()
                .position(toLatLan)
                .title("To Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromLatLan, 14));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        PolylineOptions polyline = new PolylineOptions();
        builder.include(fromLatLan);
        polyline.add(fromLatLan);
        builder.include(toLatLan);
        polyline.add(toLatLan);
        roadLine = mMap.addPolyline(polyline.color(Color.BLUE));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));


    }

    @Override
    public void onTaskDone(long distanceVal, long durationVal, Object... values) {
        if (polyline != null) {
            polyline.remove();
        }
        polyline = mMap.addPolyline((PolylineOptions) values[0]);
        // go to step1
        txt_distance.setText(Utils.getDistanceStr(distanceVal));
        txt_duration.setText(Utils.getDurationStr(durationVal));
        ly_detail.setVisibility(View.VISIBLE);
    }

    public void openBottomView(Ride ride) {
        if (!isOpenedBottomView()) {
            ly_bottom.setVisibility(View.VISIBLE);
            RunAnimation(ly_bottom, AnimationUtils.loadAnimation(activity, R.anim.slideup));
        }
        ly_detail.setVisibility(View.GONE);
        LatLng fromLatLan = new LatLng(ride.from_lat, ride.from_lng);
        LatLng toLatLan = new LatLng(ride.to_lat, ride.to_lng);

        if (markerFrom != null) markerFrom.remove();
        if (markerTo != null) markerTo.remove();
        if (polyline != null) polyline.remove();
        markerFrom = mMap.addMarker(new MarkerOptions()
                .position(fromLatLan)
                .title(ride.from_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        markerTo = mMap.addMarker(new MarkerOptions()
                .position(toLatLan)
                .title(ride.to_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromLatLan, 16));
        new FetchURL(activity, Fragment_driver_orders.this).execute(Utils.getDirectionUrl(fromLatLan, toLatLan, "driving", activity), "driving");
        Toast.makeText(activity, "Loading route..", Toast.LENGTH_LONG).show();
    }
    public void closeBottomView() {
        ly_bottom.setVisibility(View.GONE);
    }
    public boolean isOpenedBottomView() {
        if (ly_bottom.getVisibility() == View.VISIBLE) {
            return true;
        }
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
    @Override
    public void onResume() {
        super.onResume();
        getOrders();
        App.setPreferenceInt(App.NewRide, 0);
        activity.refreshMenuBadge();
    }
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}