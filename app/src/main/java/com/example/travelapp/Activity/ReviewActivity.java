package com.example.travelapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.Adapter.RatingAdapter;
import com.example.travelapp.Adapter.ReviewData;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.RatingModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.example.travelapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    ImageView ivTourImage;

    private TextView tvRatingValue,tvTourName;
    private TextInputEditText etComment, etName, etEmail;
    private Button btnSubmit;
    private RecyclerView rvPreviousRatings;
    private List<RatingModel> ratingList = new ArrayList<>();
    private RatingAdapter adapter;
    private ItemDomain item;
    private String oderId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);
        item = (ItemDomain) getIntent().getSerializableExtra("object");
        oderId = getIntent().getStringExtra("orderid");
        databaseReference = FirebaseDatabase.getInstance().getReference("review");

        // Ánh xạ view
      widget();
      loaddata();
        fetchReviewsFromFirebase();

        // Cập nhật điểm đánh giá theo RatingBar
                ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                    tvRatingValue.setText("Đánh giá của bạn: " + rating);
                });

                // Set RecyclerView
                adapter = new RatingAdapter(ratingList);
                rvPreviousRatings.setLayoutManager(new LinearLayoutManager(this));
                rvPreviousRatings.setAdapter(adapter);

                // Gửi đánh giá
                btnSubmit.setOnClickListener(v -> submitRating());
            }

    private void fetchReviewsFromFirebase() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ratingList.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String comment = snapshot.child("comment").getValue(String.class);
                    String ratingStr = snapshot.child("ratingValue").getValue(String.class);

                    float rating = 0f;
                    try {
                        rating = Float.parseFloat(ratingStr);
                    } catch (NumberFormatException e) {
                        // bỏ qua nếu lỗi
                    }
            // Nếu name rỗng hoặc null thì gán "Ẩn danh"
                                if (name == null || name.trim().isEmpty()) {
                                    name = "Ẩn danh";
                                }
                    RatingModel ratingModel = new RatingModel(name, rating, comment);
                    ratingList.add(0, ratingModel); // thêm lên đầu danh sách
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Không thể tải đánh giá!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loaddata() {
        if (item != null) {
            tvTourName.setText(item.getTitle());

            int imageResId = getResources().getIdentifier(
                    item.getPic(), "drawable", getPackageName());

            if (imageResId != 0) {
                ivTourImage.setImageResource(imageResId);
            } else {
                ivTourImage.setImageResource(R.drawable.intro_pic); // fallback ảnh
            }
        }
    }

    private void widget() {
        ratingBar = findViewById(R.id.ratingBar);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        etComment = findViewById(R.id.etComment);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        rvPreviousRatings = findViewById(R.id.rvPreviousRatings);
        tvTourName = findViewById(R.id.tvTourName);
        ivTourImage = findViewById(R.id.ivTourImage);
    }


    private void submitRating() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (rating == 0.0f) {
            Toast.makeText(this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nhận xét!", Toast.LENGTH_SHORT).show();
            return;
        }



        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long count = task.getResult().getChildrenCount() + 1;
                String reviewId = String.format("RVI-%03d", count);
                Date date = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String created = sdf.format(date);

                ReviewData reviewData = new ReviewData(
                        oderId,
                        String.valueOf(rating),
                        comment,
                        name,
                        email,
                        reviewId,
                        created
                );

                databaseReference.child(reviewId).setValue(reviewData)
                        .addOnSuccessListener(unused -> {
                            // Thêm vào danh sách hiển thị
                            ratingList.add(0, new RatingModel(name.isEmpty() ? "Ẩn danh" :name, rating, comment));
                            adapter.notifyItemInserted(0);
                            rvPreviousRatings.scrollToPosition(0);

                            // Reset input
                            ratingBar.setRating(0);
                            etComment.setText("");
                            etName.setText("");
                            etEmail.setText("");

                            Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                            new android.os.Handler().postDelayed(() -> {
                                Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // nếu muốn đóng ReviewActivity sau khi chuyển
                            }, 1000);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Gửi đánh giá thất bại!", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
        }

