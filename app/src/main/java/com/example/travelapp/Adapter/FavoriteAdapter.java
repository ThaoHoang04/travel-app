package com.example.travelapp.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ViewholderRecommenBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FavoriteAdapter extends BaseAdapter {
    private final Context context;
    private List<ItemDomain> favoriteList;
    ViewholderRecommenBinding binding;

    public FavoriteAdapter(Context context, List<ItemDomain> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @Override
    public int getCount() {
        return favoriteList.size();
    }

    @Override
    public Object getItem(int position) {
        return favoriteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<ItemDomain> newList) {
        this.favoriteList = newList;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
            holder = new ViewHolder();
            holder.titleTxt = convertView.findViewById(R.id.titleTxt);
            holder.imageView = convertView.findViewById(R.id.pic);
            holder.scoreTxt = convertView.findViewById(R.id.scoreTxt);
            holder.addressTxt = convertView.findViewById(R.id.addressTxt);
            holder.priceTxt = convertView.findViewById(R.id.priceTxt);
//            holder.btnreview = convertView.findViewById(R.id.btnreview); // Thêm dòng này


//            holder.descriptionTxt = convertView.findViewById(R.id.favoriteDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ItemDomain item = favoriteList.get(position);
        if (item != null) {
            holder.titleTxt.setText(item.getTitle());
            Glide.with(context).load(item.getPic()).placeholder(R.drawable.intro_pic).into(holder.imageView);
            holder.titleTxt.setText(item.getTitle());
            holder.priceTxt.setText(item.getPrice()+ "VND");
            holder.addressTxt.setText(item.getAddress());
            holder.scoreTxt.setText(""+(item.getScore()));


        }

        return convertView;
    }

    static class ViewHolder {
        TextView titleTxt,descriptionTxt,scoreTxt,addressTxt,priceTxt;
        ImageView imageView;
        Button btnreview;
    }
}
