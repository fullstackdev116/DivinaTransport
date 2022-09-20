package com.ujs.divinatransport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.ujs.divinatransport.Utils.MyUtils;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SignupActivityCustomer extends AppCompatActivity {
    EditText edit_phone, edit_name;
    CountryCodePicker txt_countryCode;
    String country_code, number;
    Timer timer;
    int otp_sec = 60;
    public View parentLayout;
    ProgressDialog progressDialog;
    Button btn_verify, btn_submit;
    String user_phone = "", user_name = "";
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("I want to be a PASSENGER");

        progressDialog = new ProgressDialog(this);
        parentLayout = findViewById(android.R.id.content);
        edit_phone = findViewById(R.id.edit_phone);
        edit_name = findViewById(R.id.edit_name);
        txt_countryCode = findViewById(R.id.txt_countryCode);
        txt_countryCode.setCountryForPhoneCode(1);
        edit_phone.setText("1111111111");
        TextView txt_howitworks = findViewById(R.id.txt_howitworks);

        btn_verify = findViewById(R.id.btn_verify);
        btn_submit = findViewById(R.id.btn_submit);
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyUtils.isEmptyEditText(edit_phone)) {
                    country_code = txt_countryCode.getSelectedCountryCode();
                    number = edit_phone.getText().toString().trim();
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    App.hideKeyboard(SignupActivityCustomer.this);
                    sendAuthSMS(country_code + number);
                } else {
                    MyUtils.showAlert(SignupActivityCustomer.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_input_your_mobile_number));
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!MyUtils.isEmptyEditText(edit_name)) {
                    user_name = edit_name.getText().toString().trim();
                } else {
                    MyUtils.showAlert(SignupActivityCustomer.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                if (!checkBox.isChecked()) {
                    MyUtils.showAlert(SignupActivityCustomer.this, getResources().getString(R.string.warning), "You must agree on the terms of services for the Divina Transport");
                    return;
                }
                showProgress();
                String token = MyUtils.getDeviceToken(SignupActivityCustomer.this);
                User user = new User("", "", user_name, user_phone, "", 0, 0, MyUtils.PASSENGER, 3, token, 0);
                MyUtils.mDatabase.child(MyUtils.tbl_user).push().setValue(user);
                Snackbar.make(parentLayout, getResources().getString(R.string.user_registered_successfully), 3000).show();
                App.goToMainPage(SignupActivityCustomer.this, progressDialog);
            }
        });

        ImageCarousel carousel = findViewById(R.id.carousel);

        List<CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem(R.drawable.welcome1,"Passenger signup details first page"));
        list.add(new CarouselItem(R.drawable.welcome2,"Passenger signup details second page"));
        list.add(new CarouselItem(R.drawable.welcome3,"Passenger signup details third page"));
        carousel.setData(list);

        checkBox = findViewById(R.id.chk_agree);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Button btn_terms = findViewById(R.id.btn_terms);
        btn_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTermsDialog();
            }
        });
    }
    public void openTermsDialog() {
        final Dialog dlg = new Dialog(this, R.style.Theme_Transparent);
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
    private void sendAuthSMS(final String mobileNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                dismissProgress();
                Log.d("msg", "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                dismissProgress();
                Log.d("msg", e.getLocalizedMessage());
                MyUtils.showAlert(SignupActivityCustomer.this, getResources().getString(R.string.error), e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                dismissProgress();
                openVerifyDialog(verificationID);
                Snackbar.make(parentLayout, getResources().getString(R.string.sms_code_has_been_sent_to_your_phone), 3000).show();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+" + mobileNumber, 60, TimeUnit.SECONDS, this, mCallback);
        showProgress();

    }
    public void openVerifyDialog(String verificationID) {
        final Dialog dlg = new Dialog(this, R.style.Theme_Transparent);
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
                runOnUiThread(new Runnable() {
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
        App.hideKeyboard(this);
        showProgress();
        MyUtils.auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            MyUtils.mDatabase.child(MyUtils.tbl_user).orderByChild(MyUtils.PHONE).equalTo(country_code+number)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            dismissProgress();
                                            if (dataSnapshot.getValue() != null) {
                                                MyUtils.FirebaseLogout();
                                                MyUtils.showAlert(SignupActivityCustomer.this, getResources().getString(R.string.warning), getResources().getString(R.string.phone_number_already_exists));
                                            } else {
                                                Snackbar.make(parentLayout, getResources().getString(R.string.phone_verified_successfully), 3000).show();
                                                btn_verify.setText("Verified ✅");
                                                btn_verify.setEnabled(false);
                                                btn_verify.setTextColor(getResources().getColor(R.color.teal_200));
                                                edit_phone.setEnabled(false);
                                                txt_countryCode.setEnabled(false);
                                                txt_countryCode.setCcpClickable(false);
                                                user_phone = country_code + number;
                                                btn_submit.setEnabled(true);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            dismissProgress();
                                            Snackbar.make(parentLayout, getResources().getString(R.string.sms_verification_failed_please_try_again), 3000).show();

                                            Log.w( "loadPost:onCancelled", databaseError.toException());
                                            // ...
                                        }
                                    });
                        } else {
                            dismissProgress();
                            Snackbar.make(parentLayout, getResources().getString(R.string.sms_verification_failed_please_try_again), 3000).show();
                        }

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}