package com.example.divinatransport.DriverMainFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.divinatransport.DriverMainActivity;
import com.example.divinatransport.R;
import com.example.divinatransport.idcamera.IDCardCamera;
import com.example.divinatransport.idcamera.utils.FileUtils;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class Fragment_driver_set_car extends Fragment {
    DriverMainActivity activity;
    LinearLayout ly_carType;
    int[] carTypes = new int[13];
    String[] carNames = new String[13];
    private ImageView img_carType, img_car;
    private TextView txt_carName;
    private androidx.cardview.widget.CardView cv_carTyppe;
    MaterialDayPicker dayPicker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_set_car, container, false);
        ly_carType = v.findViewById(R.id.ly_carType);
        img_carType = v.findViewById(R.id.img_carType);
        img_car = v.findViewById(R.id.img_car);
        txt_carName = v.findViewById(R.id.txt_carType);
        cv_carTyppe = v.findViewById(R.id.cv_carType);
        dayPicker = v.findViewById(R.id.day_picker);

        carTypes = new int[]{R.drawable.car_auris, R.drawable.car_avensis, R.drawable.car_camry, R.drawable.car_corolla, R.drawable.car_gt86,
                    R.drawable.car_hiace, R.drawable.car_highlander, R.drawable.car_hilux, R.drawable.car_land_cruiser_200, R.drawable.car_land_cruiser_prado,
                    R.drawable.car_prius, R.drawable.car_rav4, R.drawable.car_yaris};
        carNames = new String[]{"Auris", "Avensis", "Camry", "Corolla", "GT86", "Hiace", "Highlander",
                "Hilux", "Land Cruiser 200", "Land Cruiser Prado", "Prius", "RAV4", "Yaris"};

        loadCarTypes();

        Button btn_capture = v.findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDCardCamera.create(activity).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
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
                for(int n = 0; n < carTypes.length; n++) {
                    int drawable = carTypes[n];
                    String name = carNames[n];
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
                            Fragment_driver_set_car.this.cv_carTyppe.setVisibility(View.VISIBLE);
                            Fragment_driver_set_car.this.txt_carName.setText(name);
                            Glide.with(activity).load(drawable).apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_car2).dontAnimate()).into(Fragment_driver_set_car.this.img_carType);
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
        activity = (DriverMainActivity) context;
    }
}