package com.ujs.divinatransport;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ujs.divinatransport.DriverSignupFragments.Fragment_driver_signup_intro;
import com.ujs.divinatransport.DriverSignupFragments.Fragment_driver_signup_license;
import com.ujs.divinatransport.DriverSignupFragments.Fragment_driver_signup_setcar;
import com.ujs.divinatransport.DriverSignupFragments.Fragment_driver_signup_userinfo;
import com.ujs.divinatransport.Model.Car;
import com.ujs.divinatransport.Model.DrivingLicense;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.Utils.Utils;

import java.util.ArrayList;

public class SignupActivityDriver extends AppCompatActivity {
    FragmentTransaction transaction;
    ArrayList<String> arr_step = new ArrayList<>();
    int index_step = 0;
    TextView txt_title;
    public Button btn_back, btn_next;
    public boolean termsAgreed = false;
    public ProgressDialog progressDialog;
    public View parentLayout;

    public Uri user_photo;
    public String user_name = "";
    public String user_email = "";
    public String user_phone = "";

    public Uri license_photo;
    public String license_first_name = "";
    public String license_last_name = "";
    public String license_gender = "";
    public String license_birth_date = "";
    public String license_issue_date = "";
    public String license_expiry_date = "";
    public String license_number = "";

    public Uri car_photo;
    public String car_type;
    public int car_seats;
    public float car_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("I want to be a DRIVER");
        index_step = getIntent().getIntExtra("index_step", 0);
        progressDialog = new ProgressDialog(this);
        parentLayout = findViewById(android.R.id.content);

        arr_step.add("How does it work?");
        arr_step.add("Personal Information");
        arr_step.add("Driver License");
        arr_step.add("Car Information");

        txt_title = findViewById(R.id.txt_title);
        btn_back = findViewById(R.id.btn_back);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.hideKeyboard(SignupActivityDriver.this);
                if (btn_next.getText().equals("Submit")) {
                    if (car_price == 0) {
                        Utils.showAlert(SignupActivityDriver.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                        index_step--;
                        return;
                    }
                    if (car_photo == null) {
                        Utils.showAlert(SignupActivityDriver.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_a_photo));
                        index_step--;
                        return;
                    }
                    uploadCarPhotoToFirebase();
                } else {
                    switchStep(true);
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index_step > 1) {
                    new AlertDialog.Builder(SignupActivityDriver.this)
                            .setTitle(getResources().getString(R.string.warning))
                            .setMessage(getResources().getString(R.string.former_info_will_be_reset))
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switchStep(false);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                } else {
                    switchStep(false);
                }
            }
        });
        if (index_step == 0) {
            selectFragment(new Fragment_driver_signup_intro());
        } else if (index_step == 2) {
            selectFragment(new Fragment_driver_signup_license());
            Snackbar.make(parentLayout, getResources().getString(R.string.driving_license_missed), 3000).show();
        } else if (index_step == 3)  {
            selectFragment(new Fragment_driver_signup_setcar());
            Snackbar.make(parentLayout, getResources().getString(R.string.car_info_missed), 3000).show();
        }
    }

    private void switchStep(boolean isIncreasing) {
        if (isIncreasing)
            index_step ++;
        else
            index_step--;
        switch (index_step) {
            case 0:
                selectFragment(new Fragment_driver_signup_intro());
                break;
            case 1:
                selectFragment(new Fragment_driver_signup_userinfo());
                break;
            case 2:
                if (isIncreasing) {
                    if (user_photo == null) {
                        Utils.showAlert(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_a_photo));
                        index_step--;
                        return;
                    }
                    if (user_name.length()*user_email.length() == 0) {
                        Utils.showAlert(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                        index_step--;
                        return;
                    }
                    if (!Utils.isValidEmail(user_email)) {
                        Utils.showAlert(this, getResources().getString(R.string.warning), getResources().getString(R.string.invalid_email));
                        index_step--;
                        return;
                    }
                    uploadUserPhotoToFirebase();
                }
                break;
            case 3:
                if (isIncreasing) {
                    if (license_first_name.length()*license_last_name.length()*license_gender.length()*license_number.length()*license_birth_date.length()*
                            license_issue_date.length()*license_expiry_date.length() == 0) {
                        Utils.showAlert(SignupActivityDriver.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                        index_step--;
                        return;
                    }
                    if (license_photo == null) {
                        Utils.showAlert(SignupActivityDriver.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_a_photo));
                        index_step--;
                        return;
                    }
                    uploadLicensePhotoToFirebase();
                }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
    public void uploadLicensePhotoToFirebase() {
        showProgress();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Utils.mStorage.child(Utils.storage_driving_license+ts);
        file_refer.putFile(license_photo, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dismissProgress();
                        String downloadUrl = uri.toString();
                        DrivingLicense license = new DrivingLicense("", Utils.cur_user.uid, downloadUrl, license_first_name, license_last_name, license_gender, license_birth_date, license_issue_date, license_expiry_date, license_number);
                        Utils.mDatabase.child(Utils.tbl_driving_license).push().setValue(license);
                        Utils.cur_user.state = 1;
                        Utils.mDatabase.child(Utils.tbl_user).child(Utils.cur_user.uid).child("state").setValue(1);
                        Snackbar.make(parentLayout, getResources().getString(R.string.driving_license_registered_successfully), 3000).show();
                        selectFragment(new Fragment_driver_signup_setcar());
                    }
                });
            }

        });
    }
    public void uploadUserPhotoToFirebase() {
        showProgress();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Utils.mStorage.child(Utils.storage_user+ts);
        file_refer.putFile(user_photo, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dismissProgress();
                        String downloadUrl = uri.toString();
                        String token = Utils.getDeviceToken(SignupActivityDriver.this);
                        User user = new User("", user_email, user_name, user_phone, downloadUrl, 0, 0, "DRIVER", 0, token, 0);
//                        Utils.mDatabase.child(Utils.tbl_user).push().setValue(user);
                        String pushKey = Utils.mDatabase.child(Utils.tbl_user).push().getKey();
                        Utils.mDatabase.child(Utils.tbl_user).child(pushKey).setValue(user);
                        user.uid = pushKey;
                        Utils.cur_user = user;
                        Snackbar.make(parentLayout, getResources().getString(R.string.user_registered_successfully), 3000).show();

                        selectFragment(new Fragment_driver_signup_license());

                    }
                });
            }

        });
    }
    public void uploadCarPhotoToFirebase() {
        showProgress();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Utils.mStorage.child(Utils.storage_car+ts);
        file_refer.putFile(car_photo, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dismissProgress();
                        String downloadUrl = uri.toString();
                        Car car = new Car("", Utils.cur_user.uid, downloadUrl, car_type, car_seats, car_price, true);
                        Utils.mDatabase.child(Utils.tbl_car).push().setValue(car);
                        Utils.cur_user.state = 2;
                        Utils.mDatabase.child(Utils.tbl_user).child(Utils.cur_user.uid).child("state").setValue(2);
                        Snackbar.make(parentLayout, getResources().getString(R.string.car_registered_successfully), 3000).show();
                        new AlertDialog.Builder(SignupActivityDriver.this)
                                .setTitle("Success")
                                .setMessage(getResources().getString(R.string.please_wait_admin_enable_login))
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Utils.FirebaseLogout();
                                        finish();
                                    }
                                }).show();
                    }
                });
            }

        });
    }
}