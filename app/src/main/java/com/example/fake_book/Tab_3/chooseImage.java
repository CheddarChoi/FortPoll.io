package com.example.fake_book.Tab_3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.fake_book.MainActivity;
import com.example.fake_book.R;
import com.example.fake_book.Tab_1.RecyclerItemClickListener;
import com.example.fake_book.Tab_2.CardAdapter;

import java.util.ArrayList;

public class chooseImage extends AppCompatActivity {

    ArrayList<Uri> imagelist;
    private CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        imagelist = MainActivity.imagelist;

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        final RecyclerView recyclerView = findViewById(R.id.folder_album_recycler_view) ;
        GridLayoutManager mGridLayoutManager;
        mGridLayoutManager = new GridLayoutManager(chooseImage.this, 3);
        recyclerView.setLayoutManager(mGridLayoutManager);
        adapter = new CardAdapter(imagelist);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(chooseImage.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onBackPressed(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public void onBackPressed(int position) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("photo_uri", imagelist.get(position).toString());
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
