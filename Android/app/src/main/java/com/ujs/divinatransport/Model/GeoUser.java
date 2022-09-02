package com.ujs.divinatransport.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import android.location.Location;

import com.firebase.geofire.GeoLocation;

import java.io.Serializable;

public class GeoUser implements Serializable {
    public User user;
    public GeoLocation location;

    public GeoUser(User user, GeoLocation location) {
        this.user = user;
        this.location = location;
    }
    public GeoUser() {
        this.user = new User();
        this.location = new GeoLocation(0, 0);
    }
}
