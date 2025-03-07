package com.example.travelapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.example.travelapp.databinding.ActivityDetailactivityBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Detailactivity extends AppCompatActivity {
    private ActivityDetailactivityBinding binding;
    private ItemDomain object;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        if (object != null) {
            setVariable();
            checkFavoriteStatus();
        }
    }

    private void setVariable() {
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$ " + object.getPrice());
        binding.bedTxt.setText(String.valueOf(object.getBed()));
        binding.durationTxt.setText(object.getDuration());
        binding.distanceTxt.setText(object.getDistance());
        binding.decriptionTxt.setText(object.getDescription());
        binding.addressTxt.setText(object.getAddress());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());

        Glide.with(this).load(object.getPic()).into(binding.pic);
        binding.backBtn.setOnClickListener(v -> finish());
    // Chuyển sang TicketActivity khi nhấn "Add to Cart"
        binding.addToCart.setOnClickListener(v -> {
            Intent intent = new Intent(Detailactivity.this, TicketActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        });
        binding.imageView7.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) {
                binding.imageView7.setImageResource(R.drawable.baseline_favorite_24);
                saveToFavorites(object);
            } else {
                binding.imageView7.setImageResource(R.drawable.fav_icon);
                removeFromFavorites(object);
            }
        });
    }

    private void getIntentExtra() {
        object = (ItemDomain) getIntent().getSerializableExtra("object");
    }

    private void checkFavoriteStatus() {
        List<ItemDomain> favoriteList = getFavorites();
        isFavorite = favoriteList.stream().anyMatch(item -> item.getTitle().equals(object.getTitle()));
        binding.imageView7.setImageResource(isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.fav_icon);
    }

    private void saveToFavorites(ItemDomain item) {
        List<ItemDomain> favoriteList = getFavorites();
        if (!favoriteList.contains(item)) favoriteList.add(item);
        saveFavorites(favoriteList);
    }

    private void removeFromFavorites(ItemDomain item) {
        List<ItemDomain> favoriteList = getFavorites();
        favoriteList.removeIf(fav -> fav.getTitle().equals(item.getTitle()));
        saveFavorites(favoriteList);
    }

    private List<ItemDomain> getFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favorite_list", null);
        Type type = new TypeToken<ArrayList<ItemDomain>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    private void saveFavorites(List<ItemDomain> favoriteList) {
        SharedPreferences sharedPreferences = getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("favorite_list", new Gson().toJson(favoriteList)).apply();
    }

}
