package com.ujs.divinatransport.CustomerMainFragments;

import static android.graphics.Color.WHITE;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.Utils.MyUtils;
import com.ujs.divinatransport.idcamera.utils.PermissionUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_customer_profile extends Fragment {
    MainActivityCustomer activity;
    TextView txt_phone;
    EditText edit_name;
    CircleImageView img_photo;

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
    String name;
    int MY_PERMISSION_CAMERA = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.customer_fragment_profile, container, false);
        txt_phone = v.findViewById(R.id.txt_phone);
        edit_name = v.findViewById(R.id.edit_name);
        img_photo = v.findViewById(R.id.img_photo1);
        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChoiceDialog();
            }
        });
        v.findViewById(R.id.btn_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edit_name.getText().toString().trim();
                if (user_photo == null && name.length() == 0) {
                    MyUtils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                App.hideKeyboard(activity);
                if (user_photo == null) {
                    MyUtils.mDatabase.child(MyUtils.tbl_user).child(MyUtils.cur_user.uid).child("name").setValue(name);
                    MyUtils.cur_user.name = name;
                    activity.setProfile();
                    Snackbar.make(activity.parentLayout, getResources().getString(R.string.user_updated_successfully), 3000).show();
                } else {
                    uploadUserPhotoToFirebase();
                }
            }
        });

        boolean checkPermissionFirst = PermissionUtils.checkPermissionFirst(activity, MY_PERMISSION_CAMERA,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (!checkPermissionFirst) {
            Snackbar.make(activity.parentLayout, getResources().getString(R.string.enable_permissions), 2000).show();
        }
        loadProfile();
        return v;
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
                        activity.setProfile();
                        Snackbar.make(activity.parentLayout, getResources().getString(R.string.user_updated_successfully), 3000).show();
                    }
                });
            }

        });
    }
    void loadProfile() {
        txt_phone.setText("+" + MyUtils.cur_user.phone);
        edit_name.setText(MyUtils.cur_user.name);
        Glide.with(activity).load(MyUtils.cur_user.photo).apply(new RequestOptions().override(150, 150).placeholder(R.drawable.ic_avatar).centerInside()).into(img_photo);
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
//        img_photo.setImageURI(Uri.parse(uri));
        Glide.with(activity).load(uri).apply(new RequestOptions().placeholder(R.drawable.ic_avatar).centerCrop()).into(img_photo);
//        Glide.with(this).load(uri)
//                .apply(new RequestOptions()
//                        .placeholder(R.drawable.ic_avatar).centerCrop().dontAnimate()).into(img_photo);
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
                .setMaxCropResultSize(99999, 99999)
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
    @Override
    public void onResume() {
        super.onResume();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityCustomer) context;
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