package com.example.fake_book;

import com.example.fake_book.Tab_1.Contact;
import com.example.fake_book.Tab_2.Image_list;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MyService {
    @POST("addContact")
    @FormUrlEncoded
    Observable<String> addNewContact (@Field("name") String name,
                                      @Field("phoneNumber") String phoneNumber,
                                      @Field("email") String email);

    @GET("getContact")
    Call<List<Contact>> getContacts();

    @Multipart
    @POST("addContactImage")
    Call<ResponseBody> addContactImage (@Part MultipartBody.Part image);

    @POST("editphotos")
    @FormUrlEncoded
    Observable<String> editContact (@Field("phoneNumber") String phoneNumber, @Field("photoPaths") String photoPaths);

    @Multipart
    @POST("upload")
    Call<ResponseBody> addNewImage (@Part MultipartBody.Part image);

    @GET("getImages")
    Call<List<Image_list>> getImages();
}
