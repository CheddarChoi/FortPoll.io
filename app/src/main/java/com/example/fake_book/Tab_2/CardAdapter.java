package com.example.fake_book.Tab_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.MainActivity;
import com.example.fake_book.R;

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

            card.img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder oDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    oDialog.setMessage("사진을 삭제합니다.")
                            .setTitle("Delete Photo")
                            .setNegativeButton("Commit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.imagelist.remove(card.index);
                                    CardAdapter.this.notifyDataSetChanged();
                                    Toast.makeText(context, "commit", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "cancel", Toast.LENGTH_LONG).show();
                                }
                            })
                            .show();
                    return true;
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
        holder.card.img.setImageURI(this.mData.get(position));
        Bitmap bitmap=((BitmapDrawable)holder.card.img.getDrawable()).getBitmap();
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

