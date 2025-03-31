package com.example.travelapp.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private GeoPoint selectedLocation; // Vị trí người dùng chọn
    private Marker selectedMarker; // Marker của vị trí mới
    private final GeoPoint initialLocation = new GeoPoint(21.0285, 105.8542); // Hà Nội
    private Marker initialMarker; // Marker vị trí ban đầu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploer);

        // Khởi tạo các thành phần giao diện
        placeName = findViewById(R.id.placeName);
        placeDistance = findViewById(R.id.placeDistance);
        imageSlider = findViewById(R.id.imageSlider);
        indicator = findViewById(R.id.indicator);
        startButton = findViewById(R.id.startButton);
        mapView = findViewById(R.id.mapView);

        // Khởi tạo bản đồ
        khoiTaoBanDo();

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Item");

        // Khởi tạo Adapter cho ViewPager2
        sliderAdapter = new ImageSliderAdapter(this, imageUrls);
        imageSlider.setAdapter(sliderAdapter);

        // Liên kết indicator với ViewPager2
        indicator.setViewPager(imageSlider);

        // Tải ảnh từ Firebase
        fetchBannerData();

        // Xử lý sự kiện chạm vào bản đồ
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    float y = event.getY();
                    GeoPoint tappedPoint = (GeoPoint) mapView.getProjection().fromPixels((int) x, (int) y);

                    selectedLocation = tappedPoint;
                    getPlaceName(tappedPoint);
                    addSelectedMarker(tappedPoint);
                }
                return false;
            }
        });

        // Xử lý sự kiện khi nhấn nút Start
        startButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                openGoogleMaps(selectedLocation);
            }
        });
    }

    private void khoiTaoBanDo() {
        try {
            // Cấu hình OpenStreetMap và đặt User-Agent
            Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("OSM", MODE_PRIVATE));
            Configuration.getInstance().setUserAgentValue(getPackageName());

            // Thiết lập nguồn tile cho bản đồ
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setMultiTouchControls(true);
            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(initialLocation);

            // Thêm marker cho vị trí ban đầu (Hà Nội)
            initialMarker = new Marker(mapView);
            initialMarker.setPosition(initialLocation);
            initialMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            initialMarker.setTitle("Vị trí ban đầu (Hà Nội)");
            mapView.getOverlays().add(initialMarker);

            mapView.invalidate(); // Cập nhật lại bản đồ
        } catch (Exception e) {
            Log.e("LỖI BẢN ĐỒ", "Lỗi khi khởi tạo bản đồ: " + e.getMessage());
        }
    }

    private void addSelectedMarker(GeoPoint point) {
        if (selectedMarker != null) {
            mapView.getOverlays().remove(selectedMarker); // Xóa marker cũ
        }

        selectedMarker = new Marker(mapView);
        selectedMarker.setPosition(point);
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        selectedMarker.setTitle("Vị trí mới");
        mapView.getOverlays().add(selectedMarker);
        mapView.invalidate(); // Cập nhật bản đồ
    }

    private void getPlaceName(GeoPoint point) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String locationName = addresses.get(0).getAddressLine(0);
                placeName.setText(locationName);
            } else {
                placeName.setText("Không tìm thấy tên địa điểm");
            }
        } catch (IOException e) {
            placeName.setText("Lỗi lấy tên địa điểm");
            Log.e("Geocoder", "Lỗi: " + e.getMessage());
        }

        double distance = tinhKhoangCach(initialLocation, point);
        placeDistance.setText(String.format(Locale.getDefault(), "%.2f km away", distance));
    }

    private double tinhKhoangCach(GeoPoint point1, GeoPoint point2) {
        double R = 6371; // Bán kính Trái đất (km)
        double dLat = Math.toRadians(point2.getLatitude() - point1.getLatitude());
        double dLon = Math.toRadians(point2.getLongitude() - point1.getLongitude());
        double lat1 = Math.toRadians(point1.getLatitude());
        double lat2 = Math.toRadians(point2.getLatitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Khoảng cách tính bằng km
    }

    private void openGoogleMaps(GeoPoint point) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", point.getLatitude(), point.getLongitude(), point.getLatitude(), point.getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi truy vấn: " + error.getMessage());
            }
        });
    }
}
