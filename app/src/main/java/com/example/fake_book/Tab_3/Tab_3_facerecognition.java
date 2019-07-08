package com.example.fake_book.Tab_3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fake_book.MainActivity;
import com.example.fake_book.R;
import com.example.fake_book.Tab_1.Item;
import com.example.fake_book.Tab_1.PhonebookAdapter;
import com.example.fake_book.Tab_1.RecyclerItemClickListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Tab_3_facerecognition extends AppCompatActivity {

    RecyclerView contact_list;
    ArrayList<Item> phonebooklist;
    int[] result_array;
    Bitmap bm;
    LinearLayoutManager mLinearLayoutManager;
    PhonebookAdapter phonebookadapter;
    Paint myRectPaint, myTextPaint, myRectPaint_white, myTextPaint_white;

    SparseArray<Face> faces;

    Button prev_button, next_button;
    ImageView image;
    int index = 0;
    int STROKE_WIDTH, TEXT_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_3_facerecognition);

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        //recycler view
        contact_list = findViewById(R.id.contact_list);
        phonebooklist = MainActivity.phonebooklist;
        mLinearLayoutManager = new LinearLayoutManager(Tab_3_facerecognition.this);
        contact_list.setLayoutManager(mLinearLayoutManager);
        phonebookadapter = new PhonebookAdapter(phonebooklist, R.layout.listitem_layout_mini);
        contact_list.setAdapter(phonebookadapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(contact_list.getContext(), mLinearLayoutManager.getOrientation());
        contact_list.addItemDecoration(dividerItemDecoration);
        contact_list.addOnItemTouchListener(new RecyclerItemClickListener(Tab_3_facerecognition.this, contact_list, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                result_array[index] = position;
                image.setImageBitmap(draw_result(bm, index));
                Toast.makeText(Tab_3_facerecognition.this, "This is " + phonebooklist.get(position).getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongItemClick(View view, final int position) {
            }
        }));

        //button setting
        prev_button = findViewById(R.id.prev_button);
        next_button = findViewById(R.id.next_button);
        image = findViewById(R.id.imageView_recognition);

        String loaded_photo_path = Uri.parse(getIntent().getStringExtra("photo_uri")).getPath();
        System.out.println(loaded_photo_path);
        File image_file = new File(loaded_photo_path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        final Bitmap bm = BitmapFactory.decodeFile(image_file.getAbsolutePath(),bmOptions);

        //face recognition
        FaceDetector faceDetector = new FaceDetector.Builder(Tab_3_facerecognition.this).setTrackingEnabled(false).build();
        if(!faceDetector.isOperational()){
            Toast.makeText(Tab_3_facerecognition.this,"Could not set up face detector!", Toast.LENGTH_LONG).show();
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(bm).build();
        faces = faceDetector.detect(frame);

        result_array = new int[faces.size()];
        for (int i=0 ; i<result_array.length ; i++)
            result_array[i] = -1;

        if (faces.size() == 0)  {
            Toast.makeText(Tab_3_facerecognition.this, "No face found!", Toast.LENGTH_LONG).show();
            return;
        }

        STROKE_WIDTH = bm.getWidth()/300;
        TEXT_SIZE = bm.getWidth()/30;

        //Paints
        myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(STROKE_WIDTH);
        myRectPaint.setColor(Color.parseColor("#FFFF9800"));
        myRectPaint.setStyle(Paint.Style.STROKE);

        myRectPaint_white = new Paint();
        myRectPaint_white.setStrokeWidth(STROKE_WIDTH);
        myRectPaint_white.setColor(Color.WHITE);
        myRectPaint_white.setStyle(Paint.Style.STROKE);

        myTextPaint = new Paint();
        myTextPaint.setAntiAlias(true);
        myTextPaint.setStyle(Paint.Style.FILL);
        myTextPaint.setColor(Color.parseColor("#FFFF9800"));
        myTextPaint.setTextSize(TEXT_SIZE);

        myTextPaint_white = new Paint();
        myTextPaint_white.setAntiAlias(true);
        myTextPaint_white.setStyle(Paint.Style.FILL);
        myTextPaint_white.setColor(Color.WHITE);
        myTextPaint_white.setTextSize(TEXT_SIZE);

        image.setImageBitmap(draw_result(bm,index));

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == faces.size()-1){
                    Intent result = new Intent();
                    result.putExtra("result",result_array);
                    setResult(RESULT_OK,result);
                    finish();
                }
                else {
                    index++;
                    image.setImageBitmap(draw_result(bm, index));
                }
            }
        });
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0)
                    Toast.makeText(Tab_3_facerecognition.this, "First Face!",Toast.LENGTH_LONG).show();
                else {
                    index--;
                    image.setImageBitmap(draw_result(bm, index));
                }
            }
        });
    }

    // index에 해당하는 얼굴에 네모 그려서 Bitmap 반환
    public Bitmap draw_result (Bitmap original_image, int index) {
        Bitmap tempBitmap = original_image.copy(Bitmap.Config.ARGB_8888, true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(tempBitmap, 0, 0, null);

        for (int i = 0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            if (index == i) {
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                if (result_array[i] != -1) {
                    tempCanvas.drawText(phonebooklist.get(result_array[i]).getName(), x1, y2 + (STROKE_WIDTH*12), myTextPaint);
                }
            }
            else {
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint_white);
                if (result_array[i] != -1) {
                    tempCanvas.drawText(phonebooklist.get(result_array[i]).getName(), x1, y2 + (STROKE_WIDTH*12), myTextPaint_white);
                }
            }
        }
        return (new BitmapDrawable(getResources(), tempBitmap)).getBitmap();
    }

    public Uri save_NewImage(Bitmap inImage, String filename) throws IOException {
        File new_image = new File(getStoragePath(), filename);
        new_image.createNewFile();
        FileOutputStream out = new FileOutputStream(new_image);
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return Uri.parse(new_image.getPath());
    }

    public String getStoragePath() {
        return getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
    }
}
