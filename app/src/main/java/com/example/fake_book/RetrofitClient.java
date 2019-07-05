package com.example.fake_book;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit addcontact_RetrofitInstance(){
        if( instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("http://143.248.39.96:3000/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return instance;
    }

    public static Retrofit loadContact_RetrofitInstance(){
        if( instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl("http://143.248.39.96:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
