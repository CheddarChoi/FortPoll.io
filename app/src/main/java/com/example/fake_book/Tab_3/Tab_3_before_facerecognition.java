package com.example.fake_book.Tab_3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fake_book.MainActivity;
import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.example.fake_book.Tab_1.Item;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Tab_3_before_facerecognition extends AppCompatActivity {

    Button start_button;
    FloatingActionButton rotate_button;
    ImageView image;
    Context context;
    boolean isPhotoLoaded = false;

    ArrayList<Item> phonebooklist = MainActivity.phonebooklist;

    Uri selected_image_uri;

    Retrofit retrofitClient;
    MyService myService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Tab_3_before_facerecognition.this;

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        setContentView(R.layout.tab_3_before_recognition);
        start_button = findViewById(R.id.start_button);
        rotate_button = findViewById(R.id.rotate_button);
        image = findViewById(R.id.imageView_recognition);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tab_3_before_facerecognition.this, chooseImage.class);
                startActivityForResult(intent, 2);
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhotoLoaded) {
                    Intent face_recognition = new Intent(Tab_3_before_facerecognition.this, Tab_3_facerecognition.class);
                    face_recognition.putExtra("photo_uri", selected_image_uri.toString());
                    startActivityForResult(face_recognition, 3);
                } else
                    Toast.makeText(Tab_3_before_facerecognition.this, "Load Photo First!", Toast.LENGTH_LONG).show();
            }
        });

        rotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = image.getDrawable();
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                Bitmap bitmap = bitmapDrawable.getBitmap();

                bitmap = imgRotate(bitmap);
                image.setImageBitmap(bitmap);

                File rotated_file = new File(selected_image_uri.getPath());
                OutputStream out = null;
                try {
                    rotated_file.createNewFile();
                    out = new FileOutputStream(rotated_file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2: {
                if (resultCode == Activity.RESULT_OK) {
                    selected_image_uri = Uri.parse(data.getStringExtra("photo_uri"));
                    image.setImageURI(selected_image_uri);
                    isPhotoLoaded = true;
                }
                break;
            }

            case 3: {
                if (resultCode == Activity.RESULT_OK) {
                    int[] result_array = data.getIntArrayExtra("result");
                    for (int selected_person : result_array) {
                        if (selected_person != -1) {
                            phonebooklist.get(selected_person).addPhoto(selected_image_uri);
                            editContactPhotos(phonebooklist.get(selected_person).getNumber(), selected_image_uri.toString());
                        }
                    }
                }
                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
                break;
            }
        }
    }

    private Bitmap imgRotate(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }

    public void editContactPhotos(String phoneNumber, String photoPaths) {
        retrofitClient = RetrofitClient.editContactRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        compositeDisposable.add(myService.editContact(phoneNumber, photoPaths)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(Tab_3_before_facerecognition.this, "" + response, Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }
}