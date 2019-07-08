package com.example.fake_book.Tab_2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fake_book.MainActivity;
import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Tab_2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    //variables for fab animation
    private FloatingActionButton fab;
    ArrayList<Uri> imagelist;
    ArrayList<String> fileArray;
    private CardAdapter adapter;

    private final int PICK_FROM_ALBUM = 1;
    private final int PICK_FROM_CAMERA= 2;

    private Uri imgUri, photoURI;
    MyService myService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_2_main, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        imagelist = MainActivity.imagelist;

        load_Images();

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = view.findViewById(R.id.folder_album_recycler_view) ;
        GridLayoutManager mGridLayoutManager;
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mGridLayoutManager);
        adapter = new CardAdapter(imagelist, getActivity()) ;
        recyclerView.setAdapter(adapter);

        fab = view.findViewById(R.id.fab_menu);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),addImage.class);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(getActivity(), "result code: "+ resultCode, Toast.LENGTH_SHORT).show();
            return;
        }
        photoURI = imgUri;

        switch (requestCode){
            case PICK_FROM_ALBUM : {
                if(data.getData()!=null){
                    try{
                        photoURI = data.getData();
                        if (!imagelist.add(photoURI))
                            Toast.makeText(getActivity(), "list add failed", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            }

            case PICK_FROM_CAMERA: {
                try{
                    if (!imagelist.add(imgUri))
                        Toast.makeText(getActivity(), "list add failed", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public interface GetImagesCallback {
        void onGetImagesData(List<Image_list> images);
        void onError();
    }

    @Override
    public void onRefresh() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                load_Images();
                swipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

    private void load_Images() {
        imagelist.clear();
        load_Images(new GetImagesCallback(){
            @Override
            public void onGetImagesData(List<Image_list> images) {
                imagelist.clear();

                fileArray = new ArrayList<>();
                for (Image_list i : images)
                    fileArray.add(i.getFilename());

                for(int j=0 ; j < fileArray.size() ; j++){
                    String filename = fileArray.get(j);
                    File image_file = new File(getStoragePath()+"/"+filename);
                    if (image_file.exists()) {
                        imagelist.add(Uri.parse(image_file.getPath()));
                        adapter.notifyDataSetChanged();
                    }
                    else
                        new New_Image().execute("http://143.248.39.96:3000/getImage/" + filename, filename);
                }
            }
            @Override
            public void onError() {
                Toast.makeText(getContext(),"이미지 로딩에 실패하였습니다.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void load_Images(final GetImagesCallback getImagesCallback) {

        Retrofit retrofitClient = RetrofitClient.ImagesRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        Call<List<Image_list>> call = myService.getImages();
        call.enqueue(new Callback<List<Image_list>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NotNull Call<List<Image_list>> call, @NotNull Response<List<Image_list>> response) {
                if(!response.isSuccessful()){
                    getImagesCallback.onError();
                    return;
                }
                getImagesCallback.onGetImagesData(response.body());
            }

            @Override
            public void onFailure(Call<List<Image_list>> call, Throwable t) {
                getImagesCallback.onError();
            }
        });
    }

    private class New_Image extends AsyncTask<String, String, Bitmap> {

        ProgressDialog pDialog;
        Bitmap mBitmap;
        String filename;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("이미지 로딩중입니다...");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            filename = args[1];
            try {
                mBitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mBitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                try {
                    imagelist.add(save_NewImage(image, filename));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
            }
        }
    }

    public Uri save_NewImage(Bitmap inImage, String filename) throws IOException {
        File new_image = new File(getStoragePath(), filename);
        new_image.createNewFile();
        FileOutputStream out = new FileOutputStream(new_image);
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return Uri.parse(new_image.getPath());
    }

    public String getStoragePath() {
        return getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
    }
}