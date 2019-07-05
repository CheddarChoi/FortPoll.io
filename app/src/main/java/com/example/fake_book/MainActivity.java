package com.example.fake_book;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.fake_book.Tab_1.Tab_1;
import com.example.fake_book.Tab_2.Tab_2;
import com.example.fake_book.Tab_3.Tab_3;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;
    private ViewPager pager;
    FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String user_info = intent.getStringExtra("user_info");
        System.out.println(user_info);

        pager = findViewById(R.id.pager);

        adapter.addItem(new Tab_1(), "PhoneBook");
        adapter.addItem(new Tab_2(), "Gallery");
        adapter.addItem(new Tab_3(), "??");
        pager.setAdapter(adapter);

        TabLayout tab_layout = findViewById(R.id.tab_layout);
        tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);
        tab_layout.setupWithViewPager(pager);

        pager.setCurrentItem(0);

        mContext = this;
    }
}
