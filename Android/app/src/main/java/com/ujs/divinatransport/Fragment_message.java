package com.ujs.divinatransport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.Adapter.MessageListAdapter;
import com.ujs.divinatransport.Model.ChatRoom;
import com.ujs.divinatransport.Model.Message;
import com.ujs.divinatransport.Utils.Utils;

import java.util.ArrayList;

public class Fragment_message extends Fragment {
    ListView listView;
    MainActivityCustomer activityCustomer;
    MainActivityDriver activityDriver;
    Activity activity;

    ArrayList<ChatRoom> arrayList = new ArrayList<>();
    MessageListAdapter messageListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        listView = v.findViewById(R.id.listView);
        messageListAdapter = new MessageListAdapter(activity, arrayList);
        listView.setAdapter(messageListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (arrayList.size() == 0) return;
                messageListAdapter.index_update = -1;
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("roomId", arrayList.get(i)._id);
                startActivity(intent);
            }
        });
        readMessages();
        return v;
    }
    ProgressDialog progressDialog;
    void readMessages() {
        arrayList.clear();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Utils.mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(Utils.tbl_chat)) {
                    addMessages();
                } else {
                    progressDialog.dismiss();
                    String[] listItems = {"No messages"};
                    listView.setAdapter(new ArrayAdapter(activity,  android.R.layout.simple_list_item_1, listItems));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void addMessages() {
        Utils.mDatabase.child(Utils.tbl_chat).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue()!=null) {
                    boolean flag = dataSnapshot.getKey().contains(Utils.cur_user.uid);
                    if (flag) {
                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom._id = dataSnapshot.getKey();
                        for (DataSnapshot datas:dataSnapshot.child("messages").getChildren()) {
                            Message message = datas.getValue(Message.class);
                            chatRoom.messages.add(message);
                        }
                        arrayList.add(chatRoom);

                    }
                }
                if (arrayList.size() == 0) {
                    String[] listItems = {"No messages"};
                    listView.setAdapter(new ArrayAdapter(activity,  android.R.layout.simple_list_item_1, listItems));
                } else {
                    messageListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue()!=null) {
                    boolean flag = dataSnapshot.getKey().contains(Utils.cur_user.uid);
                    if ( flag) {
                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom._id = dataSnapshot.getKey();
                        for (DataSnapshot datas:dataSnapshot.child("messages").getChildren()) {
                            Message message = datas.getValue(Message.class);
                            chatRoom.messages.add(message);
                        }
                        int index_update = 0;
                        for (int i = 0; i < arrayList.size(); i++) {
                            ChatRoom room = arrayList.get(i);
                            if (room._id.equals(chatRoom._id)) {
                                arrayList.remove(i);
                                index_update = i;
                                break;
                            }
                        }
                        arrayList.add(index_update, chatRoom);
                        messageListAdapter.index_update = index_update;

                    }
                }
                if (arrayList.size() == 0) {
                    String[] listItems = {"No messages"};
                    listView.setAdapter(new ArrayAdapter(activity,  android.R.layout.simple_list_item_1, listItems));
                } else {
                    messageListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database Error:", databaseError.getMessage());
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (messageListAdapter!=null) {
            messageListAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (Utils.cur_user.type.equals(Utils.DRIVER)) {
//            activityDriver =(MainActivityDriver) context;
//        } else {
//            activityDriver =(MainActivityDriver) context;
//        }
        activity = (Activity) context;
    }

}