package com.example.travelapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelapp.Adapter.FavoriteAdapter;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookMarkActivity extends AppCompatActivity {
    private ListView favoriteListView;
    private FavoriteAdapter favoriteAdapter;
    private List<ItemDomain> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        favoriteListView = findViewById(R.id.favoriteListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        favoriteList = getFavorites();
        favoriteAdapter = new FavoriteAdapter(this, favoriteList);
        favoriteListView.setAdapter(favoriteAdapter);
        favoriteAdapter.updateFavorites();

        // Bắt sự kiện khi người dùng nhấn vào một mục trong danh sách
        favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemDomain selectedItem = favoriteList.get(position);
                Intent intent = new Intent(BookMarkActivity.this, Detailactivity.class);
                intent.putExtra("object", selectedItem);
                startActivity(intent);
            }
        });
    }

    private List<ItemDomain> getFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favorite_list", null);
        Type type = new TypeToken<List<ItemDomain>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }
}
