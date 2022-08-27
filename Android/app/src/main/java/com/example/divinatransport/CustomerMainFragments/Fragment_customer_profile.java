package com.example.divinatransport.CustomerMainFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.divinatransport.MainActivityCustomer;
import com.example.divinatransport.R;

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