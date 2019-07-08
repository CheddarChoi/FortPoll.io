package com.example.fake_book.Tab_1;

import android.graphics.Bitmap;

public class Item {
    private String name, number, email;
    private Bitmap profile_pic;

    public Item(String name, String number, String email, Bitmap profile_pic) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.profile_pic = profile_pic;
    }

    public Bitmap getProfile_pic() {
        return profile_pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}