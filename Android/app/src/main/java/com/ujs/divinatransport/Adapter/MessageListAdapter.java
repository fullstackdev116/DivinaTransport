package com.ujs.divinatransport.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ujs.divinatransport.R;

public class MessageListAdapter extends BaseAdapter {
    Activity activity;

    public MessageListAdapter(Activity _activity) {
        activity = _activity;
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
            view = inflater.inflate(R.layout.cell_message, null);
        }
        return view;
    }

}
