package com.ujs.divinatransport.CustomerMainFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.R;

public class Fragment_customer_profile extends Fragment {
    MainActivityCustomer activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.customer_fragment_profile, container, false);

        return v;
    }



    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityCustomer) context;
    }
}