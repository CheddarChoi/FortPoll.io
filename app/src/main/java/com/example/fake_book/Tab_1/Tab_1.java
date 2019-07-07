package com.example.fake_book.Tab_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class Tab_1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private PhonebookAdapter phonebookadapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    ArrayList<Contact> mContact;
    ArrayList<Item> phonebooklist;

    private FloatingActionButton add_contacts, deleteAll;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "MainActivity";

    MyService myService;
    Retrofit retrofit;

    final int ADD_CONTACT = 1000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_1_main, container, false);
        phonebooklist = MainActivity.phonebooklist;

        LoadContacts();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = view.findViewById(R.id.recycler_view);
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

        add_contacts = view.findViewById(R.id.add_contact);
        deleteAll = view.findViewById(R.id.deleteAll);
        deleteAll.hide();

        add_contacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), AddContact.class);
                startActivityForResult(intent,ADD_CONTACT);
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadContacts();
                swipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

    public interface GetDataCallback {
        void onGetContactsData(List<Contact> contacts);
        void onError();
    }

    private void LoadContacts() {
        phonebooklist.clear();
        LoadContactsConnect(new GetDataCallback() {
            @Override
            public void onGetContactsData(List<Contact> contacts) {
                assert contacts != null;
                for(Contact c : contacts){
                    System.out.println(c.getEmail()+" "+c.getName()+" "+c.getPhoneNumber());
                    mContact.add(new Contact(c.getName(), c.getPhoneNumber(), c.getEmail()));
                    System.out.println(mContact.size());
                    phonebooklist.add(new Item(c.getName(), c.getPhoneNumber(), c.getEmail(), new ArrayList<Uri>()));
                    phonebookadapter.notifyDataSetChanged();
                }
                Log.d(TAG, "ALL CONTACTS LOADED");
            }
            @Override
            public void onError() {
                Log.d(TAG, "Error while loading contacts..");
            }
        });
    }

    private void LoadContactsConnect(final GetDataCallback getDataCallback) {
        mContact = new ArrayList<>();
        retrofit = RetrofitClient.ContactsRetrofitInstance();
        myService = retrofit.create(MyService.class);

        Call<List<Contact>> call = myService.getContacts();

        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(@NotNull Call<List<Contact>> call, @NotNull Response<List<Contact>> response) {
                if(!response.isSuccessful()){
                    getDataCallback.onError();
                    Log.d(TAG, "xlxlxl"+response.code());
                    return;
                }
                getDataCallback.onGetContactsData(response.body());
                call.cancel();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                getDataCallback.onError();
                //Something went wrong with the communication with the server or processing the Response or JSON doesn't fit to whatever we are trying to parser into.
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(resultCode) {
            case ADD_CONTACT: {
                LoadContacts();
                phonebookadapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


