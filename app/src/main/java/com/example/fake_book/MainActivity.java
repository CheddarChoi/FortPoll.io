package com.example.fake_book;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;
    private ViewPager pager;
    FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
    Bitmap user_image;
    URL user_image_url;
    public static ArrayList<Item> phonebooklist;
    public static ArrayList<Uri> imagelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        phonebooklist = new ArrayList<>();
        imagelist = new ArrayList<>();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tedPermission();

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
        try {
            user_image_url = new URL(inBundle.get("imageUrl").toString());
            user_image = getBitmapFromUrl(user_image_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this,"Welcome, "+user_name, Toast.LENGTH_LONG).show();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Drawable new_drawable = new BitmapDrawable(getResources(), user_image);
        getMenuInflater().inflate(R.menu.menu_with_logout, menu);
        Menu mOptionsMenu = menu;
        mOptionsMenu.findItem(R.id.profile).setIcon(new_drawable);
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

    private void tedPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() { }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(Objects.requireNonNull(getApplicationContext()))
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission] ")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS)
                .check();

    }

    private Bitmap getBitmapFromUrl(URL url){
        try {
            AsyncTask<URL, Void, Bitmap> asyncTask = new AsyncTask<URL, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(URL... url) {
                    try {
                        return BitmapFactory.decodeStream(((URL) url[0]).openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

            Bitmap bitmap = asyncTask.execute(url).get();
            return bitmap;
        }
        catch (Exception e) {
            return null;
        }
    }
}