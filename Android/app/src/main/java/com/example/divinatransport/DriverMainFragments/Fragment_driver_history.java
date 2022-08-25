package com.example.divinatransport.DriverMainFragments;

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

import com.example.divinatransport.Adapter.HistoryListAdapter;
import com.example.divinatransport.Adapter.OrderListAdapter;
import com.example.divinatransport.MainActivity;
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

public class Fragment_driver_history extends Fragment {
    ListView listView;
    MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_history, container, false);
        listView = v.findViewById(R.id.listView);
        HistoryListAdapter adapter = new HistoryListAdapter(activity, this);
        listView.setAdapter(adapter);
        return v;
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