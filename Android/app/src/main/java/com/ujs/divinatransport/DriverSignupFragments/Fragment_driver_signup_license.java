package com.ujs.divinatransport.DriverSignupFragments;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.ujs.divinatransport.SignupActivityDriver;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.MyMediaScannerClient;
import com.ujs.divinatransport.Utils.OnMediaScanned;
import com.ujs.divinatransport.idcamera.IDCardCamera;
import com.ujs.divinatransport.idcamera.utils.FileUtils;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;


public class Fragment_driver_signup_license extends Fragment {
    ImageView imageView;
    SignupActivityDriver activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.driver_fragment_signup_license, container, false);
        imageView = v.findViewById(R.id.img_front);

        EditText edit_firstname = v.findViewById(R.id.edit_firstname);
        edit_firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.license_first_name = s.toString().trim();
            }
        });
        EditText edit_number = v.findViewById(R.id.edit_number);
        edit_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.license_number = s.toString().trim();
            }
        });
        EditText edit_lastname = v.findViewById(R.id.edit_lastname);
        edit_lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.license_last_name = s.toString().trim();
            }
        });
        EditText edit_birth_date = v.findViewById(R.id.edit_birth_date);
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener birthdateSelectListner =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String dateStr = String.valueOf(month+1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
                edit_birth_date.setText(dateStr);
                activity.license_birth_date = dateStr;
            }
        };
        edit_birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, birthdateSelectListner,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        EditText edit_issue_date = v.findViewById(R.id.edit_issue_date);
        DatePickerDialog.OnDateSetListener issuedateSelectListner =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String dateStr = String.valueOf(month+1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
                edit_issue_date.setText(dateStr);
                activity.license_issue_date = dateStr;
            }
        };
        edit_issue_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, issuedateSelectListner,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        EditText edit_expiry_date = v.findViewById(R.id.edit_expiry_date);
        DatePickerDialog.OnDateSetListener expirydateSelectListner =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String dateStr = String.valueOf(month+1) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
                edit_expiry_date.setText(dateStr);
                activity.license_expiry_date = dateStr;
            }
        };
        edit_expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, expirydateSelectListner,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        RadioButton radio_man = v.findViewById(R.id.radio_man);
        radio_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.license_gender = "M";
            }
        });
        RadioButton radio_woman = v.findViewById(R.id.radio_woman);
        radio_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.license_gender = "F";
            }
        });

        Button btn_capture = v.findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                activity.openIDCamera();
                IDCardCamera.create(activity).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
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
                if (requestCode == IDCardCamera.TYPE_IDCARD_BACK) { //身份证正面
                    imageView.setImageBitmap(BitmapFactory.decodeFile(path));
//                    activity.getContentResolver().notifyChange(Uri.parse(path), null);
                    try {
                        Uri uriContent = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), path, null, null));
                        activity.license_photo = uriContent;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                }

                FileUtils.clearCache(activity);
            }
        }
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SignupActivityDriver) context;
    }
}