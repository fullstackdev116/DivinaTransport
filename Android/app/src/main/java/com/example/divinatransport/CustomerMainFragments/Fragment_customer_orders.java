package com.example.divinatransport.CustomerMainFragments;

import static com.example.divinatransport.App.RunAnimation;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.divinatransport.Adapter.CustomerOrderListAdapter;
import com.example.divinatransport.Adapter.DriverOrderListAdapter;
import com.example.divinatransport.CustomerMainActivity;
import com.example.divinatransport.DriverMainActivity;
import com.example.divinatransport.R;
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
import com.google.maps.GeoApiContext;

public class Fragment_customer_orders extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    ListView listView;
    CustomerMainActivity activity;
    RelativeLayout ly_bottom;
    SupportMapFragment mapFragment;
    GeoApiContext mContext;
    private GoogleMap mMap;
    Marker markerFrom, markerTo;
    Polyline roadLine;
    ImageButton btn_close;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_fragment_orders, container, false);
        ly_bottom = v.findViewById(R.id.ly_bottom);
        ly_bottom.setVisibility(View.GONE);
        listView = v.findViewById(R.id.listView);
        CustomerOrderListAdapter adapter = new CustomerOrderListAdapter(activity, this);
        listView.setAdapter(adapter);
        btn_close = v.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomView();
            }
        });
        loadMap();
        return v;
    }

    void loadMap() {
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        mContext = new GeoApiContext().setApiKey(getString(R.string.google_api_key));
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
    void removeRoad() {
        if (markerFrom != null) markerFrom.remove();
        if (markerTo != null) markerTo.remove();
        if (roadLine != null) roadLine.remove();
    }

    public void openBottomView() {
        ly_bottom.setVisibility(View.VISIBLE);
        RunAnimation(ly_bottom, AnimationUtils.loadAnimation(activity, R.anim.slideup));
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
        activity = (CustomerMainActivity) context;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}