package com.example.fake_book.Tab_1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.fake_book.MainActivity;
import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

public class AddContact extends AppCompatActivity {
    EditText edit_name, edit_number, edit_email;
    ImageView imageView_photo;
    String name, number, email;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA= 2;

    int position;
    private boolean isPhotoLoaded = false;
    private boolean Picked_from_camera = false;
    private Uri loadedPhoto_uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.island);
    private String mCurrentPhotoPath;

    ArrayList <Item> phonebooklist = MainActivity.phonebooklist;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MyService myService;
    Retrofit retrofitClient;

    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_edit);

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        actionBar.setTitle("Add New Contact");

        position = phonebooklist.size();
        name = "";
        number = "";
        email = "";
        /*bytes = getIntent().getByteArrayExtra("photo");
        photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);*/

        edit_name = findViewById(R.id.edit_name);
        edit_number = findViewById(R.id.edit_number);
        edit_email = findViewById(R.id.edit_email);
        imageView_photo = findViewById(R.id.imageView_phonebook);

        edit_name.setText(name);
        edit_number.setText(number);
        edit_email.setText(email);
        imageView_photo.setImageResource(R.drawable.island);

        imageView_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                customDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.commit:
                if (edit_name.getText().toString().length() == 0 | edit_number.getText().toString().length() == 0) {
                    Toast.makeText(this, "Fill name and number", Toast.LENGTH_LONG).show();
                } else {
                    addContact(edit_name.getText().toString(),
                            edit_number.getText().toString().replaceAll("-",""),
                            edit_email.getText().toString());
                    addContactImage(loadedPhoto_uri);
                    finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
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

        File file = new File(getRealPathFromURI(uri,getApplicationContext()));

        RequestBody req = RequestBody.create(MediaType.parse("image/*"), file);

        //Setting file name to unique name (phone number, is most likely unique) + file extension
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", edit_number.getText().toString() + file.getName().substring(file.getName().lastIndexOf(".")), req);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_commit, menu);
        return true;
    }

    public void customDialog(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
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
            if(camera_intent.resolveActivity(AddContact.this.getPackageManager()) != null){
                File new_photo = createImageFile();
                if(new_photo != null){
                    isPhotoLoaded = true;
                    loadedPhoto_uri = FileProvider.getUriForFile(AddContact.this, AddContact.this.getPackageName(),new_photo);
                    camera_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, loadedPhoto_uri);
                    startActivityForResult(camera_intent, PICK_FROM_CAMERA);
                }
            }
        }
        else
            Toast.makeText(AddContact.this,"저장공간에 접근할 수 없습니다.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(AddContact.this, "result code: "+ resultCode, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode){
            case PICK_FROM_ALBUM : {
                if(data.getData() != null)
                    loadedPhoto_uri = Uri.parse(data.getData().toString());
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

        System.out.println(loadedPhoto_uri);

        if (!isPhotoLoaded){
            return;
        }else{
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), loadedPhoto_uri);
                bitmap = modifyOrientation(bitmap, getRealPathFromURI(loadedPhoto_uri, getApplicationContext()));
                imageView_photo.setImageBitmap(bitmap);
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
        (AddContact.this).sendBroadcast(mediaScanIntent);
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

    public String getRealPathFromURI(final Uri uri, final Context context) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if(Picked_from_camera == true) {
            return mCurrentPhotoPath;
        }

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
