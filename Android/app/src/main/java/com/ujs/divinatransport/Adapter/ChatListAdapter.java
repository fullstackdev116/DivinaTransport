package com.ujs.divinatransport.Adapter;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.ChatActivity;
import com.ujs.divinatransport.ImageViewerActivity;
import com.ujs.divinatransport.Model.Message;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.DownloadTask;
import com.ujs.divinatransport.Utils.OnTaskResult;
import com.ujs.divinatransport.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class ChatListAdapter extends BaseAdapter {
    ArrayList<Message> arrayList;
    ChatActivity context;
    public int index_update = -1;
    public String roomId;

    public ChatListAdapter(ChatActivity _context, ArrayList<Message> _arrayList) {
        context = _context;
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
        final Message message = arrayList.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean flag_me = message.sender_id.equals(Utils.cur_user.uid);

        if (flag_me) {
            view = inflater.inflate(R.layout.cell_chat_right, null);
        } else {
            view = inflater.inflate(R.layout.cell_chat_left, null);
        }
        RelativeLayout ly_message = view.findViewById(R.id.ly_message);
        RelativeLayout ly_cover = view.findViewById(R.id.ly_cover);
        TextView txt_message = view.findViewById(R.id.txt_message);
        final TextView txt_seen = view.findViewById(R.id.txt_seen);
        final TextView txt_time = view.findViewById(R.id.txt_time);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        final ImageView img_pic = view.findViewById(R.id.img_pic);
        final VoicePlayerView voicePlayerView = view.findViewById(R.id.voicePlayerView);
        voicePlayerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new BottomSheet.Builder(context,R.style.BottomSheet_StyleDialog).title("Actions").sheet(R.menu.bottom_sheet_audio).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.delete:
                                if (message.sender_id.equals(Utils.cur_user.uid)) {
                                    context.delete_message(message);
                                    Toast.makeText(context, context.getResources().getString(R.string.voice_message_deleted), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, context.getResources().getString(R.string.you_are_not_allowed), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
        if (flag_me && message.seen) {
            txt_seen.setVisibility(View.VISIBLE);
        } else {
            txt_seen.setVisibility(View.INVISIBLE);
        }
        if (!flag_me && !message.seen) {
            txt_seen.setVisibility(View.VISIBLE);
            Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").child(message._id).child("seen").setValue(true);
        }
        txt_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new BottomSheet.Builder(context,R.style.BottomSheet_StyleDialog).title("Actions").sheet(R.menu.bottom_sheet_text).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.copy_text:
                                Utils.copy_text(context, message.message);
                                Toast.makeText(context, context.getResources().getString(R.string.message_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.share_text:
                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.message_from_chat));
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, message.message);
                                context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_via)));
                                break;
                            case R.id.delete:
                                if (message.sender_id.equals(Utils.cur_user.uid)) {
                                    context.delete_message(message);
                                    Toast.makeText(context, context.getResources().getString(R.string.message_deleted), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, context.getResources().getString(R.string.you_are_not_allowed), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewerActivity.class);
                intent.putExtra("url", message.file);
                context.startActivity(intent);
            }
        });
        img_pic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new BottomSheet.Builder(context,R.style.BottomSheet_StyleDialog).title(context.getResources().getString(R.string.actions)).sheet(R.menu.bottom_sheet_img).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.download:
                                do_download(message.file);
                                break;
                            case R.id.copy_url:
                                Utils.copy_text(context, message.file);
                                Toast.makeText(context, context.getResources().getString(R.string.image_url_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.share_url:
                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.image_url));
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, message.file);
                                context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_via)));
                                break;
                            case R.id.delete:
                                if (message.sender_id.equals(Utils.cur_user.uid)) {
                                    context.delete_message(message);
                                    Toast.makeText(context, context.getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, context.getResources().getString(R.string.you_are_not_allowed), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
        if (message.message.length()>0) {
            txt_message.setVisibility(View.VISIBLE);
            img_pic.setVisibility(View.GONE);
            voicePlayerView.setVisibility(View.GONE);
            txt_message.setText(message.message);
        } else {
            if (message.file.length() == 0) {
                ly_cover.setVisibility(View.GONE);
                return view;
            }
            txt_message.setVisibility(View.GONE);
            if (message.file_type.equals("jpeg")) {
                img_pic.setVisibility(View.VISIBLE);
                voicePlayerView.setVisibility(View.GONE);
                Glide.with(context).load(message.file).apply(new RequestOptions()
                        .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_pic);
            } else if (message.file_type.equals("wav")) {
                ly_message.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                img_pic.setVisibility(View.GONE);
                voicePlayerView.setVisibility(View.VISIBLE);
                voicePlayerView.setTimingVisibility(false);
                SetAudioTask setAudioTask = new SetAudioTask(voicePlayerView);
                setAudioTask.execute(message.file);
            }
        }
        txt_time.setText(Utils.getTimeString(new Date(message.timestamp)));
        Utils.mDatabase.child(Utils.tbl_user).child(message.sender_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (context != null) {
                        Glide.with(context).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.ic_avatar_white).centerCrop().dontAnimate()).into(img_photo);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    private class SetAudioTask extends AsyncTask<String, Void, String> {
        private VoicePlayerView voicePlayerView;

        public SetAudioTask(VoicePlayerView voicePlayerView) {
            this.voicePlayerView = voicePlayerView;
        }

        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            voicePlayerView.setAudio(path);
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            voicePlayerView.setTimingVisibility(true);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    void do_download(final String url) {
        final ProgressDialog mProgressDialog = new android.app.ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.download));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setProgressPercentFormat(null);
        mProgressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        final String path = App.MY_IMAGE_PATH + File.separator + App.getTimestampString() + ".jpg";
        final DownloadTask downloadTask = new DownloadTask(context, path, new OnTaskResult() {
            @Override
            public void onTaskCompleted() {
                mProgressDialog.dismiss();
                Toast.makeText(context,context.getResources().getString(R.string.downloaded_in_) + App.MY_IMAGE_PATH, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskStarted() {
                mProgressDialog.setMessage(context.getResources().getString(R.string.downloading_image_));
                mProgressDialog.show();
            }

            @Override
            public void onTaskUpdated(Integer... progress) {

            }

            @SuppressLint("ShowToast")
            @Override
            public void onTaskError(String result) {
                mProgressDialog.dismiss();
                Toast.makeText(context, result, Toast.LENGTH_SHORT);
            }
        });
        downloadTask.execute(url);
    }
}
