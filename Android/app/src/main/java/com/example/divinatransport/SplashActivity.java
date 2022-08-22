package com.example.divinatransport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RunAnimation(findViewById(R.id.imageView), AnimationUtils.loadAnimation(this, R.anim.fade));
        RunAnimation(findViewById(R.id.ly_title), AnimationUtils.loadAnimation(this, R.anim.fade));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write whatever to want to do after delay specified (1 sec)
//                RunAnimation(findViewById(R.id.txt_doyouknow), AnimationUtils.loadAnimation(SignupActivity.this, R.anim.blink));
                findViewById(R.id.ly_bottom).setVisibility(View.VISIBLE);
                RunAnimation(findViewById(R.id.ly_bottom), AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade1));
                RunAnimation(findViewById(R.id.txt_doyouknow), AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate));
            }
        }, 3000);

        Button btn_driver = findViewById(R.id.btn_driver);
        btn_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, DriverSignupActivity.class);
                startActivity(intent);
            }
        });
    }
    private void RunAnimation(View v, Animation a)
    {
        a.reset();
        v.clearAnimation();
        v.startAnimation(a);
    }
}