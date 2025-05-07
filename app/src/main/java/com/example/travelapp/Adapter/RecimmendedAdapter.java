package com.example.travelapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.Activity.Detailactivity;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.databinding.ViewholderRecommenBinding;

import java.util.ArrayList;

public class RecimmendedAdapter extends RecyclerView.Adapter<RecimmendedAdapter.Viewholder> {
    ArrayList<ItemDomain> items;
    Context context;
//    ViewholderRecommenBinding binding;

    public RecimmendedAdapter(ArrayList<ItemDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecimmendedAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderRecommenBinding binding = ViewholderRecommenBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new Viewholder(binding);
    }@Override
    public void onBindViewHolder(@NonNull RecimmendedAdapter.Viewholder holder, int position) {
        ItemDomain item = items.get(position); // Không cần dùng getAdapterPosition ở đây

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText(item.getPrice() + "VND");
        holder.binding.addressTxt.setText(item.getAddress());
        holder.binding.scoreTxt.setText("" + item.getScore());

        Glide.with(context)
                .load(item.getPic())
                .into(holder.binding.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detailactivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderRecommenBinding binding;

        public Viewholder(ViewholderRecommenBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

