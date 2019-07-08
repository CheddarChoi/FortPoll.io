package com.example.fake_book.Tab_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.R;
import com.example.fake_book.Tab_1.AddContact;

import java.io.IOException;
import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<Uri> mData;
    static ArrayList<Card> cards;
    Context context;

    //생성자
    public CardAdapter(ArrayList<Uri> list, Context context)
    {
        this.mData = list ;
        this.cards = new ArrayList<>();
        this.context = context;

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        Card card;

        ViewHolder(View itemView) {
            super(itemView);
            card = new Card();

            card.img = itemView.findViewById(R.id.card_image);
            card.img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!card.clicked) {
                        card.clicked = true;
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.width = (int) (layoutParams.width / 1.1);
                        layoutParams.height = (int) (layoutParams.height / 1.1);
                        view.setLayoutParams(layoutParams);
                        cards.add(card);
                    }
                    else {
                        card.clicked = false;
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.width = (int) (layoutParams.width * 1.1);
                        layoutParams.height = (int) (layoutParams.height * 1.1);
                        view.setLayoutParams(layoutParams);
                        cards.remove(card);
                        if (cards.isEmpty());
                    }
                    return true;
                }
            });

            card.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImgActivity.class);
                    intent.putExtra("imgUri", mData.get(card.index));
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.card, parent, false) ;

        return new CardAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.ViewHolder holder, int position) {

        Uri selected_photo_uri = this.mData.get(position);
        System.out.println(selected_photo_uri);

        Bitmap bitmap = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selected_photo_uri);
        } catch (IOException e) {
            System.out.println("image안됨");
        }
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 200, bitmap.getHeight()*200/bitmap.getWidth(), true);
        holder.card.img.setImageBitmap(resized);
        holder.card.index = position;
        holder.card.uri = this.mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    class Card {
        ImageView img;
        int index;
        boolean clicked = false;
        Uri uri;
    }

}

