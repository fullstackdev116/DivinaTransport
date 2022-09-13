package com.ujs.divinatransport.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.Model.ChatRoom;
import com.ujs.divinatransport.Model.Message;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class MessageListAdapter extends BaseAdapter {
    ArrayList<ChatRoom> arrayList;
    Activity activity;
    public int index_update = -1;

    public MessageListAdapter(Activity _activity, ArrayList<ChatRoom> _arrayList) {
        activity = _activity;
        this.arrayList = _arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ChatRoom chatRoom = arrayList.get(i);
        Message message = chatRoom.messages.get(chatRoom.messages.size()-1);
        String user_id = Utils.getChatUserId(chatRoom._id);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.cell_message, null);
        }
        final LinearLayout ly_status = view.findViewById(R.id.ly_status);
        final TextView txt_user = view.findViewById(R.id.txt_user);
        TextView txt_message = view.findViewById(R.id.txt_message);
        final TextView txt_time = view.findViewById(R.id.txt_time);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        txt_message.setText(message.message);
        if (message.message.length() == 0) {
            txt_message.setText("["+ activity.getResources().getString(R.string.file_attached) + "]");
            if (message.file.length() == 0) {
                txt_message.setText("[" + activity.getResources().getString(R.string.chat_open) + "]");
            }
        }
        txt_message.setTextColor(Color.parseColor("#222222"));
//        if (index_update == i) {
//            if (message.receiver_id.equals(Utils.mUser.getUid()) && !message.seen) {
//                txt_message.setTextColor(Color.parseColor("#A55510"));
//            }
//        }
        if (message.receiver_id.equals(Utils.mUser.getUid()) && !message.seen) {
            txt_message.setTextColor(Color.parseColor("#A55510"));
        }
        txt_time.setText(Utils.getTimeString(new Date(message.timestamp)));
        Utils.mDatabase.child(Utils.tbl_user).child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    user.uid = dataSnapshot.getKey();
                    txt_user.setText(user.name);
                    Glide.with(activity).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.ic_avatar).centerCrop().dontAnimate()).into(img_photo);
                    if (user.status == 0) {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_offline));
                    } else {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_online));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}
