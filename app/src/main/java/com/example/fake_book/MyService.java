package com.example.fake_book;

import com.example.fake_book.Tab_1.Contact;
import com.example.fake_book.Tab_2.Image;
import com.example.fake_book.Tab_2.Images;

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
import retrofit2.http.Path;
public interface MyService {
    @POST("addContact")
    @FormUrlEncoded
    Observable<String> addNewContact (@Field("name") String name,
                                      @Field("phoneNumber") String phoneNumber,
                                      @Field("email") String email);

    @GET("getContact")
    Call<List<Contact>> getContacts();

    @Multipart
    @POST("upload")
    Call<ResponseBody> addNewImage (@Part MultipartBody.Part image);

    @GET("getImages")
    Call<List<Images>> getImages();

    @GET("getImage/:{filename}")
    Call<Image> getImage(@Path("filename") String imageName);
}
