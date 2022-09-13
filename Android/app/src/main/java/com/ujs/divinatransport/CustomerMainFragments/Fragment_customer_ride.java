package com.ujs.divinatransport.CustomerMainFragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.Model.Car;
import com.ujs.divinatransport.Model.GeoUser;
import com.ujs.divinatransport.Model.Ride;
import com.ujs.divinatransport.Model.User;
import com.ujs.divinatransport.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.snackbar.Snackbar;
import com.kienht.bottomsheetbehavior.BottomSheetBehavior;
import com.ujs.divinatransport.App;
import com.ujs.divinatransport.service.AlarmBroadcast;
import com.ujs.divinatransport.Utils.Utils;
import com.ujs.divinatransport.directionhelpers.FetchURL;
import com.ujs.divinatransport.directionhelpers.TaskLoadedCallback;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRUpdateLocationCallBack;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRUpdateLocationCallBack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.madapps.placesautocomplete.PlaceAPI;
import in.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter;
import in.madapps.placesautocomplete.listener.OnPlacesDetailsListener;
import in.madapps.placesautocomplete.model.Place;
import in.madapps.placesautocomplete.model.PlaceDetails;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pl.droidsonroids.gif.GifImageView;

public class Fragment_customer_ride extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {
    public int AUTOCOMPLETE_REQUEST_CODE = 1;
    MainActivityCustomer activity;
    BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    Polyline polyline;
    ArrayList<GeoUser> arr_nearby = new ArrayList<>();

    boolean location_track = false;
    Marker marker_start, marker_target;

    LinearLayout ly_top, ly_topClick;
    RelativeLayout ly_topContent;
    boolean touch_enabled_start = false, touch_enabled_target = false;
    TextView txt_state;
    ImageButton btn_down;
    AutoCompleteTextView edit_start, edit_target;
    ImageButton btn_view;

    CircleImageView img_photo;
    ImageView img_carType, img_car;
    TextView txt_name, txt_rate, txt_seats, txt_carPrice, txt_state_driver, txt_target_step3, txt_time_remaining_step1;
    RatingBar ratingBar;
    ImageButton btn_sos;
    ImageView img_touch_start, img_touch_target;
    Button btn_cancel_ride, btn_ediapay, btn_cache;
    GifImageView gif_loading;
    Button btn_cancel_driver;

    LatLng pos_start, pos_target;
    MapRipple ripple_ride;

    float price = 0.0f;
    int pay_balance = 0;
    long distance = 0, duration = 0;
    Ride my_ride;
    GeoUser sel_user;
    Car sel_car;

    RelativeLayout ly_ridingSteps;
    LinearLayout ly_step1, ly_step2, ly_step3, ly_step4;
    CircleImageView img_driver;
    RatingBar rate_driver;
    TextView txt_name_driver, txt_accept_step1, txt_seats_driver, txt_price_driver, txt_time_remaining_step3, txt_distance_remaining_step3;
    TextView txt_balance, txt_estimate;
    ImageView img_carType_driver;

//    MaterialRatingBar ratingBar_driver;
    // step 2 --------------
    LinearLayout ly_pay;
    TextView txt_confirm;
    // step4 --------------
    MaterialRatingBar rate_driver_step4;
    TextView txt_rate_driver;
    EditText edit_review;
    Button btn_submit_review;

    Dialog pickupDialog;


    private static final int VOICE_RECOGNITION_REQUEST_CODE_START = 201;
    private static final int VOICE_RECOGNITION_REQUEST_CODE_TARGET = 202;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_fragment_ride, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* ----- step1 components ----- */
        ly_ridingSteps = v.findViewById(R.id.ly_ridingSteps);
        ly_step1 = v.findViewById(R.id.ly_step1);
        ly_step2 = v.findViewById(R.id.ly_step2);
        ly_step3 = v.findViewById(R.id.ly_step3);
        ly_step4 = v.findViewById(R.id.ly_step4);
        img_driver = v.findViewById(R.id.img_driver);
        rate_driver = v.findViewById(R.id.rate_driver);
        txt_estimate = v.findViewById(R.id.txt_estimate);
        txt_name_driver = v.findViewById(R.id.txt_name_driver);
        txt_accept_step1 = v.findViewById(R.id.txt_accept_step1);
        txt_seats_driver = v.findViewById(R.id.txt_seats_driver);
        txt_price_driver = v.findViewById(R.id.txt_price_driver);
        txt_time_remaining_step1 = v.findViewById(R.id.txt_time_remaining_step1);
        txt_time_remaining_step3 = v.findViewById(R.id.txt_time_remaining_step3);
        txt_distance_remaining_step3 = v.findViewById(R.id.txt_distance_remaining_step3);
        img_carType_driver = v.findViewById(R.id.img_carType_driver);
        btn_ediapay = v.findViewById(R.id.btn_ediapay);
        btn_cache = v.findViewById(R.id.btn_cache);
        txt_target_step3 = v.findViewById(R.id.txt_target_step3);
        rate_driver_step4 = v.findViewById(R.id.rate_driver_step4);
        txt_rate_driver = v.findViewById(R.id.txt_rate_driver);
        btn_submit_review = v.findViewById(R.id.btn_submit_review);
        gif_loading = v.findViewById(R.id.gif_loading);

        txt_state = v.findViewById(R.id.txt_state);
        ly_topClick = v.findViewById(R.id.ly_topClick);
        btn_cancel_ride = v.findViewById(R.id.btn_cancel_ride);
        txt_balance = v.findViewById(R.id.txt_balance);

        img_photo = v.findViewById(R.id.img_photo);
        img_carType = v.findViewById(R.id.img_carType);
        img_car = v.findViewById(R.id.img_car);
        txt_name = v.findViewById(R.id.txt_name);
        txt_rate = v.findViewById(R.id.txt_rate);
        txt_seats = v.findViewById(R.id.txt_seats);
        txt_carPrice = v.findViewById(R.id.txt_carPrice);
        ratingBar = v.findViewById(R.id.rate);
        txt_state_driver = v.findViewById(R.id.txt_state_driver);
        ly_pay = v.findViewById(R.id.ly_pay);
        txt_confirm = v.findViewById(R.id.txt_confirm);
        edit_review = v.findViewById(R.id.edit_review);
        btn_cancel_driver = v.findViewById(R.id.btn_cancel_driver);

        btn_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.confirm))
                        .setMessage("Did you pay the balance via cache?")
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (my_ride != null) {
                                    my_ride.paid = 1;
                                    my_ride.price = pay_balance;
                                    Utils.mDatabase.child(Utils.tbl_ride).child(my_ride._id).setValue(my_ride);
                                    ly_pay.setVisibility(View.GONE);
                                    txt_confirm.setVisibility(View.VISIBLE);
                                }
                            }
                        }).show();

            }
        });
        btn_ediapay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_cancel_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.warning))
                        .setMessage("Are you sure to cancel the driver?")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                my_ride.state = 0;
                                my_ride.driver_id = "";
                                Utils.mDatabase.child(Utils.tbl_ride).child(my_ride._id).setValue(my_ride);
                                goRideState();
                            }
                        }).show();
            }
        });

        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = edit_review.getText().toString().trim();
                if (review.length() == 0) {
                    Utils.showAlert(getContext(), getResources().getString(R.string.warning), "Please fill in review field.");
                    return;
                }
                if (rate_driver_step4.getRating() == 0) {
                    Utils.showAlert(getContext(), getResources().getString(R.string.warning), "Please rate your riding.");
                    return;
                }
                my_ride.review = review;
                my_ride.rate = rate_driver_step4.getRating();

                String key = Utils.mDatabase.child(Utils.tbl_history).push().getKey();
                Utils.mDatabase.child(Utils.tbl_history).child(key).setValue(my_ride);

                activity.showProgress();
                Utils.mDatabase.child(Utils.tbl_user).child(my_ride.driver_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User user = dataSnapshot.getValue(User.class);
                            user.uid = dataSnapshot.getKey();
                            if (user.rate == 0) {
                                user.rate = my_ride.rate;
                            } else {
                                user.rate = (user.rate + my_ride.rate)/2;
                            }
                            if (my_ride.isSOS) {
                                user.point += 30;
                            } else {
                                user.point += 20;
                            }
                            Utils.mDatabase.child(Utils.tbl_user).child(user.uid).setValue(user);
                            Utils.mDatabase.child(Utils.tbl_ride).child(my_ride._id).setValue(null);
                            my_ride = null;
                        }
                        activity.dismissProgress();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        activity.dismissProgress();
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

                pos_start = null;
                pos_target = null;
                if (marker_start != null) marker_start.remove();
                if (marker_target != null) marker_target.remove();
                if (polyline != null) polyline.remove();
            }
        });

        v.findViewById(R.id.img_record_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceActivity(VOICE_RECOGNITION_REQUEST_CODE_START);
            }
        });
        v.findViewById(R.id.img_record_target).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceActivity(VOICE_RECOGNITION_REQUEST_CODE_TARGET);
            }
        });
        btn_cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.warning))
                        .setMessage("Are you sure to cancel the riding?")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btn_cancel_ride.setVisibility(View.GONE);
                                btn_sos.setVisibility(View.VISIBLE);
                                cancelRide();
                            }
                        }).show();

            }
        });
        btn_sos = v.findViewById(R.id.btn_sos);
        btn_sos.setTag("sos");
        btn_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = btn_sos.getTag().toString();
                if (tag.equals("sos")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getResources().getString(R.string.warning))
                            .setMessage("Are you sure to send SOS?")
                            .setCancelable(true)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    my_ride = new Ride("", Utils.cur_user.uid, "", 0, 0, 0, 0, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude(), new Date(), 0, 0, true, 0, "", "", "", 0.0f, "");
                                    String key = Utils.mDatabase.child(Utils.tbl_ride).push().getKey();
                                    Utils.mDatabase.child(Utils.tbl_ride).child(key).setValue(my_ride);
                                    my_ride._id = key;
                                    goRideState();
                                }
                            }).show();
                } else {
                    btn_sos.setImageDrawable(getResources().getDrawable(R.drawable.ic_sos));
                    btn_sos.setTag("sos");
                    cancelRide();
                }
            }
        });
        v.findViewById(R.id.img_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                pos_start = new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude());
                new Utils.GetAddressTask(getContext(), edit_start, new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude())).execute();
                addStartMarker();
            }
        });
        ImageButton btn_menu = v.findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openDrawer();
            }
        });
        ImageButton btn_location = v.findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location_track = !location_track;
                String msg = "";
                if (!location_track) {
                    btn_location.setBackground(getResources().getDrawable(R.drawable.ic_location_view));
                    msg = "Location Tracking is disabled.";
                } else {
                    btn_location.setBackground(getResources().getDrawable(R.drawable.ic_location_track));
                    msg = "Location Tracking is enabled.";
//                    findMyLocation();
                }
                Snackbar.make(getView(), msg, 3000).show();
            }
        });
        ly_top = v.findViewById(R.id.ly_top);
        ly_topContent = v.findViewById(R.id.ly_topContent);
        ly_topContent.setVisibility(View.GONE);
        btn_down = v.findViewById(R.id.btn_down);
        btn_down.setTag(0);

        ly_topClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTopViewState();
            }
        });

        img_touch_start = v.findViewById(R.id.img_touch_start);
        img_touch_target = v.findViewById(R.id.img_touch_target);
        img_touch_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (touch_enabled_start) {
                    touch_enabled_start = false;
                    ImageViewCompat.setImageTintList(img_touch_start, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
                } else {
                    touch_enabled_start = true;
                    touch_enabled_target = false;
                    ImageViewCompat.setImageTintList(img_touch_start, ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    Snackbar.make(getView(), "Please touch your start point on Map.", 3000).show();
                }
                ImageViewCompat.setImageTintList(img_touch_target, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
            }
        });
        img_touch_target.findViewById(R.id.img_touch_target).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (touch_enabled_target) {
                    touch_enabled_target = false;
                    ImageViewCompat.setImageTintList(img_touch_target, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
                } else {
                    touch_enabled_target = true;
                    touch_enabled_start = false;
                    ImageViewCompat.setImageTintList(img_touch_target, ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    Snackbar.make(getView(), "Please touch your target point on Map.", 3000).show();
                }
                ImageViewCompat.setImageTintList(img_touch_start, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
            }
        });

        PlaceAPI placeAPI = new PlaceAPI.Builder().build(activity);
        placeAPI.setApiKey(getResources().getString(R.string.google_api_key_billed));
        edit_start = v.findViewById(R.id.edit_start);
        edit_start.setAdapter(new PlacesAutoCompleteAdapter(activity, placeAPI));
        edit_start.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Place place;
                    place = (Place)parent.getItemAtPosition(position);
                    edit_start.setText(place.getDescription());
                    placeAPI.fetchPlaceDetails(place.getId(), new OnPlacesDetailsListener() {
                        @Override
                        public void onPlaceDetailsFetched(@NonNull PlaceDetails placeDetails) {
                            pos_start = new LatLng(placeDetails.getLat(), placeDetails.getLng());
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addStartMarker();
                                }
                            });
                        }

                        @Override
                        public void onError(@NonNull String s) {
                            Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        ImageView img_cancel_start = v.findViewById(R.id.img_cancel_start);
        img_cancel_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_start.setText("");
                if (marker_start != null) marker_start.remove();
                if (polyline != null) polyline.remove();
                pos_start = null;
            }
        });
        edit_start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    img_cancel_start.setVisibility(View.VISIBLE);
                } else {
                    img_cancel_start.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_target = v.findViewById(R.id.edit_target);
        edit_target.setAdapter(new PlacesAutoCompleteAdapter(activity, placeAPI));
        edit_target.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Place place;
                    place = (Place)(parent.getItemAtPosition(position));
                    edit_target.setText(place.getDescription());
                    placeAPI.fetchPlaceDetails(place.getId(), new OnPlacesDetailsListener() {
                        @Override
                        public void onPlaceDetailsFetched(@NonNull PlaceDetails placeDetails) {
                            pos_target = new LatLng(placeDetails.getLat(), placeDetails.getLng());
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addTargetMarker();
                                }
                            });

                        }

                        @Override
                        public void onError(@NonNull String s) {

                        }
                    });
                }catch (Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        ImageView img_cancel_target = v.findViewById(R.id.img_cancel_target);
        img_cancel_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_target.setText("");
                if (marker_target != null) marker_target.remove();
                if (polyline != null) polyline.remove();
                pos_target = null;
            }
        });
        edit_target.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    img_cancel_target.setVisibility(View.VISIBLE);
                } else {
                    img_cancel_target.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView img_expand = v.findViewById(R.id.expand_icon);
        bottomSheetBehavior = BottomSheetBehavior.from(v.findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                float rotation = 0;
                switch (i) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        rotation = 0;
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        rotation = 180;
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        rotation = 180;
                        break;
                }
                img_expand.animate().rotationX(rotation).start();
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        RelativeLayout ly_clickable = v.findViewById(R.id.clickable);
        ly_clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        View sheet_header_shadow = v.findViewById(R.id.sheet_header_shadow);
        NestedScrollView nestedScrollView = v.findViewById(R.id.description_scrollview);
        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v1, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                sheet_header_shadow.setActivated(v1.canScrollVertically(-1));
            }
        });

        btn_view = v.findViewById(R.id.btn_view);
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos_target == null || pos_target == null) {
                    Utils.showAlert(getContext(), getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }

                openPickupDialog();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                touch_enabled_start = false;
                ImageViewCompat.setImageTintList(img_touch_start, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
                touch_enabled_target = false;
                ImageViewCompat.setImageTintList(img_touch_target, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
            }
        });
        if (App.prefs.getString(App.App_launched, "").length() == 0) {
            App.setPreference(App.App_launched, "true");
            openSupportDialog();
        }
        activity.locationUpdateCallback = new MainActivityCustomer.LocationUpdateCallback() {
            @Override
            public void locationUpdateCallback() {
                mLastLocation = Utils.cur_location;
                addMarker(mMap, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude(), location_track, R.drawable.ic_pin0);
                Utils.geo_passenger.setLocation(Utils.cur_user.uid, new GeoLocation(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Snackbar.make(getView(), error.getMessage(), 3000).show();
                        } else {
                            getListNearbyCars();
                        }
                    }
                });
                if (ripple_ride != null) {
                    if (ripple_ride.isAnimationRunning()) {
                        ripple_ride.withLatLng(new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()));
                    }
                }
                if (isFirstLoad) {
                    isFirstLoad = false;
                    LatLng myLatlng = new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 16f));
                }
                checkMyRide1();
            }
        };
        return v;
    }
    void checkMyRide1() {
        Utils.mDatabase.child(Utils.tbl_ride).orderByChild("passenger_id").equalTo(Utils.cur_user.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for(DataSnapshot datas: snapshot.getChildren()){
                        Ride ride = datas.getValue(Ride.class);
                        ride._id = datas.getKey();

                        my_ride = ride;
                        goRideState();

                        for (GeoUser geoUser : arr_nearby) {
                            User user = geoUser.user;
                            if (my_ride.driver_id.equals(user.uid)) {
                                sel_user = geoUser;
                                break;
                            }
                        }
                        if (sel_user != null) {
                            getCarInfo(my_ride.driver_id);
                        }
                    }
                } else {
                    if (ripple_ride != null) {
                        if (ripple_ride.isAnimationRunning()) {
                            ripple_ride.stopRippleMapAnimation();
                        }
                        ripple_ride = null;
                    }
                    my_ride = null;
                    ly_ridingSteps.setVisibility(View.GONE);
                    ly_topClick.setEnabled(true);
                    btn_sos.setVisibility(View.VISIBLE);
                    btn_sos.setImageDrawable(getResources().getDrawable(R.drawable.ic_sos));
                    btn_sos.setTag("sos");
                    btn_cancel_ride.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color", "Error: " + error.getMessage());
//                activity.dismissProgress();
            }
        });
    }
    void cancelRide() {
        if (my_ride != null) Utils.mDatabase.child(Utils.tbl_ride).child(my_ride._id).setValue(null);
        App.removePreference(Utils.MY_RIDE);
        my_ride = null;
        touch_enabled_start = false; touch_enabled_target = false;
        ImageViewCompat.setImageTintList(img_touch_target, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
        ImageViewCompat.setImageTintList(img_touch_start, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
        ly_topClick.setEnabled(true);
        if (ripple_ride != null) {
            if (ripple_ride.isAnimationRunning()) {
                ripple_ride.stopRippleMapAnimation();
            }
            ripple_ride = null;
        }
    }
    void addStartMarker() {
        if (marker_start != null) marker_start.remove();
        marker_start = mMap.addMarker(new MarkerOptions().position(pos_start)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        marker_start.setTitle(edit_start.getText().toString());
        if (pos_target != null) {
            new FetchURL(activity, Fragment_customer_ride.this).execute(Utils.getDirectionUrl(pos_start, pos_target, "driving", activity), "driving");
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_start, 16f));
    }
    void addTargetMarker() {
        if (marker_target != null) marker_target.remove();
        marker_target = mMap.addMarker(new MarkerOptions().position(pos_target)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        marker_target.setTitle(edit_target.getText().toString());
        if (pos_start != null) {
            new FetchURL(activity, Fragment_customer_ride.this).execute(Utils.getDirectionUrl(pos_start, pos_target, "driving", activity), "driving");
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_target, 16f));
    }
    @Override
    public void onTaskDone(long distanceVal, long durationVal, Object... values) {
        if (polyline != null) {
            polyline.remove();
        }
        polyline = mMap.addPolyline((PolylineOptions) values[0]);
        // go to step1
        distance = distanceVal;
        duration = durationVal;
        price = Math.round(((float)distance/1000) * 100);

    }
    void rideNow() {
        String from_address = edit_start.getText().toString().trim();
        String to_address = edit_target.getText().toString().trim();
        if (my_ride == null) {
            my_ride = new Ride("", Utils.cur_user.uid, driver_id, pos_start.latitude, pos_start.longitude, pos_target.latitude, pos_target.longitude, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude(), new Date(), 0, 0, false, 0, from_address, to_address, "", 0.0f, "");
        }
        String key = Utils.mDatabase.child(Utils.tbl_ride).push().getKey();
        Utils.mDatabase.child(Utils.tbl_ride).child(key).setValue(my_ride);
        my_ride._id = key;
        closeTopView();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        goRideState();
    }
    String driver_id = "";
    public void openPickupDialog() {
        pickupDialog = new Dialog(activity);
        Window window = pickupDialog.getWindow();
        View view = getLayoutInflater().inflate(R.layout.fragment_bottomsheet, null);
        TextView txt_distance = view.findViewById(R.id.txt_distance);
        TextView txt_duration = view.findViewById(R.id.txt_duration);
        DecimalFormat df = new DecimalFormat("0.00");
        txt_distance.setText(String.valueOf(df.format((float)distance/1000))+ " Km");
        txt_duration.setText(Utils.getDurationStr(duration));

        Button btn_ride = view.findViewById(R.id.btn_ride);
        btn_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send notification
                rideNow();
                pickupDialog.dismiss();
            }
        });
        Button btn_order = view.findViewById(R.id.btn_order);
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        CircleImageView img_photo1 = view.findViewById(R.id.img_photo);
        TextView txt_name1 = view.findViewById(R.id.txt_name);
        LinearLayout ly_one = view.findViewById(R.id.ly_one);
        LinearLayout ly_all = view.findViewById(R.id.ly_all);
        if (sel_user != null) {
            Glide.with(activity).load(sel_user.user.photo).apply(new RequestOptions().placeholder(R.drawable.ic_avatar_white).centerCrop()).into(img_photo1);
            txt_name1.setText(sel_user.user.name);
        } else {
            ly_one.setVisibility(View.GONE);
        }
        ly_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_one.setBackgroundResource(R.drawable.frame_border_red);
                ly_all.setBackgroundColor(Color.TRANSPARENT);
                ly_all.setPadding(10,10,10,10);
                ly_one.setPadding(10,10,10,10);
                driver_id = sel_user.user.uid;

                btn_order.setEnabled(true);
                btn_order.setBackground(getResources().getDrawable(R.drawable.round_frame));
            }
        });
        ly_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_all.setBackgroundResource(R.drawable.frame_border_red);
                ly_one.setBackgroundColor(Color.TRANSPARENT);
                ly_all.setPadding(10,10,10,10);
                ly_one.setPadding(10,10,10,10);
                driver_id = "";
                btn_order.setEnabled(false);
                btn_order.setBackground(getResources().getDrawable(R.drawable.frame_border));
            }
        });
        if (driver_id.length() == 0) {
            btn_order.setEnabled(false);
            btn_order.setBackground(getResources().getDrawable(R.drawable.frame_border));
        } else {
            btn_order.setEnabled(true);
            btn_order.setBackground(getResources().getDrawable(R.drawable.round_frame));
        }

        int width = (int)(getResources().getDisplayMetrics().widthPixels*1);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.35);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        pickupDialog.setCancelable(true);
        pickupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pickupDialog.setContentView(view);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(width, height);
        pickupDialog.show();
    }
    boolean isFirstLoad = true;
    @SuppressLint("SetTextI18n")
    void goRideState() {
        LatLng myLatlng;
        if (Utils.cur_location != null) {
            myLatlng = new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude());
        } else {
            myLatlng = new LatLng(my_ride.cur_lat, my_ride.cur_lng);
        }

        if (ripple_ride != null) {
            if (ripple_ride.isAnimationRunning()) {
                ripple_ride.stopRippleMapAnimation();
            }
            ripple_ride = null;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ly_topClick.setEnabled(false);
        if (pos_start == null && my_ride.from_lat != 0) {
            pos_start = new LatLng(my_ride.from_lat, my_ride.from_lng);
            pos_target = new LatLng(my_ride.to_lat, my_ride.to_lng);
            new Utils.GetAddressTask(getContext(), edit_start, pos_start);
            new Utils.GetAddressTask(getContext(), edit_target, pos_target);

            if (marker_start != null) marker_start.remove();
            marker_start = mMap.addMarker(new MarkerOptions().position(pos_start)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            marker_start.setTitle(edit_start.getText().toString());

            if (marker_target != null) marker_target.remove();
            marker_target = mMap.addMarker(new MarkerOptions().position(pos_target)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            marker_target.setTitle(edit_target.getText().toString());

            new FetchURL(activity, Fragment_customer_ride.this).execute(Utils.getDirectionUrl(pos_start, pos_target, "driving", activity), "driving");
        }
        ly_ridingSteps.setVisibility(View.VISIBLE);
        ly_step1.setVisibility(View.GONE); ly_step2.setVisibility(View.GONE); ly_step3.setVisibility(View.GONE); ly_step4.setVisibility(View.GONE);
        btn_sos.setVisibility(View.GONE);
        gif_loading.setVisibility(View.VISIBLE);

        if (sel_user != null) {
            User user = sel_user.user;
            Glide.with(activity).load(user.photo).apply(new RequestOptions().placeholder(R.drawable.ic_avatar).centerCrop()).into(img_driver);
            txt_name_driver.setText(user.name);
            rate_driver.setRating(user.rate);
        }
        switch (my_ride.state) {
            case 0:
                closeTopView();
                ly_ridingSteps.setVisibility(View.GONE);
                int color = Color.GREEN;
                if (my_ride.isSOS) {
                    color = Color.RED;
                    btn_sos.setImageDrawable(getResources().getDrawable(R.drawable.ic_sos_cancel));
                    btn_sos.setTag("cancel");
                    btn_cancel_ride.setVisibility(View.GONE);
                    btn_sos.setVisibility(View.VISIBLE);
                } else {
                    btn_sos.setVisibility(View.GONE);
                    btn_cancel_ride.setVisibility(View.VISIBLE);
                }
                touch_enabled_start = false; touch_enabled_target = false;
                ImageViewCompat.setImageTintList(img_touch_target, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
                ImageViewCompat.setImageTintList(img_touch_start, ColorStateList.valueOf(getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray)));
                ripple_ride = Utils.initRadar(mMap, myLatlng, getContext(), color);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my, 16f));
                break;
            case 1:
                btn_sos.setVisibility(View.GONE);
                if (my_ride.isSOS) {
                    openTopView();
                    btn_view.setVisibility(View.GONE);
                } else {
                    closeTopView();
                }
                btn_cancel_ride.setVisibility(View.GONE);
                if (sel_user != null) {
                    ly_step1.setVisibility(View.VISIBLE);
                    LatLng pStart = new LatLng(sel_user.location.latitude, sel_user.location.longitude);
                    LatLng pTarget = new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude());
                    double dist = Utils.distanceBetween(pStart, pTarget);
                    long seconds = (long)dist/10; // car speed: 10m/sec
                    txt_time_remaining_step1.setText(Utils.getDurationStr(seconds) + " remaining to reach");
                    if (sel_car != null) {
                        pay_balance = Math.round((float)distance/1852*sel_car.price);
                        txt_estimate.setText("Estimate: " + String.valueOf(pay_balance)+ " XOF");
                    }
                    if (dist < 5) {
                        if (!my_ride.isSOS) {
                            my_ride.state = 2;
                            Utils.mDatabase.child(Utils.tbl_ride).child(my_ride._id).setValue(my_ride);
                        } else {
                            if (pos_start == null || pos_target == null) {
                                Snackbar.make(activity.parentLayout, "Please set departure and target address!", 2000).show();
                            } else {
                                my_ride.from_lat = pos_start.latitude;
                                my_ride.from_lng = pos_start.longitude;
                                my_ride.to_lat = pos_target.latitude;
                                my_ride.to_lng = pos_target.longitude;
                                my_ride.from_address = edit_start.getText().toString();
                                my_ride.to_address = edit_target.getText().toString();
                                my_ride.state = 2;
                                Utils.mDatabase.child(Utils.tbl_ride).child(my_ride._id).setValue(my_ride);
                            }
                        }
                    }
                    gif_loading.setVisibility(View.GONE);
                }
                break;
            case 2:
                closeTopView();
                btn_view.setVisibility(View.VISIBLE);
                if (sel_car != null) {
                    ly_step2.setVisibility(View.VISIBLE);
                    pay_balance = Math.round((float)distance/1852*sel_car.price);
                    txt_balance.setText(String.valueOf(pay_balance)+ " XOF");
                    gif_loading.setVisibility(View.GONE);
                    if (my_ride.paid == 1) {
                        ly_pay.setVisibility(View.GONE);
                        txt_confirm.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case 3:
                LatLng pos_cur = new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude());
                new FetchURL(activity, Fragment_customer_ride.this).execute(Utils.getDirectionUrl(pos_cur, pos_target, "driving", activity), "driving");
                if (distance > 0) {
                    ly_step3.setVisibility(View.VISIBLE);
                    txt_target_step3.setText(my_ride.to_address);
                    txt_time_remaining_step3.setText(Utils.getDurationStr(duration));
                    txt_distance_remaining_step3.setText(Utils.getDistanceStr(distance));
                    txt_time_remaining_step3.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.blink));
                    txt_distance_remaining_step3.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.blink));
                    if (distance < 100) {
                        my_ride.state = 4;
                        Utils.mDatabase.child(Utils.tbl_ride).child(my_ride._id).setValue(my_ride);
                    }
                    gif_loading.setVisibility(View.GONE);
                }
                break;
            case 4:
                ly_step4.setVisibility(View.VISIBLE);
                rate_driver_step4.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                        txt_rate_driver.setText(String.valueOf(rating));
                    }
                });
                gif_loading.setVisibility(View.GONE);
                break;
        }
    }

//    private String select_key = "";
    Map<String, GeoLocation> mGeoLastLocation = new HashMap<String, GeoLocation>();
    Map<String, GeoLocation> oldGeoLocation = new HashMap<String, GeoLocation>();
    Map<String, Integer> markerCountGeo = new HashMap<String, Integer>();
    Map<String, Marker> markerGeo = new HashMap<String, Marker>();

    private Location mLastLocation;
    private Location oldLocation;
    private int markerCount = 0;
    private Marker marker;
    public void addMarker(GoogleMap googleMap, double lat, double lon, boolean isCamera, int drawable) {
        try {
            if (markerCount == 1) {
                if (oldLocation != null) {
                    new HRMarkerAnimation(googleMap, 1000, new HRUpdateLocationCallBack() {
                        @Override
                        public void onHRUpdatedLocation(Location updatedLocation) {
                            oldLocation = updatedLocation;
                        }
                    }).animateMarker(mLastLocation, oldLocation, marker, isCamera, false);
                } else {
                    oldLocation = mLastLocation;
                }
            } else if (markerCount == 0) {
                if (marker != null) {
                    marker.remove();
                }
                mMap = googleMap;

                LatLng latLng = new LatLng(lat, lon);

                marker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(drawable)));
                marker.setTitle("It's me");
                marker.setTag(Utils.cur_user.uid);
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCount = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addMarkerGeo(String title, String key, GoogleMap googleMap, double lat, double lon, boolean isCamera, int drawable) {
        try {
            if (markerCountGeo.get(key) == 1) {
                if (oldGeoLocation.get(key) != null) {
                    new GeoHRMarkerAnimation(googleMap, 1000, new GeoHRUpdateLocationCallBack() {
                        @Override
                        public void onGeoHRUpdatedLocation(GeoLocation updatedLocation) {
                            oldGeoLocation.put(key, updatedLocation);
                        }
                    }).animateMarkerGeo(mGeoLastLocation.get(key), oldGeoLocation.get(key), markerGeo.get(key), isCamera, true);
                } else {
                    oldGeoLocation.put(key, mGeoLastLocation.get(key));
                }
            } else if (markerCountGeo.get(key) == 0) {
                if (markerGeo.get(key) != null) {
                    markerGeo.get(key).remove();
                }
                mMap = googleMap;

                LatLng latLng = new LatLng(lat, lon);

                markerGeo.put(key, mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(drawable))));
                markerGeo.get(key).setTitle(title);
                markerGeo.get(key).setTag(key);
                mMap.setPadding(2000, 4000, 2000, 4000);
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCountGeo.put(key, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getNearByInfo(String key, GeoLocation location, boolean isRemove) {
        Utils.mDatabase.child(Utils.tbl_user).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    User user = snapshot.getValue(User.class);
                    user.uid = snapshot.getKey();
                    for (GeoUser geoUser : arr_nearby) {
                        if (geoUser.user.uid.equals(user.uid)) {
                            arr_nearby.remove(geoUser);
                            break;
                        }
                    }
                    if (!isRemove) {
                        GeoUser n_geoUser = new GeoUser(user, location);
                        if (sel_user != null) {
                            if (n_geoUser.user.uid.equals(sel_user.user.uid)) {
                                sel_user = n_geoUser;
                            }
                        }
                        arr_nearby.add(n_geoUser);

                        mGeoLastLocation.put(key, location);
                        if (markerCountGeo.get(key) == null) {
                            markerCountGeo.put(key, 0);
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int car_drawable = R.drawable.ic_car_yellow;
                                addMarkerGeo(user.name, key, mMap, location.latitude, location.longitude, false, car_drawable);
                            }
                        });
                    } else {
                        markerGeo.get(key).remove();
                        markerCountGeo.put(key, 0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("color", "Error: " + error.getMessage());
            }
        });
    }
    private void getListNearbyCars() {
        GeoQuery geoQuery = Utils.geo_driver.queryAtLocation(new GeoLocation(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()), (double)Utils.geo_radius/1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getNearByInfo(key, location, false);
            }

            @Override
            public void onKeyExited(String key) {
                getNearByInfo(key, null, true);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                getNearByInfo(key, location, false);
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Utils.basePos, 16f));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                sel_user = null;
                sel_car = null;

                if (touch_enabled_start) {
                    new Utils.GetAddressTask(getContext(), edit_start, latLng).execute();
                    edit_start.setText("Searching address...");
                    pos_start = latLng;
                    addStartMarker();
                } else if (touch_enabled_target) {
                    new Utils.GetAddressTask(getContext(), edit_target, latLng).execute();
                    edit_target.setText("Searching address...");
                    pos_target = latLng;
                    addTargetMarker();
                }
            }
        });

        my_ride = (Ride) App.readObjectPreference("my_ride", Ride.class);
        if (my_ride != null) {
            App.removePreference("my_ride");
            pos_start = new LatLng(my_ride.from_lat, my_ride.from_lng);
            pos_target = new LatLng(my_ride.to_lat, my_ride.to_lng);
            addStartMarker(); addTargetMarker();
            edit_start.setText(my_ride.from_address);
            edit_target.setText(my_ride.to_address);
            rideNow();
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        if (marker.getTag() == null) return false;
        if (my_ride != null) return false;
        String key = marker.getTag().toString();
        if (key.equals(Utils.cur_user.uid)) return false;

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        for (GeoUser geoUser : arr_nearby) {
            User user = geoUser.user;
            if (key.equals(user.uid)) {
                sel_user = geoUser;
                Glide.with(activity).load(user.photo).apply(new RequestOptions().placeholder(R.drawable.ic_avatar_white).centerCrop()).into(img_photo);
                txt_name.setText(user.name);
                txt_rate.setText(String.valueOf(user.rate));
                ratingBar.setRating(user.rate);
                txt_seats.setText("");
                txt_carPrice.setText("");
                img_carType.setImageResource(R.drawable.ic_car2);
                img_car.setImageResource(R.drawable.ic_car2);
                getCarInfo(user.uid);
                break;
            }
        }
        return false;
    }

    void getCarInfo(String uid) {
        Utils.mDatabase.child(Utils.tbl_car).orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                sel_car = datas.getValue(Car.class);
                                sel_car._id = datas.getKey();
                                int index = Arrays.asList(Utils.carNames).indexOf(sel_car.type);
                                Glide.with(activity).load(Utils.carTypes[index]).apply(new RequestOptions().placeholder(R.drawable.ic_car2).fitCenter()).into(img_carType);
                                Glide.with(activity).load(sel_car.photo).apply(new RequestOptions().placeholder(R.drawable.ic_car2).centerInside()).into(img_car);
                                txt_seats.setText(String.valueOf(sel_car.seats));
                                txt_carPrice.setText("XOF "+String.valueOf(sel_car.price)+"/mile");

                                Glide.with(activity).load(Utils.carTypes[index]).apply(new RequestOptions().placeholder(R.drawable.ic_car2).fitCenter()).into(img_carType_driver);
                                txt_seats_driver.setText(String.valueOf(sel_car.seats));
                                txt_price_driver.setText("XOF "+String.valueOf(sel_car.price)+"/mile");

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
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        Dialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                Date date = calendar.getTime();
                String from_address = edit_start.getText().toString().trim();
                String to_address = edit_target.getText().toString().trim();
                Ride ride = new Ride("", Utils.cur_user.uid, driver_id, pos_start.latitude, pos_start.longitude, pos_target.latitude, pos_target.longitude, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude(), date, -1, 0, false, 0, from_address, to_address, "", 0.0f, "");
                String key = Utils.mDatabase.child(Utils.tbl_order).push().getKey();
                Utils.mDatabase.child(Utils.tbl_order).child(key).setValue(ride);
                closeTopView();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (pickupDialog != null) pickupDialog.dismiss();
                Toast.makeText(activity, "You have ordered successfully.", Toast.LENGTH_SHORT).show();

                Utils.setAlarm(activity, date, new Intent(getContext(), AlarmBroadcast.class));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        datePickerDialog.show();
    }

    public void openPaymentDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.initRadar(mMap, new LatLng(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()), getContext(), Color.GREEN);
            }
        });
        TextView txt_price = view.findViewById(R.id.txt_price);
        txt_price.setText(String.valueOf(price)+" XOF");
        TextView txt_payment = view.findViewById(R.id.txt_payment);
        Button btn_ediapay = view.findViewById(R.id.btn_ediapay);
        Button btn_cache = view.findViewById(R.id.btn_cache);
        RelativeLayout ly_merchantID = view.findViewById(R.id.ly_merchantID);
        EditText edit_merchantId = view.findViewById(R.id.edit_merchantID);
        CheckBox chk_store = view.findViewById(R.id.chk_store);
        String merchantID = App.prefs.getString(Utils.MERCHANTID, "");
        if (merchantID.length() > 0) {
            edit_merchantId.setText(merchantID);
            chk_store.setChecked(true);
        }
        chk_store.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String merchantID = edit_merchantId.getText().toString().trim();
                    if (merchantID.length() == 0) {
                        Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                        chk_store.setChecked(false);
                        return;
                    }
                    App.setPreference(Utils.MERCHANTID, merchantID);
                } else {
                    App.removePreference(Utils.MERCHANTID);
                }
            }
        });
        ImageButton btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        btn_ediapay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ediapay.setBackground(getResources().getDrawable(R.drawable.round_frame_red));
                btn_ediapay.setTextColor(Color.WHITE);
                btn_cache.setBackground(getResources().getDrawable(R.drawable.frame_border_red));
                btn_cache.setTextColor(Color.GRAY);
                txt_payment.setText("I want to pay via EDIAPAY");
                ly_merchantID.setVisibility(View.VISIBLE);
            }
        });
        btn_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cache.setBackground(getResources().getDrawable(R.drawable.round_frame_red));
                btn_cache.setTextColor(Color.WHITE);
                btn_ediapay.setBackground(getResources().getDrawable(R.drawable.frame_border_red));
                btn_ediapay.setTextColor(Color.GRAY);
                txt_payment.setText("I want to pay via CACHE");
                ly_merchantID.setVisibility(View.GONE);
            }
        });

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.70);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.setCancelable(true);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dlg.show();
    }

    void changeTopViewState() {
        if ((int)(btn_down.getTag()) == 0) {
            btn_down.animate().rotationX(180).start();
            openTopView();
            btn_down.setTag(1);
        } else {
            btn_down.animate().rotationX(0).start();
            closeTopView();
            btn_down.setTag(0);
        }
    }
    public void openSupportDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_support, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.15);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        ly_top.setVisibility(View.GONE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dlg.dismiss();
                ly_top.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }
    public void openTopView() {
        if (ly_topContent.getVisibility() == View.GONE) {
            ly_topContent.setVisibility(View.VISIBLE);
            App.RunAnimation(ly_top, AnimationUtils.loadAnimation(activity, R.anim.slidedown));
        }
    }
    public void closeTopView() {
        if (ly_topContent.getVisibility() == View.VISIBLE) {
            App.RunAnimation(ly_top, AnimationUtils.loadAnimation(activity, R.anim.slideup1));
            ly_topContent.setVisibility(View.GONE);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityCustomer) context;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (ripple_ride != null) {
            ripple_ride.startRippleMapAnimation();
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        if (ripple_ride != null) {
            if (ripple_ride.isAnimationRunning()) {
                ripple_ride.stopRippleMapAnimation();
            }
        }
    }
    void startVoiceActivity(int reqCode) {
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Google Speech recognition \nDivina Transport");
            startActivityForResult(intent, reqCode);
        } else {
            Snackbar.make(getView(), "Google Speech Service not available on your device", 3000).show();
        }
    }

}