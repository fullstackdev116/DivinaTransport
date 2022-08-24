package com.example.divinatransport.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.divinatransport.DriverMainFragments.Fragment_driver_orders;
import com.example.divinatransport.MainActivity;
import com.example.divinatransport.R;

import java.util.ArrayList;

public class OrderListAdapter extends BaseAdapter {
    MainActivity activity;
    Fragment_driver_orders fragment;

    public OrderListAdapter(MainActivity _activity, Fragment_driver_orders _fragment) {
        activity = _activity;
        fragment = _fragment;
    }

    @Override
    public int getCount() {
        return 13;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.cell_order, null);
        }
        Button btn_view = view.findViewById(R.id.btn_view);
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fragment.isOpenedBottomView()) {
                    fragment.openBottomView();
                }
            }
        });
        return view;
    }

}
