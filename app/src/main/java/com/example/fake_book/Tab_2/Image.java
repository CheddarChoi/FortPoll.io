package com.example.fake_book.Tab_2;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

public class Image {
    private android.media.Image image;
    public android.media.Image getImage() {
        return image;
    }
}
