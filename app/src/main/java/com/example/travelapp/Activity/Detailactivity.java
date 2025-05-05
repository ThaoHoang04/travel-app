package com.example.travelapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.Adapter.RatingAdapter;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.RatingModel;
import com.example.travelapp.R;
import com.example.travelapp.Vnpay.VnpayMainActivity;
import com.example.travelapp.databinding.ActivityDetailactivityBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Detailactivity extends AppCompatActivity {
    private ActivityDetailactivityBinding binding;
    private ItemDomain object;
    private boolean isFavorite = false;
    private String cartId;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private RatingAdapter adapter;
    private List<RatingModel> ratingList = new ArrayList<>();
    private RecyclerView rvPreviousRatings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy username từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", ""); // Lấy username từ bộ nhớ
//        username = getIntent().getStringExtra("username");


        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy username!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(username);

        // Lấy cartId từ Firebase
        userRef.child("cartId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartId = snapshot.getValue(String.class);

                Log.d("FIREBASE", "cartId lấy từ Firebase: " + cartId);

                if (cartId == null || cartId.isEmpty()) {
                    Toast.makeText(Detailactivity.this, "Lỗi: Không tìm thấy cartId!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    databaseReference = database.getReference("favoriteItems").child(cartId);
                    getIntentExtra();
                    if (object != null) {
                        setVariable();
                        checkFavoriteStatus();
                        fetchReviewsFromFirebase(object.getItemsId());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Lỗi khi lấy cartId từ Firebase!", error.toException());
                Toast.makeText(Detailactivity.this, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void setVariable() {
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText( object.getPrice() + "VND");
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
            Intent intent = new Intent(Detailactivity.this, VnpayMainActivity.class);
            intent.putExtra("object", object);
            intent.putExtra("itemId", object.getItemsId());
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện yêu thích
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
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = false;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ItemDomain existingItem = itemSnapshot.getValue(ItemDomain.class);
                    if (existingItem != null && existingItem.getTitle().equals(object.getTitle())) {
                        isFavorite = true;
                        break;
                    }
                }
                binding.imageView7.setImageResource(isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.fav_icon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Lỗi khi kiểm tra trạng thái yêu thích!", error.toException());
            }
        });
    }

        private void saveToFavorites(ItemDomain item) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean exists = false;
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        ItemDomain existingItem = itemSnapshot.getValue(ItemDomain.class);
                        if (existingItem != null && existingItem.getTitle().equals(item.getTitle())) {
                            exists = true;
                            break;
                        }
                    }

                if (!exists) {
                    //tạo key và lưu vào firebase
                    String key = databaseReference.push().getKey();
                    if (key != null) {
                        databaseReference.child(key).setValue(item)
                                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Đã thêm vào danh sách yêu thích!"))
                                .addOnFailureListener(e -> Log.e("FIREBASE", "Lỗi khi lưu!", e));
                    }
                } else {
                    Log.d("FIREBASE", "Mục này đã có trong danh sách yêu thích!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Lỗi khi lưu yêu thích!", error.toException());
            }
        });
    }
    private void fetchReviewsFromFirebase(int currentItemId) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Review");

        // Bước 1: Lấy toàn bộ Review
        reviewRef.get().addOnCompleteListener(reviewTask -> {
            if (reviewTask.isSuccessful()) {
                List<DataSnapshot> matchingReviews = new ArrayList<>();

                // Bước 2: Duyệt tất cả review, lấy orderId
                for (DataSnapshot reviewSnapshot : reviewTask.getResult().getChildren()) {
                    String orderId = reviewSnapshot.child("orderId").getValue(String.class);
                    if (orderId != null) {
                        matchingReviews.add(reviewSnapshot);
                    }
                }

                // Bước 3: Lấy toàn bộ Order để đối chiếu
                orderRef.get().addOnCompleteListener(orderTask -> {
                    if (orderTask.isSuccessful()) {
                        ratingList.clear();

                        for (DataSnapshot reviewSnapshot : matchingReviews) {
                            String reviewOrderId = reviewSnapshot.child("orderId").getValue(String.class);

                            // Duyệt toàn bộ Order để tìm order có id trùng
                            for (DataSnapshot orderSnapshot : orderTask.getResult().getChildren()) {
                                String orderIdInDb = orderSnapshot.child("orderId").getValue(String.class);

                                if (reviewOrderId != null && reviewOrderId.equals(orderIdInDb)) {
                                    // So sánh itemId
                                    Long itemIdLong = orderSnapshot.child("itemId").getValue(Long.class);
                                    if (itemIdLong != null && itemIdLong.intValue() == currentItemId) {

                                        // Nếu trùng thì lấy dữ liệu review
                                        String name = reviewSnapshot.child("name").getValue(String.class);
                                        String comment = reviewSnapshot.child("comment").getValue(String.class);
                                        String ratingStr = reviewSnapshot.child("ratingValue").getValue(String.class);

                                        float rating = 0f;
                                        try {
                                            rating = Float.parseFloat(ratingStr);
                                        } catch (NumberFormatException e) {
                                            // ignore
                                        }

                                        if (name == null || name.trim().isEmpty()) {
                                            name = "Ẩn danh";
                                        }

                                        ratingList.add(0, new RatingModel(name, rating, comment));
                                        break; // đã match rồi thì không cần check order khác
                                    }
                                }
                            }
                        }

                        // Hiển thị
                        rvPreviousRatings = findViewById(R.id.rvPreviousRatings);
                        adapter = new RatingAdapter(ratingList);
                        rvPreviousRatings.setLayoutManager(new LinearLayoutManager(this));
                        rvPreviousRatings.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        rvPreviousRatings.scrollToPosition(0);
                    } else {
                        Toast.makeText(this, "Không thể tải dữ liệu đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Không thể tải đánh giá!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromFavorites(ItemDomain item) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ItemDomain existingItem = itemSnapshot.getValue(ItemDomain.class);
                    if (existingItem != null && existingItem.getTitle().equals(item.getTitle())) {
                        itemSnapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Đã xóa khỏi danh sách yêu thích!"))
                                .addOnFailureListener(e -> Log.e("FIREBASE", "Lỗi khi xóa!", e));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Lỗi khi xóa yêu thích!", error.toException());
            }
        });
    }
}
