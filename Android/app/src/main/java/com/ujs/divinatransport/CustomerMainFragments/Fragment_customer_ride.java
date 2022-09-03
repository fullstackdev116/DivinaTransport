package com.ujs.divinatransport.CustomerMainFragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ujs.divinatransport.MainActivityCustomer;
import com.ujs.divinatransport.Model.Car;
import com.ujs.divinatransport.Model.GeoUser;
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
import com.ujs.divinatransport.Utils.Utils;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.geolocation.GeoHRUpdateLocationCallBack;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRMarkerAnimation;
import com.ujs.divinatransport.hrmovecarmarkeranimation.location.HRUpdateLocationCallBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.madapps.placesautocomplete.PlaceAPI;
import in.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter;
import in.madapps.placesautocomplete.model.Place;

public class Fragment_customer_ride extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public int AUTOCOMPLETE_REQUEST_CODE = 1;
    MainActivityCustomer activity;
    BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    ProgressBar mProgressBar;
    Polyline polyline;
    long distance = 0, duration = 0, price = 0;
    ArrayList<GeoUser> arr_nearby = new ArrayList<>();
    GeoUser sel_user;
    boolean location_track = false;

    LinearLayout ly_top;
    RelativeLayout ly_topContent;
    boolean toggle = false;
    TextView txt_state;
    ImageButton btn_down;
    AutoCompleteTextView edit_start, edit_target;

    CircleImageView img_photo;
    ImageView img_carType, img_car;
    TextView txt_name, txt_rate, txt_seats, txt_carPrice, txt_state_driver;
    RatingBar ratingBar;

    private static final int VOICE_RECOGNITION_REQUEST_CODE_START = 201;
    private static final int VOICE_RECOGNITION_REQUEST_CODE_TARGET = 202;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_fragment_ride, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mProgressBar = v.findViewById(R.id.progress_bar);
        txt_state = v.findViewById(R.id.txt_state);

        img_photo = v.findViewById(R.id.img_photo);
        img_carType = v.findViewById(R.id.img_carType);
        img_car = v.findViewById(R.id.img_car);
        txt_name = v.findViewById(R.id.txt_name);
        txt_rate = v.findViewById(R.id.txt_rate);
        txt_seats = v.findViewById(R.id.txt_seats);
        txt_carPrice = v.findViewById(R.id.txt_carPrice);
        ratingBar = v.findViewById(R.id.rate);
        txt_state_driver = v.findViewById(R.id.txt_state_driver);

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
        v.findViewById(R.id.img_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_start.setText(Utils.getCompleteAddressString(activity, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()));
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
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTopViewState();
            }
        });
        txt_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTopViewState();
            }
        });

        PlaceAPI placeAPI = new PlaceAPI.Builder().build(activity);
        placeAPI.setApiKey(getResources().getString(R.string.google_api_key));
        edit_start = v.findViewById(R.id.edit_start);
        edit_start.setAdapter(new PlacesAutoCompleteAdapter(activity, placeAPI));
        edit_start.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Place place;
                    place = (Place)parent.getItemAtPosition(position);
                    edit_start.setText(place.getDescription());
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

        v.findViewById(R.id.btn_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPickupDialog();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        openSupportDialog();
        activity.locationUpdateCallback = new MainActivityCustomer.LocationUpdateCallback() {
            @Override
            public void locationUpdateCallback() {
                mLastLocation = Utils.cur_location;
                addMarker(mMap, Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude(), location_track, R.drawable.ic_pin0);
                Utils.geo_customer.setLocation(Utils.cur_user.uid, new GeoLocation(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Snackbar.make(getView(), error.getMessage(), 3000).show();
                        } else {
                            getListNearbyCars();
                        }
                    }
                });
            }
        };


        return v;
    }
    public void openPickupDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.fragment_bottomsheet, null);
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
            }
        });
        ly_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_all.setBackgroundResource(R.drawable.frame_border_red);
                ly_one.setBackgroundColor(Color.TRANSPARENT);
                ly_all.setPadding(10,10,10,10);
                ly_one.setPadding(10,10,10,10);
            }
        });

        int width = (int)(getResources().getDisplayMetrics().widthPixels*1);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.3);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.setCancelable(true);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(width, height);
        dlg.show();
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
                        arr_nearby.add(n_geoUser);

                        mGeoLastLocation.put(key, location);
                        if (markerCountGeo.get(key) == null) {
                            markerCountGeo.put(key, 0);
                        }
                        addMarkerGeo(user.name, key, mMap, location.latitude, location.longitude, false, R.drawable.ic_car_dark);
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

    private String select_key = "";
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

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
                    }).animateMarkerGeo(mGeoLastLocation.get(key), oldGeoLocation.get(key), markerGeo.get(key), isCamera);
                } else {
                    oldGeoLocation.put(key, mGeoLastLocation.get(key));
                }
            } else if (markerCountGeo.get(key) == 0) {
                if (markerGeo.get(key) != null) {
                    markerGeo.get(key).remove();
                }
                mMap = googleMap;

                LatLng latLng = new LatLng(lat, lon);

                if (key.equals(select_key)) {
                    drawable = R.drawable.ic_car_orange;
                }
                markerGeo.put(key, mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(drawable))));
                markerGeo.get(key).setTitle(title);
                markerGeo.get(key).setTag(key);
                mMap.setPadding(2000, 4000, 2000, 4000);
                if (isCamera)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                /*################### Set Marker Count to 1 after first marker is created ###################*/

                markerCountGeo.put(key, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getListNearbyCars() {
        GeoQuery geoQuery = Utils.geo_car.queryAtLocation(new GeoLocation(Utils.cur_location.getLatitude(), Utils.cur_location.getLongitude()), Utils.geo_radius);
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
    private void rideCar() {
        changeTopViewState();
        txt_state.setText("Which taxi to ride?");

    }
    private void openDatePicker() {
//        txt_state.setText("Please select your order day");
        Calendar calendar = Calendar.getInstance();
        Dialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                openPaymentDialog();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                txt_state.setText("Where to go?");
            }
        });
        datePickerDialog.show();
    }
    public void openPaymentDialog() {
//        txt_state.setText("Please confirm your payment");
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        TextView txt_payment = view.findViewById(R.id.txt_payment);
        ImageButton btn_ediapay = view.findViewById(R.id.btn_ediapay);
        ImageButton btn_cache = view.findViewById(R.id.btn_cache);
        ImageButton btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
//                txt_state.setText("Where to go?");
            }
        });
        btn_ediapay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable buttonDrawable = btn_ediapay.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.red_dark));
                btn_ediapay.setBackground(buttonDrawable);

                buttonDrawable = btn_cache.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray));
                btn_cache.setBackground(buttonDrawable);

                txt_payment.setText("I want to pay via EDIAPAY");
            }
        });
        btn_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable buttonDrawable = btn_cache.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.red_dark));
                btn_cache.setBackground(buttonDrawable);

                buttonDrawable = btn_ediapay.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(me.jagar.chatvoiceplayerlibrary.R.color.gray));
                btn_ediapay.setBackground(buttonDrawable);

                txt_payment.setText("I want to pay via CACHE");
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
    private void displayChoiceDialog() {
        String choiceString[] = new String[] {"I want ride a car" ,"I want to order a car"};
        AlertDialog.Builder dialog= new AlertDialog.Builder(getActivity());
        dialog.setItems(choiceString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0) {
                    rideCar();
                } else {
                    openDatePicker();
                }

            }
        }).show();
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
        ly_topContent.setVisibility(View.VISIBLE);
        App.RunAnimation(ly_top, AnimationUtils.loadAnimation(activity, R.anim.slidedown));
    }
    public void closeTopView() {
        App.RunAnimation(ly_top, AnimationUtils.loadAnimation(activity, R.anim.slideup1));
        ly_topContent.setVisibility(View.GONE);
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

        LatLng latLng = new LatLng(37.422f, -122.12f);   // basic area for people
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        if (marker.getTag().toString().equals(Utils.cur_user.uid)) {
            marker.showInfoWindow();
            return true;
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Utils.showTextViewMessage(txt_state_driver, "Thank you for choosing me!");

        if (!select_key.equals("")) {
            markerCountGeo.put(select_key, 0);
            select_key = "";
            sel_user = null;
        }

        select_key = marker.getTag().toString();
        String title = marker.getTitle();
        double lat = marker.getPosition().latitude;
        double lng = marker.getPosition().longitude;

        marker.remove();

        markerCountGeo.put(select_key, 0);
        addMarkerGeo(title, select_key, mMap, lat, lng, false, R.drawable.ic_car_orange);

        for (GeoUser geoUser : arr_nearby) {
            User user = geoUser.user;
            if (select_key.equals(user.uid)) {
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
        return true;
    }

    void getCarInfo(String uid) {
        activity.showProgress();
        Utils.mDatabase.child(Utils.tbl_car).orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        activity.dismissProgress();
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Car car = datas.getValue(Car.class);
                                car._id = datas.getKey();
                                int index = Arrays.asList(Utils.carNames).indexOf(car.type);
                                Glide.with(activity).load(Utils.carTypes[index]).apply(new RequestOptions().placeholder(R.drawable.ic_car2).centerInside()).into(img_carType);
                                Glide.with(activity).load(car.photo).apply(new RequestOptions().placeholder(R.drawable.ic_car2).centerInside()).into(img_car);
                                txt_seats.setText(String.valueOf(car.seats));
                                txt_carPrice.setText("XOH "+String.valueOf(car.price)+"/mile");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivityCustomer) context;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Google place result
        if (requestCode > 200) {
            ArrayList<String> matches = new ArrayList<>();
            if (resultCode == RESULT_OK) {
                // Fill the list view with the strings the recognizer thought it
                // could have heard
                matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                Snackbar.make(getView(), "No match result", 3000).show();
            } else if (resultCode == RESULT_CANCELED) {
//                matches = new ArrayList<>();
//                matches.add("hello");
//                matches.add("helloer");
            }
            if (matches.size() > 0) {
                if (requestCode == VOICE_RECOGNITION_REQUEST_CODE_START)
                    edit_start.setText(matches.get(0));
                else
                    edit_target.setText(matches.get(0));
            }
        }
    }
}