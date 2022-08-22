package com.example.divinatransport.DriverMainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.divinatransport.R;

public class Fragment_driver_set_car extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_set_car, container, false);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}