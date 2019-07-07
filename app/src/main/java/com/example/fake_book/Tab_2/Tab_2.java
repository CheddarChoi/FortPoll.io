package com.example.fake_book.Tab_2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.MainActivity;
import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Url;

import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;

public class Tab_2 extends Fragment {

    //variables for fab animation
    private FloatingActionButton fab, fab_upload, fab_download;
    private Animation fab_open, fab_close, fab_rotate, fab_rotate_backward;
    private Boolean isFabOpen = false;
    public ArrayList<Uri> imagelist;
    private CardAdapter adapter;
    MyService myService;

    ArrayList<String> fileArray;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_2_main, container, false);

        imagelist = new ArrayList<>();
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        final RecyclerView recyclerView = view.findViewById(R.id.image_recycler_view) ;
        GridLayoutManager mGridLayoutManager;
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mGridLayoutManager);
        adapter = new CardAdapter(imagelist) ;

        //variables for fab animations
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        fab_rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate);
        fab_rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_backward);

        fab = view.findViewById(R.id.fab_menu);
        fab_upload = view.findViewById(R.id.fab_upload_from_database);
        fab_download = view.findViewById(R.id.fab_downloadload_from_database);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });

        fab_download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                download_Images(new GetImagesCallback(){
                    @Override
                    public void onGetImagesData(List<Images> images) {
                        imagelist.clear();

                        fileArray = new ArrayList<>();
                        System.out.println(images);
                        for (Images i : images){
                            fileArray.add(i.getFilename());
                        }
                        System.out.println(fileArray);

                        for(int j=0;j<fileArray.size();j++){
                            new LoadImage().execute("http://143.248.39.96:3000/getImage/"+fileArray.get(j));
                            System.out.println("URL is " + "http://143.248.39.96:3000/getImage/"+fileArray.get(j));
                        }


                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onError() {
                        Log.d(getTag(), "불쌍하니까..");
                    }
                });


                (adapter).notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });

        fab_upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),addImage.class);
                startActivityForResult(intent, 1);

                (adapter).notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });

        recyclerView.setAdapter(adapter);
        return view;
    }

    public interface GetImagesCallback {
        void onGetImagesData(List<Images> images);
        void onError();
    }

    private void download_Images(final GetImagesCallback getImagesCallback) {

        Retrofit retrofitClient = RetrofitClient.ImagesRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        Call<List<Images>> call = myService.getImages();
        call.enqueue(new Callback<List<Images>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NotNull Call<List<Images>> call, @NotNull Response<List<Images>> response) {
                if(!response.isSuccessful()){

                    getImagesCallback.onError();
                    return;
                }
                getImagesCallback.onGetImagesData(response.body());

                Toast.makeText(getContext(), "GOT IMAGES SUCCESSFULLY!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Images>> call, Throwable t) {
                getImagesCallback.onError();
                //Something went wrong with the communication with the server or processing the Response or JSON doesn't fit to whatever we are trying to parser into.
                Toast.makeText(getContext(), "UNSUCCESSFUL!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void anim() {
        if (isFabOpen) {
            fab_upload.startAnimation(fab_close);
            fab_upload.setClickable(false);

            fab_download.startAnimation(fab_close);
            fab_download.setClickable(false);

            fab.startAnimation(fab_rotate_backward);
            isFabOpen = false;
        } else {
            fab_upload.startAnimation(fab_open);
            fab_upload.setClickable(true);

            fab_download.startAnimation(fab_open);
            fab_download.setClickable(true);

            fab.startAnimation(fab_rotate);
            isFabOpen = true;
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        ProgressDialog pDialog;
        Bitmap mBitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("이미지 로딩중입니다...");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                mBitmap = BitmapFactory
                        .decodeStream((InputStream) new URL(args[0])
                                .getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mBitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                System.out.println("URI is " + getImageUri(getContext(),image).toString());
                imagelist.add(getImageUri(getContext(),image));
                adapter.notifyDataSetChanged();
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity(), "이미지가 존재하지 않습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

