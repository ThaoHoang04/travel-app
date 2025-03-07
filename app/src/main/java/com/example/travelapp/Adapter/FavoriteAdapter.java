package com.example.travelapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends BaseAdapter {
    private final Context context;
    private List<ItemDomain> favoriteList;

    public FavoriteAdapter(Context context, List<ItemDomain> favoriteList) {
        this.context = context;
        this.favoriteList = getFavorites();
    }

    @Override
    public int getCount() {
        return favoriteList.size();
    }

    @Override
    public Object getItem(int position) {
        return favoriteList.get(position);
    }

    public void updateFavorites() {
        this.favoriteList = getFavorites();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    private List<ItemDomain> getFavorites() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favorite_list", null);
        Type type = new TypeToken<List<ItemDomain>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    static class ViewHolder {
        TextView titleTxt;
        ImageView imageView;
    }
}
