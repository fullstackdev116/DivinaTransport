package com.ujs.divinatransport.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//

import java.io.Serializable;
import java.util.Date;

public class Ride implements Serializable {
    public String _id;
    public String passenger_id;
    public String driver_id;
    public double from_lat;
    public double from_lng;
    public double to_lat;
    public double to_lng;
    public double cur_lat;
    public double cur_lng;
    public Date date;
    public int state;
    public float price;
    public boolean isSOS;
    public int paid;
    public String from_address;
    public String to_address;
    public String transaction_id;
    public float rate;
    public String review;

    public Ride(String _id, String passenger_id, String driver_id, double from_lat, double from_lng, double to_lat, double to_lng, double cur_lat, double cur_lng, Date date, int state, float price, boolean isSOS, int paid, String from_address, String to_address, String transaction_id, float rate, String review) {
        this._id = _id;
        this.passenger_id = passenger_id;
        this.driver_id = driver_id;
        this.from_lat = from_lat;
        this.from_lng = from_lng;
        this.to_lat = to_lat;
        this.to_lng = to_lng;
        this.cur_lat = cur_lat;
        this.cur_lng = cur_lng;
        this.date = date;
        this.state = state;
        this.price = price;
        this.isSOS = isSOS;
        this.paid = paid;
        this.from_address = from_address;
        this.to_address = to_address;
        this.transaction_id = transaction_id;
        this.rate = rate;
        this.review = review;
    }
    public Ride() {
        this._id = "";
        this.passenger_id = "";
        this.driver_id = "";
        this.from_lat = 0;
        this.from_lng = 0;
        this.to_lat = 0;
        this.to_lng = 0;
        this.cur_lat = 0;
        this.cur_lng = 0;
        this.date = null;
        this.state = 0;
        this.price = 0.0f;
        this.isSOS = false;
        this.paid = 0;
        this.from_address = "";
        this.to_address = "";
        this.transaction_id = "";
        this.rate = 0.0f;
        this.review = "";
    }
}
