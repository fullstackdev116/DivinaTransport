package com.ujs.divinatransport;

import static com.ujs.divinatransport.App.RunAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.jkb.vcedittext.VerificationAction;
import com.jkb.vcedittext.VerificationCodeEditText;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.Utils.Utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class SplashActivity extends AppCompatActivity {
    EditText edit_phone;
    CountryCodePicker txt_countryCode;
    String country_code, number;
    public View parentLayout;
    ProgressDialog progressDialog;
    Timer timer;
    int otp_sec = 60;
    TextView txt_msg;
    Dialog dlg_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        parentLayout = findViewById(android.R.id.content);
        findViewById(R.id.ly_bottom).setVisibility(View.GONE);
//        RunAnimation(findViewById(R.id.imageView), AnimationUtils.loadAnimation(this, R.anim.fade));
//        RunAnimation(findViewById(R.id.ly_title), AnimationUtils.loadAnimation(this, R.anim.fade));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.ly_bottom).setVisibility(View.VISIBLE);
//                RunAnimation(findViewById(R.id.ly_bottom), AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade1));
//                RunAnimation(findViewById(R.id.txt_doyouknow), AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate));

                if (Utils.mUser != null) {
                    progressDialog.show();
                    App.goToMainPage(SplashActivity.this, progressDialog);
                }
            }
        }, 2000);

        findViewById(R.id.btn_driver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, SignupActivityDriver.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, SignupActivityCustomer.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginDialog();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    public void openLoginDialog() {
        dlg_login = new Dialog(this);
        Window window = dlg_login.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        edit_phone = view.findViewById(R.id.edit_phone);
        txt_countryCode = view.findViewById(R.id.txt_countryCode);
        txt_countryCode.setCountryForPhoneCode(1);
        edit_phone.setText("1111111111");
        txt_msg = view.findViewById(R.id.txt_msg);
        Button btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEmptyEditText(edit_phone)) {
                    country_code = txt_countryCode.getSelectedCountryCode();
                    number = edit_phone.getText().toString().trim();
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    sendAuthSMS(country_code + number);
                } else {
                    Snackbar.make(parentLayout, getResources().getString(R.string.please_input_your_mobile_number), 1000).show();
                }
            }
        });
        ImageView img_close = view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_login.dismiss();
            }
        });
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.9);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.55);
        dlg_login.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg_login.setContentView(view);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        window.setLayout(width, height);
        dlg_login.show();
        edit_phone.requestFocus();
        App.showKeyboard(this);
    }
    private void sendAuthSMS(final String mobileNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                Log.d("msg", "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Log.d("msg", e.getLocalizedMessage());
                Utils.showAlert(SplashActivity.this, getResources().getString(R.string.error), e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressDialog.dismiss();
                openVerifyDialog(verificationID);
                Snackbar.make(parentLayout, getResources().getString(R.string.sms_code_has_been_sent_to_your_phone), 2000).show();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+" + mobileNumber, 60, TimeUnit.SECONDS, this, mCallback);
        progressDialog.show();

    }
    public void openVerifyDialog(String verificationID) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_o_t_p, null);
        TextView txt_remaining = view.findViewById(R.id.txt_remaining);
        VerificationCodeEditText edit_code = view.findViewById(R.id.edit_code);
        edit_code.setOnVerificationCodeChangedListener(new VerificationAction.OnVerificationCodeChangedListener() {
            @Override
            public void onVerCodeChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onInputCompleted(CharSequence s) {
                txt_remaining.setVisibility(View.INVISIBLE);
                timer.purge();
                timer.cancel();
                String input_code = edit_code.getText().toString();
                try {
                    dlg.dismiss();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, input_code);
                    signInWithPhone(credential);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dlg.show();
        edit_code.requestFocus();
        App.showKeyboard(this);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (timer != null) {
                    edit_code.setText("");
                    timer.purge();
                    timer.cancel();
                }
            }
        });
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        otp_sec = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                otp_sec --;
                SplashActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        txt_remaining.setText("Please input your SMS Verification Code. \nIt will be useful in " + String.valueOf(otp_sec) + " seconds.");
                        if (otp_sec == 0) {
                            txt_remaining.setText("Time has been out! Please resend the verification code.");
                            edit_code.setVisibility(View.INVISIBLE);
                            timer.purge();
                            timer.cancel();
                        }
                    }
                });
            }

        }, 0, 1000);
    }
    public void signInWithPhone(PhoneAuthCredential credential)
    {
        progressDialog.show();
        Utils.auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dlg_login.dismiss();
                            App.goToMainPage(SplashActivity.this, progressDialog);
                        } else {
                            progressDialog.dismiss();
                            txt_msg.setVisibility(View.VISIBLE);
                        }

                    }
                });
    }

}