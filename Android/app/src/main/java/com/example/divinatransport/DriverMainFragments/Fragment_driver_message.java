package com.example.divinatransport.DriverMainFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.divinatransport.Adapter.HistoryListAdapter;
import com.example.divinatransport.Adapter.MessageListAdapter;
import com.example.divinatransport.ChatActivity;
import com.example.divinatransport.MainActivity;
import com.example.divinatransport.R;

public class Fragment_driver_message extends Fragment {
    ListView listView;
    MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_message, container, false);
        listView = v.findViewById(R.id.listView);
        MessageListAdapter adapter = new MessageListAdapter(activity, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, ChatActivity.class);
                startActivity(intent);
            }
        });
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