package com.example.fake_book.Tab_1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.example.fake_book.Tab_2.addImage;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddContact extends Activity {
    private Uri cameraURI, albumURI;
    private String mCurrentPhotoPath;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA= 2;

    private static boolean Picked_from_camera = false;

    Button btn_addProfileImage;
    MaterialEditText edt_email, edt_phoneNumber;
    EditText edt_name;
    Button btn_addcontact;
    ImageView contact_image;
    Retrofit retrofitClient;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MyService myService;


    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontact);

        btn_addProfileImage = (Button) findViewById(R.id.btn_contactImage);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_phoneNumber = (MaterialEditText) findViewById(R.id.item_phonenumber);
        edt_email = (MaterialEditText) findViewById(R.id.edt_email);

        contact_image = (ImageView) findViewById(R.id.contact_imageview);


        btn_addcontact = (Button) findViewById(R.id.btn_addcontact);

        btn_addcontact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addContact(edt_name.getText().toString(), edt_phoneNumber.getText().toString(), edt_email.getText().toString());
                addContactImage(albumURI); //OR CAMERA URI;
                finish();
            }
        });
        btn_addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog();

            }
        });
    }

    private void addContact(String name, String phoneNumber, String email) {
        retrofitClient = RetrofitClient.ContactsRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        if(TextUtils.isEmpty(name))
            Toast.makeText(this, "Name cannot be null or Empty", Toast.LENGTH_LONG).show();
        if(TextUtils.isEmpty(phoneNumber))
            Toast.makeText(this, "Name cannot be null or Empty", Toast.LENGTH_LONG).show();

        compositeDisposable.add(myService.addNewContact(name, phoneNumber, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(AddContact.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    private void addContactImage(Uri uri){
        Retrofit retrofitClient = RetrofitClient.contactImagesRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        File file = new File(getRealPathFromURI(uri));

        RequestBody req = RequestBody.create(MediaType.parse("image/*"), file);

        //Setting file name to unique name (phone number, is most likely unique) + file extension
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", edt_phoneNumber.getText().toString() + file.getName().substring(file.getName().lastIndexOf(".")), req);

        Call<ResponseBody> call = myService.addContactImage(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    Toast.makeText(AddContact.this,"Uploaded Successfully!",Toast.LENGTH_SHORT).show();
                    finish();
                }

                Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(addImage.this,"Uploading Failed!",Toast.LENGTH_SHORT).show();
                Toast.makeText(AddContact.this, t.getMessage(),Toast.LENGTH_SHORT).show();
            }


        });
    }




    private String getRealPathFromURI(Uri contentURI) {
        if(Picked_from_camera) {
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
                Toast.makeText(AddContact.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AddContact.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(AddContact.this)
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
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void takePhoto(){
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(AddContact.this.getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(AddContact.this,AddContact.this.getPackageName(),photoFile);
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
        String imgFileName = edt_phoneNumber.getHelperText() + ".jpg";
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
            Toast.makeText(AddContact.this, "result code: "+ resultCode, Toast.LENGTH_SHORT).show();
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
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }

        try {
            InputStream in = AddContact.this.getContentResolver().openInputStream(albumURI);
            ExifInterface exif = new ExifInterface(in);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), albumURI);
            btn_addProfileImage.setVisibility(View.INVISIBLE);
            contact_image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        (AddContact.this).sendBroadcast(mediaScanIntent);
        Toast.makeText(AddContact.this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }

}
