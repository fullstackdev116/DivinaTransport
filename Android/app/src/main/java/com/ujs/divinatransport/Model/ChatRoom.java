package com.ujs.divinatransport.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoom implements Serializable {
    public String _id;
    public ArrayList<Message> messages;

    public ChatRoom(String _id, ArrayList<Message> messages) {
        this._id = _id;
        this.messages = messages;
    }
    public ChatRoom() {
        this._id = "";
        this.messages = new ArrayList<>();
    }
}
