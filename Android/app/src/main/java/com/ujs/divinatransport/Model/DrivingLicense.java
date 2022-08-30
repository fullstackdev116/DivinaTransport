package com.ujs.divinatransport.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import android.net.Uri;

import java.io.Serializable;

public class DrivingLicense implements Serializable {
    public String _id;
    public String uid;
    public String photo;
    public String first_name = "";
    public String last_name = "";
    public String gender = "";
    public String birth_date = "";
    public String issue_date = "";
    public String expiry_date = "";
    public String number = "";

    public DrivingLicense(String _id, String uid, String photo, String first_name, String last_name, String gender, String birth_date, String issue_date, String expiry_date, String number) {
        this._id = _id;
        this.uid = uid;
        this.photo = photo;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.birth_date = birth_date;
        this.issue_date = issue_date;
        this.expiry_date = expiry_date;
        this.number = number;
    }
    public DrivingLicense() {
        this._id = "";
        this.uid = "";
        this.photo = "";
        this.first_name = "";
        this.last_name = "";
        this.gender = "";
        this.birth_date = "";
        this.issue_date = "";
        this.expiry_date = "";
        this.number = "";
    }
}
