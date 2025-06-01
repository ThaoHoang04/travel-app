package com.example.travelapp.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelapp.Adapter.FavoriteAdapter;
import com.example.travelapp.Domain.ApiClient;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.ItemResponse;
import com.example.travelapp.Interface.ApiService;
import com.example.travelapp.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        searchLocation();
    }
    public void  searchLocation() {
        // Lấy Intent từ SearchActivity
        Intent intent = getIntent();

        // Kiểm tra Intent có chứa dữ liệu với key "searchResults" không
        if (intent != null && intent.hasExtra("searchResults")) {
            // Lấy giá trị String từ Intent
            String searchQuery = intent.getStringExtra("searchResults");

            // Hiển thị giá trị nhận được
            Toast.makeText(this, "Từ khóa tìm kiếm: " + searchQuery, Toast.LENGTH_SHORT).show();

            // Bạn có thể sử dụng searchQuery để tìm kiếm hoặc hiển thị kết quả

            // Gọi API tìm kiếm (ví dụ dùng Retrofit)
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ItemResponse> call = apiService.searchLocation(searchQuery);
            call.enqueue(new Callback<ItemResponse>() {
                @Override
                public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                    // Đảm bảo rằng yêu cầu đã thành công và dữ liệu trong phản hồi không phải là null.
                    if (response.isSuccessful() && response.body() != null) {
                        List<ItemDomain> result = response.body().getData();
                        if (result.isEmpty()) {
                            Toast.makeText(TestActivity.this, "Không tìm thấy địa điểm", Toast.LENGTH_SHORT).show();
                        } else {
                            // Cập nhật dữ liệu vào ListView
                            FavoriteAdapter adapter2 = new FavoriteAdapter(TestActivity.this, result);
                            ListView listView = findViewById(R.id.searchListView);
                            listView.setAdapter(adapter2);
                            // Thêm sự kiện click cho từng item trong ListView
                            listView.setOnItemClickListener((parent, view, position, id) -> {
                                // Lấy item được nhấn
                                ItemDomain selectedItem = result.get(position);
                                // Bạn có thể mở một màn hình mới và truyền dữ liệu về địa điểm đã chọn
                                Intent detailIntent = new Intent(TestActivity.this, Detailactivity.class);
                                detailIntent.putExtra("object", selectedItem); // Truyền dữ liệu vào intent
                                startActivity(detailIntent);
                            });
                        }

                    } else {
                        Toast.makeText(TestActivity.this, "Đã xảy ra lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    Toast.makeText(TestActivity.this, "Không kết nối được tới server", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}