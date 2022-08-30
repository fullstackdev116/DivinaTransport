package com.ujs.divinatransport.DriverSignupFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ujs.divinatransport.SignupActivityDriver;
import com.ujs.divinatransport.R;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class Fragment_driver_signup_intro extends Fragment {
    SignupActivityDriver activity;
    CheckBox checkBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.driver_fragment_signup_intro, container, false);
        ImageCarousel carousel = v.findViewById(R.id.carousel);

        List<CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem(R.drawable.welcome1,"Driver signup details first page"));
        list.add(new CarouselItem(R.drawable.welcome2,"Driver signup details second page"));
        list.add(new CarouselItem(R.drawable.welcome3,"Driver signup details third page"));
        carousel.setData(list);

        checkBox = v.findViewById(R.id.chk_agree);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.termsAgreed = checkBox.isChecked();
                activity.btn_next.setEnabled(activity.termsAgreed);
            }
        });

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        checkBox.setChecked(activity.termsAgreed);
        activity.btn_next.setEnabled(activity.termsAgreed);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SignupActivityDriver) context;
    }
}