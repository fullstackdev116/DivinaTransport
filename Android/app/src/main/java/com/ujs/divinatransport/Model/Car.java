package com.ujs.divinatransport.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//

import java.io.Serializable;

public class Car implements Serializable {
    public String _id;
    public String uid;
    public String photo;
    public String type = "";
    public int seats = 0;
    public float price = 0.0f;

    public Car(String _id, String uid, String photo, String type, int seats, float price) {
        this._id = _id;
        this.uid = uid;
        this.photo = photo;
        this.type = type;
        this.seats = seats;
        this.price = price;
    }
    public Car() {
        this._id = "";
        this.uid = "";
        this.photo = "";
        this.type = "";
        this.seats = 0;
        this.price = 0.0f;
    }
}
