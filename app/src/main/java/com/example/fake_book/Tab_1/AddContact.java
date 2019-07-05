package com.example.fake_book.Tab_1;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AddContact extends Activity {
    MaterialEditText edt_email, edt_phoneNumber;
    EditText edt_name;
    Button btn_addcontact;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MyService myService;

    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontact);

        Retrofit retrofitClient = RetrofitClient.addcontact_RetrofitInstance();
        myService = retrofitClient.create(MyService.class);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_phoneNumber = (MaterialEditText) findViewById(R.id.item_phonenumber);
        edt_email = (MaterialEditText) findViewById(R.id.edt_email);

        btn_addcontact = (Button) findViewById(R.id.btn_addcontact);

        btn_addcontact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addContact(edt_name.getText().toString(), edt_phoneNumber.getText().toString(), edt_email.getText().toString());
                finish();
            }
        });
    }

    private void addContact(String name, String phoneNumber, String email) {
        if(TextUtils.isEmpty(name))
            Toast.makeText(this, "Name cannot be null or Empty", Toast.LENGTH_LONG).show();
        if(TextUtils.isEmpty(phoneNumber))
            Toast.makeText(this, "Name cannot be null or Empty", Toast.LENGTH_LONG).show();

        compositeDisposable.add(myService.addNewContact(name, phoneNumber, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(AddContact.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }
}
