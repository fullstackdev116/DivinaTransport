package com.ujs.divinatransport.DriverMainFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.Adapter.DriverOrderListAdapter;
import com.ujs.divinatransport.Adapter.HistoryListAdapter;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Fragment_driver_history extends Fragment {
    ListView listView;
    MainActivityDriver activity;
    ArrayList<Ride> arrayList = new ArrayList<>();
    HistoryListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_history, container, false);
        listView = v.findViewById(R.id.listView);
        adapter = new HistoryListAdapter(activity, this, arrayList);
        listView.setAdapter(adapter);

        getHistory();
        return v;
    }
    public void getHistory() {
        activity.showProgress();
        Utils.mDatabase.child(Utils.tbl_history).orderByChild("driver_id").equalTo(Utils.cur_user.uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        arrayList.clear();
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Ride ride = datas.getValue(Ride.class);
                                ride._id = datas.getKey();
                                arrayList.add(ride);
                            }
                        }
                        Collections.sort(arrayList, new Comparator<Ride>() {
                            @Override
                            public int compare(Ride rhs, Ride lhs) {
                                return lhs.date.compareTo(rhs.date);
                            }
                        });
                        if (arrayList.size() == 0) {
                            String[] listItems = {"No history"};
                            listView.setAdapter(new ArrayAdapter(activity,  android.R.layout.simple_list_item_1, listItems));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                        activity.dismissProgress();
                    }
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityDriver) context;
    }

}