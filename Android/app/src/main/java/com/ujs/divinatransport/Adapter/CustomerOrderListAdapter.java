package com.ujs.divinatransport.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.CustomerMainFragments.Fragment_customer_orders;
import com.ujs.divinatransport.R;

public class CustomerOrderListAdapter extends BaseAdapter {
    MainActivityCustomer activity;
    Fragment_customer_orders fragment;

    public CustomerOrderListAdapter(MainActivityCustomer _activity, Fragment_customer_orders _fragment) {
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
