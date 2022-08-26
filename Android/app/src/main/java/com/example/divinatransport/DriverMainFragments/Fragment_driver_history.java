package com.example.divinatransport.DriverMainFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.divinatransport.Adapter.HistoryListAdapter;
import com.example.divinatransport.DriverMainActivity;
import com.example.divinatransport.R;

public class Fragment_driver_history extends Fragment {
    ListView listView;
    DriverMainActivity activity;

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
        activity = (DriverMainActivity) context;
    }

}