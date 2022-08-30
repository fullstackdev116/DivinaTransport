package com.ujs.divinatransport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ListView;

import com.ujs.divinatransport.Adapter.ChatListAdapter;
import com.ujs.divinatransport.R;

public class ChatActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        App.hideKeyboard(this);

        listView = findViewById(R.id.listView);
        ChatListAdapter chatListAdapter = new ChatListAdapter(this);
        listView.setAdapter(chatListAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}