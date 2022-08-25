package com.example.divinatransport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.divinatransport.DriverMainFragments.Fragment_driver_history;
import com.example.divinatransport.DriverMainFragments.Fragment_driver_orders;
import com.example.divinatransport.MainActivity;
import com.example.divinatransport.R;

public class HistoryListAdapter extends BaseAdapter {
    MainActivity activity;
    Fragment_driver_history fragment;

    public HistoryListAdapter(MainActivity _activity, Fragment_driver_history _fragment) {
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
            view = inflater.inflate(R.layout.cell_history, null);
        }
        return view;
    }

}
