package com.example.fake_book.Tab_1;

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

import com.example.fake_book.MainActivity;
import com.example.fake_book.MyService;
import com.example.fake_book.PhotoTools;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
            Toast.makeText(this, "Number cannot be null or Empty", Toast.LENGTH_LONG).show();

        compositeDisposable.add(myService.addNewContact(name, phoneNumber, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) {
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
                finish();
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
        if(Picked_from_camera == true) {
            return mCurrentPhotoPath;
        }
        return PhotoTools.getRealPathFromURI(uri, context);
    }
}
