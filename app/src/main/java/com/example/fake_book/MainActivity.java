package com.example.fake_book;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.example.fake_book.Tab_1.Item;
import com.example.fake_book.Tab_1.Tab_1;
import com.example.fake_book.Tab_2.Tab_2;
import com.example.fake_book.Tab_3.Tab_3;
import com.facebook.login.LoginManager;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;
    private ViewPager pager;
    FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
    Bitmap user_image;
    public static ArrayList<Item> phonebooklist;
    public static ArrayList<Uri> imagelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        phonebooklist = new ArrayList<>();
        imagelist = new ArrayList<>();
        //for test
        phonebooklist.add(new Item("CheddarChoi", "010-2796-5866", "cde1103@kaist.ac.kr", new ArrayList<Uri>()));
        phonebooklist.add(new Item("Bob", "010-2134-4778", "helloworld@kaist.ac.kr", new ArrayList<Uri>()));
        phonebooklist.add(new Item("Vinny", "010-4464-7789", "niceVinny@kaist.ac.kr", new ArrayList<Uri>()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));


        Intent intent = getIntent();
        Bundle inBundle = intent.getExtras();
        String user_name = inBundle.get("name").toString();
        String user_id = inBundle.get("id").toString();
        String user_image_url = inBundle.get("imageUrl").toString();
        Toast.makeText(MainActivity.this,"Welcome, "+user_name, Toast.LENGTH_LONG).show();

        Picasso.get().load(user_image_url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                user_image = bitmap;
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        pager = findViewById(R.id.pager);

        adapter.addItem(new Tab_1(), "");
        adapter.addItem(new Tab_2(), "");
        adapter.addItem(new Tab_3(), "");
        pager.setAdapter(adapter);

        TabLayout tab_layout = findViewById(R.id.tab_layout);
        tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);
        tab_layout.setupWithViewPager(pager);
        tab_layout.getTabAt(0).setIcon(R.drawable.ic_phonebook);
        tab_layout.getTabAt(1).setIcon(R.drawable.ic_gallery);
        tab_layout.getTabAt(2).setIcon(R.drawable.ic_friend);
        pager.setCurrentItem(0);

        mContext = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                LoginManager.getInstance().logOut();
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 뒤로가기 버튼을 두번 연속으로 눌러야 종료되게끔 하는 메소드
    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            ActivityCompat.finishAffinity(MainActivity.this);
        }
    }
}