package com.example.fake_book.Tab_1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fake_book.MainActivity;
import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AddContact extends AppCompatActivity {
    EditText edit_name, edit_number, edit_email;
    ImageView ImageView_photo;
    String name, number, email;
    Bitmap photo;
    byte[] bytes;

    int position;

    ArrayList <Item> phonebooklist = MainActivity.phonebooklist;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MyService myService;
    Retrofit retrofitClient;

    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_edit);

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        actionBar.setTitle("Add New Contact");

        position = phonebooklist.size();
        name = "";
        number = "";
        email = "";
        /*bytes = getIntent().getByteArrayExtra("photo");
        photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);*/

        edit_name = findViewById(R.id.edit_name);
        edit_number = findViewById(R.id.edit_number);
        edit_email = findViewById(R.id.edit_email);
        /*ImageView_photo = findViewById(R.id.imageView_phonebook);*/

        edit_name.setText(name);
        edit_number.setText(number);
        edit_email.setText(email);
       /* ImageView_photo.setImageBitmap(photo);

        ImageView_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // permission check
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_ALBUM);
                    onClick(v);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    Toast.makeText(getApplicationContext().getApplicationContext(), "new image", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void addContact(String name, String phoneNumber, String email) {
        retrofitClient = RetrofitClient.ContactsRetrofitInstance();
        myService = retrofitClient.create(MyService.class);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.commit:
                if (edit_name.getText().toString().length() == 0 | edit_number.getText().toString().length() == 0) {
                    Toast.makeText(this, "Fill name and number", Toast.LENGTH_LONG).show();
                } else {
                    addContact(edit_name.getText().toString(), edit_number.getText().toString(), edit_email.getText().toString());
                    finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_commit, menu);
        return true;
    }
}
