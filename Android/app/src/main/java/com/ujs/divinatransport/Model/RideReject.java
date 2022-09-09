package com.ujs.divinatransport.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//

import java.io.Serializable;
import java.util.Date;

public class RideReject implements Serializable {
    public String _id;
    public String ride_id;
    public String driver_id;
    public Date date;

    public RideReject(String _id, String ride_id, String driver_id, Date date) {
        this._id = _id;
        this.ride_id = ride_id;
        this.driver_id = driver_id;
        this.date = date;
    }
    public RideReject() {
        this._id = "";
        this.ride_id = "";
        this.driver_id = "";
        this.date = new Date();
    }
}
