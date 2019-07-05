package com.example.fake_book.Tab_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fake_book.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookAdapter.ViewHolder> {
    public ArrayList<Item> mList;

    public PhonebookAdapter(ArrayList<Item> list) {
        mList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView number;
        public ViewHolder(View view) {
            super(view);
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
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }
}