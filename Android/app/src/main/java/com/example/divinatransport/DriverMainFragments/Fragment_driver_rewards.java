package com.example.divinatransport.DriverMainFragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.divinatransport.Adapter.HistoryListAdapter;
import com.example.divinatransport.MainActivity;
import com.example.divinatransport.R;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class Fragment_driver_rewards extends Fragment {
    MainActivity activity;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_rewards, container, false);
        CircularProgressIndicator circularProgress = v.findViewById(R.id.circular_progress);
        circularProgress.setProgress(890, 1000);
        circularProgress.setTooltipText("You got 890 points");
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

}