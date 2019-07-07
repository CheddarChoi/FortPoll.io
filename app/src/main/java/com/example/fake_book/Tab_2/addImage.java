package com.example.fake_book.Tab_2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.DSAKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

public class addImage extends AppCompatActivity {

    private Uri cameraURI, albumURI;
    private String mCurrentPhotoPath;

    Button uploadImage, addImage;
    ImageView loadedPhoto;

    public MaterialEditText description;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA= 2;

    private static boolean Picked_from_camera = false;

    public boolean isPhotoLoaded = false;

    public ArrayList<Uri> uploadedPhotos;

    MyService myService;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addimage);

        uploadedPhotos = new ArrayList<>();

        loadedPhoto = findViewById(R.id.photoLoaded);
        uploadImage = findViewById(R.id.btn_uploadImage);
        addImage = findViewById(R.id.btn_addImage);
        description = findViewById(R.id.description);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tedPermission();
                isPhotoLoaded = false;
                customDialog();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (albumURI != null) {
                    try {
                        System.out.println(albumURI);
                        uploadImage(albumURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(cameraURI != null){
                    try {
                        System.out.println(cameraURI);
                        uploadImage(cameraURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(addImage.this, "No Photo Added!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage(Uri uri) throws IOException {
        Retrofit retrofitClient = RetrofitClient.ImagesRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        System.out.println(uri);
        System.out.println(getRealPathFromURI(uri));
        File file = new File(getRealPathFromURI(uri));
        RequestBody req = RequestBody.create(MediaType.parse("image/*"), file);
        String Description = description.getHelperText();

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), req);

        //RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image/type");

        Call<ResponseBody> call = myService.addNewImage(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    Toast.makeText(addImage.this,"Uploaded Successfully!",Toast.LENGTH_SHORT).show();
                    finish();
                }

                Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(addImage.this,"Uploading Failed!",Toast.LENGTH_SHORT).show();
                Toast.makeText(addImage.this, t.getMessage(),Toast.LENGTH_SHORT).show();
            }


        });
    }
    private String getRealPathFromURI(Uri contentURI) {
        if(Picked_from_camera = true) {
            return mCurrentPhotoPath;
        }else {
            String result;
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }
    }


    public void customDialog(){
            final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setIcon(R.drawable.baseline_add_photo_alternate_black_48dp);
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

    private void tedPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                Toast.makeText(addImage.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(addImage.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(addImage.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission] ")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private void selectAlbum(){
        //앨범 열기
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
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(addImage.this.getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                    isPhotoLoaded = true;
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(addImage.this,addImage.this.getPackageName(),photoFile);
                    cameraURI = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
        }
    }

    private File createImageFile() throws IOException {
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ireh");
        if(!storageDir.exists()){
            Log.v("알림","storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v("알림","storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(addImage.this, "result code: "+ resultCode, Toast.LENGTH_SHORT).show();
            return;
        }
        albumURI = cameraURI;
        switch (requestCode){
            case PICK_FROM_ALBUM : {
                //앨범에서 가져오기
                if(data.getData()!=null){
                    albumURI = data.getData();
                }
                break;
            }

            case PICK_FROM_CAMERA: {
                try{
                    Log.v("알림", "FROM_CAMERA 처리");
                    galleryAddPic();
                    Picked_from_camera = true;
                    if (!uploadedPhotos.add(cameraURI))
                        Toast.makeText(addImage.this, "list add failed", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }

        try {
            InputStream in = addImage.this.getContentResolver().openInputStream(albumURI);
            ExifInterface exif = new ExifInterface(in);

            if (!isPhotoLoaded){
                return;
            }else{
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), albumURI);
                    uploadImage.setVisibility(View.INVISIBLE);
                    loadedPhoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        (addImage.this).sendBroadcast(mediaScanIntent);
        Toast.makeText(addImage.this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }
}
