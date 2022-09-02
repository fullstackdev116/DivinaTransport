package com.ujs.divinatransport.DriverSignupFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.travijuu.numberpicker.library.NumberPicker;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.SignupActivityDriver;
import com.ujs.divinatransport.Utils.Utils;
import com.ujs.divinatransport.idcamera.IDCardCamera;
import com.ujs.divinatransport.idcamera.utils.FileUtils;

import java.io.FileNotFoundException;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class Fragment_driver_signup_setcar extends Fragment {
    SignupActivityDriver activity;
    LinearLayout ly_carType;
    private ImageView img_carType, img_car;
    private TextView txt_carName;
    private androidx.cardview.widget.CardView cv_carTyppe;
    NumberPicker numberPicker;
    EditText edit_price;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_signup_setcar, container, false);
        ly_carType = v.findViewById(R.id.ly_carType);
        img_carType = v.findViewById(R.id.img_carType);
        img_car = v.findViewById(R.id.img_car);
        txt_carName = v.findViewById(R.id.txt_carType);
        cv_carTyppe = v.findViewById(R.id.cv_carType);
        edit_price = v.findViewById(R.id.edit_price);
        numberPicker = v.findViewById(R.id.number_picker);
        numberPicker.setValue(2);
        activity.car_seats = 2;
        numberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.car_seats = numberPicker.getValue();
            }
        });
        loadCarTypes();

        Button btn_capture = v.findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDCardCamera.create(activity).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
            }
        });

        edit_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.car_price = Float.parseFloat(s.toString());
            }
        });
        return v;
    }
    void loadCarTypes() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater inflater = LayoutInflater.from(activity);
                ly_carType.removeAllViews();
                for(int n = 0; n < Utils.carTypes.length; n++) {
                    int drawable = Utils.carTypes[n];
                    String name = Utils.carNames[n];
                    View view = inflater.inflate(R.layout.cell_car_type, null);
                    RelativeLayout ly_cover = view.findViewById(R.id.ly_cover);
                    ImageView img_carType = view.findViewById(R.id.img_carType);
                    img_carType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < ly_carType.getChildCount(); i++) {
                                View vv = ly_carType.getChildAt(i);
                                vv.findViewById(R.id.ly_cover).setBackgroundColor(Color.TRANSPARENT);
                            }
                            ly_cover.setBackgroundColor(getResources().getColor(R.color.red_dark));
                            Fragment_driver_signup_setcar.this.cv_carTyppe.setVisibility(View.VISIBLE);
                            Fragment_driver_signup_setcar.this.txt_carName.setText(name);
                            activity.car_type = name;
                            Glide.with(activity).load(drawable).apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_car2).dontAnimate()).into(Fragment_driver_signup_setcar.this.img_carType);
                        }
                    });
                    Glide.with(activity).load(drawable).apply(new RequestOptions()
                            .placeholder(R.drawable.ic_car2).dontAnimate()).into(img_carType);
                    ly_carType.addView(view);
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == IDCardCamera.RESULT_CODE) {
            final String path = IDCardCamera.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                if (requestCode == IDCardCamera.TYPE_IDCARD_BACK) { //身份证正面
                    img_car.setImageBitmap(BitmapFactory.decodeFile(path));
                    try {
                        Uri uriContent = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), path, null, null));
                        activity.car_photo = uriContent;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {  //身份证反面
                }
                FileUtils.clearCache(activity);

            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SignupActivityDriver) context;
    }
}