package com.ujs.divinatransport.DriverMainFragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.Utils;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class Fragment_driver_rewards extends Fragment {
    MainActivityDriver activity;
    ImageView img_reward;
    TextView txt_reward;
    Button btn_reward;
    CircularProgressIndicator circularProgress;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_rewards, container, false);
        circularProgress = v.findViewById(R.id.circular_progress);

        img_reward = v.findViewById(R.id.img_reward);
        txt_reward = v.findViewById(R.id.txt_reward);
        btn_reward = v.findViewById(R.id.btn_reward);
        btn_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(activity.parentLayout, "You will get rewards soon!", 2000).show();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        circularProgress.setProgress(Utils.cur_user.point, 1000);
        int remain = 1000 - Utils.cur_user.point;
        String message = "Keep trying to get " + String.valueOf(remain) + " points more to get a reward!";
        img_reward.setImageDrawable(getResources().getDrawable(R.drawable.ic_keep_going));
        if (Utils.cur_user.point > 800) {
            message = "Almost done!\\nYou will get a reward once you reach the 1000 points!";
            img_reward.setImageDrawable(getResources().getDrawable(R.drawable.ic_rewards));
        }
        txt_reward.setText(message);
        if (Utils.cur_user.point >= 1000) {
            btn_reward.setEnabled(true);
        } else {
            btn_reward.setEnabled(false);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityDriver) context;
    }

}