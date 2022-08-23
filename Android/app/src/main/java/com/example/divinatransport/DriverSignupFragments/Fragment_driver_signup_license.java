package com.example.divinatransport.DriverSignupFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.divinatransport.DriverSignupActivity;
import com.example.divinatransport.R;
import com.example.divinatransport.idcamera.IDCardCamera;


public class Fragment_driver_signup_license extends Fragment {
    ImageView imageView;
    DriverSignupActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.driver_fragment_signup_license, container, false);
        imageView = v.findViewById(R.id.img_front);

        Button btn_capture = v.findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                activity.openIDCamera();
                IDCardCamera.create(activity).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == IDCardCamera.RESULT_CODE) {
            //获取图片路径，显示图片
            final String path = IDCardCamera.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                if (requestCode == IDCardCamera.TYPE_IDCARD_FRONT) { //身份证正面
                    imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                } else if (requestCode == IDCardCamera.TYPE_IDCARD_BACK) {  //身份证反面
//                    mIvBack.setImageBitmap(BitmapFactory.decodeFile(path));
                }

                //实际开发中将图片上传到服务器成功后需要删除全部缓存图片
//                FileUtils.clearCache(this);
            }
        }
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DriverSignupActivity) context;
    }
}