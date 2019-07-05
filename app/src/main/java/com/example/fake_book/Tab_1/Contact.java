package com.example.fake_book.Tab_1;

import com.google.gson.annotations.SerializedName;

public class Contact {
    @SerializedName("name")
    private String name;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("email")
    private String email;

    public Contact(String contactname, String contactphoneNumber, String contactemail) {
        this.name = contactname;
        this.phoneNumber = contactphoneNumber;
        this.email = contactemail;
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
