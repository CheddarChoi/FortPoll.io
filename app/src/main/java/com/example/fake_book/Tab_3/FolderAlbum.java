package com.example.fake_book.Tab_3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fake_book.MainActivity;
import com.example.fake_book.R;
import com.example.fake_book.Tab_1.Item;
import com.example.fake_book.Tab_2.CardAdapter;

import java.util.ArrayList;

public class FolderAlbum extends AppCompatActivity {

    RecyclerView folder_album_recycler_view;
    TextView name,number,email,photo_num,call_num,messege_num;
    CardAdapter adapter;
    ArrayList<Uri> folder_imagelist;
    ArrayList<Item> phonebooklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_album);

        int position = getIntent().getIntExtra("position", -1);
        phonebooklist = MainActivity.phonebooklist;
        Item thisitem = phonebooklist.get(position);
        folder_imagelist = thisitem.getPhotos();

        folder_album_recycler_view = findViewById(R.id.folder_album_recycler_view);
        GridLayoutManager mGridLayoutManager;
        mGridLayoutManager = new GridLayoutManager(FolderAlbum.this, 3);
        folder_album_recycler_view.setLayoutManager(mGridLayoutManager);
        adapter = new CardAdapter(folder_imagelist) ;
        folder_album_recycler_view.setAdapter(adapter);

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setTitle(thisitem.getName());
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        name = findViewById(R.id.item_name);
        number = findViewById(R.id.item_phonenumber);
        email = findViewById(R.id.item_email);
        name.setText(thisitem.getName());
        number.setText(thisitem.getNumber());
        email.setText(thisitem.getEmail());
        photo_num = findViewById(R.id.photo_num);
        photo_num.setText(String.valueOf(thisitem.getPhotos().size()));
        call_num = findViewById(R.id.call_num);
        call_num.setText(String.valueOf(get_call_num(thisitem.getNumber())));
        messege_num = findViewById(R.id.message_num);
        messege_num.setText(String.valueOf(get_message_num(thisitem.getNumber())));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int get_call_num(String input_number) {
        int call_num = 0;

        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);

        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            if(phNumber.equals(input_number.replaceAll("-","")))  call_num = call_num+1;
        }
        managedCursor.close();
        return call_num;
    }

    public int get_message_num(String input_number) {
        int message_num = 0;

        Uri allMessage = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,
                new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                null, null,
                "date DESC");

        while (c.moveToNext()) {
            String phNumber = c.getString(2);
            System.out.println(phNumber);
            if(phNumber.equals(input_number.replaceAll("-","")))  message_num = message_num+1;
        }
        return message_num;
    }
}
