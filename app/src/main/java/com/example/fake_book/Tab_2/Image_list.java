package com.example.fake_book.Tab_2;

import com.google.gson.annotations.SerializedName;

public class Image_list {
    @SerializedName("filename")
    private String filename;

    public Image_list(String filename){
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
