package com.example.fake_book.Tab_1;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class Item {
    private String name, number, email;
    private Bitmap profile_pic;
    private ArrayList<Uri> photos;

    public Item(String name, String number, String email, Bitmap profile_pic, ArrayList<Uri> photos) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.profile_pic = profile_pic;
        this.photos = photos;
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

    public ArrayList<Uri> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Uri> photos) {
        this.photos = photos;
    }

    public void addPhoto(Uri photo){
        this.photos.add(photo);
    }

    public Bitmap getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Bitmap profile_pic) {
        this.profile_pic = profile_pic;
    }
}