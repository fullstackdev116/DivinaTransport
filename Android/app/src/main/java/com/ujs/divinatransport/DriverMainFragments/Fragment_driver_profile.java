package com.ujs.divinatransport.DriverMainFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.R;

public class Fragment_driver_profile extends Fragment {
    MainActivityDriver activity;
    LinearLayout ly_feedback;
    ImageButton btn_edit;

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
        loadFeedbacks();
        return v;
    }
    void loadFeedbacks() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater inflater = LayoutInflater.from(activity);
                ly_feedback.removeAllViews();
                for(int n = 0; n < 10; n++) {
                    View view = inflater.inflate(R.layout.cell_feedback, null);
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