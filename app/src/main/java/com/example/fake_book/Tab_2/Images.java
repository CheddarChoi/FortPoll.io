package com.example.fake_book.Tab_2;

import com.google.gson.annotations.SerializedName;

public class Images {
    @SerializedName("filename")
    private String filename;

    public Images(String filename){
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
