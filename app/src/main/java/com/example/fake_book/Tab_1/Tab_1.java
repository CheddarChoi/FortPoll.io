package com.example.fake_book.Tab_1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.lang.reflect.Array;
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

    private FloatingActionButton menu, read_contacts, add_contacts, deleteAll;
    boolean isMenuOpen = false;

    private static final String TAG = "MainActivity";

    Retrofit retrofit;
    MyService myService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:started.");

        View rootView = inflater.inflate(R.layout.tab_1_main, container, false);

        phonebooklist = new ArrayList<>();

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        phonebookadapter = new PhonebookAdapter(phonebooklist);
        mRecyclerView.setAdapter(phonebookadapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        menu = rootView.findViewById(R.id.menu);
        read_contacts = rootView.findViewById(R.id.read_contact);
        add_contacts = rootView.findViewById(R.id.add_contact);
        deleteAll = rootView.findViewById(R.id.deleteAll);
        deleteAll.hide();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOpen();
            }
        });

        read_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                phonebooklist.clear();
                LoadContacts(new GetDataCallback() {
                    @Override
                    public void onGetContactsData(List<Contact> contacts) {
                        assert contacts != null;
                        for(Contact c : contacts){
                            System.out.println(c.getEmail()+" "+c.getName()+" "+c.getPhoneNumber());
                            mContact.add(new Contact(c.getName(), c.getPhoneNumber(), c.getEmail()));
                            System.out.println(mContact.size());
                            phonebooklist.add(new Item(c.getName(), c.getPhoneNumber(), c.getEmail()));
                        }
                        Log.d(TAG, "ALL CONTACTS LOADED");
                        phonebookadapter.notifyDataSetChanged();
                        System.out.println(phonebookadapter.getItemCount());
                        System.out.println(mContact.size());
                        System.out.println(phonebooklist.size());
                    }
                    @Override
                    public void onError() {
                        Log.d(TAG, "불쌍하니까..");
                    }
                });
            }
        });

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


    private void menuOpen(){
        if(!isMenuOpen){
            add_contacts.animate().translationY(-getResources().getDimension(R.dimen.add_contact));
            read_contacts.animate().translationY(-getResources().getDimension(R.dimen.read_contacts));
            deleteAll.show();

            isMenuOpen = true;
        } else {
            add_contacts.animate().translationY(0);
            read_contacts.animate().translationY(0);
            deleteAll.hide();

            isMenuOpen = false;
        }
    }
}


