package com.example.divinatransport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.divinatransport.DriverSignupFragments.Fragment_driver_signup_intro;
import com.example.divinatransport.DriverSignupFragments.Fragment_driver_signup_license;
import com.example.divinatransport.DriverSignupFragments.Fragment_driver_signup_userinfo;
import com.example.divinatransport.idcamera.IDCardCamera;

import java.util.ArrayList;

public class DriverSignupActivity extends AppCompatActivity {
    FragmentTransaction transaction;
    ArrayList<String> arr_step = new ArrayList<>();
    int index_step = 0;
    TextView txt_title;
    public Button btn_back, btn_next;
    public boolean termsAgreed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("I want to be a DRIVER");

        arr_step.add("How does it work?");
        arr_step.add("Personal Information");
        arr_step.add("Driver License");

        txt_title = findViewById(R.id.txt_title);
        btn_back = findViewById(R.id.btn_back);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_next.getText().equals("Submit")) {
                    Intent intent = new Intent(DriverSignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    index_step ++;
                    switchStep();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index_step --;
                switchStep();
            }
        });
        selectFragment(new Fragment_driver_signup_intro());
    }

    private void switchStep() {
        switch (index_step) {
            case 0:
                selectFragment(new Fragment_driver_signup_intro());
                break;
            case 1:
                selectFragment(new Fragment_driver_signup_userinfo());
                break;
            case 2:
                selectFragment(new Fragment_driver_signup_license());
                break;
        }
    }

    private void selectFragment(Fragment fragment) {
        txt_title.setText(arr_step.get(index_step));
        btn_back.setEnabled(true);
        btn_next.setEnabled(true);
        if (index_step >= arr_step.size()-1) {
            btn_next.setText("Submit");
        } else {
            btn_next.setText("Next");
        }
        if (index_step <= 0) {
            btn_back.setEnabled(false);
        }

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void openIDCamera() {
        IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}