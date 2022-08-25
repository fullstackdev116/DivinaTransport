package com.example.divinatransport.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cocosw.bottomsheet.BottomSheet;
import com.example.divinatransport.ChatActivity;
import com.example.divinatransport.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class ChatListAdapter extends BaseAdapter {

    ChatActivity context;
    public ChatListAdapter(ChatActivity _context) {
        context = _context;
    }
    @Override
    public int getCount() {
        return 5;
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
        LayoutInflater inflater = LayoutInflater.from(context);

        if (i%2 == 1) {
            view = inflater.inflate(R.layout.cell_chat_right, null);
        } else {
            view = inflater.inflate(R.layout.cell_chat_left, null);
        }
        CardView card_message = view.findViewById(R.id.card_message);
        RelativeLayout ly_cover = view.findViewById(R.id.ly_cover);
        TextView txt_message = view.findViewById(R.id.txt_message);
        final TextView txt_seen = view.findViewById(R.id.txt_seen);
        final TextView txt_time = view.findViewById(R.id.txt_time);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        final ImageView img_pic = view.findViewById(R.id.img_pic);
        final VoicePlayerView voicePlayerView = view.findViewById(R.id.voicePlayerView);

        txt_seen.setVisibility(View.VISIBLE);


        return view;
    }

}
