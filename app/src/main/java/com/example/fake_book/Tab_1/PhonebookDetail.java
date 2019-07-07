package com.example.fake_book.Tab_1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fake_book.R;

public class PhonebookDetail extends AppCompatActivity implements View.OnClickListener {
    TextView textView_name, textView_number, textView_email;
    ImageView ImageView_photo;
    String name, number, email;
    Bitmap photo;
    ImageButton callbutton, messagebutton, emailbutton;
    byte[] bytes;
    boolean isDelete = false;
    boolean isEdit = false;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_detail);
        position = getIntent().getIntExtra("position",-1);
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        email = getIntent().getStringExtra("email");
        /*bytes = getIntent().getByteArrayExtra("photo");
        photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);*/

        // Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setTitle("Contact Detail");
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        textView_name = findViewById(R.id.edit_name);
        textView_number = findViewById(R.id.edit_number);
        textView_email = findViewById(R.id.edit_email);
        /*ImageView_photo = findViewById(R.id.imageView_phonebook);*/
        callbutton = findViewById(R.id.callButton);
        messagebutton = findViewById(R.id.messageButton);
        emailbutton = findViewById(R.id.emailButton);

        callbutton.setOnClickListener(this);
        messagebutton.setOnClickListener(this);
        emailbutton.setOnClickListener(this);

        textView_name.setText(name);
        textView_number.setText(number);
        textView_email.setText(email);
        /*ImageView_photo.setImageBitmap(photo);*/

        if (email.length() == 0){
            emailbutton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_edit_delete, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.callButton:
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + number)));
                break;
            case R.id.messageButton:
                Intent intentsms = new Intent( Intent.ACTION_VIEW, Uri.parse("sms:" + number));
                intentsms.putExtra("sms_body", "");
                startActivity(intentsms);
                break;
            case R.id.emailButton:
                Intent intentemail = new Intent(Intent.ACTION_SEND);
                intentemail.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
                intentemail.setType("message/rfc822");
                startActivity(intentemail);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit:
                Intent edit_intent = new Intent(this, PhonebookEdit.class);
                edit_intent.putExtra("position",position);
                edit_intent.putExtra("name",name);
                edit_intent.putExtra("number",number);
                edit_intent.putExtra("email",email);
                /*edit_intent.putExtra("photo",bytes);*/
                startActivityForResult(edit_intent, 2);
                return true;
            case R.id.delete:
                isDelete = true;
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isDelete", isDelete);
        resultIntent.putExtra("isEdit", isEdit);
        resultIntent.putExtra("position", position);
        resultIntent.putExtra("name", name);
        resultIntent.putExtra("number", number);
        resultIntent.putExtra("email", email);
        /*resultIntent.putExtra("photo",bytes);*/
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 2 : {
                if (resultCode == Activity.RESULT_OK) {
                    isEdit = data.getBooleanExtra("isEdit", false);
                    if (isEdit){
                        name = data.getStringExtra("name");
                        number = data.getStringExtra("number");
                        email = data.getStringExtra("email");
                        /*bytes = data.getByteArrayExtra("photo");
                        photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);*/
                        textView_name.setText(name);
                        textView_number.setText(number);
                        textView_email.setText(email);
                        /*ImageView_photo.setImageBitmap(photo);*/
                    }
                }
                break;
            }
        }
    }
}
