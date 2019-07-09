package com.example.fake_book.Tab_3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.R;
import com.example.fake_book.Tab_2.ImgActivity;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private ArrayList<ArrayList<Uri>> folder_list;
    private ArrayList<String> name_list;
    static ArrayList<Card> cards;
    Context context;

    FolderAdapter(ArrayList<ArrayList<Uri>> list, ArrayList<String> name_list)
    {
        this.folder_list = list;
        this.name_list = name_list;
        this.cards = new ArrayList<>();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Card card;
        ViewHolder(View itemView) {
            super(itemView);
            card = new Card();
            card.name = itemView.findViewById(R.id.folder_name);
            card.img = itemView.findViewById(R.id.card_image);

            card.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }

    @NonNull
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup rootview, int viewType) {
        context = rootview.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.folder, rootview, false) ;

        return new FolderAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.ViewHolder holder, int position) {
        if(folder_list.get(position).size() != 0){
            holder.card.img.setImageURI(this.folder_list.get(position).get(0));
            Bitmap bitmap=((BitmapDrawable)holder.card.img.getDrawable()).getBitmap();
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 200, bitmap.getHeight()*200/bitmap.getWidth(), true);
            holder.card.img.setImageBitmap(resized);
            holder.card.uri = this.folder_list.get(position).get(0);
        }
        holder.card.name.setText(this.name_list.get(position));
        holder.card.index = position;
    }

    @Override
    public int getItemCount() {
        return folder_list.size() ;
    }

    class Card {
        ImageView img;
        TextView name;
        int index;
        Uri uri;
    }

}

