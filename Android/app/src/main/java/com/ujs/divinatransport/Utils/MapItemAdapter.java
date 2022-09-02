package com.ujs.divinatransport.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapItemAdapter implements GoogleMap.InfoWindowAdapter {

    CircleImageView imageView;
  TextView tv;

  public MapItemAdapter(Context context) {
      tv = new TextView(context);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      tv.setLayoutParams(lp);
      tv.setGravity(Gravity.CENTER);
      imageView = new CircleImageView(context);
      lp = new LinearLayout.LayoutParams(100, 100);
      imageView.setLayoutParams(lp);
  }


  @Override
  public View getInfoWindow(Marker marker) {
      tv.setText(marker.getTitle());
      imageView.setCircleBackgroundColor(Color.RED);
      imageView.setBackgroundColor(Color.BLUE);
      return tv;
  }

  @Override
  public View getInfoContents(Marker marker) {
      return null;
  }
}