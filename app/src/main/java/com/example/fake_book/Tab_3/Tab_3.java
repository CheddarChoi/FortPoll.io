package com.example.fake_book.Tab_3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.MainActivity;
import com.example.fake_book.R;
import com.example.fake_book.Tab_1.Item;
import com.example.fake_book.Tab_1.RecyclerItemClickListener;

import java.util.ArrayList;

public class Tab_3 extends Fragment {

    ArrayList<Item> phonebooklist;
    ArrayList<Uri> imagelist;
    ArrayList<String> foldernamelist;
    ArrayList<ArrayList<Uri>> folderlist;
    private FolderAdapter adapter;
    Button recognition_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_3_main, container, false);

        phonebooklist = MainActivity.phonebooklist;
        imagelist = MainActivity.imagelist;

        //codes for make folderlist/foldernamelist
        updatePhonebooklist();

        final RecyclerView recyclerView = view.findViewById(R.id.folder_recycler_view) ;
        GridLayoutManager mGridLayoutManager;
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mGridLayoutManager);
        adapter = new FolderAdapter(folderlist, foldernamelist) ;
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent detail_intent = new Intent(getActivity(), FolderAlbum.class);
                detail_intent.putExtra("position", position);

                startActivityForResult(detail_intent, 1);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        recognition_btn = view.findViewById(R.id.recognition_button);
        recognition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Tab_3_before_facerecognition.class);
                startActivityForResult(intent,1);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1 : {
                if (resultCode == Activity.RESULT_OK) {
                    updatePhonebooklist();
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void updatePhonebooklist() {
        folderlist = new ArrayList<>();
        foldernamelist = new ArrayList<>();
        for (Item item : phonebooklist){
            foldernamelist.add(item.getName());
            folderlist.add(item.getPhotos());
        }
    }
}