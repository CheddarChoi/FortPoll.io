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
    int layout_type;

    public PhonebookAdapter(ArrayList<Item> list, int layout_type) {
        mList = list;
        this.layout_type = layout_type;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout_type, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, int position) {
        viewHolder.name.setText(mList.get(position).getName());
        if (layout_type == R.layout.listitem_layout)
            viewHolder.number.setText(mList.get(position).getNumber());
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }
}