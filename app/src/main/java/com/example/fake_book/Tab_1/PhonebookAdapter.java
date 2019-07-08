package com.example.fake_book.Tab_1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fake_book.MyService;
import com.example.fake_book.R;
import com.example.fake_book.RetrofitClient;
import com.example.fake_book.Tab_2.Tab_2;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;
import static java.security.AccessController.getContext;

public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookAdapter.ViewHolder> {
    public ArrayList<Item> mList;
    private Context mContext;

    public PhonebookAdapter(Context context,ArrayList<Item> list) {
        this.mContext = context;
        mList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profilepic;
        public TextView name;
        public TextView number;
        public ViewHolder(View view) {
            super(view);
            profilepic = view.findViewById(R.id.image);
            name = view.findViewById(R.id.item_name);
            number = view.findViewById(R.id.item_phonenumber);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, int position) {
        viewHolder.name.setText(mList.get(position).getName());
        viewHolder.number.setText(mList.get(position).getNumber());
        viewHolder.profilepic.setImageBitmap(mList.get(position).getProfile_pic());
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }





}