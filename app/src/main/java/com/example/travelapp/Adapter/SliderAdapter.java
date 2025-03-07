package com.example.travelapp.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.slider;
import com.example.travelapp.R;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewholder> {
    private ArrayList<slider> sliders;
    private ViewPager2 viewPager2;
    private Context context;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliders.addAll(sliders);
            notifyDataSetChanged();
        }
    };

    public SliderAdapter( ArrayList<slider> sliders,ViewPager2 viewPager2) {
        this.sliders = sliders;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderAdapter.SliderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewholder(LayoutInflater.from(context).inflate(R.layout.slider_items,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderViewholder holder, int position) {
            holder.setImage(sliders.get(position));
            if(position == sliders.size()-2) {
                viewPager2.post(runnable);
            }
    }

    @Override
    public int getItemCount() {
        return sliders.size();
    }

    public class SliderViewholder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public SliderViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlider);
        }
            void setImage(slider slider) {
                Glide.with(context)
                        .load(slider.getUrl())
                        .into(imageView);

        }
    }
}
