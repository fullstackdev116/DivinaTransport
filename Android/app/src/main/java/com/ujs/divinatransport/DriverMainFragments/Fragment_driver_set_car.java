package com.ujs.divinatransport.DriverMainFragments;

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
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.DriverSignupFragments.Fragment_driver_signup_license;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.Car;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.SignupActivityDriver;
import com.ujs.divinatransport.Utils.Utils;
import com.ujs.divinatransport.idcamera.IDCardCamera;
import com.ujs.divinatransport.idcamera.utils.FileUtils;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class Fragment_driver_set_car extends Fragment {
    MainActivityDriver activity;
    LinearLayout ly_carType;
    private ImageView img_carType, img_car;
    private TextView txt_carName;
    private androidx.cardview.widget.CardView cv_carTyppe;
    NumberPicker numberPicker;
    EditText edit_price;
    int car_seats = 0;
    float car_price = 0.0f;
    Uri car_photo;
    String carType = "";
    Car car;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driver_fragment_set_car, container, false);
        ly_carType = v.findViewById(R.id.ly_carType);
        img_carType = v.findViewById(R.id.img_carType);
        img_car = v.findViewById(R.id.img_car);
        txt_carName = v.findViewById(R.id.txt_carType);
        cv_carTyppe = v.findViewById(R.id.cv_carType);
        edit_price = v.findViewById(R.id.edit_price);
        numberPicker = v.findViewById(R.id.number_picker);
        numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                car_seats = value;
            }
        });
        loadCarTypes();

        Button btn_capture = v.findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDCardCamera.create(Fragment_driver_set_car.this).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
            }
        });
        v.findViewById(R.id.btn_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_price.getText().toString().trim().length() == 0) {
                    Utils.showAlert(getContext(), getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                App.hideKeyboard(activity);
                car_price = Float.valueOf(edit_price.getText().toString());
                car.price = car_price;
                car.type = txt_carName.getText().toString();
                car.seats = numberPicker.getValue();

                if (car_photo != null) {
                    activity.showProgress();
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
                                    activity.dismissProgress();
                                    String downloadUrl = uri.toString();
                                    car.photo = downloadUrl;
                                    Utils.mDatabase.child(Utils.tbl_car).child(car._id).setValue(car);

                                }
                            });
                        }

                    });
                } else {
                    Utils.mDatabase.child(Utils.tbl_car).child(car._id).setValue(car);
                }
                Snackbar.make(activity.parentLayout, getResources().getString(R.string.car_updated_successfully), 3000).show();
            }
        });

        activity.showProgress();
        Utils.mDatabase.child(Utils.tbl_car).orderByChild("uid").equalTo(Utils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                car = datas.getValue(Car.class);
                                car._id = datas.getKey();
                                Glide.with(activity).load(car.photo).apply(new RequestOptions().placeholder(R.drawable.ic_car2).centerCrop()).into(img_car);
                                int index = Arrays.asList(Utils.carNames).indexOf(car.type);
                                cv_carTyppe.setVisibility(View.VISIBLE);
                                Glide.with(activity).load(Utils.carTypes[index]).apply(new RequestOptions().placeholder(R.drawable.ic_car2).fitCenter()).into(img_carType);

                                txt_carName.setText(car.type);
                                numberPicker.setValue(car.seats);
                                edit_price.setText(String.valueOf(car.price));
                                car_price = car.price;
                                car_seats = car.seats;
                                carType = car.type;

//                                car_photo = Uri.parse(car.photo);

                            }
                        } else { // go to signup intro
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
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
                    try {
                        Uri uriContent = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), path, null, null));
                        car_photo = uriContent;
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
        activity = (MainActivityDriver) context;
    }
}