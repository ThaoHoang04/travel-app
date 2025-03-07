package com.example.travelapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.travelapp.Adapter.ImageSliderAdapter;
import com.example.travelapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class ExplorerActivity extends BaseActivity {
    private MapView mapView;
    private TextView placeName, placeDistance;
    private ViewPager2 imageSlider;
    private Button startButton;
    private DatabaseReference databaseReference;
    private ImageSliderAdapter sliderAdapter;
    private CircleIndicator3 indicator;
    private List<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploer);

        placeName = findViewById(R.id.placeName);
        placeDistance = findViewById(R.id.placeDistance);
        imageSlider = findViewById(R.id.imageSlider);
        indicator = findViewById(R.id.indicator);
        startButton = findViewById(R.id.startButton);
        mapView = findViewById(R.id.mapView);

        // Cấu hình OpenStreetMap
//        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("OSM", MODE_PRIVATE));
//        mapView.setMultiTouchControls(true);
//        mapView.getController().setZoom(15.0);

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Banner");

        // Khởi tạo Adapter cho ViewPager2 (Cần truyền Context)
        sliderAdapter = new ImageSliderAdapter(this, imageUrls);
        imageSlider.setAdapter(sliderAdapter);

        // Liên kết indicator với ViewPager2
        indicator.setViewPager(imageSlider);

        // Tải ảnh từ Firebase
        fetchBannerData();
    }

    private void fetchBannerData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    imageUrls.clear();
                    for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                        String imageUrl = bannerSnapshot.child("url").getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageUrls.add(imageUrl);
                        }
                    }
                    sliderAdapter.updateImages(imageUrls);
                } else {
                    Log.e("Firebase", "Không có dữ liệu");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi truy vấn Firebase
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }
}
