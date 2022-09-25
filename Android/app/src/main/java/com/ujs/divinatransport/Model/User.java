package com.ujs.divinatransport.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class User implements Serializable {
    public String uid;
    public String email;
    public String name;
    public String phone;
    public String photo;
    public float rate;
    public int point;
    public String type;
    public int state;
    public String token;
    public int status;
    public int rides;
    public int rejects;

    public User(String uid, String email, String name, String phone, String photo, float rate, int point, String type, int state, String token, int status, int rides, int rejects) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
        this.phone = phone;
        this.email = email;
        this.rate = rate;
        this.point = point;
        this.type = type;
        this.state = state;
        this.token = token;
        this.status = status;
        this.rides = rides;
        this.rejects = rejects;
    }
    public User() {
        this.uid = "";
        this.name = "";
        this.photo = "";
        this.phone = "";
        this.email = "";
        this.type = "";
        this.token = "";
        this.rate = 0;
        this.point = 0;
        this.state = 0;
        this.status = 0;
        this.rides = 0;
        this.rejects = 0;
    }
}
