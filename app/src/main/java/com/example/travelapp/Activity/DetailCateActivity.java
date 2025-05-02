package com.example.travelapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelapp.Adapter.FavoriteAdapter;
import com.example.travelapp.Domain.ApiClient;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Interface.ApiService;
import com.example.travelapp.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_cate);
       detailItemInCategory();

    }

    private void detailItemInCategory() {
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("items")) {
            // Lấy giá trị String từ Intent
                int cateId=  intent.getIntExtra("items",-1);
            //goi API
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<List<ItemDomain>> call = apiService.GetItem(cateId);
            call.enqueue(new Callback<List<ItemDomain>>() {
                @Override
                public void onResponse(Call<List<ItemDomain>> call, Response<List<ItemDomain>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        List<ItemDomain> result = response.body();
                        if (result.isEmpty()){
//                            Toast.makeText(DetailCateActivity.this, "Không lấy được items", Toast.LENGTH_SHORT).show();
                            TextView textView = findViewById(R.id.textView4);
                            textView.setText("Không có tour nào");
                        }
                        else {
                            // cap nhap dl vao listview
                            FavoriteAdapter favoriteAdapter = new FavoriteAdapter(DetailCateActivity.this, result);
                            ListView listView = findViewById(R.id.resultListview);
                            listView.setAdapter(favoriteAdapter);
                            // Thêm sự kiện click cho từng item trong ListView
                            listView.setOnItemClickListener((parent, view, position, id) -> {// Lấy item được nhấn
                                ItemDomain selectedItem = result.get(position);
                                // Bạn có thể mở một màn hình mới và truyền dữ liệu về địa điểm đã chọn
                                Intent detailIntent = new Intent(DetailCateActivity.this, Detailactivity.class);
                                detailIntent.putExtra("object", selectedItem); // Truyền dữ liệu vào intent
                                startActivity(detailIntent);
                            });

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ItemDomain>> call, Throwable t) {

                }
            });

        }
    }
}