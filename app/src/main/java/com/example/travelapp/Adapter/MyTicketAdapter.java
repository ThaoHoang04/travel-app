package com.example.travelapp.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ViewholderRecommenBinding;

import java.util.List;

public class MyTicketAdapter extends BaseAdapter {
private  final Context context;
private List<ItemDomain> item;
ViewholderRecommenBinding binding;
    public MyTicketAdapter(Context context, List<ItemDomain> item) {
        this.context = context;
        this.item = item;
    }


    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_my_ticket_adapter, parent, false);
            holder = new ViewHolder();
            holder.titleTxt = convertView.findViewById(R.id.titleTxt);
            holder.imageView = convertView.findViewById(R.id.pic);
            holder.date = convertView.findViewById(R.id.dateTxt);
            holder.addressTxt = convertView.findViewById(R.id.addressTxt);
            holder.priceTxt = convertView.findViewById(R.id.priceTxt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ItemDomain items = item.get(position);
        if (item != null) {
            holder.titleTxt.setText(items.getTitle());
            Glide.with(context).load(items.getPic()).placeholder(R.drawable.intro_pic).into(holder.imageView);
            holder.titleTxt.setText(items.getTitle());
            holder.priceTxt.setText(items.getPrice()+ "VND");
            holder.addressTxt.setText(items.getAddress());
            holder.date.setText(""+(items.getDateTour() ));

        }

        return convertView;

    }
    static class ViewHolder {
        TextView titleTxt,descriptionTxt,date,addressTxt,priceTxt;
        ImageView imageView;
    }
}