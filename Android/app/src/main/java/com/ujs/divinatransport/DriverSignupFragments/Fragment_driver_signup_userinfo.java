package com.ujs.divinatransport.DriverSignupFragments;

import static android.graphics.Color.WHITE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.R;
import com.ujs.divinatransport.SignupActivityCustomer;
import com.ujs.divinatransport.SignupActivityDriver;
import com.ujs.divinatransport.Utils.Utils;
import com.ujs.divinatransport.idcamera.utils.PermissionUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Fragment_driver_signup_userinfo extends Fragment {
    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    static final String FILE_NAMING_PREFIX = "JPEG_";
    static final String FILE_NAMING_SUFFIX = "_";
    static final String FILE_FORMAT = ".jpg";
    static final String AUTHORITY_SUFFIX = ".cropper.fileprovider";
    SignupActivityDriver activity;

    private Uri cameraUri;
    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), this::onTakePictureResult);
    private final ActivityResultLauncher<String> chooseGallery =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onChooseGalleryResult);
    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), this::onCropImageResult);

    Button btn_verify;
    ImageView img_photo;

    EditText edit_phone;
    CountryCodePicker txt_countryCode;
    String country_code, number;
    Timer timer;
    int otp_sec = 60;
    int MY_PERMISSION_CAMERA = 101;

    private static final String PROGRESS_DIALOG_TAG = "ProcessProgressDialog";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.driver_fragment_signup_userinfo, container, false);
        activity.btn_next.setEnabled(false);

        EditText edit_name = v.findViewById(R.id.edit_name);
        EditText edit_email = v.findViewById(R.id.edit_email);
        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.user_name = s.toString().trim();
            }
        });
        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.user_email = s.toString().trim();
            }
        });
        edit_phone = v.findViewById(R.id.edit_phone);
        txt_countryCode = v.findViewById(R.id.txt_countryCode);
        img_photo = v.findViewById(R.id.img_photo);
        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChoiceDialog();
            }
        });
        btn_verify = v.findViewById(R.id.btn_verify);

        txt_countryCode.setCountryForPhoneCode(1);
        edit_phone.setText("1111111111");

        btn_verify.findViewById(R.id.btn_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEmptyEditText(edit_phone)) {
                    country_code = txt_countryCode.getSelectedCountryCode();
                    number = edit_phone.getText().toString().trim();
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    sendAuthSMS(country_code + number);
                } else {
                    Snackbar.make(getView(), getResources().getString(R.string.please_input_your_mobile_number), 1000).show();
                }

            }
        });

        boolean checkPermissionFirst = PermissionUtils.checkPermissionFirst(activity, MY_PERMISSION_CAMERA,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (!checkPermissionFirst) {
            Snackbar.make(activity.parentLayout, getResources().getString(R.string.enable_permissions), 2000).show();
        }

        return v;
    }
    private void sendAuthSMS(final String mobileNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                activity.dismissProgress();
                Log.d("msg", "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                activity.dismissProgress();
                Log.d("msg", e.getLocalizedMessage());
                Utils.showAlert(activity, getResources().getString(R.string.error), e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                activity.dismissProgress();
                openVerifyDialog(verificationID);
                Snackbar.make(getView(), getResources().getString(R.string.sms_code_has_been_sent_to_your_phone), 2000).show();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+" + mobileNumber, 60, TimeUnit.SECONDS, activity, mCallback);
        activity.showProgress();

    }
    public void openVerifyDialog(String verificationID) {
        final Dialog dlg = new Dialog(activity, R.style.Theme_Transparent);
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
        App.showKeyboard(activity);
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
                activity.runOnUiThread(new Runnable() {
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
        activity.showProgress();
        Utils.auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.PHONE).equalTo(country_code+number)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            activity.dismissProgress();
                                            if (dataSnapshot.getValue() != null) {
                                                Utils.FirebaseLogout();
                                                Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.phone_number_already_exists));
                                            } else {
                                                activity.dismissProgress();
                                                Snackbar.make(getView(), getResources().getString(R.string.phone_verified_successfully), 2000).show();
                                                btn_verify.setText("Verified ✅");
                                                btn_verify.setEnabled(false);
                                                btn_verify.setTextColor(getResources().getColor(R.color.teal_200));
                                                edit_phone.setEnabled(false);
                                                txt_countryCode.setEnabled(false);
                                                txt_countryCode.setCcpClickable(false);
                                                activity.btn_next.setEnabled(true);
                                                activity.user_phone = country_code + number;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            activity.dismissProgress();
                                            Snackbar.make(getView(), getResources().getString(R.string.sms_verification_failed_please_try_again), 3000).show();

                                            Log.w( "loadPost:onCancelled", databaseError.toException());
                                            // ...
                                        }
                                    });
                        } else {
                            activity.dismissProgress();
                            Snackbar.make(getView(), getResources().getString(R.string.sms_verification_failed_please_try_again), 2000).show();
                        }

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
        Glide.with(this).load(uri)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_avatar).centerCrop().dontAnimate()).into(img_photo);
        try {
            Uri uriContent = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), uri, null, null));
            activity.user_photo = uriContent;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SignupActivityDriver) context;
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