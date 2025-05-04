package com.example.travelapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelapp.Adapter.MyTicketAdapter;
import com.example.travelapp.Domain.ApiClient;
import com.example.travelapp.Domain.ItemDomain;
import com.example.travelapp.Domain.MyTicket;
import com.example.travelapp.Interface.ApiService;
import com.example.travelapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_ticket);
        loadTicket();

    }


    private void loadTicket() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", ""); // Lấy username từ bộ nhớ

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<MyTicket>> call = apiService.getOrders(username);
        call.enqueue(new Callback<List<MyTicket>>() {
            @Override
            public void onResponse(Call<List<MyTicket>> call, Response<List<MyTicket>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<MyTicket> result = response.body();
                    if(result.isEmpty()) {
                        Toast.makeText(MyTicketActivity.this, "Không lấy được items", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        List<ItemDomain> tourList = new ArrayList<>();

                        for (MyTicket rs : result) {
                            ItemDomain item = rs.getTourInfo();
                            tourList.add(item);  // Gom tất cả lại
                        }

                    // Sau khi gom xong mới set adapter 1 lần
                        MyTicketAdapter myTicketAdapter = new MyTicketAdapter(MyTicketActivity.this, tourList);
                        ListView listView = findViewById(R.id.lvMyTicket);
                        listView.setAdapter(myTicketAdapter);
                        // Thêm sự kiện click cho từng item trong ListView
                        listView.setOnItemClickListener((parent, view, position, id) -> {// Lấy item được nhấn
                            ItemDomain selectedItem = result.get(position).getTourInfo();
                            // Bạn có thể mở một màn hình mới và truyền dữ liệu về địa điểm đã chọn
                            Intent ticketIntent = new Intent(MyTicketActivity.this, TicketActivity.class);
                            ticketIntent.putExtra("object", selectedItem);
                            startActivity(ticketIntent);// Truyền dữ liệu vào intent
                        });

                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyTicket>> call, Throwable t) {

            }
        });
    }
}