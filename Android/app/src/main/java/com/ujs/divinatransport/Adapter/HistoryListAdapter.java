package com.ujs.divinatransport.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.DriverMainFragments.Fragment_driver_history;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryListAdapter extends BaseAdapter {
    MainActivityDriver activity;
    Fragment_driver_history fragment;
    ArrayList<Ride> arrayList;

    public HistoryListAdapter(MainActivityDriver _activity, Fragment_driver_history _fragment, ArrayList<Ride> _arrayList) {
        activity = _activity;
        fragment = _fragment;
        arrayList = _arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
        TextView txt_date = view.findViewById(R.id.txt_date);
        TextView txt_start = view.findViewById(R.id.txt_start);
        TextView txt_target = view.findViewById(R.id.txt_target);
        TextView txt_sos = view.findViewById(R.id.txt_sos);
        TextView txt_price = view.findViewById(R.id.txt_price);
        TextView txt_review = view.findViewById(R.id.txt_review);
        TextView txt_name = view.findViewById(R.id.txt_name);
        CircleImageView img_photo = view.findViewById(R.id.img_photo);
        RatingBar ratingBar = view.findViewById(R.id.rate);

        Ride ride = arrayList.get(i);
        txt_date.setText(Utils.getDateString(ride.date));
        txt_start.setText(ride.from_address);
        txt_target.setText(ride.to_address);
        if (ride.isSOS) {
            txt_sos.setVisibility(View.VISIBLE);
        } else {
            txt_sos.setVisibility(View.GONE);
        }
        if (ride.transaction_id.length() > 0) {
            txt_price.setText(String.valueOf(ride.price) + " XOF (EdiaPay)");
        } else {
            txt_price.setText(String.valueOf(ride.price) + " XOF (Cache)");
        }
        txt_review.setText(ride.review);
        ratingBar.setRating(ride.rate);
        Utils.mDatabase.child(Utils.tbl_user).child(ride.passenger_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User user = dataSnapshot.getValue(User.class);
                            user.uid = dataSnapshot.getKey();
                            Glide.with(activity).load(user.photo).apply(new RequestOptions().placeholder(R.drawable.ic_avatar).centerCrop()).into(img_photo);
                            txt_name.setText(user.name);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });



        return view;
    }

}
