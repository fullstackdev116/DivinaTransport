package com.ujs.divinatransport.Adapter;

import static android.Manifest.permission.CALL_PHONE;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.CustomerMainFragments.Fragment_customer_orders;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.MyUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerOrderListAdapter extends BaseAdapter {
    MainActivityCustomer activity;
    Fragment_customer_orders fragment;
    ArrayList<Ride> arrayList;

    public CustomerOrderListAdapter(MainActivityCustomer _activity, Fragment_customer_orders _fragment, ArrayList<Ride> arrayList) {
        activity = _activity;
        fragment = _fragment;
        this.arrayList = arrayList;
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
            view = inflater.inflate(R.layout.cell_order_passenger, null);
        }
        Ride ride = arrayList.get(i);
        TextView txt_date = view.findViewById(R.id.txt_date);
        TextView txt_start = view.findViewById(R.id.txt_start);
        TextView txt_target = view.findViewById(R.id.txt_target);
        TextView txt_phone = view.findViewById(R.id.txt_phone);
        TextView txt_name = view.findViewById(R.id.txt_name);
        CircleImageView img_photo = view.findViewById(R.id.img_photo);
        txt_date.setText(MyUtils.getDateString(ride.date));
        txt_start.setText(ride.from_address);
        txt_target.setText(ride.to_address);
        ImageButton btn_call = view.findViewById(R.id.btn_call);

        MyUtils.mDatabase.child(MyUtils.tbl_user).child(ride.driver_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User user = dataSnapshot.getValue(User.class);
                            user.uid = dataSnapshot.getKey();
                            Glide.with(activity).load(user.photo).apply(new RequestOptions().placeholder(R.drawable.ic_avatar).centerCrop()).into(img_photo);
                            txt_phone.setText("+"+user.phone);
                            txt_name.setText(user.name);
                            btn_call.setTag(user.phone);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        Button btn_view = view.findViewById(R.id.btn_view);
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_view.getText().toString().equals(activity.getResources().getString(R.string.view_road))) {
                    fragment.openBottomView(ride);
                } else {
                    App.setObjectPreference("my_ride", ride);
                    MyUtils.mDatabase.child(MyUtils.tbl_order).child(ride._id).setValue(null);
                    activity.navController.navigate(R.id.nav_ride);
                }
            }
        });
        btn_view.setPaintFlags(btn_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();
        String today = MyUtils.getDateString(currentLocalTime);
        if (MyUtils.getDateString(ride.date).equals(today)) {
            btn_view.setText(activity.getResources().getString(R.string.ride_now));
            btn_view.setTextColor(activity.getResources().getColor(R.color.teal_200));
        } else {
            btn_view.setText(activity.getResources().getString(R.string.view_road));
            btn_view.setTextColor(activity.getResources().getColor(R.color.white));
        }
        RelativeLayout ly_cancel = view.findViewById(R.id.ly_cancel);
        TextView txt_waiting = view.findViewById(R.id.txt_waiting);
        if (ride.state == 0) {
            ly_cancel.setVisibility(View.GONE);
            txt_waiting.setVisibility(View.GONE);
        } else {
            ly_cancel.setVisibility(View.VISIBLE);
            txt_waiting.setVisibility(View.VISIBLE);
        }
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setPaintFlags(btn_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getResources().getString(R.string.warning))
                        .setMessage("Are you sure to cancel this order?")
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.showProgress();
                                MyUtils.mDatabase.child(MyUtils.tbl_order).child(ride._id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        activity.dismissProgress();
                                        if (snapshot.getValue() != null) {
                                            Ride ride1 = snapshot.getValue(Ride.class);
                                            ride1._id = snapshot.getKey();
                                            if (ride1.state == -1) {
                                                MyUtils.mDatabase.child(MyUtils.tbl_order).child(ride1._id).setValue(null);
                                                fragment.getOrders();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }).show();
            }
        });
        ImageButton btn_chat = view.findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.goToChatPage(activity, ride.driver_id);
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ArrayList<String> arrPermissionRequests = new ArrayList<>();
                    arrPermissionRequests.add(CALL_PHONE);
                    ActivityCompat.requestPermissions(activity, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), activity.MY_PERMISSION_CALL);
                } else {
                    String phone = btn_call.getTag().toString();
                    App.dialNumber(phone, activity);
                }
            }
        });
        return view;
    }

}
