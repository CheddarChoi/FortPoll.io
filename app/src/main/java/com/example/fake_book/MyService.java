package com.example.fake_book;

import com.example.fake_book.Tab_1.Contact;
import com.example.fake_book.Tab_1.Item;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MyService {
    @POST("addContact")
    @FormUrlEncoded
    Observable<String> addNewContact (@Field("name") String name,
                                      @Field("phoneNumber") String phoneNumber,
                                      @Field("email") String email);

    @GET("getContact")
    Call<List<Contact>> getContacts();
}
