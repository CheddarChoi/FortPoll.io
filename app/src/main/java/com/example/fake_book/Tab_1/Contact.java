package com.example.fake_book.Tab_1;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Contact {
    @SerializedName("name")
    private String name;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("photos")
    private List<String> photoPaths;

    public Contact(String contactname, String contactphoneNumber, String contactemail, List<String> photoPaths) {
        this.name = contactname;
        this.phoneNumber = contactphoneNumber;
        this.email = contactemail;
        this.photoPaths = photoPaths;
    }

    public void setPhotoPaths(List<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    public List<String> getPhotoPaths() {
        return photoPaths;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String contactname) {
        this.name = contactname;
    }

    public void setPhoneNumber(String contactphoneNumber) {
        this.phoneNumber = contactphoneNumber;
    }

    public void setEmail(String contactemail) {
        this.email = contactemail;
    }
}