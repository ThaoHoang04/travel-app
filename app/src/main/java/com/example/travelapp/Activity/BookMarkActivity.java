package com.example.travelapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelapp.Adapter.FavoriteAdapter;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookMarkActivity extends AppCompatActivity {
    private ListView favoriteListView;
    private FavoriteAdapter favoriteAdapter;
    private List<ItemDomain> favoriteList;
    private DatabaseReference databaseReference;
    private String cartId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        favoriteListView = findViewById(R.id.favoriteListView);
        favoriteList = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(this, favoriteList);
        favoriteListView.setAdapter(favoriteAdapter);

        // Lấy cartId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        cartId = sharedPreferences.getString("cartId", "");

        if (cartId == null || cartId.isEmpty()) {
            Log.e("BOOKMARK", "Lỗi: Không tìm thấy cartId! Chuyển về LoginActivity.");
            Intent intent = new Intent(BookMarkActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("favoriteItems").child(cartId);

        loadFavorites();

        // Bắt sự kiện khi người dùng nhấn vào một mục trong danh sách
        favoriteListView.setOnItemClickListener((parent, view, position, id) -> {
            ItemDomain selectedItem = favoriteList.get(position);
            Intent intent = new Intent(BookMarkActivity.this, Detailactivity.class);
            intent.putExtra("object", selectedItem);
            startActivity(intent);
        });
    }

    private void loadFavorites() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ItemDomain item = itemSnapshot.getValue(ItemDomain.class);
                    if (item != null) {
                        favoriteList.add(item);
                    }
                }

                Log.d("FIREBASE", "Số mục yêu thích: " + favoriteList.size());

                if (favoriteList.isEmpty()) {
                    Toast.makeText(BookMarkActivity.this, "Không có mục yêu thích nào!", Toast.LENGTH_SHORT).show();
                }

                favoriteAdapter.updateData(favoriteList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Lỗi khi tải danh sách yêu thích!", error.toException());
            }
        });
    }
}
