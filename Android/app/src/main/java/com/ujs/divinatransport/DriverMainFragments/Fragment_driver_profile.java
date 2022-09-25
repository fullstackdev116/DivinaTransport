package com.ujs.divinatransport.DriverMainFragments;

import static android.graphics.Color.WHITE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.MainActivityDriver;
import com.ujs.divinatransport.Model.Car;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.MyUtils;
import com.ujs.divinatransport.idcamera.utils.PermissionUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_driver_profile extends Fragment {
    MainActivityDriver activity;
    LinearLayout ly_feedback;
    ImageButton btn_edit;
    TextView txt_email, txt_phone, txt_rate, txt_points, txt_name, txt_orders, txt_rejects, txt_drives;
    ImageView img_carType;
    CircleImageView img_photo, img_photo1;
    RatingBar rate;
    ArrayList<Ride> arrayList = new ArrayList<>();

    private Uri cameraUri;
    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), this::onTakePictureResult);
    private final ActivityResultLauncher<String> chooseGallery =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onChooseGalleryResult);
    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), this::onCropImageResult);
    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    static final String FILE_NAMING_PREFIX = "JPEG_";
    static final String FILE_NAMING_SUFFIX = "_";
    static final String FILE_FORMAT = ".jpg";
    static final String AUTHORITY_SUFFIX = ".cropper.fileprovider";
    Uri user_photo;
    String name, email;
    int MY_PERMISSION_CAMERA = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.driver_fragment_profile, container, false);
        ly_feedback = v.findViewById(R.id.ly_feedback);
        btn_edit = v.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDialog();
            }
        });
        txt_email = v.findViewById(R.id.txt_email);
        txt_phone = v.findViewById(R.id.txt_phone);
        txt_rate = v.findViewById(R.id.txt_rate);
        txt_points = v.findViewById(R.id.txt_points);
        txt_name = v.findViewById(R.id.txt_name);
        txt_orders = v.findViewById(R.id.txt_orders);
        txt_rejects = v.findViewById(R.id.txt_rejects);
        txt_drives = v.findViewById(R.id.txt_drives);

        img_carType = v.findViewById(R.id.img_carType);
        img_photo = v.findViewById(R.id.img_photo);
        rate = v.findViewById(R.id.rate);

        loadFeedbacks();
        loadProfile();
        boolean checkPermissionFirst = PermissionUtils.checkPermissionFirst(activity, MY_PERMISSION_CAMERA,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (!checkPermissionFirst) {
            Snackbar.make(activity.parentLayout, getResources().getString(R.string.enable_permissions), 2000).show();
        }
        return v;
    }
    void loadProfile() {
        txt_email.setText(MyUtils.cur_user.email);
        txt_phone.setText("+" + MyUtils.cur_user.phone);
        txt_name.setText(MyUtils.cur_user.name);
        rate.setRating(MyUtils.cur_user.rate);
        txt_rate.setText(String.valueOf(MyUtils.cur_user.rate));
        txt_points.setText(String.valueOf(MyUtils.cur_user.point));
        Glide.with(activity).load(MyUtils.cur_user.photo).apply(new RequestOptions().override(150, 150)
                .placeholder(R.drawable.ic_avatar_white).fitCenter()).into(img_photo);
        txt_rejects.setText(String.valueOf(MyUtils.cur_user.rejects));
        txt_drives.setText(String.valueOf(MyUtils.cur_user.rides));

//        MyUtils.mDatabase.child(MyUtils.tbl_ride_reject).orderByChild("driver_id").equalTo(MyUtils.cur_user.uid)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        activity.dismissProgress();
//                        if (dataSnapshot.getValue() != null) {
//
//                            long cnt = dataSnapshot.getChildrenCount();
//                            txt_rejects.setText(String.valueOf(cnt));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w( "loadPost:onCancelled", databaseError.toException());
//                        // ...
//                    }
//                });

        arrayList.clear();
        MyUtils.mDatabase.child(MyUtils.tbl_history).orderByChild("driver_id").equalTo(MyUtils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Ride ride = datas.getValue(Ride.class);
                                ride._id = datas.getKey();
                                arrayList.add(ride);
                            }
                            if (arrayList.size() > 0) loadFeedbacks();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        MyUtils.mDatabase.child(MyUtils.tbl_car).orderByChild("uid").equalTo(MyUtils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Car car = datas.getValue(Car.class);
                                car._id = datas.getKey();
                                int index = Arrays.asList(MyUtils.carNames).indexOf(car.type);
                                Glide.with(activity).load(MyUtils.carTypes[index]).apply(new RequestOptions().placeholder(R.drawable.ic_car2).fitCenter()).into(img_carType);
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        activity.showProgress();
        MyUtils.mDatabase.child(MyUtils.tbl_order).orderByChild("driver_id").equalTo(MyUtils.cur_user.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {

                            long cnt = dataSnapshot.getChildrenCount();
                            txt_orders.setText(String.valueOf(cnt));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }

    void loadFeedbacks() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LayoutInflater inflater = LayoutInflater.from(activity);
                ly_feedback.removeAllViews();
                for(int n = 0; n < arrayList.size(); n++) {
                    Ride ride = arrayList.get(n);
                    View view = inflater.inflate(R.layout.cell_feedback, null);
                    TextView txt_message = view.findViewById(R.id.txt_message);
                    txt_message.setText(ride.review);
                    RatingBar rate = view.findViewById(R.id.rate);
                    rate.setRating(ride.rate);
                    TextView txt_name = view.findViewById(R.id.txt_name);
                    MyUtils.mDatabase.child(MyUtils.tbl_user).child(ride.passenger_id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        User user = dataSnapshot.getValue(User.class);
                                        user.uid = dataSnapshot.getKey();
                                        txt_name.setText(user.name);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w( "loadPost:onCancelled", databaseError.toException());
                                    // ...
                                }
                            });
                    TextView txt_rate = view.findViewById(R.id.txt_rate);
                    txt_rate.setText(String.valueOf(ride.rate));
                    TextView txt_date = view.findViewById(R.id.txt_date);
                    txt_date.setText(MyUtils.getDateString(ride.date));
                    ly_feedback.addView(view);
                }
            }
        });
    }
    public void openUpdateDialog() {
        final Dialog dlg = new Dialog(activity, R.style.Theme_Transparent);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_profile, null);
        EditText edit_name = view.findViewById(R.id.edit_name);
        EditText edit_email = view.findViewById(R.id.edit_email);
        TextView txt_phone = view.findViewById(R.id.txt_phone);
        img_photo1 = view.findViewById(R.id.img_photo1);
        img_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChoiceDialog();
            }
        });
        edit_name.setText(MyUtils.cur_user.name);
        edit_email.setText(MyUtils.cur_user.email);
        txt_phone.setText(MyUtils.cur_user.phone);
        Glide.with(activity).load(MyUtils.cur_user.photo).apply(new RequestOptions().override(150, 150).placeholder(R.drawable.ic_avatar).centerInside()).into(img_photo1);
        ImageButton btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        Button btn_update = view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edit_name.getText().toString().trim();
                email = edit_email.getText().toString().trim();
                if (user_photo == null && name.length()*email.length() == 0) {
                    MyUtils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                if (!MyUtils.isValidEmail(email)) {
                    MyUtils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.invalid_email));
                    return;
                }
                App.hideKeyboard(activity);
                if (user_photo == null) {
                    MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("name").setValue(name);
                    MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("email").setValue(email);
                    MyUtils.cur_user.name = name;
                    MyUtils.cur_user.email = email;
                    activity.setProfile();
                    loadProfile();
                    Snackbar.make(activity.parentLayout, getResources().getString(R.string.user_updated_successfully), 3000).show();
                } else {
                    uploadUserPhotoToFirebase();
                }
            }
        });
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dlg.show();
    }
    public void uploadUserPhotoToFirebase() {
        activity.showProgress();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = MyUtils.mStorage.child(MyUtils.storage_user+ts);
        file_refer.putFile(user_photo, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        activity.dismissProgress();
                        String downloadUrl = uri.toString();
                        MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("photo").setValue(downloadUrl);
                        MyUtils.cur_user.photo = downloadUrl;
                        if (name.length() > 0) {
                            MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("name").setValue(name);
                            MyUtils.cur_user.name = name;
                        }
                        if (email.length() > 0) {
                            MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("email").setValue(email);
                            MyUtils.cur_user.email = email;
                        }
                        activity.setProfile();
                        loadProfile();
                        Snackbar.make(activity.parentLayout, getResources().getString(R.string.user_updated_successfully), 3000).show();
                    }
                });
            }

        });
    }

    private void displayChoiceDialog() {
        String choiceString[] = new String[] {"Gallery" ,"Camera"};
        AlertDialog.Builder dialog= new AlertDialog.Builder(activity);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Select image from");
        dialog.setItems(choiceString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0) {
                    chooseGallery.launch("image/*");
                } else {
                    startTakePicture();
                }

            }
        }).show();
    }

    private void startTakePicture() {
        try {
            Context ctx = requireContext();
            String authorities = ctx.getPackageName() + AUTHORITY_SUFFIX;
            cameraUri = FileProvider.getUriForFile(ctx, authorities, createImageFile());
            takePicture.launch(cameraUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onTakePictureResult(boolean success) {
        if (success) { cropPictureWithUri(cameraUri); }
        else { showErrorMessage("taking picture failed"); }
    }
    public void onChooseGalleryResult(Uri uri) {
        if (uri != null) {
            cropPictureWithUri(uri);
        }
    }

    public void handleCropImageResult(@NotNull String uri) {
        Glide.with(activity).load(uri).apply(new RequestOptions().placeholder(R.drawable.ic_avatar).centerCrop()).into(img_photo1);
        Uri uriContent = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            uriContent = Uri.parse(uri);
        } else {
            try {
                uriContent = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), uri, null, null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (uriContent != null) {
            user_photo = uriContent;
        }
    }
    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            handleCropImageResult(Objects.requireNonNull(result.getUriContent())
                    .toString()
                    .replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            showErrorMessage("cropping image was cancelled by the user");
        } else {
            showErrorMessage("cropping image failed");
        }
    }
    public void cropPictureWithUri(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
                .setMaxZoom(4)
                .setAutoZoomEnabled(true)
                .setMultiTouchEnabled(true)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(true)
                .setAllowFlipping(true)
                .setSnapRadius(3f)
                .setTouchRadius(48f)
                .setInitialCropWindowPaddingRatio(0.1f)
                .setBorderLineThickness(3f)
                .setBorderLineColor(Color.argb(170, 255, 255, 255))
                .setBorderCornerThickness(2f)
                .setBorderCornerOffset(5f)
                .setBorderCornerLength(14f)
                .setBorderCornerColor(WHITE)
                .setGuidelinesThickness(1f)
                .setGuidelinesColor(R.color.white)
                .setBackgroundColor(Color.argb(119, 0, 0, 0))
                .setMinCropWindowSize(24, 24)
                .setMinCropResultSize(20, 20)
                .setMaxCropResultSize(999, 999)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setOutputUri(null)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .setRequestedSize(0, 0)
                .setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setInitialCropWindowRectangle(null)
//                .setInitialRotation(90)
                .setAllowCounterRotation(false)
                .setFlipHorizontally(false)
                .setFlipVertically(false)
                .setCropMenuCropButtonTitle(null)
                .setCropMenuCropButtonIcon(0)
                .setAllowRotation(true)
                .setNoOutputImage(false)
                .setFixAspectRatio(false);
        cropImage.launch(options);
    }
    private File createImageFile() throws IOException {
        SimpleDateFormat timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                FILE_NAMING_PREFIX + timeStamp + FILE_NAMING_SUFFIX,
                FILE_FORMAT,
                storageDir
        );
    }
    public void showErrorMessage(@NotNull String message) {
        Log.e("Camera Error:", message);
        Toast.makeText(activity, "Crop failed: " + message, Toast.LENGTH_SHORT).show();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityDriver) context;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] _permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, _permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                PermissionUtils.checkPermissionFirst(activity, MY_PERMISSION_CAMERA,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
            }
        }
    }
}