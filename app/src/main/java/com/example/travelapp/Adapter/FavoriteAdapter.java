package com.example.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;

import java.util.List;

public class FavoriteAdapter extends BaseAdapter {
    private final Context context;
    private List<ItemDomain> favoriteList;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
            holder = new ViewHolder();
            holder.titleTxt = convertView.findViewById(R.id.favoriteTitle);
            holder.imageView = convertView.findViewById(R.id.favoriteImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ItemDomain item = favoriteList.get(position);
        if (item != null) {
            holder.titleTxt.setText(item.getTitle());
            Glide.with(context).load(item.getPic()).placeholder(R.drawable.intro_pic).into(holder.imageView);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView titleTxt;
        ImageView imageView;
    }
}
