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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fake_book.MainActivity;
import com.example.fake_book.R;
import com.example.fake_book.Tab_1.Item;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Tab_3_before_facerecognition extends AppCompatActivity {

    Button start_button;
    FloatingActionButton rotate_button;
    ImageView image;
    Context context;
    boolean isPhotoLoaded = false;
    Bitmap bm;

    ArrayList<Item> phonebooklist = MainActivity.phonebooklist;

    Uri selected_image_uri;

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
                startActivityForResult(intent,2);
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhotoLoaded) {
                    Intent face_recognition = new Intent(Tab_3_before_facerecognition.this, Tab_3_facerecognition.class);
                    face_recognition.putExtra("photo_uri", selected_image_uri.toString());
                    startActivityForResult(face_recognition, 3);
                }
                else
                    Toast.makeText(Tab_3_before_facerecognition.this,"Load Photo First!",Toast.LENGTH_LONG).show();
            }
        });

        rotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bm = imgRotate(bm);
                image.setImageBitmap(bm);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2 : {
                if (resultCode == Activity.RESULT_OK) {
                    selected_image_uri = Uri.parse(data.getStringExtra("photo_uri"));
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), selected_image_uri);
                        image.setImageBitmap(bm);
                        isPhotoLoaded = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            case 3 : {
                if (resultCode == Activity.RESULT_OK) {
                    int[] result_array = data.getIntArrayExtra("result");
                    for (int selected_person : result_array){
                        if (selected_person != -1)
                            phonebooklist.get(selected_person).addPhoto(selected_image_uri);
                    }
                }
                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
                break;
            }
        }
    }

    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        OutputStream os = null;
        try {
            os = getApplicationContext().getContentResolver().openOutputStream(selected_image_uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        resizedBitmap.compress(Bitmap.CompressFormat.PNG,100,os);

        return resizedBitmap;
    }
}