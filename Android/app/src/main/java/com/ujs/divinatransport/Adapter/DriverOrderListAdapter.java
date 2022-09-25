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

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.DriverMainFragments.Fragment_driver_orders;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.Model.RideReject;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.MyUtils;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverOrderListAdapter extends BaseAdapter {
    MainActivityDriver activity;
    Fragment_driver_orders fragment;
    ArrayList<Ride> arrayList;

    public DriverOrderListAdapter(MainActivityDriver _activity, Fragment_driver_orders _fragment, ArrayList<Ride> arrayList) {
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
            view = inflater.inflate(R.layout.cell_order_driver, null);
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

        MyUtils.mDatabase.child(MyUtils.tbl_user).child(ride.passenger_id)
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
                if (btn_view.getText().toString().equals("View Road")) {
                    fragment.openBottomView(ride);
                }
            }
        });
        btn_view.setPaintFlags(btn_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Button btn_accept = view.findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ride.state = 0;
                MyUtils.mDatabase.child(MyUtils.tbl_order).child(ride._id).setValue(ride);
                fragment.getOrders();
            }
        });
        btn_accept.setPaintFlags(btn_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Button btn_reject = view.findViewById(R.id.btn_reject);
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getResources().getString(R.string.warning))
                        .setMessage("Are you sure to reject this order?")
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RideReject rideReject = new RideReject("", ride._id, MyUtils.cur_user.uid, new Date());
                                String key = MyUtils.mDatabase.child(MyUtils.tbl_ride_reject).push().getKey();
                                MyUtils.mDatabase.child(MyUtils.tbl_ride_reject).child(key).setValue(rideReject);

                                MyUtils.mDatabase.child(MyUtils.tbl_order).child(ride._id).setValue(null);

                                MyUtils.cur_user.rejects ++;
                                MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).setValue(MyUtils.cur_user);

                                fragment.getOrders();
                            }
                        }).show();
            }
        });
        btn_reject.setPaintFlags(btn_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        RelativeLayout ly_accept = view.findViewById(R.id.ly_accept);
        if (ride.state == 0) {
            ly_accept.setVisibility(View.GONE);
        } else {
            ly_accept.setVisibility(View.VISIBLE);
        }

        ImageButton btn_chat = view.findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.goToChatPage(activity, ride.passenger_id);
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
