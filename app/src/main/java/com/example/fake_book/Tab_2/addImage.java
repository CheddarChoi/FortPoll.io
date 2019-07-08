package com.example.fake_book.Tab_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.fake_book.MyService;
import com.example.fake_book.PhotoTools;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class addImage extends AppCompatActivity {

    private Uri loadedPhoto_uri;
    private String mCurrentPhotoPath;

    Button addImage;
    ImageView loadedPhoto;

    public MaterialEditText description;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA= 2;

    private boolean Picked_from_camera = false;

    public boolean isPhotoLoaded = false;

    MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addimage);

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        actionBar.setTitle("Add Image_list");

        loadedPhoto = findViewById(R.id.photoLoaded);
        addImage = findViewById(R.id.btn_addImage);
        description = findViewById(R.id.description);

        loadedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPhotoLoaded = false;
                set_Dialog();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (isPhotoLoaded) {
                    uploadImage(loadedPhoto_uri);
                }
                else{
                    Toast.makeText(addImage.this, "No Photo Added!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void set_Dialog(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.baseline_add_white_18dp);
        builderSingle.setTitle("CHOOSE");

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }
        );

        builderSingle.setPositiveButton(
                "앨범",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectAlbum();
                    }
                }
        );

        builderSingle.setNeutralButton(
                "카메라",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takePhoto();
                    }
                }
        );
        builderSingle.show();
    }

    // 앨범에서 사진 가져오기
    private void selectAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        isPhotoLoaded = true;
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void takePhoto(){
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(camera_intent.resolveActivity(addImage.this.getPackageManager()) != null){
                File new_photo = createImageFile();
                if(new_photo != null){
                    isPhotoLoaded = true;
                    loadedPhoto_uri = FileProvider.getUriForFile(addImage.this,addImage.this.getPackageName(),new_photo);
                    camera_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, loadedPhoto_uri);
                    startActivityForResult(camera_intent, PICK_FROM_CAMERA);
                }
            }
        }
        else
            Toast.makeText(addImage.this,"저장공간에 접근할 수 없습니다.", Toast.LENGTH_LONG).show();
    }

    private File createImageFile() {
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");
        if(!storageDir.exists())
            storageDir.mkdirs();
        File imageFile = new File(storageDir, imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case PICK_FROM_ALBUM : {
                if(data.getData()!=null)
                    loadedPhoto_uri = data.getData();
                break;
            }

            case PICK_FROM_CAMERA: {
                try{
                    galleryAddPic();
                    Picked_from_camera = true;
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }

        if (!isPhotoLoaded) {
            return;
        } else {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), loadedPhoto_uri);
                bitmap = PhotoTools.modifyOrientation(bitmap, getRealPathFromURI(loadedPhoto_uri, getApplicationContext()));

                System.out.println(getRealPathFromURI(loadedPhoto_uri, getApplicationContext()));
                File file = new File(getRealPathFromURI(loadedPhoto_uri, getApplicationContext()));
                OutputStream out = null;
                try {
                    file.createNewFile();
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                loadedPhoto.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        (addImage.this).sendBroadcast(mediaScanIntent);
    }


    private void uploadImage(Uri uri) {
        Retrofit retrofitClient = RetrofitClient.ImagesRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        System.out.println(getRealPathFromURI(uri, getApplicationContext()));
        File file = new File(getRealPathFromURI(uri, getApplicationContext()));
        RequestBody req = RequestBody.create(MediaType.parse("image/*"), file);
        String Description = description.getHelperText();

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), req);

        Call<ResponseBody> call = myService.addNewImage(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Toast.makeText(addImage.this,"Uploaded Successfully!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(addImage.this, t.getMessage(),Toast.LENGTH_SHORT).show();
            }


        });
    }
    private String getRealPathFromURI(final Uri uri, final Context context) {
        if(Picked_from_camera == true) {
            return mCurrentPhotoPath;
        }
        return PhotoTools.getRealPathFromURI(uri, context);
    }
}