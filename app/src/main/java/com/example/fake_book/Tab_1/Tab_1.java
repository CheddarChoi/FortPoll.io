package com.example.fake_book.Tab_1;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.MainActivity;
import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Tab_1 extends Fragment {
    private PhonebookAdapter phonebookadapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    ArrayList<Contact> mContact;
    ArrayList<Item> phonebooklist;

    private FloatingActionButton add_contacts, deleteAll;

    private static final String TAG = "MainActivity";

    MyService myService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:started.");

        View rootView = inflater.inflate(R.layout.tab_1_main, container, false);

        phonebooklist = MainActivity.phonebooklist;

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        phonebookadapter = new PhonebookAdapter(phonebooklist, R.layout.listitem_layout);
        mRecyclerView.setAdapter(phonebookadapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent detail_intent = new Intent(getActivity(), PhonebookDetail.class);
                detail_intent.putExtra("position", position);
                detail_intent.putExtra("name", phonebooklist.get(position).getName());
                detail_intent.putExtra("number", phonebooklist.get(position).getNumber());
                detail_intent.putExtra("email", phonebooklist.get(position).getEmail());

/*                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bmp = phonebooklist.get(position).getPhoto();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                detail_intent.putExtra("photo", bytes);*/

                startActivityForResult(detail_intent, 1);
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                AlertDialog.Builder oDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                oDialog.setMessage("연락처를 삭제합니다.")
                        .setTitle("Delete Contact")
                        .setNegativeButton("Commit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                phonebooklist.remove(position);
                                phonebookadapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "commit", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "cancel", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        }));

        add_contacts = rootView.findViewById(R.id.add_contact);
        deleteAll = rootView.findViewById(R.id.deleteAll);
        deleteAll.hide();

        //load contacts from db
        //phonebooklist.clear();
        LoadContacts(new GetDataCallback() {
            @Override
            public void onGetContactsData(List<Contact> contacts) {
                assert contacts != null;
                Resources resources = getContext().getResources();
                for(Contact c : contacts){
                    mContact.add(new Contact(c.getName(), c.getPhoneNumber(), c.getEmail()));
                    final Uri new_image = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.island);
                    ArrayList<Uri> photolist = new ArrayList<>();
                    photolist.add(new_image);
                    phonebooklist.add(new Item(c.getName(), c.getPhoneNumber(), c.getEmail(), photolist));
                }
                Log.d(TAG, "ALL CONTACTS LOADED");
                phonebookadapter.notifyDataSetChanged();
            }
            @Override
            public void onError() {
                Log.d(TAG, "불쌍하니까..");
            }
        });
        //

        add_contacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), AddContact.class);
                startActivityForResult(intent,1);
            }
        });

        return rootView;
    }

    public interface GetDataCallback {
        void onGetContactsData(List<Contact> contacts);
        void onError();
    }

    private void LoadContacts(final GetDataCallback getDataCallback) {
        mContact = new ArrayList<>();
        Retrofit retrofit = RetrofitClient.loadContact_RetrofitInstance();
        myService = retrofit.create(MyService.class);

        Call<List<Contact>> call = myService.getContacts();

        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(@NotNull Call<List<Contact>> call, @NotNull Response<List<Contact>> response) {
                if (!response.isSuccessful()) {
                    getDataCallback.onError();
                    Log.d(TAG, "xlxlxl" + response.code());
                    return;
                }
                getDataCallback.onGetContactsData(response.body());


            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                getDataCallback.onError();
                //Something went wrong with the communication with the server or processing the Response or JSON doesn't fit to whatever we are trying to parser into.
                Log.d(TAG, t.getMessage());
            }
        });

    }
}


