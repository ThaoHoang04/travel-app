package com.example.travelapp.Adapter;

import static androidx.core.content.ContextCompat.startActivity;
import static java.lang.Float.NaN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.Activity.DetailCateActivity;
import com.example.travelapp.Activity.MainActivity;
import com.example.travelapp.Activity.TestActivity;
import com.example.travelapp.Domain.Category;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ViewholderCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Viewholder> {
    private final List<Category> list;
    private int selecPosition = -1;
    private int lastPosion = -1;
    private Context context;

    public CategoryAdapter(List<Category> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(inflater, parent, false);
        return new Viewholder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.Viewholder holder, @SuppressLint("RecyclerView") int position) {
        Category category = list.get(position);
        holder.binding.title.setText(category.getName());
        Glide.with(holder.itemView.getContext())
                .load(category.getImagePath())
                .into(holder.binding.pic);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosion = selecPosition;
                selecPosition = position;
                notifyItemChanged(lastPosion);
                notifyItemChanged(selecPosition);
                // GỌI API LUÔN Ở ĐÂY
                getItemIncate(category.getId(), category.getName());
            }
        });
        holder.binding.title.setTextColor(context.getColor(R.color.white));
        if(selecPosition == position) {
            holder.binding.pic.setBackgroundResource(0);
            holder.binding.mainlayout.setBackgroundResource(R.drawable.blue_bg);
            holder.binding.title.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.pic.setBackgroundResource(R.drawable.grey_bg);
            holder.binding.mainlayout.setBackgroundResource(0);
            holder.binding.title.setVisibility(View.GONE);
        }
    }

    private void getItemIncate(int id, String name) {
       Intent intent = new Intent(context, DetailCateActivity.class);
       intent.putExtra("items", id);
       intent.putExtra("Name", name);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private final ViewholderCategoryBinding binding;
        public Viewholder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
