package com.ujs.divinatransport.hrmovecarmarkeranimation.location;

import android.animation.ValueAnimator;
import android.location.Location;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.ujs.divinatransport.hrmovecarmarkeranimation.Utilities;


public class HRMarkerAnimation {

    private HRUpdateLocationCallBack updateLocation;
    private ValueAnimator valueAnimator;
    private GoogleMap googleMap;
    private long animationDuration;

    public HRMarkerAnimation(GoogleMap googleMap,long duration , HRUpdateLocationCallBack updateLocation) {
        this.updateLocation = updateLocation;
        this.googleMap =googleMap;
        this.animationDuration=duration;
    }

    public void animateMarker(final Location destination, final Location oldLocation, final Marker marker, boolean isCamera, boolean is_rotate) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            if (valueAnimator != null)
                valueAnimator.end();

            final Utilities.LatLngInterpolator latLngInterpolator = new Utilities.LatLngInterpolator.LinearFixed();
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(animationDuration); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        //marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                        float angle = Utilities.computeRotation(v, marker.getRotation(),
                                (float)Utilities.bearingBetweenLocations(startPosition, newPosition));
                        if (is_rotate) {
                            marker.setRotation(angle);
                        }
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setFlat(true);


                        // add new location into old location
                        updateLocation.onHRUpdatedLocation(destination);


                        //when marker goes out from screen it automatically move into center
                        if (isCamera) {
                            if (googleMap!=null){
                                if (!Utilities.isMarkerVisible(googleMap,newPosition)){
                                    googleMap.animateCamera(CameraUpdateFactory
                                            .newCameraPosition(new CameraPosition.Builder()
                                                    .target(newPosition)
                                                    .zoom(googleMap.getCameraPosition().zoom)
                                                    .build()));
                                }else {
                                    try {
                                        googleMap.animateCamera(CameraUpdateFactory
                                                .newCameraPosition(new CameraPosition.Builder()
                                                        .target(newPosition)
                                                        .tilt(0)
                                                        .zoom(googleMap.getCameraPosition().zoom)
                                                        .build()));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }

                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        // handle exception here
                    }
                }

            });
            valueAnimator.start();
        }
    }

}
