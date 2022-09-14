package com.ujs.divinatransport.DriverMainFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.Car;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.SignupActivityDriver;
import com.ujs.divinatransport.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_driver_profile extends Fragment {
    MainActivityDriver activity;
    LinearLayout ly_feedback;
    ImageButton btn_edit;
    TextView txt_email, txt_phone, txt_rate, txt_points, txt_name, txt_orders, txt_rejects, txt_drives;
    ImageView img_carType;
    CircleImageView img_photo;
    RatingBar rate;
    ArrayList<Ride> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.driver_fragment_profile, container, false);
        ly_feedback = v.findViewById(R.id.ly_feedback);
        btn_edit = v.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDialog();
            }
        });
        txt_email = v.findViewById(R.id.txt_email);
        txt_phone = v.findViewById(R.id.txt_phone);
        txt_rate = v.findViewById(R.id.txt_rate);
        txt_points = v.findViewById(R.id.txt_points);
        txt_name = v.findViewById(R.id.txt_name);
        txt_orders = v.findViewById(R.id.txt_orders);
        txt_rejects = v.findViewById(R.id.txt_rejects);
        txt_drives = v.findViewById(R.id.txt_drives);

        img_carType = v.findViewById(R.id.img_carType);
        img_photo = v.findViewById(R.id.img_photo);
        rate = v.findViewById(R.id.rate);

        loadFeedbacks();
        loadProfile();
        return v;
    }
    void loadProfile() {
        txt_email.setText(Utils.cur_user.email);
        txt_phone.setText("+" + Utils.cur_user.phone);
        txt_name.setText(Utils.cur_user.name);
        rate.setRating(Utils.cur_user.rate);
        txt_rate.setText(String.valueOf(Utils.cur_user.rate));
        txt_points.setText(String.valueOf(Utils.cur_user.point));
        Glide.with(activity).load(Utils.cur_user.photo).apply(new RequestOptions().placeholder(R.drawable.ic_avatar_white).fitCenter()).into(img_photo);

        Utils.mDatabase.child(Utils.tbl_ride_reject).orderByChild("driver_id").equalTo(Utils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {

                            long cnt = dataSnapshot.getChildrenCount();
                            txt_rejects.setText(String.valueOf(cnt));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        arrayList.clear();
        Utils.mDatabase.child(Utils.tbl_history).orderByChild("driver_id").equalTo(Utils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {
                            long cnt = dataSnapshot.getChildrenCount();
                            txt_drives.setText(String.valueOf(cnt));
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Ride ride = datas.getValue(Ride.class);
                                ride._id = datas.getKey();
                                arrayList.add(ride);
                            }
                            if (arrayList.size() > 0) loadFeedbacks();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        Utils.mDatabase.child(Utils.tbl_car).orderByChild("uid").equalTo(Utils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Car car = datas.getValue(Car.class);
                                car._id = datas.getKey();
                                int index = Arrays.asList(Utils.carNames).indexOf(car.type);
                                Glide.with(activity).load(Utils.carTypes[index]).apply(new RequestOptions().placeholder(R.drawable.ic_car2).fitCenter()).into(img_carType);
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        activity.showProgress();
        Utils.mDatabase.child(Utils.tbl_order).orderByChild("driver_id").equalTo(Utils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {

                            long cnt = dataSnapshot.getChildrenCount();
                            txt_orders.setText(String.valueOf(cnt));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }

    void loadFeedbacks() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater inflater = LayoutInflater.from(activity);
                ly_feedback.removeAllViews();
                for(int n = 0; n < arrayList.size(); n++) {
                    Ride ride = arrayList.get(n);
                    View view = inflater.inflate(R.layout.cell_feedback, null);
                    TextView txt_message = view.findViewById(R.id.txt_message);
                    txt_message.setText(ride.review);
                    RatingBar rate = view.findViewById(R.id.rate);
                    rate.setRating(ride.rate);
                    TextView txt_name = view.findViewById(R.id.txt_name);
                    Utils.mDatabase.child(Utils.tbl_user).child(ride.passenger_id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        User user = dataSnapshot.getValue(User.class);
                                        user.uid = dataSnapshot.getKey();
                                        txt_name.setText(user.name);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w( "loadPost:onCancelled", databaseError.toException());
                                    // ...
                                }
                            });
                    TextView txt_rate = view.findViewById(R.id.txt_rate);
                    txt_rate.setText(String.valueOf(ride.rate));
                    TextView txt_date = view.findViewById(R.id.txt_date);
                    txt_date.setText(Utils.getDateString(ride.date));
                    ly_feedback.addView(view);
                }
            }
        });
    }
    public void openUpdateDialog() {
        final Dialog dlg = new Dialog(activity, R.style.Theme_Transparent);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_profile, null);
        ImageButton btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
//        int width = (int)(getResources().getDisplayMetrics().widthPixels*1);
//        int height = (int)(getResources().getDisplayMetrics().heightPixels*1);
//        view.setMinimumWidth(width);
//        view.setMinimumHeight(height);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dlg.show();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityDriver) context;
    }
}