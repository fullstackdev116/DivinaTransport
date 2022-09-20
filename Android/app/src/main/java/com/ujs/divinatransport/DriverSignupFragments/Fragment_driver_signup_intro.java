package com.ujs.divinatransport.DriverSignupFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

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
        Button btn_terms = v.findViewById(R.id.btn_terms);
        btn_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTermsDialog();
            }
        });
        return v;
    }
    public void openTermsDialog() {
        final Dialog dlg = new Dialog(activity, R.style.Theme_Transparent);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_terms, null);
        WebView wv = view.findViewById(R.id.webview);
        wv.loadUrl("file:///android_asset/terms.html");
        ImageButton btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dlg.show();
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